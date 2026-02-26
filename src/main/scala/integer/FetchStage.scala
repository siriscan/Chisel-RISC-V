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

    // Inputs from Branch Predictor (for prediction)
    val predTakenIn  = Input(Bool()) // Predicted taken/not taken
    val predTargetIn = Input(UInt(conf.xlen.W)) // Predicted target PC
  })
  
  val Pmem = Module(new InstructionMem(conf.imemSize, conf.imemFile)) // 16KB Instruction Memory from imemFile (pmem.hex)

  // Program Counter (PC) Register
  val pcReg  = RegInit(conf.startPC.U(conf.xlen.W))
  val nextPc = Wire(UInt(conf.xlen.W))


  // priority: real redirect (mispredict fix) > BTB prediction > sequential
  val seqNext = pcReg + 4.U
  val specNext = Mux(io.predTakenIn, io.predTargetIn, seqNext)
  nextPc := Mux(io.takeBranch, io.branchTarget, specNext)

  pcReg := Mux(io.stall, pcReg, nextPc)

  // keep your imem timing model
  Pmem.io.address := nextPc // 1 cycle latency
  io.pc := pcReg 
  io.instruction := Pmem.io.instruction
}
