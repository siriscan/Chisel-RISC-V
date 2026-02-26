error id: 9F2FDDC6F40BFD543C49B0C9CA90163F
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ExecuteStage.scala
### scala.ScalaReflectionException: value pcPlusImm is not a method

occurred in the presentation compiler.



action parameters:
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ExecuteStage.scala
text:
```scala
package integer

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import core._


class ExecuteStage(config : CoreConfig) extends Module {
  val io = IO(new Bundle {
    // Inputs from Decode Stage
    val A = Input(UInt(32.W)) // Read Data 1
    val B = Input(UInt(32.W)) // Read Data 2
    val immediate = Input(UInt(32.W)) // Immediate Value
    val pcIn = Input(UInt(config.xlen.W)) // PC from Decode Stage
    val controlSignals = Input(new ControlSignals)

    // Outputs to Fetch Stage
    val branchTaken = Output(Bool()) // True: branch taken, False: not taken
    val branchTarget = Output(UInt(config.xlen.W)) // Target PC if branch taken


    // Outputs to Memory Stage
    val C = Output(UInt(32.W)) // ALU Result
    val pcOut = Output(UInt(config.xlen.W)) // Pass PC to Memory Stage
    val controlSignalsOut = Output(new ControlSignals) // Pass control signals to Memory Stage
    val memWriteData = Output(UInt(32.W)) // Data (rs2) to write to memory for store instructions

    // Prediction update outputs to Fetch Stage
    // (not needed in Execute Stage, but passed through)

    // Inputs for branch prediction from Decode Stage
    val predTaken  = Input(Bool())
    val predTarget = Input(UInt(config.xlen.W))

  })

    // ALU Module
    val alu = Module(new ALU(config.xlen)) // 32-bit ALU

    val imm_flag = io.controlSignals.imm_flag // Immediate flag 
    val lui_flag = io.controlSignals.lui // LUI/AUIPC flag   

    // Connect ALU inputs

    // Connect isSigned signal
    alu.io.isSigned := io.controlSignals.isSigned


    // Select A input based on LUI/AUIPC control signal
    when (lui_flag === 1.U) { // LUI
      alu.io.A := 0.U
      alu.io.B := io.immediate
    } .elsewhen (lui_flag === 2.U) { // AUIPC
      alu.io.A := io.pcIn
      alu.io.B := io.immediate
    } .otherwise {
      alu.io.A := io.A // Default A input
    }

    // Select B input based on aluSrc control signal
    alu.io.B := Mux(imm_flag, io.immediate, io.B) // 1: immediate, 0: value from register

    // Connect ALU opcode
    alu.io.opcode := io.controlSignals.aluOp

    // Connect ALU output
    io.C := alu.io.C

    // Branches
    val isBranch = io.controlSignals.branch // True if branch instruction
    val branchOp = io.controlSignals.branchOp

    val a = alu.io.A
    val b = alu.io.B
    val isEqual = (a === b) // Equality comparison
    val isLessThan = Mux(io.controlSignals.isSigned, (a.asSInt < b.asSInt), (a < b)) // Signed or unsigned comparison
    val isGreaterThan = Mux(io.controlSignals.isSigned, (a.asSInt > b.asSInt), (a > b)) // Signed or unsigned comparison
    val isBGE = isGreaterThan || isEqual  // Greater than or equal (Both signed and unsigned)

    val isBranchTaken = WireDefault(false.B) // Default not taken

    // Branch Decision Logic
    when (isBranch) {
      when(branchOp === "b000".U) { // BEQ
        isBranchTaken := isEqual
      } .elsewhen(branchOp === "b001".U) { // BNE
        isBranchTaken := !isEqual
      } .elsewhen(branchOp === "b100".U) { // BLT
        isBranchTaken := isLessThan
      } .elsewhen(branchOp === "b101".U) { // BGE
        isBranchTaken := isBGE
      } .elsewhen(branchOp === "b110".U) { // BLTU
        isBranchTaken := isLessThan
      } .elsewhen(branchOp === "b111".U) { // BGEU
        isBranchTaken := isBGE
      }
    }.otherwise {
      isBranchTaken := false.B // Not a branch instruction
    }   

    // Jumps
    val jumpType = io.controlSignals.jump // 0: No jump, 1: JAL, 2: JALR


    // Calculate Branch Target
    val pcPlusImm =  (io.pcIn + io.immediate) & 
    val jalrTarget = (a + io.immediate) & (~1.U(32.W)) // JALR target address (rs1 + imm) with LSB zeroed

    when (jumpType === 1.U) { // JAL
      io.branchTarget := pcPlusImm
    } .elsewhen (jumpType === 2.U) { // JALR
      io.branchTarget := jalrTarget
    } .elsewhen (isBranch) { // Branch instructions
      io.branchTarget := pcPlusImm
    } .otherwise {
      io.branchTarget := 0.U // Default value (not used if not branching)
    }

    // Final Branch Taken Signal
    io.branchTaken := (isBranch && isBranchTaken) || (jumpType =/= 0.U) // Taken if branch condition met or if jump instruction

    val jalrMispredict = WireDefault(false.B)
    // JALR Mispredict Detection
    jalrMispredict := !io.predTaken || (io.predTarget =/= jalrTarget)



    // Pass to Memory Stage
    io.pcOut := io.pcIn
    io.controlSignalsOut := io.controlSignals

    // Data forwarding to Memory Stage for store instructions
    io.memWriteData := io.B

  }
```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-xcKzuMEzQo6YDECxlc4Nrw== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.2\semanticdb-javac-0.11.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
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

scala.ScalaReflectionException: value pcPlusImm is not a method