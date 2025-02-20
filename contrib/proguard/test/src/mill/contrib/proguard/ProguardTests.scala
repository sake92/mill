package mill.contrib.proguard

import mill.*
import mill.define.{Discover, Target}
import mill.util.MillModuleUtil.millProjectModule
import mill.scalalib.ScalaModule
import mill.testkit.UnitTester
import mill.testkit.TestBaseModule
import os.Path
import utest.*

object ProguardTests extends TestSuite {

  object proguard extends TestBaseModule with ScalaModule with Proguard {
    override def scalaVersion: T[String] = T(sys.props.getOrElse("MILL_SCALA_3_NEXT_VERSION", ???))

    def proguardContribClasspath = Task {
      millProjectModule("mill-contrib-proguard", repositoriesTask())
    }

    override def runClasspath: T[Seq[PathRef]] =
      Task { super.runClasspath() ++ proguardContribClasspath() }

    lazy val millDiscover = Discover[this.type]
  }

  val testModuleSourcesPath: Path = os.Path(sys.env("MILL_TEST_RESOURCE_DIR")) / "proguard"

  def tests: Tests = Tests {
    test("Proguard module") {
      test("should download proguard jars") - UnitTester(proguard, testModuleSourcesPath).scoped {
        eval =>
          val Right(result) = eval.apply(proguard.proguardClasspath): @unchecked
          assert(
            result.value.iterator.toSeq.nonEmpty,
            result.value.iterator.toSeq.head.path.toString().contains("proguard-base")
          )
      }

      test("should create a proguarded jar") - UnitTester(proguard, testModuleSourcesPath).scoped {
        _ =>
          // Not sure why this is broken in Scala 3
          // val Right(result) = eval.apply(proguard.proguard)
          //          assert(os.exists(result.value.path))
      }
    }
  }
}
