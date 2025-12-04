package integer

import chisel3._
import chisel3.util._

// Configuration for the Core stuff (Integer)
// Control Signals Bundle (Passed from Decode -> Execute)

final case class CoreConfig(
  xlen: Int,
  startPC: BigInt,
  imemFile: String, // Instruction Memory Initialization File Path (Must be ASCII Hex)
  imemSize: Int = 16384 // Default 16KB Instruction Memory
)


// Control Signals that flow through the pipeline
class ControlSignals extends Bundle {
  val regWrite = Bool() // 0: No Write, 1: Write to Register
  val memRead  = Bool() // 0: No Read, 1: Read from Memory
  val memWrite = Bool() // 0: No Write, 1: Write to Memory
  val memToReg = Bool() // 0: ALU result, 1: Memory data
  val imm_flag   = Bool() // 0: Reg, 1: Immediate
  val branch   = Bool() // 0: No branch, 1: Branch
  val jump     = Bool() // 0: No jump, 1: Jump
  val aluOp    = UInt(8.W) // Simplified ALU Opcodes
  val lui      = UInt(2.W) // LUI and AUIPC flag, 0: No LUI/AUIPC, 1: LUI, 2: AUIPC
  

  // CSR Signals
  val csrRead  = Bool() // CSR Read Enable
  val csrWrite = Bool() // CSR Write Enable
  val csrToReg = Bool() // 0: ALU result, 1: CSR data
  val csrOp    = UInt(2.W) // CSR Operation

}