package integer

import chisel3._
import chisel3.util._
import chisel3.experimental._
import core._

object AtomicState extends ChiselEnum {
  val Idle, ReadReq, ReadResp, WriteReq, Done = Value
}

class AtomicUnit(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Request from EX/MEM (must remain stable while io.stall is true)
    val atomic    = Input(Bool())
    val memAddress= Input(UInt(conf.xlen.W))   // byte address
    val rs2Data   = Input(UInt(conf.xlen.W))   // rs2 value
    val rd        = Input(UInt(5.W))
    val atomicOp  = Input(UInt(5.W))           // funct5 = instr[31:27]
    val isLR      = Input(Bool())
    val isSC      = Input(Bool())

    // DataMem interface (MemoryStage muxes these to DataMem)
    val memReadEnable   = Output(Bool())
    val memWriteEnable  = Output(Bool())
    val memAddressOut   = Output(UInt(conf.xlen.W))
    val memWriteDataOut = Output(UInt(32.W))
    val memMask         = Output(Vec(4, Bool()))
    val memDataIn       = Input(UInt(32.W))    // DataMem.rdData (valid 1 cycle after memReadEnable)
    val memReadData     = Output(UInt(32.W))   // optional debug

    // Outputs to WB
    val rdOut     = Output(UInt(5.W))
    val resultOut = Output(UInt(conf.xlen.W))
    val validOut  = Output(Bool())
    val stall     = Output(Bool())
  })

  // Defaults
  io.memReadEnable   := false.B
  io.memWriteEnable  := false.B
  io.memAddressOut   := 0.U
  io.memWriteDataOut := 0.U
  io.memMask         := VecInit(Seq(false.B, false.B, false.B, false.B))
  io.memReadData     := 0.U

  io.rdOut     := io.rd
  io.resultOut := 0.U
  io.validOut  := false.B
  io.stall     := false.B

  // Internal registers for FSM
  val state     = RegInit(AtomicState.Idle)
  val addrReg   = Reg(UInt(conf.xlen.W))
  val rs2Reg    = Reg(UInt(32.W))
  val rdReg     = Reg(UInt(5.W))
  val funct5Reg = Reg(UInt(5.W))
  val isLRReg   = Reg(Bool())
  val isSCReg   = Reg(Bool())

  val oldReg    = Reg(UInt(32.W))
  val newReg    = Reg(UInt(32.W))

  // SC success latched at decision time
  val scSuccess = RegInit(false.B)

  // Reservation registers (single-core approximation)
  val resValid = RegInit(false.B)
  val resAddr  = Reg(UInt(conf.xlen.W))

  // Word address alignment is assumed (you can add a trap later)
  def amoOps(old_val: UInt, rs2_val: UInt, funct5: UInt): UInt = {
        // Define the AMO operations based on funct5
        MuxLookup(funct5, old_val)(Seq(
            "b00000".U -> (old_val + rs2_val), // AMOADD.W (New value in rs1 = old value in rs1 + rs2 value)
            "b00001".U -> rs2_val, // AMOSWAP.W (New value in rs1 = rs2 value)
            "b01100".U -> (old_val & rs2_val), // AMOAND.W (New value in rs1 = old value in rs1 & rs2 value)
            "b01000".U -> (old_val | rs2_val), // AMOOR.W (New value in rs1 = old value in rs1 | rs2 value)
            "b00100".U -> (old_val ^ rs2_val), // AMOXOR.W (New value in rs1 = old value in rs1 ^ rs2 value)
            "b10000".U -> Mux(old_val.asSInt < rs2_val.asSInt, old_val, rs2_val), // AMOMIN.W (New value in rs1 = minimum of old value in rs1 and rs2 value, signed)
            "b10100".U -> Mux(old_val.asSInt > rs2_val.asSInt, old_val, rs2_val), // AMOMAX.W (New value in rs1 = maximum of old value in rs1 and rs2 value, signed)
            "b11000".U -> Mux(old_val < rs2_val, old_val, rs2_val),                // AMOMINU.W (New value in rs1 = minimum of old value in rs1 and rs2 value, unsigned)
            "b11100".U -> Mux(old_val > rs2_val, old_val, rs2_val),                // AMOMAXU.W (New value in rs1 = maximum of old value in rs1 and rs2 value, unsigned)
            // For LR/SC, the funct5 can be treated as a NOP since the operation is determined by the isLR and isSC flags
        ))
    }

  // FSM for Atomic Operations
  switch(state) {
    is(AtomicState.Idle) {
      when(io.atomic) {
        // Latch the request
        addrReg   := io.memAddress
        rs2Reg    := io.rs2Data(31, 0)
        rdReg     := io.rd
        funct5Reg := io.atomicOp
        isLRReg   := io.isLR
        isSCReg   := io.isSC
        scSuccess := false.B
        state := AtomicState.ReadReq
      }
    }

    is(AtomicState.ReadReq) {
      io.stall := true.B
      io.memReadEnable := true.B
      io.memAddressOut := addrReg
      state := AtomicState.ReadResp
    }

    is(AtomicState.ReadResp) {
      io.stall := true.B
      io.memReadData := io.memDataIn

      val oldNow = io.memDataIn
      oldReg := oldNow

      when(isLRReg) {
        // LR.W: set reservation, return old value
        resValid := true.B
        resAddr  := addrReg

        // No memory write
        state := AtomicState.Done
      }.elsewhen(isSCReg) {
        // SC.W: succeed iff reservation matches
        val ok = resValid && (resAddr === addrReg)
        scSuccess := ok

        when(ok) {
          // Perform the store of rs2
          newReg := rs2Reg
          state := AtomicState.WriteReq
        }.otherwise {
          // No store
          state := AtomicState.Done
        }

        // SC clears reservation regardless of success/failure
        resValid := false.B
      }.otherwise {
        // AMO*: compute new, then write it
        val computed = amoOps(oldNow, rs2Reg, funct5Reg)
        newReg := computed

        // Any write to reserved address clears reservation
        when(resValid && (resAddr === addrReg)) {
          resValid := false.B
        }

        state := AtomicState.WriteReq
      }
    }

    is(AtomicState.WriteReq) {
      io.stall := true.B
      io.memWriteEnable := true.B
      io.memAddressOut := addrReg
      io.memWriteDataOut := newReg
      io.memMask := VecInit(Seq(true.B, true.B, true.B, true.B)) // word write
      state := AtomicState.Done
    }

    is(AtomicState.Done) {
      io.stall := true.B
      io.validOut := true.B
      io.rdOut := rdReg

      when(isSCReg) {
        // SC result: 0 success, 1 failure
        io.resultOut := Mux(scSuccess, 0.U, 1.U)
      }.otherwise {
        // LR and AMO return the old value
        io.resultOut := oldReg
      }

      // One-cycle pulse of validOut, then return Idle
      state := AtomicState.Idle
    }
  }
}
    