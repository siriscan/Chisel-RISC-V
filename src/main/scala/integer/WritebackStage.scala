package integer

import chisel3._
import chisel3.util._

class WritebackStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Memory Stage / MEM-WB
    val ctrl      = Input(new ControlSignals)
    val memData   = Input(UInt(conf.xlen.W))  // Data loaded from memory
    val aluResult = Input(UInt(conf.xlen.W)) 
    val rdIn      = Input(UInt(5.W))        // Destination Register

    // Outputs to Decode Stage (Register File)
    val wbData    = Output(UInt(conf.xlen.W)) // Data to write back
    val wbAddr    = Output(UInt(5.W)) // Destination Register Address
    val wbEnable  = Output(Bool())      // Write Enable
  })

  val addrLSB = io.aluResult(1, 0)  // byte offset
  val byteShift = addrLSB << 3      // 0, 8, 16, 24
  val halfShift = addrLSB(1) << 4   // 0 or 16

  val byte = (io.memData >> byteShift)(7, 0)
  val half = (io.memData >> halfShift)(15, 0)
  val word = io.memData

  val loadData = Wire(UInt(conf.xlen.W))
  loadData := word // default 

  // Decode load type using funct3 (memF3)
  // Loads: 000 LB, 001 LH, 010 LW, 100 LBU, 101 LHU
  switch(io.ctrl.memF3) {
    is("b000".U) { // LB (sign-extend)
      loadData := Cat(Fill(conf.xlen - 8, byte(7)), byte)
    }
    is("b001".U) { // LH (sign-extend)
      loadData := Cat(Fill(conf.xlen - 16, half(15)), half)
    }
    is("b010".U) { // LW
      loadData := word
    }
    is("b100".U) { // LBU (zero-extend)
      loadData := Cat(0.U((conf.xlen - 8).W), byte)
    }
    is("b101".U) { // LHU (zero-extend)
      loadData := Cat(0.U((conf.xlen - 16).W), half)
    }
  }

  // Select writeback source
  io.wbData := Mux(io.ctrl.memToReg, loadData, io.aluResult)

  io.wbAddr   := io.rdIn
  io.wbEnable := io.ctrl.regWrite
}
