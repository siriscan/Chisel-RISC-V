error id: 3E36BD68C0B0D1D84EC158BBBF1B1699
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala
### scala.ScalaReflectionException: value inst is not a method

occurred in the presentation compiler.



action parameters:
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala
text:
```scala
package integer
import chisel3._
import chisel3.util._

class RiscVPipeline extends Module {
  val io = IO(new Bundle {
    val result = Output(UInt(32.W)) // Debug output from Writeback Stage
    val memAddress = Output(UInt(32.W)) // Debug: Address sent to Data Memory
    val memDataIn = Output(UInt(32.W)) // Debug: Data written to Data Memory
    val currentInst = Output(UInt(32.W)) // Debug: Current instruction in Writeback Stage
    val decodeOpcode = Output(UInt(7.W)) // Debug: Opcode in Decode Stage
    val nextInst = Output(UInt(32.W)) // Debug: Next instruction to be fetched
    val fetchStall = Output(Bool()) // Debug: Stall signal from Hazard Unit
    val fetchPC = Output(UInt(32.W)) // Debug: Current PC in Fetch Stage
    val fetchNextPC = Output(UInt(32.W)) // Debug: Next PC in Fetch Stage
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
    val inst = UInt(32.W) /
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
    val pc        = UInt(conf.xlen.W)
  }
  val ex_mem = RegInit(0.U.asTypeOf(new EX_MEM_Bundle))

  // 4. MEM/WB Register
  class MEM_WB_Bundle extends Bundle {
    val ctrl = new ControlSignals
    val memData   = UInt(conf.xlen.W)
    val aluResult = UInt(conf.xlen.W)
    val rd        = UInt(5.W)
    val pc        = UInt(conf.xlen.W)
  }
  val mem_wb = RegInit(0.U.asTypeOf(new MEM_WB_Bundle))

  //Branch Handling
  val takeBranchDelayed = RegNext(execute.io.branchTaken, false.B) // Delay branch taken signal by 1 cycle
  val branchTargetDelayed = RegNext(execute.io.branchTarget, 0.U) // Delay branch target by 1 cycle
  // Note: Might add branch predictors later. For now, simple 1-cycle delay.

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
  } .otherwise {
    id_ex.ctrl     := decode.io.controlSignals 
    id_ex.pc       := decode.io.pcOut
    id_ex.rs1      := decode.io.A // Source Register 1 Data (A from Reg File)
    id_ex.rs2      := decode.io.B // Source Register 2 Data (B from Reg File)
    id_ex.imm      := decode.io.immediate
    id_ex.rd       := if_id.inst(11, 7)
    id_ex.rs1_addr := if_id.inst(19, 15)
    id_ex.rs2_addr := if_id.inst(24, 20)
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
  io.currentInst := if_id.inst
  io.decodeOpcode := decode.io.instruction(6,0)
  io.nextInst := fetch.io.instruction
  io.fetchStall := fetch.io.stall
  io.fetchPC    := fetch.io.pc
  io.fetchNextPC := Mux(fetch.io.takeBranch, fetch.io.branchTarget, fetch.io.pc + 4.U)
  
}

// Generate the Verilog

object RISCVPipelineGenerator extends App {
  println("Generating the Risc-V pipeline Verilog code...")
  emitVerilog(new RiscVPipeline(), Array("--target-dir", "generated"))
}
// Note: Need to make a seperate App object to generate Verilog entirely

// So far, we have implemented a basic 5-stage RISC-V pipeline with hazard detection and forwarding.
// It supports basic integer instructions including arithmetic, load/store, and branches.
// It has multiply/divide support in the Execute stage.

/* Todo: Add atomic instructions and pipeline support
 - Add single-precision floating point support
 - Add half-precision floating point support
 - Add CSR instructions and pipeline support (Not needed for this project)
 - Add vector instructions and pipeline support


 */

```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals--fVGrqvOTOyo7vc7ZJV8iw== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
Options:
-language:reflectiveCalls -deprecation -feature -Xcheckinit -Ymacro-annotations -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.reflect.api.Symbols$SymbolApi.asMethod(Symbols.scala:240)
	scala.reflect.api.Symbols$SymbolApi.asMethod$(Symbols.scala:234)
	scala.reflect.internal.Symbols$SymbolContextApiImpl.asMethod(Symbols.scala:101)
	scala.tools.nsc.typechecker.ContextErrors$TyperContextErrors$TyperErrorGen$.MissingArgsForMethodTpeError(ContextErrors.scala:818)
	scala.tools.nsc.typechecker.Typers$Typer.adaptMethodTypeToExpr$1(Typers.scala:989)
	scala.tools.nsc.typechecker.Typers$Typer.adapt(Typers.scala:1346)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6359)
	scala.tools.nsc.typechecker.Typers$Typer.typedDefDef(Typers.scala:6608)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6250)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedStat$1(Typers.scala:6422)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$5(Typers.scala:3496)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$5$adapted(Typers.scala:3491)
	scala.reflect.internal.Scopes$Scope.foreach(Scopes.scala:455)
	scala.tools.nsc.typechecker.Typers$Typer.addSynthetics$1(Typers.scala:3491)
	scala.tools.nsc.typechecker.Typers$Typer.typedStats(Typers.scala:3559)
	scala.tools.nsc.typechecker.Typers$Typer.typedTemplate(Typers.scala:2133)
	scala.tools.nsc.typechecker.Typers$Typer.typedClassDef(Typers.scala:1971)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6251)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedStat$1(Typers.scala:6422)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$10(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedStats(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedTemplate(Typers.scala:2133)
	scala.tools.nsc.typechecker.Typers$Typer.typedClassDef(Typers.scala:1971)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6251)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedStat$1(Typers.scala:6422)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$10(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedStats(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedPackageDef$1(Typers.scala:5925)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6254)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Analyzer$typerFactory$TyperPhase.apply(Analyzer.scala:126)
	scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:483)
	scala.tools.nsc.interactive.Global$TyperRun.applyPhase(Global.scala:1370)
	scala.tools.nsc.interactive.Global$TyperRun.typeCheck(Global.scala:1363)
	scala.tools.nsc.interactive.Global.typeCheck(Global.scala:681)
	scala.meta.internal.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:24)
	scala.meta.internal.pc.SimpleCollector.<init>(PcCollector.scala:348)
	scala.meta.internal.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:19)
	scala.meta.internal.pc.PcSemanticTokensProvider.Collector$lzycompute$1(PcSemanticTokensProvider.scala:19)
	scala.meta.internal.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:19)
	scala.meta.internal.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:73)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticTokens$1(ScalaPresentationCompiler.scala:207)
	scala.meta.internal.pc.CompilerAccess.retryWithCleanCompiler(CompilerAccess.scala:182)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withSharedCompiler$1(CompilerAccess.scala:155)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:154)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withInterruptableCompiler$1(CompilerAccess.scala:92)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
	java.base/java.lang.Thread.run(Thread.java:1474)
```
#### Short summary: 

scala.ScalaReflectionException: value inst is not a method