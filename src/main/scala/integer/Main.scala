package integer
import chisel3._

class Main {
  
}
object RISCV extends App {
  println("Generating the Risc-V pipeline verilog")
  emitVerilog(new RiscVPipeline(), Array("--target-dir", "generated"))
}

/* Making hex file:
  - First line is the starting address (e.g., 00000000), 
  which should match the startPC in CoreConfig.
  - Since FetchStage starts at nextPc = startPC, the first line should be
  NOP (0x00000013).

 */