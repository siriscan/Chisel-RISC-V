package core

import java.nio.file.{Files, Paths}
import chisel3._
import circt.stage.ChiselStage
import integer.RiscVPipeline

object SVGen extends App {
  Files.delete(Paths.get("generated/RiscVPipeline.sv"))

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

