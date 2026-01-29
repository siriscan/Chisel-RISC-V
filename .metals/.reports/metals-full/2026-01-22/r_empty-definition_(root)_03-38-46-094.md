error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/RiscVPipeline.
	 -chisel3/RiscVPipeline#
	 -chisel3/RiscVPipeline().
	 -core/RiscVPipeline.
	 -core/RiscVPipeline#
	 -core/RiscVPipeline().
	 -RiscVPipeline.
	 -RiscVPipeline#
	 -RiscVPipeline().
	 -scala/Predef.RiscVPipeline.
	 -scala/Predef.RiscVPipeline#
	 -scala/Predef.RiscVPipeline().
offset: 115
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
text:
```scala
package core

import java.nio.file.{Files, Paths}
import chisel3._
import circt.stage.ChiselStage
import core.RiscV@@Pipeline

object SVGen extends App {
  val sv = ChiselStage.emitSystemVerilog(new RiscVPipeline)
  println("Generating SystemVerilog file 'RiscVPipeline.sv' in generated/ directory.... ")
  Files.createDirectories(Paths.get("generated"))
  Files.write(Paths.get("generated/RiscVPipeline.sv"), sv.getBytes)
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: 