package integer
import chisel3._
import chisel3.util._

class RiscVPipeline extends Module {
  val io = IO(new Bundle {
    val result = Output(UInt(32.W)) // Debug output from Writeback Stage
    val memAddress = Output(UInt(32.W)) // Debug: Address sent to Data Memory
    val memDataIn = Output(UInt(32.W)) // Debug: Data written to Data Memory
    val currentInst = Output(UInt(32.W)) // Debug: Current instruction in IF/ID
    val decodeOpcode = Output(UInt(7.W)) // Debug: Opcode in Decode Stage
    val nextInst = Output(UInt(32.W)) // Debug: Next instruction to be fetched
    val fetchStall = Output(Bool()) // Debug: Stall signal from Hazard Unit
    val fetchPC = Output(UInt(32.W)) // Debug: Current PC in Fetch Stage
    val fetchNextPC = Output(UInt(32.W)) // Debug: Next PC in Fetch Stage
  })

  val conf = CoreConfig(xlen = 32, startPC = 0, imemFile  = "src/main/resources/pmem.hex", imemSize = 16384) 
  // 32-bit, start at address 0x00000000, instruction memory initialized from pmem.hex, 16KB IMEM

  // --- Instantiate Stages ---
  val fetch    = Module(new FetchStage(conf))
  val decode   = Module(new DecodeStage(conf))
  val execute  = Module(new ExecuteStage(conf))
  val memory   = Module(new MemoryStage(conf))
  val writeback= Module(new WritebackStage(conf))

  // --- Instantiate Control Units ---
  val forwarding = Module(new ForwardUnit(conf.xlen))
  val hazard     = Module(new HazardUnit)

  // ==================================================================
  // PIPELINE REGISTERS (The barriers between stages)
  // ==================================================================

  // 1. IF/ID Register
  // We use a Register of a Bundle to hold the data crossing the boundary
  class IF_ID_Bundle extends Bundle {
    val pc   = UInt(conf.xlen.W)
    val inst = UInt(32.W)
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
  }
  val id_ex = RegInit(0.U.asTypeOf(new ID_EX_Bundle))

  // 3. EX/MEM Register
  class EX_MEM_Bundle extends Bundle {
    val ctrl = new ControlSignals
    val aluResult = UInt(conf.xlen.W)
    val rs2Data   = UInt(conf.xlen.W) // For Store
    val rd        = UInt(5.W)
  }
  val ex_mem = RegInit(0.U.asTypeOf(new EX_MEM_Bundle))

  // 4. MEM/WB Register
  class MEM_WB_Bundle extends Bundle {
    val ctrl = new ControlSignals
    val memData   = UInt(conf.xlen.W)
    val aluResult = UInt(conf.xlen.W)
    val rd        = UInt(5.W)
  }
  val mem_wb = RegInit(0.U.asTypeOf(new MEM_WB_Bundle))


  // ==================================================================
  // WIRING THE STAGES
  // ==================================================================

  // --- FETCH STAGE ---
  fetch.io.takeBranch := execute.io.branchTaken // Branch decision comes from Execute
  fetch.io.branchTarget := execute.io.branchTarget
  fetch.io.stall    := hazard.io.stall

  // IF/ID Pipeline Update
  // Only update if not stalled. If branching (pcsr), flush the instruction (set to 0).
  when(execute.io.branchTaken) {
    if_id.inst := 0.U // Flush
    if_id.pc   := 0.U
  } .elsewhen(!hazard.io.stall) {
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
  // If stalled or branching, inject a bubble (zero out control signals)
  when(hazard.io.stall || execute.io.branchTaken) {
    id_ex.ctrl := 0.U.asTypeOf(new ControlSignals)
  } .otherwise {
    id_ex.ctrl     := decode.io.controlSignals
    id_ex.pc       := decode.io.pcOut
    id_ex.rs1      := decode.io.A
    id_ex.rs2      := decode.io.B
    id_ex.imm      := decode.io.immediate
    id_ex.rd       := if_id.inst(11, 7)
    id_ex.rs1_addr := if_id.inst(19, 15)
    id_ex.rs2_addr := if_id.inst(24, 20)
  }


  // --- EXECUTE STAGE ---
  execute.io.controlSignals := id_ex.ctrl
  execute.io.pcIn    := id_ex.pc
  execute.io.immediate := id_ex.imm
  
  // *** FORWARDING MUXES ***
  // Instead of connecting rs1 directly, we use the forwarding decision
  // forwardA: 00->Reg, 01->WB, 10->MEM
  execute.io.A := MuxLookup(forwarding.io.forwardA, id_ex.rs1)(Seq(
    "b00".U -> id_ex.rs1,
    "b01".U -> writeback.io.wbData,
    "b10".U -> ex_mem.aluResult // Forwarding from MEM stage (ALU result)
  ))

  execute.io.B := MuxLookup(forwarding.io.forwardB, id_ex.rs2)(Seq(
    "b00".U -> id_ex.rs2,
    "b01".U -> writeback.io.wbData,
    "b10".U -> ex_mem.aluResult
  ))

  // Forwarding Unit Connections
  forwarding.io.rs1_ex       := id_ex.rs1_addr
  forwarding.io.rs2_ex       := id_ex.rs2_addr
  forwarding.io.rd_mem       := ex_mem.rd
  forwarding.io.regWrite_mem := ex_mem.ctrl.regWrite
  forwarding.io.rd_wb        := mem_wb.rd
  forwarding.io.regWrite_wb  := mem_wb.ctrl.regWrite

  // EX/MEM Pipeline Update
  ex_mem.ctrl      := execute.io.controlSignalsOut
  ex_mem.aluResult := execute.io.aluResult
  ex_mem.rs2Data   := execute.io.memWriteData // Passed through Execute for Store
  ex_mem.rd        := id_ex.rd


  // --- MEMORY STAGE ---
  memory.io.ctrl      := ex_mem.ctrl
  memory.io.aluResult := ex_mem.aluResult
  memory.io.rs2Data   := ex_mem.rs2Data
  memory.io.rdIn      := ex_mem.rd

  // MEM/WB Pipeline Update
  mem_wb.ctrl      := memory.io.ctrlOut
  mem_wb.memData   := memory.io.memData
  mem_wb.aluResult := memory.io.aluOut
  mem_wb.rd        := memory.io.rdOut


  // --- WRITEBACK STAGE ---
  writeback.io.ctrl      := mem_wb.ctrl
  writeback.io.memData   := mem_wb.memData
  writeback.io.aluResult := mem_wb.aluResult
  writeback.io.rdIn      := mem_wb.rd

  // Debug Output
  io.result := writeback.io.wbData
  io.memAddress := memory.io.aluResult
  io.memDataIn  := memory.io.rs2Data
  io.currentInst := if_id.inst
  io.decodeOpcode := decode.io.instruction(6,0)
  io.nextInst := fetch.io.instruction
  io.fetchStall := fetch.io.stall
  io.fetchPC    := fetch.io.pc
  io.fetchNextPC := Mux(fetch.io.takeBranch, fetch.io.branchTarget, fetch.io.pc + 4.U)
  
}


