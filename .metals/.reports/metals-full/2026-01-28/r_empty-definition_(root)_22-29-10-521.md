error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/BranchTargetBuffer.scala:local3
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/BranchTargetBuffer.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol local3
empty definition using fallback
non-local guesses:

offset: 340
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/core/BranchTargetBuffer.scala
text:
```scala
package core

import chisel3._

class BranchTargetBuffer(entries: Int, xlen: Int) extends Module {
  val io = IO(new Bundle {

    // Fetch (Read)
    val pcIn = Input(UInt(xlen.W)) // Current PC
    val branchTaken = Input(Bool()) // Branch taken signal
    val branchTarget = Input(UInt(xlen.W)) // Target PC if branch taken
    val pcOut@@ = Output(UInt(xlen.W)) // Output PC (from BTB or current PC)
  })
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 