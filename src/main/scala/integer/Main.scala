package integer
import chisel3._


class Main {
  def main(args: Array[String]): Unit = {
    println("This is the main class for the RISC-V Pipeline. Use RISCVPipelineGenerator to generate Verilog.")
  }
}

object RISCVPipelineGenerator extends App {
  println("Generating the Risc-V pipeline Verilog code...")
  emitVerilog(new RiscVPipeline(), Array("--target-dir", "generated"))
}

/* Making hex file:
  - First line is the starting address (e.g., 00000000), 
  which should match the startPC in CoreConfig.
  - Since FetchStage starts at nextPc = startPC, the first line should be
  NOP (0x00000013).



 */

// Note: Need to make a seperate App object to generate Verilog entirely

// So far, we have implemented a basic 5-stage RISC-V pipeline with hazard detection and forwarding.
// It supports basic integer instructions including arithmetic, load/store, and branches.
// It has multiply/divide support in the Execute stage.

/* Todo: Add atomic instructions and pipeline support
 - Add single-precision floating point support
 - Add half-precision floating point support
 - Add CSR instructions and pipeline support (Not needed for this project)
 - Add vector instructions and pipeline support
 - Add cache between Memory stage and Data Memory

 * Note: For synthesis, remove the memory initialization from InstructionMem.scala
 * Also, make sure that the memory size and other parameters match FPGA constraints.
 */