error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala:predictedPC
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/FetchStage.scala
empty definition using pc, found symbol in pc: predictedPC
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/btb/io/predictedPC.
	 -chisel3/btb/io/predictedPC#
	 -chisel3/btb/io/predictedPC().
	 -chisel3/util/btb/io/predictedPC.
	 -chisel3/util/btb/io/predictedPC#
	 -chisel3/util/btb/io/predictedPC().
	 -core/btb/io/predictedPC.
	 -core/btb/io/predictedPC#
	 -core/btb/io/predictedPC().
	 -btb/io/predictedPC.
	 -btb/io/predictedPC#
	 -btb/io/predictedPC().
	 -scala/Predef.btb.io.predictedPC.
	 -scala/Predef.btb.io.predictedPC#
	 -scala/Predef.btb.io.predictedPC().
offset: 1452
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

    // Outputs to Branch Target Buffer (for branch prediction)
    val predTaken  = Output(Bool())
    val predTarget = Output(UInt(conf.xlen.W))

    // also add BTB update ports coming from Execute:
    val btbUpdateValid  = Input(Bool())
    val btbUpdatePC     = Input(UInt(conf.xlen.W))
    val btbUpdateTarget = Input(UInt(conf.xlen.W))
  })

  // Branch Target Buffer (BTB) for branch prediction
  val btb = Module(new BranchTargetBuffer(entries = 64, xlen = conf.xlen)) // 64-entry BTB
  btb.io.pcIn := pcReg

  // Connect BTB update inputs from Execute Stage
  btb.io.update := io.btbUpdateValid
  btb.io.pcUpdate := io.btbUpdatePC
  btb.io.targetPCUpdate := io.btbUpdateTarget 

  val  = btb.io.hit
  val predictedPC = btb.io.pred@@ictedPC


  // Program Counter (PC) Register
  val pcReg  = RegInit(conf.startPC.U(conf.xlen.W))
  val nextPc = Wire(UInt(conf.xlen.W))

  val Pmem = Module(new InstructionMem(conf.imemSize, conf.imemFile)) // 16KB Instruction Memory from imemFile (pmem.hex)

  // Determine next PC 
  nextPc := Mux(io.takeBranch, io.branchTarget, pcReg + 4.U) // If branch taken, go to branchTarget; else PC + 4

  pcReg := Mux(io.stall, pcReg, nextPc) // Hold PC if stall is true ; else update PC to nextPc

  // SyncReadMem has 1 cycle latency. We send nextPc. NOW...
  Pmem.io.address := nextPc

  // ...and the instruction for nextPc arrives in the NEXT cycle, matching the value of pcReg in that cycle.
  io.pc   := pcReg
  io.instruction := Pmem.io.instruction
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: predictedPC