error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala:local1
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol local1
empty definition using fallback
non-local guesses:

offset: 319
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
    val A = Output(UInt(32.W)) // Read Data 1
    val B@@ = Output(UInt(32.W)) // Read Data 2
    val instruction = Input(UInt(32.W))
    val C = Input(UInt(32.W)) // Data to write back to register file
    val writeEnable = Input(Bool())
    val writeAddress = Input(UInt(5.W))
  })
    val imm = io.instruction(31, 20) // I-type immediate
    val imm_sext = Cat(Fill(20, imm(11)), imm) // sign-extend immediate
    val imm_uext = Cat(Fill(20, 0.U), imm) // zero-extend immediate

    val opcode = io.instruction(6, 0)
    val funct3 = io.instruction(14, 12)
    val funct7 = io.instruction(31, 25)

    val regFile = Module(new RegisterFile(conf.xlen)) // 32-bit register file

    // Default valu


    regFile.io.opcode := MuxLookup(opcode, "b0000".U, Seq(
      "b0010011".U -> "b0001".U, // ADDI
      "b0000011".U -> "b0010".U, // LW
      "b0100011".U -> "b0011".U  // SW
    )) 

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 