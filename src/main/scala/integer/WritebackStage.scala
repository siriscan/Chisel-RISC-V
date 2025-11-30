package integer

import chisel3._
import chisel3.util._

class WritebackStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Memory Stage
    val ctrl      = Input(new ControlSignals)
    val memData   = Input(UInt(conf.xlen.W))
    val aluResult = Input(UInt(conf.xlen.W))
    val rdIn      = Input(UInt(5.W))

    // Outputs to Decode Stage (Register File)
    val wbData    = Output(UInt(conf.xlen.W)) // Data to Write Back
    val wbAddr    = Output(UInt(5.W))
    val wbEnable  = Output(Bool())
  })

  // Select Result: Memory Data (Load) vs ALU Result (Arithmetic)
  io.wbData := Mux(io.ctrl.memToReg, io.memData, io.aluResult)
  
  io.wbAddr   := io.rdIn
  io.wbEnable := io.ctrl.regWrite
}