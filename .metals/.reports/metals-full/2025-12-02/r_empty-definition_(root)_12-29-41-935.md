error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/CoreConfig.scala:scala/Int#
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/CoreConfig.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol scala/Int#
empty definition using fallback
non-local guesses:

offset: 637
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/CoreConfig.scala
text:
```scala
package integer

import chisel3._
import chisel3.util._

// Configuration for the Core stuff (Integer/Multiply/Atomic)
// Control Signals Bundle (Passed from Decode -> Execute)

final case class CoreConfig(
  xlen: Int, // Register and Data Width (Default: 32)
  startPC: BigInt, // Starting Program Counter Value
  imemSize: Int, // Instruction Memory Size in Bytes
  imemFile: String // Instruction Memory Initialization File Path (Must be ASCII Hex)
)

// Configuration for Floating Point Unit (Single-Precision/Half-Precision)
final case class FloatingPointConfig(
  flen: Int, // Floating Point Register Length in Bits
  numRegs: In@@t // Number of Floating Point Registers
  isHalf: Boolean // Support Half-Precision (16-bit)

)

// Configuration for Vector Extension
final case class VectorRegConfig(
  vlen: Int, // Vector Register Length in Bits
  elen: Int, // Maximum Element Length in Bits
  sew: Int,  // Standard Element Width in Bits
  numRegs: Int // Number of Vector Registers
)

// Add later
final case class MemConfig(
  dmemSize: Int, // Data Memory Size in Bytes
  dmemFile: String // Data Memory Initialization File Path (Must be ASCII Hex)
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
  val aluOp    = UInt(4.W) // Simplified ALU Opcode
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 