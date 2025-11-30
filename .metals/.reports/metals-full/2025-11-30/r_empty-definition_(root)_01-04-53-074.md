error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DataMem.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DataMem.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Bundle.
	 -Bundle#
	 -Bundle().
	 -scala/Predef.Bundle.
	 -scala/Predef.Bundle#
	 -scala/Predef.Bundle().
offset: 139
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DataMem.scala
text:
```scala
package integer

class InstructionMem(depth: Int = 16384, initFile: String = "") extends Module { // 16KB by default
  val io = IO(new B@@undle {
    val address = Input(UInt(32.W))    // Program Counter (Byte Address)
    val instruction = Output(UInt(32.W))   // 32-bit Instruction
  })
```


#### Short summary: 

empty definition using pc, found symbol in pc: 