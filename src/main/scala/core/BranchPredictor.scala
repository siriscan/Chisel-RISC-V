package core

import chisel3._
import chisel3.util._
import chisel3.experimental._

object BranchState extends ChiselEnum {
  val SNT, WNT, WT, ST = Value  // strongly/weakly not taken, weakly/strongly taken
}

object BranchPredFSM {
    def predictTaken(s: BranchState.Type): Bool =
    (s === BranchState.WT) || (s === BranchState.ST)

    def nextStateSwitch(s: BranchState.Type, taken: Bool): BranchState.Type = {
        val ns = WireDefault(s)
        when(taken) {
            switch(s) { // taken -> move toward Strongly Taken
            is(BranchState.SNT) { ns := BranchState.WNT }
            is(BranchState.WNT) { ns := BranchState.WT  }
            is(BranchState.WT)  { ns := BranchState.ST  }
            is(BranchState.ST)  { ns := BranchState.ST  }
            }
        }.otherwise { // not taken -> move toward Strongly Not Taken
            switch(s) {
            is(BranchState.ST)  { ns := BranchState.WT  }
            is(BranchState.WT)  { ns := BranchState.WNT }
            is(BranchState.WNT) { ns := BranchState.SNT }
            is(BranchState.SNT) { ns := BranchState.SNT }
            }
        }
        ns
    }

}

class BHT(conf: CoreConfig) extends Module{
    val entries = conf.BHT_entries
    
    require(isPow2(entries))
    private val idxBits = log2Ceil(entries)
    val io = IO(new Bundle {

    // Query (Fetch)
    val q_pc        = Input(UInt(conf.xlen.W))
    val q_taken     = Output(Bool())
    val q_state     = Output(BranchState.Type()) // optional for debug

    // Update (Execute)
    val u_valid     = Input(Bool())            // only true for conditional branches
    val u_pc        = Input(UInt(conf.xlen.W))
    val u_taken     = Input(Bool())
  })
    def idx(pc: UInt): UInt = pc(2 + idxBits - 1, 2) // word aligned index

    val table = RegInit(VecInit(Seq.fill(entries)(BranchState.WNT)))

    val qIdx = idx(io.q_pc)
    val qSt  = table(qIdx)

    io.q_state := qSt
    io.q_taken := BranchPredFSM.predictTaken(qSt)

    when(io.u_valid) {
        val uIdx = idx(io.u_pc)
        table(uIdx) := BranchPredFSM.nextStateSwitch(table(uIdx), io.u_taken)
        
    }

}