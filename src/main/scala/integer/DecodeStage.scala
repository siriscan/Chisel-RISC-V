package integer

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class DecodeStage extends Module {
    val io = IO(new Bundle {
    val A        = Input(UInt(32.W))
    val B        = Input(UInt(32.W))
    val instruction = Input(UInt(32.W))
    val C     = Output(UInt(32.W))
  })
  


    val imm = io.instruction(31, 20) // I-type immediate
    val imm_sext = Cat(Fill(20, imm(11)), imm) // sign-extend immediate
    val imm_uext = Cat(Fill(20, 0.U), imm) // zero-extend immediate

    val opcode = io.instruction(6, 0)
    val funct3 = io.instruction(14, 12)
    val funct7 = io.instruction(31, 25)
}
