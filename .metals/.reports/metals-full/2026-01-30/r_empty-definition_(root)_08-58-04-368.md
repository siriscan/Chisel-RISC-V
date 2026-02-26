error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/TwoBitPredictor.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/TwoBitPredictor.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/BPredState.
	 -chisel3/util/BPredState.
	 -BPredState.
	 -scala/Predef.BPredState.
offset: 202
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/TwoBitPredictor.scala
text:
```scala
package core

import chisel3._
import chisel3.util._

object TwoBitPredictor extends ChiselEnum {
  val SNT, WNT, WT, ST = Value  // Strong/Weak NotTaken, Weak/Strong Taken

  def nextStateSwitch(s: BPr@@edState.Type, taken: Bool): BPredState.Type = {
  import BPredState._
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


```


#### Short summary: 

empty definition using pc, found symbol in pc: 