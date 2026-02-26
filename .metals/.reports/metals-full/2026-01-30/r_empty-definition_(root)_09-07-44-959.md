error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/BranchPredictor.scala:chisel3/ChiselEnum#Type#
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/BranchPredictor.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/ChiselEnum#Type#
empty definition using fallback
non-local guesses:

offset: 281
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/BranchPredictor.scala
text:
```scala
package core

import chisel3._
import chisel3.util._
import chisel3.experimental._

object BranchState extends ChiselEnum {
  val SNT, WNT, WT, ST = Value  // strongly/weakly not-taken, weakly/strongly taken
}

object BranchFSM {
  def nextStateSwitch(s: BPredState.Type@@, taken: Bool): BPredState.Type = {
  val ns = WireDefault(s)
  when(taken) {
    switch(s) {
      is(SNT) { ns := WNT }
      is(WNT) { ns := WT  }
      is(WT)  { ns := ST  }
      is(ST)  { ns := ST  }
    }
  }.otherwise {
    switch(s) {
      is(ST)  { ns := WT  }
      is(WT)  { ns := WNT }
      is(WNT) { ns := SNT }
      is(SNT) { ns := SNT }
    }
  }
  ns
}

}

class BranchPredictor() extends Module{

}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 