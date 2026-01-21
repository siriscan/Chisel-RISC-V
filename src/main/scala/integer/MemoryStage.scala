package integer
import chisel3._
import chisel3.util._

class MemoryStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute Stage
    val ctrl      = Input(new ControlSignals) // Control Signals
    val aluResult = Input(UInt(conf.xlen.W)) // Address
    val rs2Data   = Input(UInt(conf.xlen.W)) // Store Data
    val rdIn      = Input(UInt(5.W))           // Destination Register

    // Outputs to Writeback Stage
    val memData   = Output(UInt(conf.xlen.W)) // Read Data
    val aluOut    = Output(UInt(conf.xlen.W)) // Pass-through ALU result
    val rdOut     = Output(UInt(5.W))          // Pass-through Destination Register
    val ctrlOut   = Output(new ControlSignals) // Pass-through Control Signals
  })

  // Instantiate Data Memory (from previous steps)
  val dmem = Module(new DataMem(16384)) // 16KB
  dmem.io.addr     := io.aluResult
  dmem.io.wrData   := io.rs2Data
  dmem.io.memRead  := io.ctrl.memRead
  dmem.io.memWrite := io.ctrl.memWrite
  dmem.io.mask     := VecInit(Seq.fill(4)(true.B)) // Default word access

  //Access sizes
  when (io.ctrl.memRead || io.ctrl.memWrite) {
    switch (io.aluResult(1,0)) { // Access size based on address bits [1:0]
      is ("b00".U) { dmem.io.mask := VecInit(Seq(true.B, false.B, false.B, false.B)) } // Byte
      is ("b01".U) { dmem.io.mask := VecInit(Seq(true.B, true.B, false.B, false.B)) }  // Half-word
      is ("b10".U) { dmem.io.mask := VecInit(Seq(true.B, true.B, true.B, true.B)) }    // Word
      is ("b11".U) { dmem.io.mask := VecInit(Seq(true.B, true.B, true.B, true.B)) }    // Word (unaligned)
    }
  }




  // Connect Outputs to Writeback Stage
  io.memData := dmem.io.rdData  // Data read from memory
  io.aluOut  := io.aluResult // Pass-through ALU result
  io.rdOut   := io.rdIn         
  io.ctrlOut := io.ctrl       
}