error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/Array.
	 -chisel3/Array#
	 -chisel3/Array().
	 -Array.
	 -Array#
	 -Array().
	 -scala/Predef.Array.
	 -scala/Predef.Array#
	 -scala/Predef.Array().
offset: 358
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
text:
```scala
package core

import java.nio.file.{Files, Paths}
import chisel3._
import circt.stage.ChiselStage
import integer.RiscVPipeline

object SVGen extends App {
  // Remove existing file if it exists
  Files.deleteIfExists(Paths.get("generated", "RiscVPipeline.sv"))

  // Generate SystemVerilog file
  val sv = ChiselStage.emitSystemVerilog(new RiscVPipeline, Arr@@ay("--target-dir", "generated"))
  println("Generating SystemVerilog file 'RiscVPipeline.sv' in generated/ directory.... ")
  Files.createDirectories(Paths.get("generated"))
  Files.write(Paths.get("generated/RiscVPipeline.sv"), sv.getBytes)

  if (Files.exists(Paths.get("generated/RiscVPipeline.sv"))){
    println("SUCCESS: SystemVerilog file generated!")
  } else {
    println("ERROR: SystemVerilog file not generated!")
  }

}


```


#### Short summary: 

empty definition using pc, found symbol in pc: 