package integer

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class DecodeStage(conf: CoreConfig) extends Module {
    val io = IO(new Bundle {

    // Inputs from Fetch Stage
    val instruction = Input(UInt(32.W))
    val pc = Input(UInt(conf.xlen.W))

    // Inputs from Writeback Stage
    val C = Input(UInt(32.W)) // Data to write back to register file
    val writeEnable = Input(Bool())
    val writeAddress = Input(UInt(5.W))

    // Outputs to Execute Stage
    val A = Output(UInt(32.W)) // Read Data 1
    val B = Output(UInt(32.W)) // Read Data 2
    val immediate = Output(UInt(32.W)) // Immediate Value
    val pcOut = Output(UInt(conf.xlen.W)) // Pass PC to Execute Stage
    val controlSignals = Output(new ControlSignals)

  })
    // Default values
    io.controlSignals := 0.U.asTypeOf(new ControlSignals)
    io.immediate := 0.U
    io.controlSignals.imm_flag := false.B
    io.pcOut := io.pc
    io.controlSignals.aluOp := 0.U // NOP


    val imm = io.instruction(31, 20) // I-type immediate

    val imm_sext = Cat(Fill(20, imm(11)), imm) // sign-extend immediate

    val opcode = io.instruction(6, 0)
    val funct3 = io.instruction(14, 12)
    val funct7 = io.instruction(31, 25)

    val regFile = Module(new RegisterFile(conf.xlen)) // 32-bit register file

    // Decode Logic
    switch(opcode) {
      is("b0010011".U) { // I-type ALU Instructions
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_sext // Sign-extended immediate
        switch(funct3) {
          is("b000".U) { io.controlSignals.aluOp := 1.U } // ADDI
          is("b111".U) { io.controlSignals.aluOp := 3.U } // ANDI
          is("b110".U) { io.controlSignals.aluOp := 4.U } // ORI
          is("b100".U) { io.controlSignals.aluOp := 5.U } // XORI
          is("b001".U) { io.controlSignals.aluOp := 6.U } // SLLI
          is("b101".U) {
            when(funct7 === "b0000000".U) {
              io.controlSignals.aluOp := 7.U // SRLI
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 8.U // SRAI
            }
          }
          is("b010".U) { io.controlSignals.aluOp := 9.U } // SLTI
          is("b011".U) { io.controlSignals.aluOp := 10.U } // SLTIU
        }
      }
      is("b0000011".U) { // Load Instructions (e.g., LW)
        io.controlSignals.imm_flag  := true.B // Use immediate
        io.controlSignals.memRead := true.B // Memory Read
        io.controlSignals.memToReg := true.B // Write memory data to register
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate
      }
      is("b0100011".U) { // Store Instructions (e.g., SW)
        val imm11_5 = io.instruction(31, 25)
        val imm4_0 = io.instruction(11, 7)
        val imm_s = Cat(imm11_5, imm4_0)
        val imm_s_sext = Cat(Fill(20, imm_s(11)), imm_s)
        io.controlSignals.imm_flag  := true.B // Use immediate
        io.controlSignals.memWrite := true.B // Memory Write
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_s_sext // Sign-extended immediate
      }
      is("b1100011".U) { // Branch Instructions (e.g., BEQ)
        val imm12 = io.instruction(31)
        val imm10_5 = io.instruction(30, 25)
        val imm4_1 = io.instruction(11, 8)
        val imm11 = io.instruction(7)
        val imm_b = Cat(imm12, imm11, imm10_5, imm4_1, 0.U(1.W))
        val imm_b_sext = Cat(Fill(19, imm_b(12)), imm_b)
        io.controlSignals.branch := true.B
        io.controlSignals.aluOp := 2.U // SUB for comparison
        io.immediate := imm_b_sext // Sign-extended immediate
      }
      is("b1101111".U) { // JAL
        val imm20 = io.instruction(31)
        val imm10_1 = io.instruction(30, 21)
        val imm11 = io.instruction(20)
        val imm19_12 = io.instruction(19, 12)
        val imm_j = Cat(imm20, imm19_12, imm11, imm10_1, 0.U(1.W))
        val imm_j_sext = Cat(Fill(11, imm_j(20)), imm_j)
        io.controlSignals.jump := true.B
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_j_sext // Sign-extended immediate
      }
      is("b0110011".U) { // R-type Instructions (ALU + M-Ext)
        io.controlSignals.imm_flag := false.B
        io.controlSignals.regWrite := true.B
        
        switch(funct3) {
          // funct3 = 000: ADD, SUB, MUL
          is("b000".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 11.U // MUL
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 2.U  // SUB
            } .otherwise {
              io.controlSignals.aluOp := 1.U  // ADD
            }
          }
          
          // funct3 = 001: SLL, MULH
          is("b001".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 12.U // MULH
            } .otherwise {
              io.controlSignals.aluOp := 6.U  // SLL
            }
          }
          
          // funct3 = 010: SLT, MULHSU
          is("b010".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 13.U // MULHSU
            } .otherwise {
              io.controlSignals.aluOp := 9.U  // SLT
            }
          }
          
          // funct3 = 011: SLTU, MULHU
          is("b011".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 14.U // MULHU
            } .otherwise {
              io.controlSignals.aluOp := 10.U // SLTU
            }
          }
          
          // funct3 = 100: XOR, DIV
          is("b100".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 15.U // DIV
            } .otherwise {
              io.controlSignals.aluOp := 5.U  // XOR
            }
          }
          
          // funct3 = 101: SRL, SRA, DIVU
          is("b101".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 16.U // DIVU
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 8.U  // SRA
            } .otherwise {
              io.controlSignals.aluOp := 7.U  // SRL
            }
          }
          
          // funct3 = 110: OR, REM
          is("b110".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 17.U // REM
            } .otherwise {
              io.controlSignals.aluOp := 4.U  // OR
            }
          }
          
          // funct3 = 111: AND, REMU
          is("b111".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 18.U // REMU
            } .otherwise {
              io.controlSignals.aluOp := 3.U  // AND
            }
          }
        }
      }


      
      }
    

    


    // Connect Register File
    regFile.io.opcode := 0.U // Default (nop)
    regFile.io.C := io.C
    regFile.io.readAddressA := io.instruction(19, 15) // rs1
    regFile.io.readAddressB := io.instruction(24, 20) // rs2
    regFile.io.writeEnable := io.writeEnable
    regFile.io.writeAddress := io.writeAddress
    io.A := regFile.io.A // rs1 data
    io.B := regFile.io.B // rs2 data

}
