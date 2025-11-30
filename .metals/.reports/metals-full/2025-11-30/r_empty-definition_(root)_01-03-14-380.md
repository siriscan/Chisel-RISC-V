error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala:
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb

found definition using fallback; symbol ForwardUnit
offset: 34
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
text:
```scala
package integer

class ForwardUnit@@(xlen: Int) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute Stage (Current Operands)
    val rs1_ex = Input(UInt(5.W))
    val rs2_ex = Input(UInt(5.W))

    // Inputs from Memory Stage (Potential Hazard)
    val rd_mem      = Input(UInt(5.W))
    val regWrite_mem = Input(Bool())

    // Inputs from Writeback Stage (Potential Hazard)
    val rd_wb       = Input(UInt(5.W))
    val regWrite_wb = Input(Bool())

    // Outputs (Forwarding Control Selectors)
    val forwardA = Output(UInt(2.W)) // 00: Reg, 01: WB, 10: MEM
    val forwardB = Output(UInt(2.W))
  })

  // Default: No forwarding (use register file value)
  io.forwardA := "b00".U
  io.forwardB := "b00".U

  // EX hazard (Forward from MEM stage)
  // If the instruction in MEM is writing to a register, AND it's not x0, 
  // AND the destination matches rs1 in Execute...
  when(io.regWrite_mem && io.rd_mem =/= 0.U && io.rd_mem === io.rs1_ex) {
    io.forwardA := "b10".U
  }
  when(io.regWrite_mem && io.rd_mem =/= 0.U && io.rd_mem === io.rs2_ex) {
    io.forwardB := "b10".U
  }

  // MEM hazard (Forward from WB stage)
  // Only forward if the MEM stage didn't already forward (priority logic)
  when(io.regWrite_wb && io.rd_wb =/= 0.U && io.rd_wb === io.rs1_ex && 
      !(io.regWrite_mem && io.rd_mem =/= 0.U && io.rd_mem === io.rs1_ex)) {
    io.forwardA := "b01".U
  }
  when(io.regWrite_wb && io.rd_wb =/= 0.U && io.rd_wb === io.rs2_ex && 
      !(io.regWrite_mem && io.rd_mem =/= 0.U && io.rd_mem === io.rs2_ex)) {
    io.forwardB := "b01".U
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 