package core

import chisel3._
import chisel3.util._

class BTB(conf: CoreConfig) extends Module {

  val entries = conf.BTB_entries
  
  require(isPow2(entries))
  private val idxBits = log2Ceil(entries)

  val io = IO(new Bundle {
    // Query (Fetch)
    val q_pc     = Input(UInt(conf.xlen.W))
    val q_hit    = Output(Bool())
    val q_target = Output(UInt(conf.xlen.W))

    // Update (Execute)
    val u_valid  = Input(Bool())
    val u_pc     = Input(UInt(conf.xlen.W))
    val u_target = Input(UInt(conf.xlen.W))
  })

  def idx(pc: UInt): UInt = pc(2 + idxBits - 1, 2) // word aligned
  def tag(pc: UInt): UInt = pc(conf.xlen - 1, 2 + idxBits)

  val valids  = RegInit(VecInit(Seq.fill(entries)(false.B)))
  val tags    = Reg(Vec(entries, UInt((conf.xlen - 2 - idxBits).W)))
  val targets = Reg(Vec(entries, UInt(conf.xlen.W)))

  // Query
  val qi = idx(io.q_pc)
  val qt = tag(io.q_pc)
  io.q_hit    := valids(qi) && (tags(qi) === qt)
  io.q_target := targets(qi)

  // Update
  when(io.u_valid) {
    val ui = idx(io.u_pc)
    tags(ui)    := tag(io.u_pc)
    targets(ui) := io.u_target
    valids(ui)  := true.B
  }
}
