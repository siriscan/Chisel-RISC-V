error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/C.
	 -chisel3/C#
	 -chisel3/C().
	 -C.
	 -C#
	 -C().
	 -scala/Predef.C.
	 -scala/Predef.C#
	 -scala/Predef.C().
offset: 207
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala
text:
```scala
import chisel3._

class RegisterFile(width: Int) extends Module {

  val io = IO(new Bundle {
    val A = Output(UInt(32.W))
    val B = Output(UInt(32.W))
    val opcode = Input(UInt(4.W))
    val C@@ = Input(UInt(32.W))
  })

  
    }


```


#### Short summary: 

empty definition using pc, found symbol in pc: 