error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Input.
	 -Input#
	 -Input().
	 -scala/Predef.Input.
	 -scala/Predef.Input#
	 -scala/Predef.Input().
offset: 312
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/MemoryStage.scala
text:
```scala
package integer

class Memory(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute
    val ctrl      = Input(new ControlSignals)
    val aluResult = Input(UInt(conf.xlen.W)) // Address
    val rs2Data   = Input(UInt(conf.xlen.W)) // Store Data
    val rdIn      = Inpu@@t(UInt(5.W))

    // Outputs to Writeback
    val memData   = Output(UInt(conf.xlen.W)) // Read Data
    val aluOut    = Output(UInt(conf.xlen.W)) // Pass-through ALU result
    val rdOut     = Output(UInt(5.W))
    val ctrlOut   = Output(new ControlSignals)
  })

  // Instantiate Data Memory (from previous steps)
  val dmem = Module(new DataMemory(16384)) // 16KB
  dmem.io.addr     := io.aluResult
  dmem.io.wrData   := io.rs2Data
  dmem.io.memRead  := io.ctrl.memRead
  dmem.io.memWrite := io.ctrl.memWrite
  dmem.io.mask     := VecInit(Seq.fill(4)(true.B)) // Default word access

  // Outputs
  io.memData := dmem.io.rdData
  io.aluOut  := io.aluResult
  io.rdOut   := io.rdIn
  io.ctrlOut := io.ctrl
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 