error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala:circt/stage/ChiselStage.emitSystemVerilogFile().(firtoolOpts)
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol circt/stage/ChiselStage.emitSystemVerilogFile().(firtoolOpts)
empty definition using fallback
non-local guesses:

offset: 1117
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala
text:
```scala
package integer

import chisel3._
import circt.stage.ChiselStage

class RegisterFile(width: Int) extends Module { // width will be used for future expansion; 32 bits for now

  val io = IO(new Bundle {
    val opcode = Input(UInt(4.W))
    val C = Input(UInt(width.W))
    val readAddressA = Input(UInt(5.W))
    val readAddressB = Input(UInt(5.W))
    val writeEnable = Input(Bool())
    val writeAddress = Input(UInt(5.W))
    val A = Output(UInt(width.W))
    val B = Output(UInt(width.W))
    
  })

  val regFile = RegInit(VecInit(Seq.fill(32)(0.U(width.W)))) // 32 registers of 32 bits each

  //Register 0 is always zero
  regFile(0) := 0.U // Forces x0 = 0

  // Read ports
  io.A := Mux(io.readAddressA === 0.U, 0.U, regFile(io.readAddressA))
  io.B := Mux(io.readAddressB === 0.U, 0.U, regFile(io.readAddressB))

  // Write port
  when(io.writeEnable && (io.writeAddress =/= 0.U)) {
    regFile(io.writeAddress) := io.C
  }
  
}

//*  */object RegisterFile extends App {
  ChiselStage.emitSystemVerilogFile(
    new integer.RegisterFile(width = 32), // 32-bit ALU
    f@@irtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 