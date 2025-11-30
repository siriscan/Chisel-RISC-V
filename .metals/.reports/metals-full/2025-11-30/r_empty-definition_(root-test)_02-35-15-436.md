error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/test/scala/integer/RiscVPipelineTest.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/test/scala/integer/RiscVPipelineTest.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/chiseltest.
	 -chiseltest/chiseltest.
	 -chiseltest.
	 -scala/Predef.chiseltest.
offset: 48
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/test/scala/integer/RiscVPipelineTest.scala
text:
```scala
package integer

import chisel3._
import chiselt@@est._
import org.scalatest.flatspec.AnyFlatSpec

class RiscVPipelineTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RISC-V 5-Stage Pipeline"

  it should "execute a program for a fixed number of cycles" in {
    // 1. Instantiate the DUT (Device Under Test)
    // 1. Instantiate the DUT (Device Under Test)
    // Run the test (omit waveform annotation if WriteVcdAnnotation is unavailable)
    test(new RiscVPipeline) { dut =>
      // 2. Simulation Loop
      // Run the processor for 100 clock cycles
      for (cycle <- 0 until 100) {
        dut.clock.step()

        // 3. Monitor Output
        // Peek at the debug output (connected to Writeback stage in previous steps)
        // .peekInt() converts the Chisel hardware value to a Scala integer
        val wbValue = dut.io.result.peekInt()
        
        // Print the state every cycle to the console
        println(s"Cycle $cycle: Writeback Data = $wbValue")
      }
    }
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 