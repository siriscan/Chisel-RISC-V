error id: 578FBC08B2C390FF8F8ADBE6E6F88635
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
### java.lang.StringIndexOutOfBoundsException: Range [11025, 11025 + -4) out of bounds for length 11464

occurred in the presentation compiler.



action parameters:
offset: 11025
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/main/scala/integer/DecodeStage.scala
text:
```scala
package integer

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import core._

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

    // Outputs to Fetch Stage for Branch/Jump Prediction
    val predictedTarget = Output(UInt(conf.xlen.W))
    val takeBranch = Output(Bool()) 

  })
    // Default values
    io.controlSignals := 0.U.asTypeOf(new ControlSignals)
    io.immediate := 0.U
    io.controlSignals.imm_flag := false.B
    io.pcOut := io.pc
    io.controlSignals.aluOp := 0.U // NOP
    io.controlSignals.branch := false.B // Default no branch
    io.controlSignals.branchOp := 0.U // Default branch funct3 (None)
    io.controlSignals.memOp := 0.U // Default funct3 for memory operations (None)
    io.controlSignals.jump := 0.U // Default no jump
    io.controlSignals.memRead := false.B // Default no memory read
    io.controlSignals.memWrite := false.B  // Default no memory write
    io.controlSignals.memToReg := false.B  // Default ALU to Reg
    io.controlSignals.regWrite := false.B  // Default no register write
    io.controlSignals.lui := 0.U // Default no LUI/AUIPC
    io.controlSignals.isSigned := false.B // Default unsigned operations

    io.predictedTarget := 0.U
    io.takeBranch := false.B


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
          is("b010".U) {
            io.controlSignals.isSigned := true.B // Signed comparison
            io.controlSignals.aluOp := 9.U // SLTI
          } 
          is("b011".U) { io.controlSignals.aluOp := 9.U } // SLTIU
        }
      }
      is("b0000011".U) { // Load Instructions (e.g., LW)
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.memRead := true.B // Memory Read
        io.controlSignals.memToReg := true.B // Write memory data to register
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate
        io.controlSignals.memOp := funct3 // Pass funct3 for memory operations

        switch(funct3) { // Not used in Decode, but decoded here for completeness. Only used in Writeback Stage for load size
          is("b000".U) { /* LB */ }
          is("b001".U) { /* LH */ }
          is("b010".U) { /* LW */ }
          is("b100".U) { /* LBU */ }
          is("b101".U) { /* LHU */ }
        }
      }
      is("b0100011".U) { // Store Instructions (e.g., SW)
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.memWrite := true.B // Memory Write
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        val imm11_5 = io.instruction(31, 25)
        val imm4_0 = io.instruction(11, 7)
        val imm_s = Cat(imm11_5, imm4_0)
        val imm_s_sext = Cat(Fill(20, imm_s(11)), imm_s)
        io.immediate := imm_s_sext // Sign-extended immediate
        io.controlSignals.memOp := funct3 // Pass funct3 for memory operations

        switch(funct3) { // Not used in Decode, but decoded here for completeness. Only used in Memory Stage for store size
          is("b000".U) { /* SB */ }
          is("b001".U) { /* SH */ }
          is("b010".U) { /* SW */ }
        }

      }
      is("b1100011".U) { // Branch Instructions (e.g., BEQ)
        val imm12 = io.instruction(31)
        val imm10_5 = io.instruction(30, 25)
        val imm4_1 = io.instruction(11, 8)
        val imm11 = io.instruction(7)
        val imm_b = Cat(imm12, imm11, imm10_5, imm4_1, 0.U(1.W))
        val imm_b_sext = Cat(Fill(19, imm_b(12)), imm_b) // Sign-extended immediate

        // Decode branch comparison function
        switch(funct3) { // Not used in Decode, but decoded here for completeness. Only used in Execute Stage for branch comparison
          is("b000".U) { /* BEQ */ }
          is("b001".U) { /* BNE */ }
          is("b100".U) { /* BLT */ io.controlSignals.isSigned := true.B }
          is("b101".U) { /* BGE */ io.controlSignals.isSigned := true.B }
          is("b110".U) { /* BLTU */ }
          is("b111".U) { /* BGEU */ }
        }
        io.controlSignals.branchOp := funct3 // Pass funct3 for branch type
        io.controlSignals.branch := true.B // This is a branch instruction
        io.immediate := imm_b_sext // Sign-extended immediate
      }
      is("b1101111".U) { // JAL
        val imm20 = io.instruction(31)
        val imm10_1 = io.instruction(30, 21)
        val imm11 = io.instruction(20)
        val imm19_12 = io.instruction(19, 12)
        val imm_j = Cat(imm20, imm19_12, imm11, imm10_1, 0.U(1.W)) // J-type immediate
        val imm_j_sext = Cat(Fill(11, imm20), imm_j) // Sign-extended immediate
        io.controlSignals.jump := 1.U // JAL
        io.controlSignals.regWrite := true.B // Write to register

        io.immediate := imm_j_sext // Sign-extended immediate

      }
      is("b1100111".U) { // JALR
        io.controlSignals.jump := 2.U // JALR
        io.controlSignals.regWrite := true.B // Write to register
        io.controlSignals.imm_flag := true.B // Use immediate
        io.controlSignals.aluOp := 1.U // ADD for address calculation
        io.immediate := imm_sext // Sign-extended immediate
        

      }
      is("b0110011".U) { // R-type Instructions (ALU + Multiply and Divide)
        io.controlSignals.imm_flag := false.B // Use register
        io.controlSignals.regWrite := true.B // Write to register        
        switch(funct3) {
          // funct3 = 000: ADD, SUB, MUL
          is("b000".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 10.U // MUL
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 2.U  // SUB
            } .otherwise {
              io.controlSignals.aluOp := 1.U  // ADD
            }
          }
          
          // funct3 = 001: SLL, MULH
          is("b001".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.isSigned := true.B // Signed multiply high
              io.controlSignals.aluOp := 11.U // MULH
            } .otherwise {
              io.controlSignals.aluOp := 6.U  // SLL
            }
          }
          
          // funct3 = 010: SLT, MULHSU
          is("b010".U) {
            when(funct7 === "b0000001".U) {  
              io.controlSignals.aluOp := 12.U // MULHSU
            } .otherwise {
              io.controlSignals.isSigned := true.B // Signed comparison
              io.controlSignals.aluOp := 9.U  // SLT
            }
          }
          
          // funct3 = 011: SLTU, MULHU
          is("b011".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 11.U // MULHU
            } .otherwise {
              
              io.controlSignals.aluOp := 9.U // SLTU
            }
          }
          
          // funct3 = 100: XOR, DIV
          is("b100".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.isSigned := true.B // Signed division
              io.controlSignals.aluOp := 13.U // DIV
            } .otherwise {
              io.controlSignals.aluOp := 5.U  // XOR
            }
          }
          
          // funct3 = 101: SRL, SRA, DIVU
          is("b101".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 13.U // DIVU
            } .elsewhen(funct7 === "b0100000".U) {
              io.controlSignals.aluOp := 8.U  // SRA
            } .otherwise {
              io.controlSignals.aluOp := 7.U  // SRL
            }
          }
          
          // funct3 = 110: OR, REM
          is("b110".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.isSigned := true.B // Signed remainder
              io.controlSignals.aluOp := 14.U // REM
            } .otherwise {
              io.controlSignals.aluOp := 4.U  // OR
            }
          }
          
          // funct3 = 111: AND, REMU
          is("b111".U) {
            when(funct7 === "b0000001".U) {
              io.controlSignals.aluOp := 14.U // REMU
            } .otherwise {
              io.controlSignals.aluOp := 3.U  // AND
            }
          }
        }
      }
      is("b0110111".U) { // LUI
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_upper // Upper immediate
        io.controlSignals.aluOp := 1.U // ADD (0 + immediate)
        io.controlSignals.imm_flag := true.B
        io.controlSignals.lui := 1.U
      }
      is("b0010111".U) { // AUIPC
        io.controlSignals.regWrite := true.B // Write to register
        io.immediate := imm_upper // Upper immediate
        io.controlSignals.imm_flag := true.B
        io.controlSignals.aluOp := 1.U // ADD (pc + immediate)
        io.controlSignals.lui := 2.U
      }

      is()@@

      
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
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-KgcKqCcLQk-0f5ebZcfqHg== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.2\semanticdb-javac-0.11.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
Options:
-language:reflectiveCalls -deprecation -feature -Xcheckinit -Ymacro-annotations -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
java.base/jdk.internal.util.Preconditions$1.apply(Preconditions.java:55)
	java.base/jdk.internal.util.Preconditions$1.apply(Preconditions.java:52)
	java.base/jdk.internal.util.Preconditions$4.apply(Preconditions.java:213)
	java.base/jdk.internal.util.Preconditions$4.apply(Preconditions.java:210)
	java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:98)
	java.base/jdk.internal.util.Preconditions.outOfBoundsCheckFromIndexSize(Preconditions.java:118)
	java.base/jdk.internal.util.Preconditions.checkFromIndexSize(Preconditions.java:397)
	java.base/java.lang.String.checkBoundsOffCount(String.java:4925)
	java.base/java.lang.String.rangeCheck(String.java:318)
	java.base/java.lang.String.<init>(String.java:314)
	scala.tools.nsc.interactive.Global.typeCompletions$1(Global.scala:1245)
	scala.tools.nsc.interactive.Global.completionsAt(Global.scala:1283)
	scala.meta.internal.pc.SignatureHelpProvider.$anonfun$treeSymbol$1(SignatureHelpProvider.scala:462)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.pc.SignatureHelpProvider.treeSymbol(SignatureHelpProvider.scala:460)
	scala.meta.internal.pc.SignatureHelpProvider$MethodCall$.unapply(SignatureHelpProvider.scala:255)
	scala.meta.internal.pc.SignatureHelpProvider$MethodCallTraverser.visit(SignatureHelpProvider.scala:366)
	scala.meta.internal.pc.SignatureHelpProvider$MethodCallTraverser.traverse(SignatureHelpProvider.scala:360)
	scala.meta.internal.pc.SignatureHelpProvider$MethodCallTraverser.fromTree(SignatureHelpProvider.scala:329)
	scala.meta.internal.pc.SignatureHelpProvider.$anonfun$signatureHelp$3(SignatureHelpProvider.scala:33)
	scala.Option.flatMap(Option.scala:283)
	scala.meta.internal.pc.SignatureHelpProvider.$anonfun$signatureHelp$2(SignatureHelpProvider.scala:31)
	scala.Option.flatMap(Option.scala:283)
	scala.meta.internal.pc.SignatureHelpProvider.signatureHelp(SignatureHelpProvider.scala:29)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$signatureHelp$1(ScalaPresentationCompiler.scala:434)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:148)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withNonInterruptableCompiler$1(CompilerAccess.scala:132)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
	java.base/java.lang.Thread.run(Thread.java:1474)
```
#### Short summary: 

java.lang.StringIndexOutOfBoundsException: Range [11025, 11025 + -4) out of bounds for length 11464