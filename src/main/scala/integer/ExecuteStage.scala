package integer

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import core._


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

    // Prediction update outputs to Fetch Stage
    // (not needed in Execute Stage, but passed through)

    // Inputs for branch prediction from Decode Stage
    val predTaken  = Input(Bool())
    val predTarget = Input(UInt(config.xlen.W))

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
      alu.io.A := io.A // Default A input
    }

    // Select B input based on aluSrc control signal
    alu.io.B := Mux(imm_flag, io.immediate, io.B) // 1: immediate, 0: value from register

    // Connect ALU opcode
    alu.io.opcode := io.controlSignals.aluOp

    // Connect ALU output
    io.C := alu.io.C

    // Branches
    val isBranch = io.controlSignals.branch // True if branch instruction
    val branchOp = io.controlSignals.branchOp

    val a = alu.io.A
    val b = alu.io.B
    val isEqual = (a === b) // Equality comparison
    val isLessThan = Mux(io.controlSignals.isSigned, (a.asSInt < b.asSInt), (a < b)) // Signed or unsigned comparison
    val isGreaterThan = Mux(io.controlSignals.isSigned, (a.asSInt > b.asSInt), (a > b)) // Signed or unsigned comparison
    val isBGE = isGreaterThan || isEqual  // Greater than or equal (Both signed and unsigned)

    val isBranchTaken = WireDefault(false.B) // Default not taken

    // Branch Decision Logic
    when (isBranch) {
      when(branchOp === "b000".U) { // BEQ
        isBranchTaken := isEqual
      } .elsewhen(branchOp === "b001".U) { // BNE
        isBranchTaken := !isEqual
      } .elsewhen(branchOp === "b100".U) { // BLT
        isBranchTaken := isLessThan
      } .elsewhen(branchOp === "b101".U) { // BGE
        isBranchTaken := isBGE
      } .elsewhen(branchOp === "b110".U) { // BLTU
        isBranchTaken := isLessThan
      } .elsewhen(branchOp === "b111".U) { // BGEU
        isBranchTaken := isBGE
      }
    }.otherwise {
      isBranchTaken := false.B // Not a branch instruction
    }   

    // Jumps
    val jumpType = io.controlSignals.jump // 0: No jump, 1: JAL, 2: JALR


    // Calculate Branch Target
    val pcPlusImm =  (io.pcIn + io.immediate) & (~3.U(32.W)) // Branch target address (PC + imm) with last 2 bits zeroed for word alignment
    val jalrTarget = (a + io.immediate) & (~3.U(32.W)) // JALR target address (rs1 + imm) with last 2 bits zeroed for word alignment

    when (jumpType === 1.U) { // JAL
      io.branchTarget := pcPlusImm
    } .elsewhen (jumpType === 2.U) { // JALR
      io.branchTarget := jalrTarget
    } .elsewhen (isBranch) { // Branch instructions
      io.branchTarget := pcPlusImm
    } .otherwise {
      io.branchTarget := 0.U // Default value (not used if not branching)
    }

    // Final Branch Taken Signal
    io.branchTaken := (isBranch && isBranchTaken) || (jumpType =/= 0.U) // Taken if branch condition met or if jump instruction

    val jalrMispredict = WireDefault(false.B)
    // JALR Mispredict Detection
    jalrMispredict := !io.predTaken || (io.predTarget =/= jalrTarget)



    // Pass to Memory Stage
    io.pcOut := io.pcIn
    io.controlSignalsOut := io.controlSignals

    // Data forwarding to Memory Stage for store instructions
    io.memWriteData := io.B

  }