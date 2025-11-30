package integer

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile // For loading hex files

class DataMem(depth: Int) extends Module { // 16KB by default
  val io = IO(new Bundle {
    // Pipeline Signals
    val addr    = Input(UInt(32.W))   // Memory Address
    val wrData  = Input(UInt(32.W))   // Data to Write
    val memRead = Input(Bool())       // Read Enable
    val memWrite= Input(Bool())       // Write Enable
    val mask    = Input(Vec(4, Bool())) // Byte Write Mask (e.g., 1111 for sw, 0001 for sb)
    
    // Output
    val rdData  = Output(UInt(32.W))
  })

  // 1. Create SyncReadMem
  // Defined as a vector of 4 bytes to allow masked writes
  val mem = SyncReadMem(depth, Vec(4, UInt(8.W)))

  // 2. Address Processing
  // SyncReadMem is word-indexed, so we divide the byte address by 4.
  // (Assuming aligned accesses for simplicity)
  val wordAddr = io.addr >> 2

  // 3. Read Logic
  // Reads are synchronous: data appears 1 cycle after address is applied.
  // We read the vector of 4 bytes and concatenate them back into 32 bits.
  val rdVec = mem.read(wordAddr, io.memRead)
  io.rdData := rdVec.asUInt // Concatenates (3) ## (2) ## (1) ## (0) automatically

  // 4. Write Logic
  // We split the 32-bit input data into 4 bytes for the vector
  val wrVec = Wire(Vec(4, UInt(8.W)))
  for (i <- 0 until 4) {
    wrVec(i) := io.wrData(i * 8 + 7, i * 8)
  }

  // Perform the masked write
  when(io.memWrite) {
    mem.write(wordAddr, wrVec, io.mask)
  }

  // 5. Initialization (Optional for simulation/FPGA init)
  // Useful for loading a test program or data
  // loadMemoryFromFile(mem, "mem_init.hex") 
}