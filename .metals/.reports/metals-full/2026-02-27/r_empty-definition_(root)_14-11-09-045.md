error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala:core/SVGen.
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol core/SVGen.
empty definition using fallback
non-local guesses:

offset: 140
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/SVGen.scala
text:
```scala
package core

import java.nio.file.{Files, Paths}
import chisel3._
import circt.stage.ChiselStage
import integer.RiscVPipeline

object SVGen@@ extends App {
  import java.nio.file.{Files, Paths}

Files.deleteIfExists(Paths.get("generated", "RiscVPipeline.sv"))
  val sv = ChiselStage.emitSystemVerilog(new RiscVPipeline)
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