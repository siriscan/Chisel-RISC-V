error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala:integer/SVGen.sv.
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/sv.
	 -sv.
	 -scala/Predef.sv.
offset: 386
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
text:
```scala
package integer

import java.nio.file.{Files, Paths}
import chisel3._
import circt.stage.ChiselStage

object SVGen extends App {
  val sv = ChiselStage.emitSystemVerilog(new RiscVPipeline)
  println("Generating SystemVerilog file 'RiscVPipeline.sv' in generated/ directory.... ")
  Files.createDirectories(Paths.get("generated"))
  Files.write(Paths.get("generated/RiscVPipeline.sv"), s@@v.getBytes)
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: 