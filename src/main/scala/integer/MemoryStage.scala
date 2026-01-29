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
  val storeData = Wire(UInt(conf.xlen.W)) // Data to write to memory 
  storeData := io.rs2Data

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

  dmem.io.mask   := mask 
  dmem.io.wrData := storeData 

  // Outputs to WB
  io.memData := dmem.io.rdData // Read data from memory or 0 if not reading
  io.aluOut  := io.aluResult // Pass-through ALU result to WB
  io.rdOut   := io.rdIn // Pass-through Destination Register
  io.ctrlOut := io.ctrl // Pass-through Control Signals
}
