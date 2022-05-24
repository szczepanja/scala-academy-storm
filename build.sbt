import sbtbuildinfo.BuildInfoKey.action
import scala.util.Try
import Libs._

val repositoryResolvers = Seq(
  "twitter-repo" at "https://repo.clojars.org/"
)

val scalafixConfig = Seq(
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

val scalaCOpts = Seq(
  "-Ywarn-unused:imports",
  "-language:postfixOps"
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    action("lastCommitHash") {
      import scala.sys.process._
      // if the build is done outside of a git repository, we still want it to succeed
      Try("git rev-parse HEAD".!!.trim).getOrElse("?")
    }
  ),
  buildInfoOptions += BuildInfoOption.ToJson,
  buildInfoOptions += BuildInfoOption.ToMap,
  buildInfoPackage := "com.adform.scalaacademy.version",
  buildInfoObject := "BuildInfo"
)

lazy val rootProject = (project in file("."))
  .settings(
    name := "scala-academy-storm",
    scalaVersion := "2.13.8",
    libraryDependencies ++= allDeps,
    scalacOptions ++= scalaCOpts,
    resolvers ++= repositoryResolvers,
    testForkedParallel := false,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF", "-eF")
  )
  .settings(scalafixConfig)
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings)
