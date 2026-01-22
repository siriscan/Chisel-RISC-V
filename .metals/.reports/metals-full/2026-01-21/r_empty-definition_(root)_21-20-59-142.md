error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/imm_b/asSInt/resize.
	 -chisel3/imm_b/asSInt/resize#
	 -chisel3/imm_b/asSInt/resize().
	 -chisel3/util/imm_b/asSInt/resize.
	 -chisel3/util/imm_b/asSInt/resize#
	 -chisel3/util/imm_b/asSInt/resize().
	 -imm_b/asSInt/resize.
	 -imm_b/asSInt/resize#
	 -imm_b/asSInt/resize().
	 -scala/Predef.imm_b.asSInt.resize.
	 -scala/Predef.imm_b.asSInt.resize#
	 -scala/Predef.imm_b.asSInt.resize().
offset: 5485
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
text:
```scala
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
    io.controlSignals.branch := false.B // Default no branch
    io.controlSignals.branchOp := 0.U // Default branch funct3 (None)
    io.controlSignals.memOp := 0.U // Default funct3 for memory operations (None)
    io.controlSignals.jump := 0.U // Default no jump
    io.controlSignals.memRead := false.B // Default no memory read
    io.controlSignals.memWrite := false.B  // Default no memory write
    io.controlSignals.memToReg := false.B  // Default ALU to Reg
    io.controlSignals.regWrite := false.B  // Default no register write
    io.controlSignals.lui := 0.U // Default no LUI/AUIPC
    io.controlSignals.isSigned := false.B // Default unsigned operations


    val imm = io.instruction(31, 20) // I-type immediate

    val imm_u = io.instruction(31, 12) // U-type immediate
    val imm_upper = Cat(imm_u, Fill(12, 0.U)) // Shift left by 12

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
          is("b010".U) {
            io.controlSignals.isSigned := true.B // Signed comparison
            io.controlSignals.aluOp := 9.U // SLTI
          } 
          is("b011".U) { io.controlSignals.aluOp := 9.U } // SLTIU
        }
      }
      is("b0000011".U) { // Load Instructions (e.g., LW)
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.memRead := true.B // Memory Read
        io.controlSignals.memToReg := true.B // Write memory data to register
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate
        io.controlSignals.memOp := funct3 // Pass funct3 for memory operations

        switch(funct3) { // Not used in Decode, but decoded here for completeness. Only used in Writeback Stage for load size
          is("b000".U) { /* LB */ }
          is("b001".U) { /* LH */ }
          is("b010".U) { /* LW */ }
          is("b100".U) { /* LBU */ }
          is("b101".U) { /* LHU */ }
        }
      }
      is("b0100011".U) { // Store Instructions (e.g., SW)
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.memWrite := true.B // Memory Write
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        val imm11_5 = io.instruction(31, 25)
        val imm4_0 = io.instruction(11, 7)
        val imm_s = Cat(imm11_5, imm4_0)
        val imm_s_sext = Cat(Fill(20, imm_s(11)), imm_s)
        io.immediate := imm_s_sext // Sign-extended immediate
        io.controlSignals.memOp := funct3 // Pass funct3 for memory operations

        switch(funct3) { // Not used in Decode, but decoded here for completeness. Only used in Memory Stage for store size
          is("b000".U) { /* SB */ }
          is("b001".U) { /* SH */ }
          is("b010".U) { /* SW */ }
        }

      }
      is("b1100011".U) { // Branch Instructions (e.g., BEQ)
        val imm12 = io.instruction(31)
        val imm10_5 = io.instruction(30, 25)
        val imm4_1 = io.instruction(11, 8)
        val imm11 = io.instruction(7)
        val imm_b = Cat(imm12, imm11, imm10_5, imm4_1, 0.U(1.W))
        val imm_b_sext = imm_b.asSInt.@@resize(32)

        // Decode branch comparison function
        switch(funct3) { // Not used in Decode, but decoded here for completeness. Only used in Execute Stage for branch comparison
          is("b000".U) { /* BEQ */ }
          is("b001".U) { /* BNE */ }
          is("b100".U) { /* BLT */ io.controlSignals.isSigned := true.B }
          is("b101".U) { /* BGE */ io.controlSignals.isSigned := true.B }
          is("b110".U) { /* BLTU */ }
          is("b111".U) { /* BGEU */ }
        }
        io.controlSignals.branchOp := funct3 // Pass funct3 for branch type
        io.controlSignals.branch := true.B // This is a branch instruction
        io.immediate := imm_b_sext // Sign-extended immediate
      }
      is("b1101111".U) { // JAL
        val imm20 = io.instruction(31)
        val imm10_1 = io.instruction(30, 21)
        val imm11 = io.instruction(20)
        val imm19_12 = io.instruction(19, 12)
        val imm_j = Cat(imm20, imm19_12, imm11, imm10_1, 0.U(1.W))
        val imm_j_sext = Cat(Fill(11, imm_j(20)), imm_j)
        io.controlSignals.jump := 1.U // JAL
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_j_sext // Sign-extended immediate
      }
      is("b1100111".U) { // JALR
        io.controlSignals.jump := 2.U // JALR
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate

      }
      is("b0110011".U) { // R-type Instructions (ALU + Multiply and Divide)
        io.controlSignals.imm_flag := false.B // Use register
        io.controlSignals.regWrite := true.B // Write to register        
        switch(funct3) {
          // funct3 = 000: ADD, SUB, MUL
          is("b000".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 10.U // MUL
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 2.U  // SUB
            } .otherwise {
              io.controlSignals.aluOp := 1.U  // ADD
            }
          }
          
          // funct3 = 001: SLL, MULH
          is("b001".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.isSigned := true.B // Signed multiply high
              io.controlSignals.aluOp := 11.U // MULH
            } .otherwise {
              io.controlSignals.aluOp := 6.U  // SLL
            }
          }
          
          // funct3 = 010: SLT, MULHSU
          is("b010".U) {
            when(funct7 === "b0000001".U) {  
              io.controlSignals.aluOp := 12.U // MULHSU
            } .otherwise {
              io.controlSignals.isSigned := true.B // Signed comparison
              io.controlSignals.aluOp := 9.U  // SLT
            }
          }
          
          // funct3 = 011: SLTU, MULHU
          is("b011".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 11.U // MULHU
            } .otherwise {
              
              io.controlSignals.aluOp := 9.U // SLTU
            }
          }
          
          // funct3 = 100: XOR, DIV
          is("b100".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.isSigned := true.B // Signed division
              io.controlSignals.aluOp := 13.U // DIV
            } .otherwise {
              io.controlSignals.aluOp := 5.U  // XOR
            }
          }
          
          // funct3 = 101: SRL, SRA, DIVU
          is("b101".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 13.U // DIVU
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 8.U  // SRA
            } .otherwise {
              io.controlSignals.aluOp := 7.U  // SRL
            }
          }
          
          // funct3 = 110: OR, REM
          is("b110".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.isSigned := true.B // Signed remainder
              io.controlSignals.aluOp := 14.U // REM
            } .otherwise {
              io.controlSignals.aluOp := 4.U  // OR
            }
          }
          
          // funct3 = 111: AND, REMU
          is("b111".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 14.U // REMU
            } .otherwise {
              io.controlSignals.aluOp := 3.U  // AND
            }
          }
        }
      }
      is("b0110111".U) { // LUI
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_upper // Upper immediate
        io.controlSignals.aluOp := 1.U // ADD (0 + immediate)
        io.controlSignals.imm_flag := true.B
        io.controlSignals.lui := 1.U
      }
      is("b0010111".U) { // AUIPC
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_upper // Upper immediate
        io.controlSignals.imm_flag := true.B
        io.controlSignals.aluOp := 1.U // ADD (pc + immediate)
        io.controlSignals.lui := 2.U
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: 