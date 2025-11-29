import chisel3._

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

  // Read ports
  io.A := Mux(io.readAddressA === 0.U, 0.U, regFile(io.readAddressA))
  io.B := Mux(io.readAddressB === 0.U, 0.U, regFile(io.readAddressB))

  // Write port
  when(io.writeEnable && (io.writeAddress =/= 0.U)) {
    regFile(io.writeAddress) := io.C
  }
  
}

