package integer

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class DataMem(depthBytes: Int) extends Module {
  require(depthBytes % 4 == 0, "DataMem depth must be a multiple of 4 bytes (word-aligned).") // 32-bit words 
  private val depthWords = depthBytes / 4

  val io = IO(new Bundle {
    val addr     = Input(UInt(32.W))        // Byte address
    val wrData   = Input(UInt(32.W))        // 32-bit write data (already lane-aligned by MemoryStage for SB/SH)
    val memRead  = Input(Bool())
    val memWrite = Input(Bool())
    val mask     = Input(Vec(4, Bool()))    // byte enables [0..3]
    val rdData   = Output(UInt(32.W))       // 32-bit read data (1-cycle latency)
  })

  // Word-addressed memory: each entry is 4 bytes
  val mem = SyncReadMem(depthWords, Vec(4, UInt(8.W)))

  // Convert byte address to word address
  val wordAddr = io.addr(31, 2)

  // Read: synchronous (data valid next cycle)
  val rdVec = mem.read(wordAddr, io.memRead)
  val rdEn_d = RegNext(io.memRead, init = false.B)

  // Return 0 when not doing a read (avoids X propagation into WB/debug)
  io.rdData := Mux(rdEn_d, rdVec.asUInt, 0.U(32.W))

  // Write: masked per-byte write
  val wrVec = Wire(Vec(4, UInt(8.W)))
  for (i <- 0 until 4) {
    wrVec(i) := io.wrData((i * 8) + 7, i * 8)
  }

  when(io.memWrite) {
    mem.write(wordAddr, wrVec, io.mask)
  }

  // Optional init if you want it later:
  // loadMemoryFromFile(mem, "mem_init.hex")
}
