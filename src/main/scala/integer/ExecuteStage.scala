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

    // Outputs to Fetch Stage
    val branchTaken = Output(Bool()) // True: branch taken, False: not taken
    val branchTarget = Output(UInt(config.xlen.W)) // Target PC if branch taken


    // Outputs to Memory Stage
    val C = Output(UInt(32.W)) // ALU Result
    val pcOut = Output(UInt(config.xlen.W)) // Pass PC to Memory Stage
    val controlSignalsOut = Output(new ControlSignals) // Pass control signals to Memory Stage
    val memWriteData = Output(UInt(32.W)) // Data (rs2) to write to memory for store instructions

    
  })

    // ALU Module
    val alu = Module(new ALU(config.xlen)) // 32-bit ALU

    val imm_flag = io.controlSignals.imm_flag // Immediate flag 
    val lui_flag = io.controlSignals.lui // LUI/AUIPC flag   

    // Connect ALU inputs

    // Connect isSigned signal
    alu.io.isSigned := io.controlSignals.isSigned


    // Select A input based on LUI/AUIPC control signal
    when (lui_flag === 1.U) { // LUI
      alu.io.A := 0.U
      alu.io.B := io.immediate
    } .elsewhen (lui_flag === 2.U) { // AUIPC
      alu.io.A := io.pcIn
      alu.io.B := io.immediate
    } .otherwise {
      alu.io.A := io.A
    }

    // Select B input based on aluSrc control signal
    when(imm_flag) {
    alu.io.B := io.immediate // Use immediate
    } .otherwise {
    alu.io.B := io.B // Use register B
    }

    // Connect ALU opcode
    alu.io.opcode := io.controlSignals.aluOp

    // Connect ALU output
    io.C := alu.io.C

    // Zero flag for branch decisions
    val isZero = (alu.io.C === 0.U) // used for BEQ and BNE
    
    // Branch Decision Logic
    val currentBranch = io.controlSignals.branch
    io.branchTaken := (currentBranch && isZero) || io.controlSignals.jump // Branch taken if branch signal is high and zero flag is set, or if it's a jump
    io.branchTarget := io.pcIn + io.immediate // Target PC = PC + immediate offset


    // Pass to Memory Stage
    io.pcOut := io.pcIn
    io.controlSignalsOut := io.controlSignals
    io.memWriteData := io.B
  
}
