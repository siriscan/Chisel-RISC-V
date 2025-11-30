error id: 511AAB99E22287AC4D5D7C74426C8F2C
file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/build.sbt
### java.util.NoSuchElementException: head of empty String

occurred in the presentation compiler.



action parameters:
offset: 1511
uri: file:///C:/Users/irisc/Documents/CHISEL/Chisel-RISC-V/build.sbt
text:
```scala
import _root_.scala.xml.{TopScope=>$scope}
import _root_.sbt._
import _root_.sbt.Keys._
import _root_.sbt.nio.Keys._
import _root_.sbt.ScriptedPlugin.autoImport._, _root_.sbt.plugins.JUnitXmlReportPlugin.autoImport._, _root_.sbt.plugins.MiniDependencyTreePlugin.autoImport._, _root_.bloop.integrations.sbt.BloopPlugin.autoImport._
import _root_.sbt.plugins.IvyPlugin, _root_.sbt.plugins.JvmPlugin, _root_.sbt.plugins.CorePlugin, _root_.sbt.ScriptedPlugin, _root_.sbt.plugins.SbtPlugin, _root_.sbt.plugins.SemanticdbPlugin, _root_.sbt.plugins.JUnitXmlReportPlugin, _root_.sbt.plugins.Giter8TemplatePlugin, _root_.sbt.plugins.MiniDependencyTreePlugin, _root_.bloop.integrations.sbt.BloopPlugin
// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.16"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "com.github.siriscan"

val chiselVersion = "7.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "Chisel-RISC-V",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations",
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),

    testFrameworks += new TestFramework("org.scalatest.tools.Framework")
libraryDependencies @@+= "edu.berkeley.cs" %% "chiseltest" % "0.5.7" % "test"

  
  )

```


