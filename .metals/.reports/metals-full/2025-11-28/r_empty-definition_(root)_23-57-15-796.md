error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala:integer/ALUConsts_Muktiply.
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol integer/ALUConsts_Muktiply.
empty definition using fallback
non-local guesses:

offset: 631
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala
text:
```scala
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

//Constant definitions for operation codes
object ALUConsts_Interger {
    val nop = 0.U
    val add = 1.U
    val sub = 2.U
    val and = 3.U
    val or  = 4.U
    val xor = 5.U
    val sll = 6.U
    val srl = 7.U
    val sra = 8.U
    
}

object ALUConsts_Muktiply@@ {
    val mul  = 1.U
    val mulh = 2.U
    val mulhsu = 3.U
    val mulhu = 4.U
    
}


class ALU extends Module {
  val io = IO(new Bundle {
    val A        = Input(UInt(32.W))
    val B        = Input(UInt(32.W))
    val instruction = Input(UInt(32.W))
    val C     = Output(UInt(32.W))
  })



}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 