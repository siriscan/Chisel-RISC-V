error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala:instruction
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala
empty definition using pc, found symbol in pc: instruction
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/Pmem.io.instruction.
	 -chisel3/Pmem.io.instruction#
	 -chisel3/Pmem.io.instruction().
	 -chisel3/util/Pmem.io.instruction.
	 -chisel3/util/Pmem.io.instruction#
	 -chisel3/util/Pmem.io.instruction().
	 -core/Pmem.io.instruction.
	 -core/Pmem.io.instruction#
	 -core/Pmem.io.instruction().
	 -Pmem.io.instruction.
	 -Pmem.io.instruction#
	 -Pmem.io.instruction().
	 -scala/Predef.Pmem.io.instruction.
	 -scala/Predef.Pmem.io.instruction#
	 -scala/Predef.Pmem.io.instruction().
offset: 1414
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala
text:
```scala
package integer
import chisel3._
import chisel3.Input
import chisel3.Output
import chisel3.util._
import core._

class FetchStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {

    // Inputs from Execute Stage (Branching)
    val branchTarget = Input(UInt(conf.xlen.W)) // Branch PC if branch taken
    val takeBranch = Input(Bool()) // True: branch taken, False: PC + 4

    // Inputs from Control Unit (Hazards)
    val stall = Input(Bool()) // True: hold PC, False: update PC

    // Outputs to Decode Stage
    val pc   = Output(UInt(conf.xlen.W)) // Current PC value
    val instruction = Output(UInt(32.W)) // RISC-V instructions are always 32-bit
  })

  // Program Counter (PC) Register
  val pcReg  = RegInit(conf.startPC.U(conf.xlen.W))
  val nextPc = Wire(UInt(conf.xlen.W))

  val Pmem = Module(new InstructionMem(conf.imemSize, conf.imemFile)) // 16KB Instruction Memory from imemFile (pmem.hex)

  nextPc := Mux(io.takeBranch, io.branchTarget, pcReg + 4.U) // Default is PC + 4, unless branching

  pcReg := Mux(io.stall, pcReg, nextPc) // Hold PC if stall is true ; else update PC to nextPc

  // SyncReadMem has 1 cycle latency. We send nextPc. NOW...
  Pmem.io.address := nextPc

  // ...and the instruction for nextPc arrives in the NEXT cycle, matching the value of pcReg in that cycle.
  io.pc   := pcReg
  io.instruction := Pmem.io.instruct@@ion
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: instruction