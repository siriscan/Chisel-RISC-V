error id: 8E34C7CD59EAFA10ACF20C443BD545BB
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
### scala.reflect.internal.FatalError: 
  unexpected tree: class scala.reflect.internal.Trees$Template
<Bundle: error> {
  def <init>(): <$anon: <error>> = {
    super.<init>();
    ()
  };
  private[this] val <rs1_ex: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <rs2_ex: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <rd_mem: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <regWrite_mem: error>: <error> = <Input: error>(<Bool: error>());
  private[this] val <rd_wb: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <regWrite_wb: error>: <error> = <Input: error>(<Bool: error>());
  private[this] val <forwardA: error>: <error> = <Output: error>(<UInt: error>(2.<W: error>));
  private[this] val <forwardB: error>: <error> = <Output: error>(<UInt: error>(2.<W: error>))
}
     while compiling: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
        during phase: globalPhase=<no phase>, enteringPhase=parser
     library version: version 2.13.16
    compiler version: version 2.13.16
  reconstructed args: -deprecation -feature -Wconf:cat=feature:w -Wconf:cat=deprecation:ws -Wconf:cat=feature:ws -Wconf:cat=optimizer:ws -classpath <WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-3NLGKwyMTh-LUzAOrEi1_A==;<HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar -language:reflectiveCalls -Xcheckinit -Xplugin-require:semanticdb -Yrangepos -Ymacro-expand:discard -Ymacro-annotations -Ycache-plugin-class-loader:last-modified -Ypresentation-any-thread

  last tree to typer: Template(value <local $anon>)
       tree position: line 4 of file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
              symbol: value <local $anon>
   symbol definition: val <local $anon>: <notype> (a TermSymbol)
      symbol package: integer
       symbol owners: value <local $anon> -> <$anon: <error>> -> value io -> class ForwardUnit
           call site: <none> in <none>

== Source file context for tree position ==

     1 package integer
     2 
     3 class ForwardUnit(xlen: Int) extends Module {
     4   val io = IO(new Bundle {
     5     // Inputs from Execute Stage (Current Operands)
     6     val rs1_ex = Input(UInt(5.W))
     7     val rs2_ex = Input(UInt(5.W))

occurred in the presentation compiler.



action parameters:
offset: 366
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
text:
```scala
package integer

class ForwardUnit(xlen: Int) extends Module {
  val io = IO(new Bundle {
    // Inputs from Execute Stage (Current Operands)
    val rs1_ex = Input(UInt(5.W))
    val rs2_ex = Input(UInt(5.W))

    // Inputs from Memory Stage (Potential Hazard)
    val rd_mem      = Input(UInt(5.W))
    val regWrite_mem = Input(Bool())

    // Inputs from Writebac@@k Stage (Potential Hazard)
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


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-3NLGKwyMTh-LUzAOrEi1_A== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
Options:
-language:reflectiveCalls -deprecation -feature -Xcheckinit -Ymacro-annotations -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.reflect.internal.Reporting.abort(Reporting.scala:70)
	scala.reflect.internal.Reporting.abort$(Reporting.scala:66)
	scala.reflect.internal.SymbolTable.abort(SymbolTable.scala:28)
	scala.tools.nsc.typechecker.Typers$Typer.typedOutsidePatternMode$1(Typers.scala:6281)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6298)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedQualifier(Typers.scala:6442)
	scala.meta.internal.pc.PcDefinitionProvider.definitionTypedTreeAt(PcDefinitionProvider.scala:190)
	scala.meta.internal.pc.PcDefinitionProvider.definition(PcDefinitionProvider.scala:69)
	scala.meta.internal.pc.PcDefinitionProvider.definition(PcDefinitionProvider.scala:17)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$definition$1(ScalaPresentationCompiler.scala:490)
	scala.meta.internal.pc.CompilerAccess.retryWithCleanCompiler(CompilerAccess.scala:182)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withSharedCompiler$1(CompilerAccess.scala:155)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:154)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withNonInterruptableCompiler$1(CompilerAccess.scala:132)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
	java.base/java.lang.Thread.run(Thread.java:1474)
```
#### Short summary: 

scala.reflect.internal.FatalError: 
  unexpected tree: class scala.reflect.internal.Trees$Template
<Bundle: error> {
  def <init>(): <$anon: <error>> = {
    super.<init>();
    ()
  };
  private[this] val <rs1_ex: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <rs2_ex: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <rd_mem: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <regWrite_mem: error>: <error> = <Input: error>(<Bool: error>());
  private[this] val <rd_wb: error>: <error> = <Input: error>(<UInt: error>(5.<W: error>));
  private[this] val <regWrite_wb: error>: <error> = <Input: error>(<Bool: error>());
  private[this] val <forwardA: error>: <error> = <Output: error>(<UInt: error>(2.<W: error>));
  private[this] val <forwardB: error>: <error> = <Output: error>(<UInt: error>(2.<W: error>))
}
     while compiling: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
        during phase: globalPhase=<no phase>, enteringPhase=parser
     library version: version 2.13.16
    compiler version: version 2.13.16
  reconstructed args: -deprecation -feature -Wconf:cat=feature:w -Wconf:cat=deprecation:ws -Wconf:cat=feature:ws -Wconf:cat=optimizer:ws -classpath <WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-3NLGKwyMTh-LUzAOrEi1_A==;<HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar;<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar -language:reflectiveCalls -Xcheckinit -Xplugin-require:semanticdb -Yrangepos -Ymacro-expand:discard -Ymacro-annotations -Ycache-plugin-class-loader:last-modified -Ypresentation-any-thread

  last tree to typer: Template(value <local $anon>)
       tree position: line 4 of file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/ForwardUnit.scala
              symbol: value <local $anon>
   symbol definition: val <local $anon>: <notype> (a TermSymbol)
      symbol package: integer
       symbol owners: value <local $anon> -> <$anon: <error>> -> value io -> class ForwardUnit
           call site: <none> in <none>

== Source file context for tree position ==

     1 package integer
     2 
     3 class ForwardUnit(xlen: Int) extends Module {
     4   val io = IO(new Bundle {
     5     // Inputs from Execute Stage (Current Operands)
     6     val rs1_ex = Input(UInt(5.W))
     7     val rs2_ex = Input(UInt(5.W))