package integer

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

/**
  * ALU Module supporting I-type and R-type instructions:
    * - I-type: ADDI, ANDI, ORI, XORI, SLLI, SRLI, SRAI
    * - R-type: ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, MUL
  */

/*
    Note to self:
      Remove unsigned constants
      Use isSigned Control Signal for signed/unsigned operations instead of separate opcodes

  */
// Constants operation codes 
object ALUConsts_Interger { // Integer ALU operation codes + Multiply
    val nop = 0.U
    val add = 1.U
    val sub = 2.U
    val and = 3.U
    val or  = 4.U
    val xor = 5.U
    val sll = 6.U
    val srl = 7.U
    val sra = 8.U
    val slt = 9.U // Set Less Than and Set Less Than Unsigned

    // Multiply operations
    val mul  = 10.U 
    val mulh = 11.U // Signed multiply high and unsigned multiply high
    val mulhsu = 12.U

    // Divide operations
    val div  = 13.U // Signed divide and unsigned divide
    val rem  = 14.U // Signed remainder and unsigned remainder


    // Add more operations as needed


}


class ALU(width : Int) extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(width.W))
    val B = Input(UInt(width.W)) 
    val opcode = Input(UInt(4.W)) // ALU operation code
    val isSigned = Input(Bool()) // True for signed operations, False for unsigned

    val C = Output(UInt(width.W))
  })
    io.C := 0.U // Default output
    val B = WireDefault(io.B) // Default B input



switch(io.opcode) {
    is(ALUConsts_Interger.nop) {
      io.C := 0.U
    }

    is(ALUConsts_Interger.add) {
      io.C := io.A + B
    }
    is(ALUConsts_Interger.sub) {
      io.C := io.A - B
    }
    is(ALUConsts_Interger.and) {
      io.C := io.A & B
    }
    is(ALUConsts_Interger.or) {
      io.C := io.A | B
    }
    is(ALUConsts_Interger.xor) {
      io.C := io.A ^ B
    }
    is(ALUConsts_Interger.sll) {
      io.C := io.A << B(4,0)
    }
    is(ALUConsts_Interger.srl) {
      io.C := io.A >> B(4,0)
    }
    is(ALUConsts_Interger.sra) {
      io.C := (io.A.asSInt >> B(4,0)).asUInt
    } 


    is(ALUConsts_Interger.slt) {
      when(io.isSigned) {
        when(io.A.asSInt < B.asSInt) {
          io.C := 1.U
        } .otherwise {
          io.C := 0.U
        }
      } .otherwise {
        when(io.A < B) {
          io.C := 1.U
        } .otherwise {
          io.C := 0.U
        }
      }
    }

    // Multiply and Divide operations
    is(ALUConsts_Interger.mul) {
      io.C := (io.A * B)(31,0)
    }
    is(ALUConsts_Interger.mulh) {
      when(io.isSigned) {
        io.C := (io.A.asSInt * B.asSInt)(63,32).asUInt // mulh for signed * signed
      } .otherwise {
        io.C := (io.A.asUInt * B.asUInt)(63,32).asUInt // mulhu for unsigned * unsigned
      }
    }
    is(ALUConsts_Interger.mulhsu) {
      io.C := (io.A.asSInt * B.asUInt)(63,32).asUInt
    }
    is(ALUConsts_Interger.div) {
      when(io.isSigned) {
        io.C := (io.A.asSInt / B.asSInt).asUInt // div for signed
      } .otherwise {
        io.C := io.A / B // divu for unsigned
      }
    }
    is(ALUConsts_Interger.rem) {
      when(io.isSigned) {
        io.C := (io.A.asSInt % B.asSInt).asUInt // rem for signed
      } .otherwise {
        io.C := io.A % B // remu for unsigned
      }
    }
}

}

/**
 * Generate Verilog sources and save it in file ALU.sv
 * 
 * object ALU extends App {
  ChiselStage.emitSystemVerilogFile(
    new ALU(width = 32), // 32-bit ALU
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}
 */
