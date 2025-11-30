error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/WritebackStage.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/WritebackStage.scala
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
offset: 244
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/WritebackStage.scala
text:
```scala
package integer

class Writeback(conf: CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Memory
    val ctrl      = Input(new ControlSignals)
    val memData   = Input(UInt(conf.xlen.W))
    val aluResult = Input@@(UInt(conf.xlen.W))
    val rdIn      = Input(UInt(5.W))

    // Outputs to Decode (Register File)
    val wbData    = Output(UInt(conf.xlen.W))
    val wbAddr    = Output(UInt(5.W))
    val wbEnable  = Output(Bool())
  })

  // Select Result: Memory Data (Load) vs ALU Result (Arithmetic)
  io.wbData := Mux(io.ctrl.memToReg, io.memData, io.aluResult)
  
  io.wbAddr   := io.rdIn
  io.wbEnable := io.ctrl.regWrite
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 