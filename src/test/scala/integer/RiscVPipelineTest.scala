package integer

import chisel3._
// 1. New Simulator Import
import chisel3.simulator.EphemeralSimulator._ 
import org.scalatest.flatspec.AnyFlatSpec

// 2. Just extend AnyFlatSpec (No ChiselScalatestTester)
class RiscVPipelineTest extends AnyFlatSpec {
  behavior of "RISC-V 5-Stage Pipeline"

  it should "execute a program for a fixed number of cycles" in {
    // 3. Use 'simulate' instead of 'test'
    // This automatically picks a backend (Verilator if installed, or Firrtl)
    simulate(new RiscVPipeline) { dut =>
      
      // 4. Reset
      // In ChiselSim, reset is often implicit at start, but explicit ensures clarity
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      // 5. Simulation Loop
      for (cycle <- 0 until 100) {
        dut.clock.step()

        // 6. Monitor Output
        // 'peek()' gets the signal value. 
        // '.litValue' converts it to a Scala BigInt for printing/assertions.
        val wbValue = dut.io.result.peek().litValue
        
        println(s"Cycle $cycle: Writeback Data = $wbValue")
      }
    }
  }
}