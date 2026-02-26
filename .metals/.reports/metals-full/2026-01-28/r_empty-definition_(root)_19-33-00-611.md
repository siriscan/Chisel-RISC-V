error id: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala:branchTaken
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala
empty definition using pc, found symbol in pc: branchTaken
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/execute/io/branchTaken.
	 -chisel3/execute/io/branchTaken#
	 -chisel3/execute/io/branchTaken().
	 -chisel3/util/execute/io/branchTaken.
	 -chisel3/util/execute/io/branchTaken#
	 -chisel3/util/execute/io/branchTaken().
	 -core/execute/io/branchTaken.
	 -core/execute/io/branchTaken#
	 -core/execute/io/branchTaken().
	 -execute/io/branchTaken.
	 -execute/io/branchTaken#
	 -execute/io/branchTaken().
	 -scala/Predef.execute.io.branchTaken.
	 -scala/Predef.execute.io.branchTaken#
	 -scala/Predef.execute.io.branchTaken().
offset: 4594
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala
text:
```scala
package integer
import chisel3._
import chisel3.util._
import core._

class RiscVPipeline extends Module {
  val io = IO(new Bundle {
    val result = Output(UInt(32.W)) // Debug output from Writeback Stage
    val memAddress = Output(UInt(32.W)) // Debug: Address sent to Data Memory
    val memDataIn = Output(UInt(32.W)) // Debug: Data written to Data Memory

    val memRead = Output(Bool()) // Debug: Memory Read signal
    val memWrite = Output(Bool()) // Debug: Memory Write signal
    val memReadData = Output(UInt(32.W)) // Debug: Data read from Data Memory

    val nextInst = Output(UInt(32.W)) // Debug: Next instruction to be fetched
    
    val exBranchTaken = Output(Bool()) // Debug: Branch taken signal from Execute Stage
    val exBranchTarget = Output(UInt(32.W)) // Debug: Branch target from Execute Stage
    val ifTakeBranch = Output(Bool()) // Debug: Branch taken signal in Fetch Stage
    val ifBranchTarget = Output(UInt(32.W)) // Debug: Branch target in Fetch Stage

    val currentInst = Output(UInt(32.W)) // Debug: Current instruction in Writeback Stage
    val wbEnable = Output(Bool()) // Debug: Writeback Enable signal
    val wbAddr = Output(UInt(5.W)) // Debug: Writeback Address
    val wbOpcode = Output(UInt(7.W)) // Debug: Opcode of instruction in Writeback Stage
    val wbFunct3 = Output(UInt(3.W)) // Debug: funct3 of instruction in Writeback Stage
    val wbRd   = Output(UInt(5.W)) // Debug: rd of instruction in Writeback Stage

    val wbPC    = Output(UInt(32.W))  // Debug: PC of instruction in Writeback Stage
    val fetchPC = Output(UInt(32.W))  // Debug: PC in Fetch Stage


  })

  val conf = CoreConfig(xlen = 32, startPC = 0, imemFile  = "src/main/resources/pmem.hex", imemSize = 16384) 
  // 32-bit, start at address 0x00000000, instruction memory initialized from pmem.hex, 16KB IMEM

  // Instantiate Pipeline Stages
  val fetch    = Module(new FetchStage(conf))
  val decode   = Module(new DecodeStage(conf))
  val execute  = Module(new ExecuteStage(conf))
  val memory   = Module(new MemoryStage(conf))
  val writeback= Module(new WritebackStage(conf))

  // Instantiate Hazard and Forwarding Units
  val forwarding = Module(new ForwardUnit(conf.xlen))
  val hazard     = Module(new HazardUnit)

  // Pipeline Registers

  // 1. IF/ID Register
  // We use a Register of a Bundle to hold the data crossing the boundary
  class IF_ID_Bundle extends Bundle {
    val pc   = UInt(conf.xlen.W)
    val inst = UInt(32.W) // Fetched instruction
  }
  // Initialize to NOP (0x13 is ADDI x0, x0, 0)
  // Create a default Wire with the correct reset values

  val init_if_id = Wire(new IF_ID_Bundle)
  init_if_id.pc   := 0.U
  init_if_id.inst := "h00000013".U(32.W)

  // Use this wire for the register initialization
  val if_id = RegInit(init_if_id)

  // 2. ID/EX Register
  class ID_EX_Bundle extends Bundle {
    val ctrl = new ControlSignals
    val pc   = UInt(conf.xlen.W)
    val rs1  = UInt(conf.xlen.W)
    val rs2  = UInt(conf.xlen.W)
    val imm  = UInt(conf.xlen.W)
    val rd   = UInt(5.W)
    val rs1_addr = UInt(5.W) // Needed for Forwarding
    val rs2_addr = UInt(5.W) // Needed for Forwarding

    val inst = UInt(32.W) // For debugging (Can see instruction in Execute Stage)
  }
  val id_ex = RegInit(0.U.asTypeOf(new ID_EX_Bundle))

  // 3. EX/MEM Register
  class EX_MEM_Bundle extends Bundle {
    val ctrl = new ControlSignals
    val aluResult = UInt(conf.xlen.W)
    val rs2Data   = UInt(conf.xlen.W) // For Store
    val rd        = UInt(5.W)
    val pc        = UInt(conf.xlen.W)

    val inst = UInt(32.W) // For debugging (Can see instruction in Memory Stage)
  }
  val ex_mem = RegInit(0.U.asTypeOf(new EX_MEM_Bundle))

  // 4. MEM/WB Register
  class MEM_WB_Bundle extends Bundle {
    val ctrl = new ControlSignals
    val memData   = UInt(conf.xlen.W)
    val aluResult = UInt(conf.xlen.W)
    val rd        = UInt(5.W)
    val pc        = UInt(conf.xlen.W)

    val inst = UInt(32.W) // For debugging (Can see instruction in Writeback Stage)
  }

  val mem_wb = RegInit(0.U.asTypeOf(new MEM_WB_Bundle))

  //Branch Handling
//  val takeBranchDelayed = RegNext(RegNext(execute.io.branchTaken, false.B), false.B) // Delay branch taken signal by 2 cycles
//  val branchTargetDelayed = RegNext(RegNext(execute.io.branchTarget, 0.U), 0.U) // Delay branch target by 2 cycles  
  // Note: Might add branch predictors later. For now, simple 2-cycle delay.

  fetch.io.takeBranch   := execute.io.branchTak@@en
  fetch.io.branchTarget := execute.io.branchTarget

  val flush = execute.io.branchTaken // Flush on branch taken

  // Connections Between Stages
  // Fetch Stage Connections 
  fetch.io.takeBranch := takeBranchDelayed
  fetch.io.branchTarget := branchTargetDelayed
  fetch.io.stall    := hazard.io.stall

  // IF/ID Pipeline Update
  // Only update if not stalled. 
  when(!hazard.io.stall) {
    if_id.inst := fetch.io.instruction
    if_id.pc   := fetch.io.pc
  }
  // If stalled, keep current value (implicit in registers)

  // --- DECODE STAGE ---
  decode.io.instruction := if_id.inst
  decode.io.pc       := if_id.pc
  decode.io.writeAddress := writeback.io.wbAddr
  decode.io.C     := writeback.io.wbData
  decode.io.writeEnable := writeback.io.wbEnable

  

  // Hazard Unit Connections
  hazard.io.rs1_id     := if_id.inst(19, 15) // Extract directly from instruction
  hazard.io.rs2_id     := if_id.inst(24, 20)
  hazard.io.rd_ex      := id_ex.rd
  hazard.io.memRead_ex := id_ex.ctrl.memRead

  // ID/EX Pipeline Update
  // If stalled or branching, inject a bubble
  when(hazard.io.stall) {
    id_ex.ctrl := 0.U.asTypeOf(new ControlSignals) // Bubble (All control signals zero)
    id_ex.inst := 0.U // Can see bubble
  } .otherwise {
    id_ex.ctrl     := decode.io.controlSignals 
    id_ex.pc       := decode.io.pcOut
    id_ex.rs1      := decode.io.A // Source Register 1 Data (A from Reg File)
    id_ex.rs2      := decode.io.B // Source Register 2 Data (B from Reg File)
    id_ex.imm      := decode.io.immediate
    id_ex.rd       := if_id.inst(11, 7)
    id_ex.rs1_addr := if_id.inst(19, 15)
    id_ex.rs2_addr := if_id.inst(24, 20)
    id_ex.inst     := if_id.inst // For debugging
  }


  // Execute Stage Connections
  execute.io.controlSignals := id_ex.ctrl
  execute.io.pcIn    := id_ex.pc
  execute.io.immediate := id_ex.imm
  
  // Forwarding Connections (Muxing the inputs A and B)
  // Instead of connecting rs1 directly, we use the forwarding decision

  // forwardA: 00->Reg, 01->WB, 10->MEM
  execute.io.A := MuxLookup(forwarding.io.forwardA, id_ex.rs1)(Seq(
    "b00".U -> id_ex.rs1,
    "b01".U -> writeback.io.wbData,
    "b10".U -> ex_mem.aluResult // Forwarding from MEM stage (ALU result)
  ))

  // forwardB: 00->Reg, 01->WB, 10->MEM
  execute.io.B := MuxLookup(forwarding.io.forwardB, id_ex.rs2)(Seq(
    "b00".U -> id_ex.rs2,
    "b01".U -> writeback.io.wbData,
    "b10".U -> ex_mem.aluResult
  )) 

  // Forwarding Unit Connections
  forwarding.io.rs1_ex       := id_ex.rs1_addr
  forwarding.io.rs2_ex       := id_ex.rs2_addr
  forwarding.io.rd_mem       := ex_mem.rd
  forwarding.io.regWrite_mem := ex_mem.ctrl.regWrite && !ex_mem.ctrl.memToReg // Only forward ALU result from MEM stage
  forwarding.io.rd_wb        := mem_wb.rd
  forwarding.io.regWrite_wb  := mem_wb.ctrl.regWrite

  // EX/MEM Pipeline Update
  ex_mem.ctrl      := execute.io.controlSignalsOut
  ex_mem.aluResult := execute.io.C // Connect ALU result to EX/MEM register
  ex_mem.rs2Data   := execute.io.memWriteData // Passed through Execute for Store
  ex_mem.rd        := id_ex.rd
  ex_mem.pc        := id_ex.pc
  ex_mem.inst      := id_ex.inst // For debugging

  // Memory Stage Connections
  memory.io.ctrl      := ex_mem.ctrl
  memory.io.aluResult := ex_mem.aluResult
  memory.io.rs2Data   := ex_mem.rs2Data
  memory.io.rdIn      := ex_mem.rd

  // MEM/WB Pipeline Update
  mem_wb.ctrl      := memory.io.ctrlOut
  mem_wb.memData   := DontCare // 1 clock cycle delay due to SyncReadMem
  mem_wb.aluResult := memory.io.aluOut
  mem_wb.rd        := memory.io.rdOut
  mem_wb.pc        := ex_mem.pc
  mem_wb.inst      := ex_mem.inst // For debugging


  // Writeback Stage Connections
  writeback.io.ctrl      := mem_wb.ctrl
  writeback.io.memData   := memory.io.memData // Data loaded from memory (if any)
  writeback.io.aluResult := mem_wb.aluResult // ALU result from MEM stage 
  writeback.io.rdIn      := mem_wb.rd // Destination Register
  writeback.io.pcIn      := mem_wb.pc // PC for JAL/JALR

  // Debug Output (For Verilog Monitoring)
  io.result := writeback.io.wbData
  io.memAddress := memory.io.aluResult
  io.memDataIn  := memory.io.rs2Data
  io.memReadData := memory.io.memData
  io.memRead := memory.io.ctrl.memRead
  io.memWrite := memory.io.ctrl.memWrite

  io.currentInst := mem_wb.inst

  io.nextInst := fetch.io.instruction

  io.wbEnable := writeback.io.wbEnable
  io.wbAddr   := writeback.io.wbAddr  

  io.wbOpcode := mem_wb.inst(6,0)
  io.wbFunct3 := mem_wb.inst(14,12)
  io.wbRd     := mem_wb.inst(11,7)

  io.exBranchTaken  := execute.io.branchTaken
  io.exBranchTarget := execute.io.branchTarget
  io.ifTakeBranch   := fetch.io.takeBranch
  io.ifBranchTarget := fetch.io.branchTarget

  io.wbPC    := mem_wb.pc
  io.fetchPC := fetch.io.pc
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: branchTaken