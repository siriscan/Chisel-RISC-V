error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/AtomicUnit.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/AtomicUnit.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/isLRReg.
	 -chisel3/isLRReg#
	 -chisel3/isLRReg().
	 -chisel3/util/isLRReg.
	 -chisel3/util/isLRReg#
	 -chisel3/util/isLRReg().
	 -chisel3/experimental/isLRReg.
	 -chisel3/experimental/isLRReg#
	 -chisel3/experimental/isLRReg().
	 -core/isLRReg.
	 -core/isLRReg#
	 -core/isLRReg().
	 -isLRReg.
	 -isLRReg#
	 -isLRReg().
	 -scala/Predef.isLRReg.
	 -scala/Predef.isLRReg#
	 -scala/Predef.isLRReg().
offset: 2550
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/AtomicUnit.scala
text:
```scala
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
    // Inputs from Execute Stage    
    val atomic = Input(Bool()) // Whether the current instruction is atomic
    val atomicOp = Input(UInt(5.W)) // AMO operation type (funct5)
    val rlFlag = Input(Bool()) // Release flag
    val aqFlag = Input(Bool()) // Acquire flag
    val memAddress = Input(UInt(conf.xlen.W)) // Memory address for the atomic operation
    val memWriteData = Input(UInt(conf.xlen.W)) // Data to write for store operations (e.g., AMO.SW)
    val rd = Input(UInt(5.W)) // Destination register for the result of the atomic operation
    val rs2Data = Input(UInt(conf.xlen.W)) // Original value from rs2 (for AMO operations that use it)

    // Data Memory Interface
    val memDataIn = Input(UInt(conf.xlen.W)) // Data read from memory (DataMem.rdData)
    val memReadEnable = Output(Bool()) // Whether to perform a memory read
    val memReadData = Output(UInt(conf.xlen.W)) // Data read from memory (for AMO read phase)
    val memWriteEnable = Output(Bool()) // Whether to perform a memory write
    val memWriteDataOut = Output(UInt(conf.xlen.W)) // Data to write to memory (for store operations)
    val memAddressOut = Output(UInt(conf.xlen.W)) // Address to access in memory
    val memMask = Output(Vec(4, Bool())) // Byte mask for memory operations

    // Outputs to Writeback Stage
    val rdOut = Output(UInt(5.W)) // Destination register for writeback
    val resultOut = Output(UInt(conf.xlen.W)) // Result of the atomic operation to write back to the register file
    val validOut = Output(Bool()) // Whether the output is valid (to be used for writeback)
    val stall = Output(Bool()) // Signal to stall the pipeline during the atomic operation
  })

    // Default
    io.memReadEnable := false.B
    io.memWriteEnable := false.B
    io.memReadData := 0.U
    io.memWriteDataOut := 0.U
    io.memAddressOut := 0.U
    io.memMask := VecInit(Seq(false.B, false.B, false.B, false.B))
    io.rdOut := io.rd
    io.resultOut := 0.U
    io.validOut := false.B
    io.stall := false.B

    // State machine for handling atomic operations
    val state = RegInit(AtomicState.Idle)
    val addrReg = Reg(UInt(conf.xlen.W))
  val rs2Reg  = Reg(UInt(conf.xlen.W))
  val rdReg   = Reg(UInt(5.W))
  val funct5Reg = Reg(UInt(5.W))
  val isLRReg@@ = Reg(Bool())
  val isSCReg = Reg(Bool())

  val oldReg = Reg(UInt(32.W))
  val newReg = Reg(UInt(32.W))

  // Reservation for LR/SC (single-core)
  val resValid = RegInit(false.B)
  val resAddr  = Reg(UInt(conf.xlen.W))

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 