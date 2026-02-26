package integer

import chisel3._
import chisel3.util._
import core._

class WritebackStage(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Memory Stage / MEM-WB
    val ctrl      = Input(new ControlSignals)
    val memData   = Input(UInt(conf.xlen.W))  // Data loaded from memory
    val aluResult = Input(UInt(conf.xlen.W)) 
    val rdIn      = Input(UInt(5.W))        // Destination Register

    // Inputs from Execute Stage / EX-MEM
    val pcIn = Input(UInt(conf.xlen.W)) // PC for JAL/JALR

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

  val loadData = Wire(UInt(conf.xlen.W)) // Data after load type decoding
  val outputData = Wire(UInt(conf.xlen.W)) // Data to write back (after memToReg mux)
  loadData := word // default 

  // Decode load type using funct3 (memOp)
  // Loads: 000 LB, 001 LH, 010 LW, 100 LBU, 101 LHU
  switch(io.ctrl.memOp) {
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

  // Select between ALU result and Memory load data
  outputData := Mux(io.ctrl.memToReg, loadData, io.aluResult) // If memToReg is true, loadData; else ALU result

  // Jump and Link Handling
  val linkData = Wire(UInt(conf.xlen.W)) // Data for JAL/JALR
  when (io.ctrl.jump =/= 0.U) { // JAL or JALR
    linkData := io.pcIn + 4.U // Return address is PC + 4
  } .otherwise {
    linkData := io.pcIn // Default 
  }

  // Select writeback source
  io.wbData := Mux(io.ctrl.jump =/= 0.U, linkData, outputData) // If JAL or JALR, write linkData; else outputData
  io.wbAddr   := io.rdIn
  io.wbEnable := io.ctrl.regWrite
}
