error id: 511AAB99E22287AC4D5D7C74426C8F2C
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala
### java.util.NoSuchElementException: head of empty String

occurred in the presentation compiler.



action parameters:
offset: 343
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/RiscVPipeline.scala
text:
```scala
package integer

class RiscVPipeline extends Module {
  val io = IO(new Bundle {
    val result = Output(UInt(32.W)) // Debug output
  })

  val conf = CoreConfig(xlen = 32, startPC = 0)

  // --- Instantiate Stages ---
  val fetch    = Module(new Fetch(conf))
  val decode   = Module(new Decode(conf))
  val execute  = Module(new @@Execute(conf))
  val memory   = Module(new Memory(conf))
  val writeback= Module(new Writeback(conf))

  // --- Instantiate Control Units ---
  val forwarding = Module(new ForwardingUnit(conf.xlen))
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
  val if_id = RegInit(0.U.asTypeOf(new IF_ID_Bundle))

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
  fetch.io.pcsr     := execute.io.branchTaken // Branch decision comes from Execute
  fetch.io.pcTarget := execute.io.targetPC
  fetch.io.stall    := hazard.io.stall

  // IF/ID Pipeline Update
  // Only update if not stalled. If branching (pcsr), flush the instruction (set to 0).
  when(execute.io.branchTaken) {
    if_id.inst := 0.U // Flush
    if_id.pc   := 0.U
  } .elsewhen(!hazard.io.stall) {
    if_id.inst := fetch.io.inst
    if_id.pc   := fetch.io.pc
  }
  // If stalled, keep current value (implicit in registers)


  // --- DECODE STAGE ---
  decode.io.inst     := if_id.inst
  decode.io.pc       := if_id.pc
  decode.io.wbAddr   := writeback.io.wbAddr
  decode.io.wbData   := writeback.io.wbData
  decode.io.wbEnable := writeback.io.wbEnable

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
    id_ex.ctrl     := decode.io.ctrl
    id_ex.pc       := decode.io.pcOut
    id_ex.rs1      := decode.io.rs1Data
    id_ex.rs2      := decode.io.rs2Data
    id_ex.imm      := decode.io.imm
    id_ex.rd       := decode.io.rd
    id_ex.rs1_addr := if_id.inst(19, 15)
    id_ex.rs2_addr := if_id.inst(24, 20)
  }


  // --- EXECUTE STAGE ---
  execute.io.ctrl    := id_ex.ctrl
  execute.io.pc      := id_ex.pc
  execute.io.imm     := id_ex.imm
  execute.io.rdIn    := id_ex.rd
  
  // *** FORWARDING MUXES ***
  // Instead of connecting rs1 directly, we use the forwarding decision
  // forwardA: 00->Reg, 01->WB, 10->MEM
  execute.io.rs1Data := MuxLookup(forwarding.io.forwardA, id_ex.rs1)(Seq(
    "b00".U -> id_ex.rs1,
    "b01".U -> writeback.io.wbData,
    "b10".U -> ex_mem.aluResult // Forwarding from MEM stage (ALU result)
  ))

  execute.io.rs2Data := MuxLookup(forwarding.io.forwardB, id_ex.rs2)(Seq(
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
  ex_mem.ctrl      := execute.io.ctrlOut
  ex_mem.aluResult := execute.io.aluResult
  ex_mem.rs2Data   := execute.io.memData // Passed through Execute for Store
  ex_mem.rd        := execute.io.rdOut


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
}
```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-3NLGKwyMTh-LUzAOrEi1_A== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
Options:
-language:reflectiveCalls -deprecation -feature -Xcheckinit -Ymacro-annotations -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.collection.StringOps$.head$extension(StringOps.scala:1124)
	scala.meta.internal.metals.ClassfileComparator.compare(ClassfileComparator.scala:30)
	scala.meta.internal.metals.ClassfileComparator.compare(ClassfileComparator.scala:3)
	java.base/java.util.PriorityQueue.siftUpUsingComparator(PriorityQueue.java:660)
	java.base/java.util.PriorityQueue.siftUp(PriorityQueue.java:637)
	java.base/java.util.PriorityQueue.offer(PriorityQueue.java:330)
	java.base/java.util.PriorityQueue.add(PriorityQueue.java:311)
	scala.meta.internal.metals.ClasspathSearch.$anonfun$search$3(ClasspathSearch.scala:32)
	scala.meta.internal.metals.ClasspathSearch.$anonfun$search$3$adapted(ClasspathSearch.scala:26)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.ClasspathSearch.search(ClasspathSearch.scala:26)
	scala.meta.internal.metals.WorkspaceSymbolProvider.search(WorkspaceSymbolProvider.scala:107)
	scala.meta.internal.metals.MetalsSymbolSearch.search$1(MetalsSymbolSearch.scala:114)
	scala.meta.internal.metals.MetalsSymbolSearch.search(MetalsSymbolSearch.scala:118)
	scala.meta.internal.pc.AutoImportsProvider.autoImports(AutoImportsProvider.scala:58)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$autoImports$1(ScalaPresentationCompiler.scala:399)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:148)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withInterruptableCompiler$1(CompilerAccess.scala:92)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
	java.base/java.lang.Thread.run(Thread.java:1474)
```
#### Short summary: 

java.util.NoSuchElementException: head of empty String