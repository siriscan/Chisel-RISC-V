error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala:chisel3/UInt.
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/UInt.
empty definition using fallback
non-local guesses:

offset: 269
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ALU.scala
text:
```scala

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


class ALU extends Module {
  val io = IO(new Bundle {
    val A        = Input(UInt(32.W)
    val B        = Input(@@UInt(16.W))
    val loadingValues = Input(Bool())
    val C     = Output(UInt(32.W))
  })


}

/**
 * Generate Verilog sources and save it in file GCD.v
 */
object ALU extends App {
  ChiselStage.emitSystemVerilogFile(
    new ALU,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 