package core

import java.nio.file.{Files, Paths}
import chisel3._
import circt.stage.ChiselStage
import integer.RiscVPipeline

object SVGen extends App {
  val sv = ChiselStage.emitSystemVerilog(new RiscVPipeline)
  println("Generating SystemVerilog file 'RiscVPipeline.sv' in generated/ directory.... ")
  Files.createDirectories(Paths.get("generated"))
  Files.write(Paths.get("generated/RiscVPipeline.sv"), sv.getBytes)
}

