package integer

import chisel3._
import circt.stage.ChiselStage

class RegisterFile(width: Int) extends Module { // width will be used for future expansion; 32 bits for now

  val io = IO(new Bundle {
    val opcode = Input(UInt(4.W))
    val C = Input(UInt(width.W))
    val readAddressA = Input(UInt(5.W))
    val readAddressB = Input(UInt(5.W))
    val writeEnable = Input(Bool())
    val writeAddress = Input(UInt(5.W))
    val A = Output(UInt(width.W))
    val B = Output(UInt(width.W))
    
  })

  val regFile = RegInit(VecInit(Seq.fill(32)(0.U(width.W)))) // 32 registers of 32 bits each

  //Register 0 is always zero
  regFile(0) := 0.U // Forces x0 = 0

  // Read ports
  io.A := Mux(io.readAddressA === 0.U, 0.U, regFile(io.readAddressA))
  io.B := Mux(io.readAddressB === 0.U, 0.U, regFile(io.readAddressB))

  // Write port
  when(io.writeEnable && (io.writeAddress =/= 0.U)) {
    regFile(io.writeAddress) := io.C
  }
  
}

//Delete comments for testing Register File independently 
/* 
object RegisterFile extends App {
  ChiselStage.emitSystemVerilogFile(
    new integer.RegisterFile(width = 32), // 32-bit ALU
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}
 */