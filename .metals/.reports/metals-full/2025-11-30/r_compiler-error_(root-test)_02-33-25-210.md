error id: 511AAB99E22287AC4D5D7C74426C8F2C
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/test/scala/integer/RiscVPipelineTest.scala
### java.util.NoSuchElementException: head of empty String

occurred in the presentation compiler.



action parameters:
offset: 146
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/src/test/scala/integer/RiscVPipelineTest.scala
text:
```scala
package integer

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RiscVPipelineTest extends AnyFlatSpec with @@ChiselScalatestTester {
  behavior of "RISC-V 5-Stage Pipeline"

  it should "execute a program for a fixed number of cycles" in {
    // 1. Instantiate the DUT (Device Under Test)
    // We add the WriteVcdAnnotation to generate a waveform for debugging
    test(new RiscVPipeline).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      
      // 2. Simulation Loop
      // Run the processor for 100 clock cycles
      for (cycle <- 0 until 100) {
        dut.clock.step()

        // 3. Monitor Output
        // Peek at the debug output (connected to Writeback stage in previous steps)
        // .peekInt() converts the Chisel hardware value to a Scala integer
        val wbValue = dut.io.result.peekInt()
        
        // Print the state every cycle to the console
        println(s"Cycle $cycle: Writeback Data = $wbValue")
      }
    }
  }
}
```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\test-classes-Metals-3NLGKwyMTh-LUzAOrEi1_A== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar [exists ], <WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-3NLGKwyMTh-LUzAOrEi1_A== [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\chisel_2.13\7.0.0\chisel_2.13-7.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest_2.13\3.2.19\scalatest_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\scopt\scopt_2.13\4.1.0\scopt_2.13-4.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-text\1.13.1\commons-text-1.13.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\os-lib_2.13\0.10.7\os-lib_2.13-0.10.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native_2.13\4.0.7\json4s-native_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\github\alexarchambault\data-class_2.13\0.2.7\data-class_2.13-0.2.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle_2.13\3.3.1\upickle_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\chipsalliance\firtool-resolver_2.13\2.0.1\firtool-resolver_2.13-2.0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-core_2.13\3.2.19\scalatest-core_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-featurespec_2.13\3.2.19\scalatest-featurespec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-flatspec_2.13\3.2.19\scalatest-flatspec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-freespec_2.13\3.2.19\scalatest-freespec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-funsuite_2.13\3.2.19\scalatest-funsuite_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-funspec_2.13\3.2.19\scalatest-funspec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-propspec_2.13\3.2.19\scalatest-propspec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-refspec_2.13\3.2.19\scalatest-refspec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-wordspec_2.13\3.2.19\scalatest-wordspec_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-diagrams_2.13\3.2.19\scalatest-diagrams_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-matchers-core_2.13\3.2.19\scalatest-matchers-core_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-shouldmatchers_2.13\3.2.19\scalatest-shouldmatchers_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-mustmatchers_2.13\3.2.19\scalatest-mustmatchers_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\geny_2.13\1.1.1\geny_2.13-1.1.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-core_2.13\4.0.7\json4s-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-native-core_2.13\4.0.7\json4s-native-core_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\ujson_2.13\3.3.1\ujson_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upack_2.13\3.3.1\upack_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-implicits_2.13\3.3.1\upickle-implicits_2.13-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.13\2.2.0\scala-xml_2.13-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.11.0\scala-collection-compat_2.13-2.11.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalatest\scalatest-compatible\3.2.19\scalatest-compatible-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalactic\scalactic_2.13\3.2.19\scalactic_2.13-3.2.19.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-ast_2.13\4.0.7\json4s-ast_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\json4s\json4s-scalap_2.13\4.0.7\json4s-scalap_2.13-4.0.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\thoughtworks\paranamer\paranamer\2.8\paranamer-2.8.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\upickle-core_2.13\3.3.1\upickle-core_2.13-3.3.1.jar [exists ]
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