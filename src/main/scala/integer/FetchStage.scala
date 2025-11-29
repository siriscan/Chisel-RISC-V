package integer
import chisel3._
import chisel3.Input
import integer.InstructionMem

class FetchStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute Stage (Branching)
    val branchTarget = Input(UInt(conf.xlen.W))
    val takeBranch   = Input(Bool())
    
    // Outputs to Decode Stage
    val pc   = Output(UInt(conf.xlen.W))
    val inst = Output(UInt(32.W)) // RISC-V instructions are always 32-bit
  })

  val pcReg  = RegInit(conf.pcReset.U(conf.xlen.W))
  val nextPc = Wire(UInt(conf.xlen.W))

  // 1. Instantiate Instruction Memory (from previous step)
  val imem = Module(new InstructionMem(16384, conf.imemFile))

  // 2. PC Logic
  // Default is PC + 4, unless branching
  nextPc := Mux(io.takeBranch, io.branchTarget, pcReg + 4.U)
  
  // 3. Update PC Register
  pcReg := nextPc

  // 4. Memory Interface
  // SyncReadMem has 1 cycle latency. We send nextPc NOW...
  imem.io.address := nextPc
  
  // ...and the instruction for nextPc arrives in the NEXT cycle,
  // matching the value of pcReg in that cycle.
  io.pc   := pcReg
  io.inst := imem.io.instruction
}
