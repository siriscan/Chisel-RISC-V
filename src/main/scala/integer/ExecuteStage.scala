package integer

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


class ExecuteStage(config : CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Decode Stage
    val A = Input(UInt(32.W)) // Read Data 1
    val B = Input(UInt(32.W)) // Read Data 2
    val immediate = Input(UInt(32.W)) // Immediate Value
    val pcIn = Input(UInt(config.xlen.W)) // PC from Decode Stage
    val controlSignals = Input(new ControlSignals)

    // Outputs to Memory Stage
    val aluResult = Output(UInt(32.W))
    val zeroFlag = Output(Bool()) // For branch decisions
    val pcOut = Output(UInt(config.xlen.W)) // Pass PC to Memory Stage
  })

    // ALU Module
    val alu = Module(new ALU(32)) // 32-bit ALU

    val imm_flag = io.controlSignals.imm_flag

    // Connect ALU inputs
    alu.io.A := io.A

    // Select B input based on aluSrc control signal
    when(imm_flag) {
    alu.io.B := io.immediate // Use immediate
    } .otherwise {
    alu.io.B := io.B // Use register B
    }

    alu.io.opcode := io.controlSignals.aluOp

    // Connect ALU output
    io.aluResult := alu.io.C

    // Zero flag for branch decisions
    io.zeroFlag := (alu.io.C === 0.U)

    // Pass PC to Memory Stage
    io.pcOut := io.pcIn
  
}
