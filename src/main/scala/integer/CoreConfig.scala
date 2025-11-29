package integer

import chisel3._
import chisel3.util._

// Configuration for the Core stuff (Integer)
final case class CoreConfig(
  xlen: Int = 32,
  pcReset: Long = 0,
  imemFile: String = "" // Instruction Memory Initialization File Path
)

// Control Signals Bundle (Passed from Decode -> Execute)
class ControlSignals extends Bundle {
  val aluOp    = UInt(4.W)
  val regWrite = Bool()
  val memRead  = Bool()
  val memWrite = Bool()
  val branch   = Bool()
  val memToReg = Bool()
  val op2Sel   = Bool() // 0: rs2, 1: immediate
}