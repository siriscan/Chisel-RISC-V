error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/int.
	 -chisel3/int#
	 -chisel3/int().
	 -chisel3/util/int.
	 -chisel3/util/int#
	 -chisel3/util/int().
	 -core/int.
	 -core/int#
	 -core/int().
	 -int.
	 -int#
	 -int().
	 -scala/Predef.int.
	 -scala/Predef.int#
	 -scala/Predef.int().
offset: 121
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala
text:
```scala
package integer
import chisel3._
import chisel3.Input
import chisel3.Output
import chisel3.util._
import core._

i@@nt ENTRIES = 64;

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

  // Branch Target Buffer (BTB) for branch prediction
  val btb = Module(new BranchTargetBuffer(entries = 64, xlen = conf.xlen)) // 64-entry BTB


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
  io.instruction := Pmem.io.instruction

  // Early Jumping Support
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 