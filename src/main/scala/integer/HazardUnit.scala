package integer
import chisel3._
import chisel3.util._

class HazardUnit extends Module {
  val io = IO(new Bundle {
    val rs1_id = Input(UInt(5.W)) // Source Register 1 in Decode Stage
    val rs2_id = Input(UInt(5.W)) // Source Register 2 in Decode Stage
    val rd_ex  = Input(UInt(5.W)) // Destination Register in Execute Stage
    val memRead_ex = Input(Bool())  // True if instruction in Execute Stage is a Load

    val stall = Output(Bool()) // 1 = Stall
  })

  // Stall if the instruction in Execute is a Load, and its destination
  // matches either source register in the Decode stage.
  io.stall := io.memRead_ex && (io.rd_ex === io.rs1_id || io.rd_ex === io.rs2_id)
}