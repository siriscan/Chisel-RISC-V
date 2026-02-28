error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala:memWriteEnable
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala
empty definition using pc, found symbol in pc: memWriteEnable
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/atomic/io/memWriteEnable.
	 -chisel3/atomic/io/memWriteEnable#
	 -chisel3/atomic/io/memWriteEnable().
	 -chisel3/util/atomic/io/memWriteEnable.
	 -chisel3/util/atomic/io/memWriteEnable#
	 -chisel3/util/atomic/io/memWriteEnable().
	 -core/atomic/io/memWriteEnable.
	 -core/atomic/io/memWriteEnable#
	 -core/atomic/io/memWriteEnable().
	 -atomic/io/memWriteEnable.
	 -atomic/io/memWriteEnable#
	 -atomic/io/memWriteEnable().
	 -scala/Predef.atomic.io.memWriteEnable.
	 -scala/Predef.atomic.io.memWriteEnable#
	 -scala/Predef.atomic.io.memWriteEnable().
offset: 3320
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala
text:
```scala
package integer

import chisel3._
import chisel3.util._
import core._

class MemoryStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute Stage
    val ctrl      = Input(new ControlSignals)   // Control Signals
    val aluResult = Input(UInt(conf.xlen.W))    // ALU Result (Address if memWrite or memRead is true)
    val rs2Data   = Input(UInt(conf.xlen.W))    // Store Data (unshifted)
    val rdIn      = Input(UInt(5.W))            // Destination Register

    // Outputs to Writeback Stage
    val memData   = Output(UInt(conf.xlen.W))   // Read Data
    val aluOut    = Output(UInt(conf.xlen.W))   // Pass-through ALU result
    val rdOut     = Output(UInt(5.W))           // Pass-through Destination Register
    val ctrlOut   = Output(new ControlSignals)  // Pass-through Control Signals

    val stall      = Output(Bool())              // Stall signal for Load-Use hazard
  })

  // Data Memory
  val dmem = Module(new DataMem(16384)) // 16KB Data Memory
  val atomic = Module(new AtomicUnit(conf)) // Atomic Unit for AMO/LR/SC instructions

  val addrLSB = io.aluResult(1, 0) // byte offset within the 32-bit word
  val byteShift = addrLSB << 3     // 0, 8, 16, 24

  // Atomic Unit Connections
  atomic.io.atomic  := io.ctrl.atomic
  atomic.io.memAddress   := io.aluResult
  atomic.io.rs2Data    := io.rs2Data
  atomic.io.rd     := io.rdIn
  atomic.io.atomicOp := io.ctrl.amoOp
  atomic.io.isLR   := io.ctrl.isLR
  atomic.io.isSC   := io.ctrl.isSC
  atomic.io.memDataIn := dmem.io.rdData
  

  // Default: no write lanes enabled
  val mask = Wire(Vec(4, Bool()))
  mask := VecInit(Seq(false.B, false.B, false.B, false.B))

  // Default: unshifted data (good for SW)
  val storeData = Wire(UInt(conf.xlen.W)) // Data to write to memory 
  storeData := io.rs2Data

  val normAddr   = io.aluResult
  val normRead   = io.ctrl.memRead
  val normWrite  = io.ctrl.memWrite
  val normMask   = mask
  val normWrData = storeData




  // Only relevant on stores
  when(io.ctrl.memWrite) {
    switch(io.ctrl.memOp) { // Use funct3 to determine store size
      is("b000".U) { // SB
        // Enable exactly one byte lane based on addr[1:0]
        mask := VecInit(Seq(
          addrLSB === 0.U,
          addrLSB === 1.U,
          addrLSB === 2.U,
          addrLSB === 3.U
        ))
        // Shift byte into the selected lane
        storeData := io.rs2Data << byteShift
      }

      is("b001".U) { // SH
        // Two-byte lanes: either [0,1] or [2,3] based on addr[1]
        mask := VecInit(Seq(
          addrLSB(1) === 0.U,
          addrLSB(1) === 0.U,
          addrLSB(1) === 1.U,
          addrLSB(1) === 1.U
        ))
        // Shift halfword into the selected half
        storeData := io.rs2Data << (addrLSB(1) << 4) // 0 or 16
      }

      is("b010".U) { // SW
        mask := VecInit(Seq(true.B, true.B, true.B, true.B))
        storeData := io.rs2Data
      }
    }
  }

  // Connect to Data Memory
  val useAtomic = atomic.io.stall || io.ctrl.atomic
  dmem.io.addr     := Mux(useAtomic, atomic.io.memAddressOut,   normAddr)
  dmem.io.memRead  := Mux(useAtomic, atomic.io.memReadEnable,   normRead)
  dmem.io.memWrite := Mux(useAtomic, atomic.io.memWrit@@eEnable,  normWrite)
  dmem.io.mask     := Mux(useAtomic, atomic.io.memMask,   normMask)
  dmem.io.wrData   := Mux(useAtomic, atomic.io.memWriteDataOut, normWrData)

  // Switch outputs to Writeback Stage between normal memory access and atomic unit
  val outCtrl = Wire(new ControlSignals)
  outCtrl := io.ctrl


  // Outputs to WB
  io.memData := dmem.io.rdData // Read data from memory or 0 if not reading
  io.aluOut  := io.aluResult // Pass-through ALU result to WB
  io.rdOut   := io.rdIn // Pass-through Destination Register
  io.ctrlOut := io.ctrl // Pass-through Control Signals
  io.stall   := atomic.io.stall // Stall if atomic unit is performing an operation
  



}

```


#### Short summary: 

empty definition using pc, found symbol in pc: memWriteEnable