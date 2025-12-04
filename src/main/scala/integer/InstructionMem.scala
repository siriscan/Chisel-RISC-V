package integer

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile // Essential for file loading

class InstructionMem(depth: Int, initFile: String = "") extends Module { // 16KB by default
  val io = IO(new Bundle {
    val address = Input(UInt(32.W))    // Program Counter (Byte Address)
    val instruction = Output(UInt(32.W))   // 32-bit Instruction
  })

  // 1. Create the Memory
  // 32-bit wide memory (standard for RISC-V instructions)
  val mem = SyncReadMem(depth, UInt(32.W))

  // 2. Address Calculation
  // Convert byte address to word address by dropping the 2 LSBs
  val wordAddr = io.address(31, 2)
  


  // 3. Read Logic
  // Provide the instruction corresponding to the address.
  // Note: This read is synchronous (1 cycle latency).
  io.instruction := mem.read(wordAddr)

  // 4. Initialization
  // If a file path is provided, generate the Verilog to load it.
  // The file should be a standard ASCII Hex file.
  if (initFile.trim().nonEmpty) {
    loadMemoryFromFile(mem, initFile)
  }
}