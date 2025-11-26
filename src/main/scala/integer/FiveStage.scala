
import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


class FiveStage extends Module {
  val io = IO(new Bundle {
    


  })


}

/**
 * Generate Verilog sources and save it in file FiveStage.v
 */
object FiveStage extends App {
  ChiselStage.emitSystemVerilogFile(
    new FiveStage,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}
