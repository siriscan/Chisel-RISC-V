error id: EA51624340FF67788E6E9884DF712D5B
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
### java.lang.IndexOutOfBoundsException: -1 is out of bounds (min 0, max 2)

occurred in the presentation compiler.



action parameters:
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
text:
```scala
package integer

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class DecodeStage(conf: CoreConfig) extends Module {
    val io = IO(new Bundle {

    // Inputs from Fetch Stage
    val instruction = Input(UInt(32.W))
    val pc = Input(UInt(conf.xlen.W))

    // Inputs from Writeback Stage
    val C = Input(UInt(32.W)) // Data to write back to register file
    val writeEnable = Input(Bool())
    val writeAddress = Input(UInt(5.W))

    // Outputs to Execute Stage
    val A = Output(UInt(32.W)) // Read Data 1
    val B = Output(UInt(32.W)) // Read Data 2
    val immediate = Output(UInt(32.W)) // Immediate Value
    val pcOut = Output(UInt(conf.xlen.W)) // Pass PC to Execute Stage
    val controlSignals = Output(new ControlSignals)

  })
    // Default values
    io.controlSignals := 0.U.asTypeOf(new ControlSignals)
    io.immediate := 0.U
    io.controlSignals.imm_flag := false.B
    io.pcOut := io.pc
    io.controlSignals.aluOp := 0.U // NOP


    val imm = io.instruction(31, 20) // I-type immediate

    val imm_u = io.instruction(31, 12) // U-type immediate
    val imm_upper = Cat(imm_u, Fill(12, 0.U)) // Shift left by 12

    val imm_sext = Cat(Fill(20, imm(11)), imm) // sign-extend immediate

    val opcode = io.instruction(6, 0)
    val funct3 = io.instruction(14, 12)
    val funct7 = io.instruction(31, 25)

    val regFile = Module(new RegisterFile(conf.xlen)) // 32-bit register file

    // Decode Logic
    switch(opcode) {
      is("b0010011".U) { // I-type ALU Instructions
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_sext // Sign-extended immediate
        switch(funct3) {
          is("b000".U) { io.controlSignals.aluOp := 1.U } // ADDI
          is("b111".U) { io.controlSignals.aluOp := 3.U } // ANDI
          is("b110".U) { io.controlSignals.aluOp := 4.U } // ORI
          is("b100".U) { io.controlSignals.aluOp := 5.U } // XORI
          is("b001".U) { io.controlSignals.aluOp := 6.U } // SLLI
          is("b101".U) {
            when(funct7 === "b0000000".U) {
              io.controlSignals.aluOp := 7.U // SRLI
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 8.U // SRAI
            }
          }
          is("b010".U) { io.controlSignals.aluOp := 9.U } // SLTI
          is("b011".U) { io.controlSignals.aluOp := 10.U } // SLTIU
        }
      }
      is("b0000011".U) { // Load Instructions (e.g., LW)
        io.controlSignals.imm_flag  := true.B // Use immediate
        io.controlSignals.memRead := true.B // Memory Read
        io.controlSignals.memToReg := true.B // Write memory data to register
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate
      }
      is("b0100011".U) { // Store Instructions (e.g., SW)
        val imm11_5 = io.instruction(31, 25)
        val imm4_0 = io.instruction(11, 7)
        val imm_s = Cat(imm11_5, imm4_0)
        val imm_s_sext = Cat(Fill(20, imm_s(11)), imm_s)
        io.controlSignals.imm_flag  := true.B // Use immediate
        io.controlSignals.memWrite := true.B // Memory Write
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_s_sext // Sign-extended immediate
      }
      is("b1100011".U) { // Branch Instructions (e.g., BEQ)
        val imm12 = io.instruction(31)
        val imm10_5 = io.instruction(30, 25)
        val imm4_1 = io.instruction(11, 8)
        val imm11 = io.instruction(7)
        val imm_b = Cat(imm12, imm11, imm10_5, imm4_1, 0.U(1.W))
        val imm_b_sext = Cat(Fill(19, imm_b(12)), imm_b)
        io.controlSignals.branch := true.B
        io.controlSignals.aluOp := 2.U // SUB for comparison
        io.immediate := imm_b_sext // Sign-extended immediate
      }
      is("b1101111".U) { // JAL
        val imm20 = io.instruction(31)
        val imm10_1 = io.instruction(30, 21)
        val imm11 = io.instruction(20)
        val imm19_12 = io.instruction(19, 12)
        val imm_j = Cat(imm20, imm19_12, imm11, imm10_1, 0.U(1.W))
        val imm_j_sext = Cat(Fill(11, imm_j(20)), imm_j)
        io.controlSignals.jump := true.B
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_j_sext // Sign-extended immediate
      }
      is("b1100111".U) { // JALR
        io.controlSignals.jump := true.B
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate
      }
      is("b0110011".U) { // R-type Instructions (ALU + M-Ext)
        io.controlSignals.imm_flag := false.B // Use register
        io.controlSignals.regWrite := true.B // Write to register        
        switch(funct3) {
          // funct3 = 000: ADD, SUB, MUL
          is("b000".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 11.U // MUL
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 2.U  // SUB
            } .otherwise {
              io.controlSignals.aluOp := 1.U  // ADD
            }
          }
          
          // funct3 = 001: SLL, MULH
          is("b001".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 12.U // MULH
            } .otherwise {
              io.controlSignals.aluOp := 6.U  // SLL
            }
          }
          
          // funct3 = 010: SLT, MULHSU
          is("b010".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 13.U // MULHSU
            } .otherwise {
              io.controlSignals.aluOp := 9.U  // SLT
            }
          }
          
          // funct3 = 011: SLTU, MULHU
          is("b011".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 14.U // MULHU
            } .otherwise {
              io.controlSignals.aluOp := 10.U // SLTU
            }
          }
          
          // funct3 = 100: XOR, DIV
          is("b100".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 15.U // DIV
            } .otherwise {
              io.controlSignals.aluOp := 5.U  // XOR
            }
          }
          
          // funct3 = 101: SRL, SRA, DIVU
          is("b101".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 16.U // DIVU
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 8.U  // SRA
            } .otherwise {
              io.controlSignals.aluOp := 7.U  // SRL
            }
          }
          
          // funct3 = 110: OR, REM
          is("b110".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 17.U // REM
            } .otherwise {
              io.controlSignals.aluOp := 4.U  // OR
            }
          }
          
          // funct3 = 111: AND, REMU
          is("b111".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 18.U // REMU
            } .otherwise {
              io.controlSignals.aluOp := 3.U  // AND
            }
          }
        }
      }
      is("b0110111".U) { // LUI
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_upper // Upper immediate
        io.controlSignals.aluOp := 0.U // Pass-through
      }
      is


      
    }
    

    


    // Connect Register File
    regFile.io.opcode := 0.U // Default (nop)
    regFile.io.C := io.C
    regFile.io.readAddressA := io.instruction(19, 15) // rs1
    regFile.io.readAddressB := io.instruction(24, 20) // rs2
    regFile.io.writeEnable := io.writeEnable
    regFile.io.writeAddress := io.writeAddress
    io.A := regFile.io.A // rs1 data
    io.B := regFile.io.B // rs2 data

}

```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-gx9ioUoaQSSFVqCzaHt7ag== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
Options:
-language:reflectiveCalls -deprecation -feature -Xcheckinit -Ymacro-annotations -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.collection.generic.CommonErrors$.indexOutOfBounds(CommonErrors.scala:23)
	scala.collection.mutable.ArrayBuffer.apply(ArrayBuffer.scala:102)
	scala.reflect.internal.Types$Type.findMemberInternal$1(Types.scala:1030)
	scala.reflect.internal.Types$Type.findMember(Types.scala:1035)
	scala.reflect.internal.Types$Type.memberBasedOnName(Types.scala:661)
	scala.reflect.internal.Types$Type.member(Types.scala:625)
	scala.tools.nsc.typechecker.Contexts$SymbolLookup.nextDefinition$1(Contexts.scala:1393)
	scala.tools.nsc.typechecker.Contexts$SymbolLookup.apply(Contexts.scala:1467)
	scala.tools.nsc.typechecker.Contexts$Context.lookupSymbol(Contexts.scala:1233)
	scala.tools.nsc.typechecker.Typers$Typer.typedIdent$2(Typers.scala:5719)
	scala.tools.nsc.typechecker.Typers$Typer.typedIdentOrWildcard$1(Typers.scala:5813)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6286)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedSelectOrSuperCall$1(Typers.scala:6442)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6289)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedSelectOrSuperCall$1(Typers.scala:6442)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6289)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedSelectOrSuperCall$1(Typers.scala:6442)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6289)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typed1$41(Typers.scala:5262)
	scala.tools.nsc.typechecker.Typers$Typer.silent(Typers.scala:703)
	scala.tools.nsc.typechecker.Typers$Typer.normalTypedApply$1(Typers.scala:5264)
	scala.tools.nsc.typechecker.Typers$Typer.typedApply$1(Typers.scala:5296)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6288)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedStat$1(Typers.scala:6422)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$10(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedStats(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedBlock(Typers.scala:2643)
	scala.tools.nsc.typechecker.Typers$Typer.typedOutsidePatternMode$1(Typers.scala:6262)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6298)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedArg(Typers.scala:3565)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.typedArgWithFormal$1(PatternTypers.scala:134)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.$anonfun$typedArgsForFormals$4(PatternTypers.scala:150)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.typedArgsForFormals(PatternTypers.scala:150)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.typedArgsForFormals$(PatternTypers.scala:131)
	scala.tools.nsc.typechecker.Typers$Typer.typedArgsForFormals(Typers.scala:202)
	scala.tools.nsc.typechecker.Typers$Typer.handleMonomorphicCall$1(Typers.scala:3921)
	scala.tools.nsc.typechecker.Typers$Typer.doTypedApply(Typers.scala:3972)
	scala.tools.nsc.typechecker.Typers$Typer.normalTypedApply$1(Typers.scala:5285)
	scala.tools.nsc.typechecker.Typers$Typer.typedApply$1(Typers.scala:5296)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6288)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedStat$1(Typers.scala:6422)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$10(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedStats(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedBlock(Typers.scala:2643)
	scala.tools.nsc.typechecker.Typers$Typer.typedOutsidePatternMode$1(Typers.scala:6262)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6298)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Typers$Typer.typedArg(Typers.scala:3565)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.typedArgWithFormal$1(PatternTypers.scala:134)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.$anonfun$typedArgsForFormals$4(PatternTypers.scala:150)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.typedArgsForFormals(PatternTypers.scala:150)
	scala.tools.nsc.typechecker.PatternTypers$PatternTyper.typedArgsForFormals$(PatternTypers.scala:131)
	scala.tools.nsc.typechecker.Typers$Typer.typedArgsForFormals(Typers.scala:202)
	scala.tools.nsc.typechecker.Typers$Typer.handleMonomorphicCall$1(Typers.scala:3921)
	scala.tools.nsc.typechecker.Typers$Typer.doTypedApply(Typers.scala:3972)
	scala.tools.nsc.typechecker.Typers$Typer.normalTypedApply$1(Typers.scala:5285)
	scala.tools.nsc.typechecker.Typers$Typer.typedApply$1(Typers.scala:5296)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6288)
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
	scala.meta.internal.pc.Compat.$anonfun$runOutline$1(Compat.scala:74)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.meta.internal.pc.Compat.runOutline(Compat.scala:66)
	scala.meta.internal.pc.Compat.runOutline(Compat.scala:35)
	scala.meta.internal.pc.Compat.runOutline$(Compat.scala:33)
	scala.meta.internal.pc.MetalsGlobal.runOutline(MetalsGlobal.scala:39)
	scala.meta.internal.pc.ScalaCompilerWrapper.compiler(ScalaCompilerAccess.scala:18)
	scala.meta.internal.pc.ScalaCompilerWrapper.compiler(ScalaCompilerAccess.scala:13)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticTokens$1(ScalaPresentationCompiler.scala:206)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:148)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withInterruptableCompiler$1(CompilerAccess.scala:92)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
	java.base/java.lang.Thread.run(Thread.java:1474)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: -1 is out of bounds (min 0, max 2)