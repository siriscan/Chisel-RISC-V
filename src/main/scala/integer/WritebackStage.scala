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



  // 1. Calculate the bit offset (0, 8, 16, 24)
  val offset = io.aluResult(1, 0) << 3 
  
  // 2. Shift and Mask to get the raw 8-bit byte
  // We shift the full 32-bit word right and take the bottom 8 bits
  val rawByte = (io.memData >> offset)(7, 0) 

  // 3. Sign Extension Logic
  // If bit 7 is 1, fill the upper 24 bits with 1s. Otherwise 0s.
  val extendedByte = Wire(UInt(32.W))
  
  // Assuming you have a signal 'isUnsigned' (e.g. from control logic for 'lbu')
  // If you don't have 'lbu' support yet, just use the 'else' case.
  /* if (io.ctrl.isUnsigned) {
    extendedByte := rawByte.asUInt
  } else { 
  */
    // Sign Extension: Cat 24 copies of the sign bit with the byte
    extendedByte := Cat(Fill(24, rawByte(7)), rawByte)
  /* } */

  // 4. Select Result
  io.wbData := Mux(io.ctrl.memToReg, extendedByte, io.aluResult)


  // Select Result: Memory Data (Load) vs ALU Result (Arithmetic)
  
  io.wbAddr   := io.rdIn
  io.wbEnable := io.ctrl.regWrite
}