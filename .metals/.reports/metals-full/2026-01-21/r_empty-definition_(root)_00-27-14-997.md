error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb

found definition using fallback; symbol otherwise
offset: 2516
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala
text:
```scala
package integer

import chisel3._
import chisel3.util._

class MemoryStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute Stage
    val ctrl      = Input(new ControlSignals)   // Control Signals (must include memF3)
    val aluResult = Input(UInt(conf.xlen.W))    // Address
    val rs2Data   = Input(UInt(conf.xlen.W))    // Store Data (unshifted)
    val rdIn      = Input(UInt(5.W))            // Destination Register

    // Outputs to Writeback Stage
    val memData   = Output(UInt(conf.xlen.W))   // Read Data
    val aluOut    = Output(UInt(conf.xlen.W))   // Pass-through ALU result
    val rdOut     = Output(UInt(5.W))           // Pass-through Destination Register
    val ctrlOut   = Output(new ControlSignals)  // Pass-through Control Signals
  })

  // Data Memory
  val dmem = Module(new DataMem(16384)) // 16KB Data Memory

  val addrLSB = io.aluResult(1, 0) // byte offset within the 32-bit word
  val byteShift = addrLSB << 3     // 0, 8, 16, 24

  // Default connections
  dmem.io.addr     := io.aluResult
  dmem.io.memRead  := io.ctrl.memRead
  dmem.io.memWrite := io.ctrl.memWrite

  // Default: no write lanes enabled
  val mask = Wire(Vec(4, Bool()))
  mask := VecInit(Seq(false.B, false.B, false.B, false.B))

  // Default: unshifted data (good for SW)
  val storeData = Wire(UInt(conf.xlen.W))
  storeData := io.rs2Data

  // Only relevant on stores
  when(io.ctrl.memWrite) {
    switch(io.ctrl.memF3) {
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

      // If something unexpected shows up, do a safe "no write"
      // (prevents corrupting memory on unknown funct3)
      .otherwis@@e {
        mask := VecInit(Seq(false.B, false.B, false.B, false.B))
        storeData := io.rs2Data
      }
    }
  }

  dmem.io.mask   := mask
  dmem.io.wrData := storeData

  // Outputs to WB
  io.memData := dmem.io.rdData
  io.aluOut  := io.aluResult
  io.rdOut   := io.rdIn
  io.ctrlOut := io.ctrl
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 