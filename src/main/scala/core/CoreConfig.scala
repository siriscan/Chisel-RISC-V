package core

import chisel3._
import chisel3.util._

// Configuration for the Core stuff (Integer/Multiply/Atomic)
// Control Signals Bundle (Passed from Decode -> Execute)

final case class CoreConfig(
  xlen: Int, // Register and Data Width (Default: 32)
  startPC: BigInt, // Starting Program Counter Value
  imemSize: Int, // Instruction Memory Size in Bytes
  imemFile: String, // Instruction Memory Initialization File Path (Must be ASCII Hex)
  BTB_entries: Int,
  BHT_entries: Int
)

// Add later
final case class MemConfig(
  dmemSize: Int, // Data Memory Size in Bytes
  dmemFile: String // Data Memory Initialization File Path (Must be ASCII Hex)
)

// Configuration for Floating Point Unit (Single-Precision/Half-Precision)
final case class FloatingPointConfig(
  flen: Int, // Floating Point Register Length in Bits
  numRegs: Int, // Number of Floating Point Registers
  isBF16: Boolean, // Support for bfloat16 (true/false)
  mantissaBits: Int, // Number of Mantissa Bits
  exponentBits: Int // Number of Exponent Bits
)

// Configuration for Vector Extension
final case class VectorRegConfig(
  vlen: Int, // Vector Register Length in Bits
  elen: Int, // Maximum Element Length in Bits
  sew: Int,  // Standard Element Width in Bits
  numRegs: Int // Number of Vector Registers
)

// Control Signals that flow through the pipeline
class ControlSignals extends Bundle {
  val regWrite = Bool() // 0: No Write, 1: Write to Register
  val memOp    = UInt(3.W) // Funct3 for memory operations
  val memRead  = Bool() // 0: No Read, 1: Read from Memory
  val memWrite = Bool() // 0: No Write, 1: Write to Memory
  val memToReg = Bool() // 0: ALU result, 1: Memory data
  val imm_flag = Bool() // 0: Reg, 1: Immediate
  val branch   = Bool() // 0: No branch, 1: Branch
  val branchOp = UInt(3.W) // Funct3 for branch operations
  val jump     = UInt(2.W) // Jump flag, 0: No JAL/JALR, 1: JAL, 2: JALR
  val aluOp    = UInt(8.W) // Simplified ALU Opcodes
  val lui      = UInt(2.W) // LUI and AUIPC flag, 0: No LUI/AUIPC, 1: LUI, 2: AUIPC
  val isSigned = Bool() // 0: Unsigned operation, 1: Signed operation 

  // Atomic Instructions (LR/SC)
  val atomic      = Bool() // True if instruction is atomic (LR/SC)
  val amoOp       = UInt(5.W) // AMO Operation (Funct5 for AMO instructions)
  val rl_flag     = Bool() // Release flag for LR/SC
  val aq_flag     = Bool() // Acquire flag for LR/SC
  val isLR        = Bool() // True if instruction is LR
  val isSC        = Bool() // True if instruction is SC


  // CSR Signals (NOT USED YET)
  val csrFlag  = Bool() // True if instruction is CSR type
  val csrRead  = Bool() // CSR Read Enable
  val csrWrite = Bool() // CSR Write Enable
  val csrToReg = Bool() // 0: ALU result, 1: CSR data
  val csrOp    = UInt(2.W) // CSR Operation

}