presentation compiler configuration:
Scala version: 2.12.18
Classpath:
<WORKSPACE>\project\.bloop\chisel-risc-v-build\bloop-bsp-clients-classes\classes-Metals-wGUZGGE5T_W-7sl9m2Ni1w== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.1\semanticdb-javac-0.11.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\sbt\1.9.7\sbt-1.9.7.jar [exists ], <HOME>\.sbt\boot\scala-2.12.18\lib\scala-library.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\ch\epfl\scala\sbt-bloop_2.12_1.0\2.0.17\sbt-bloop_2.12_1.0-2.0.17.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\main_2.12\1.9.7\main_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\io_2.12\1.9.7\io_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\ch\epfl\scala\bloop-config_2.12\2.3.3\bloop-config_2.12-2.3.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\logic_2.12\1.9.7\logic_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\actions_2.12\1.9.7\actions_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\main-settings_2.12\1.9.7\main-settings_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\run_2.12\1.9.7\run_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\command_2.12\1.9.7\command_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\collections_2.12\1.9.7\collections_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\scripted-plugin_2.12\1.9.7\scripted-plugin_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-lm-integration_2.12\1.9.7\zinc-lm-integration_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-logging_2.12\1.9.7\util-logging_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.12\2.2.0\scala-xml_2.12-2.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\launcher-interface\1.4.2\launcher-interface-1.4.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\ben-manes\caffeine\caffeine\2.8.5\caffeine-2.8.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\get-coursier\lm-coursier-shaded_2.12\2.1.2\lm-coursier-shaded_2.12-2.1.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\logging\log4j\log4j-api\2.17.1\log4j-api-2.17.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\logging\log4j\log4j-core\2.17.1\log4j-core-2.17.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\logging\log4j\log4j-slf4j-impl\2.17.1\log4j-slf4j-impl-2.17.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\librarymanagement-core_2.12\1.9.3\librarymanagement-core_2.12-1.9.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\librarymanagement-ivy_2.12\1.9.3\librarymanagement-ivy_2.12-1.9.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\compiler-interface\1.9.5\compiler-interface-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-compile_2.12\1.9.5\zinc-compile_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\swoval\file-tree-views\2.1.12\file-tree-views-2.1.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\net\java\dev\jna\jna\5.13.0\jna-5.13.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\net\java\dev\jna\jna-platform\5.13.0\jna-platform-5.13.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\plokhotnyuk\jsoniter-scala\jsoniter-scala-core_2.12\2.13.5.2\jsoniter-scala-core_2.12-2.13.5.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\unroll-annotation_2.12\0.1.12\unroll-annotation_2.12-0.1.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-relation_2.12\1.9.7\util-relation_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\completion_2.12\1.9.7\completion_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\task-system_2.12\1.9.7\task-system_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\tasks_2.12\1.9.7\tasks_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\testing_2.12\1.9.7\testing_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-tracking_2.12\1.9.7\util-tracking_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\sjson-new-scalajson_2.12\0.9.1\sjson-new-scalajson_2.12-0.9.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-terminal\3.19.0\jline-terminal-3.19.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-classpath_2.12\1.9.5\zinc-classpath_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-apiinfo_2.12\1.9.5\zinc-apiinfo_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc_2.12\1.9.5\zinc_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\core-macros_2.12\1.9.7\core-macros_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-cache_2.12\1.9.7\util-cache_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-control_2.12\1.9.7\util-control_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\protocol_2.12\1.9.7\protocol_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\sjson-new-core_2.12\0.9.1\sjson-new-core_2.12-0.9.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\template-resolver\0.1\template-resolver-0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-position_2.12\1.9.7\util-position_2.12-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-compile-core_2.12\1.9.5\zinc-compile-core_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-interface\1.9.7\util-interface-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\jline\jline\2.14.7-sbt-a1b0ffbb8f64bb820f4f84a0c07a0c0964507493\jline-2.14.7-sbt-a1b0ffbb8f64bb820f4f84a0c07a0c0964507493.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-terminal-jna\3.19.0\jline-terminal-jna-3.19.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-terminal-jansi\3.19.0\jline-terminal-jansi-3.19.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lmax\disruptor\3.4.2\disruptor-3.4.2.jar [exists ], <HOME>\.sbt\boot\scala-2.12.18\lib\scala-reflect.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\checkerframework\checker-qual\3.4.1\checker-qual-3.4.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\google\errorprone\error_prone_annotations\2.4.0\error_prone_annotations-2.4.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.12\2.10.0\scala-collection-compat_2.12-2.10.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar [exists ], <HOME>\.sbt\boot\scala-2.12.18\lib\scala-compiler.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\jcraft\jsch\0.1.54\jsch-0.1.54.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\gigahorse-apache-http_2.12\0.7.0\gigahorse-apache-http_2.12-0.7.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\ivy\ivy\2.3.0-sbt-396a783bba347016e7fe30dacc60d355be607fe2\ivy-2.3.0-sbt-396a783bba347016e7fe30dacc60d355be607fe2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-reader\3.19.0\jline-reader-3.19.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-builtins\3.19.0\jline-builtins-3.19.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\test-agent\1.9.7\test-agent-1.9.7.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\test-interface\1.0\test-interface-1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\shaded-jawn-parser_2.12\0.9.1\shaded-jawn-parser_2.12-0.9.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\shaded-scalajson_2.12\1.0.0-M4\shaded-scalajson_2.12-1.0.0-M4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\compiler-bridge_2.12\1.9.5\compiler-bridge_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-classfile_2.12\1.9.5\zinc-classfile_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-core_2.12\1.9.5\zinc-core_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-persist_2.12\1.9.5\zinc-persist_2.12-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\sjson-new-murmurhash_2.12\0.9.1\sjson-new-murmurhash_2.12-0.9.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\ipcsocket\ipcsocket\1.6.2\ipcsocket-1.6.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-parser-combinators_2.12\1.1.2\scala-parser-combinators_2.12-1.1.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\net\openhft\zero-allocation-hashing\0.10.1\zero-allocation-hashing-0.10.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\fusesource\jansi\jansi\2.1.0\jansi-2.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\gigahorse-core_2.12\0.7.0\gigahorse-core_2.12-0.7.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\shaded-apache-httpasyncclient\0.7.0\shaded-apache-httpasyncclient-0.7.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-style\3.19.0\jline-style-3.19.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-persist-core-assembly\1.9.5\zinc-persist-core-assembly-1.9.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\sbinary_2.12\0.5.1\sbinary_2.12-0.5.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\ssl-config-core_2.12\0.6.1\ssl-config-core_2.12-0.6.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\reactivestreams\reactive-streams\1.0.3\reactive-streams-1.0.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\config\1.4.2\config-1.4.2.jar [exists ]
Options:
-deprecation -Wconf:cat=unused-nowarn:s -Wconf:cat=unused-nowarn:s -Xsource:3 -Yrangepos -Xplugin-require:semanticdb




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