error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala:chisel3/UInt.
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/UInt.
empty definition using fallback
non-local guesses:

offset: 396
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RegisterFile.scala
text:
```scala
import chisel3._

class RegisterFile(width: Int) extends Module { // width will be used for future expansion

  val io = IO(new Bundle {
    val opcode = Input(UInt(4.W))
    val C = Input(UInt(width.W))
    val readAddressA = Input(UInt(5.W))
    val readAddressB = Input(UInt(5.W))
    val writeEnable = Input(Bool())
    val writeAddress = Input(UInt(5.W))
width    val B = Output(U@@Int(32.W))
    
  })

  val regFile = RegInit(VecInit(Seq.fill(32)(0.U(32.W)))) // 32 registers of 32 bits each

  // Read ports
  io.A := Mux(io.readAddressA === 0.U, 0.U, regFile(io.readAddressA))
  io.B := Mux(io.readAddressB === 0.U, 0.U, regFile(io.readAddressB))

  // Write port
  when(io.writeEnable && (io.writeAddress =/= 0.U)) {
    regFile(io.writeAddress) := io.C
  }
  
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: 