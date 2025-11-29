error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/nop.
	 -chisel3/nop#
	 -chisel3/nop().
	 -chisel3/util/nop.
	 -chisel3/util/nop#
	 -chisel3/util/nop().
	 -nop.
	 -nop#
	 -nop().
	 -scala/Predef.nop.
	 -scala/Predef.nop#
	 -scala/Predef.nop().
offset: 412
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
val @@nop = 0.U



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