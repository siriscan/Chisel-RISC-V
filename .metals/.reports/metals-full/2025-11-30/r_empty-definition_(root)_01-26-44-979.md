error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/Main.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/Main.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/ChiselStage.
	 -chisel3/ChiselStage#
	 -chisel3/ChiselStage().
	 -chisel3/stage/ChiselStage.
	 -chisel3/stage/ChiselStage#
	 -chisel3/stage/ChiselStage().
	 -chisel3/util/ChiselStage.
	 -chisel3/util/ChiselStage#
	 -chisel3/util/ChiselStage().
	 -ChiselStage.
	 -ChiselStage#
	 -ChiselStage().
	 -scala/Predef.ChiselStage.
	 -scala/Predef.ChiselStage#
	 -scala/Predef.ChiselStage().
offset: 54
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/Main.scala
text:
```scala
package integer
import chisel3._
import circt.stage.Ch@@iselStage
import chisel3.util._

object Main extends App {
  // 1. Define the build directory (e.g., "generated")
  val buildArgs = Array("--target-dir", "generated")

  // 2. Call emitVerilog on your Top Level module
  // This generates RiscVPipeline.v and supporting files
  emitVerilog(new RiscVPipeline(), buildArgs)
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 