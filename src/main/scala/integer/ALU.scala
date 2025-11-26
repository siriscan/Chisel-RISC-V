
import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


class ALU extends Module {
  val io = IO(new Bundle {
    val A        = Input(UInt(32.W))
    val B        = Input(UInt(32.W))
    val function = Input(Bool())
    val C     = Output(UInt(32.W))
  })


}

/**
 * Generate Verilog sources and save it in file GCD.v
 */
object ALU extends App {
  ChiselStage.emitSystemVerilogFile(
    new ALU,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}
