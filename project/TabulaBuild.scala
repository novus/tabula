import sbt._
import Keys._
import ScalariformPlugin.{format, formatPreferences}

object Versions {
  val ScalaVersion = "2.8.1"
  val PoiVersion = "3.7"
  val SpecsVersion = "1.6.7"
  val ScalaTimeVersion = "0.4"
}

object BuildSettings {
  import Versions._

  lazy val buildSettings = Defaults.defaultSettings ++ formatSettings ++ Seq(
    organization := "tabula",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq("-deprecation",  "-unchecked"),
    publishTo <<= version {
      case v: String =>
        Some(Resolver.file("repo", file("../repo/%s".format({ if (v.endsWith("-SNAPSHOT")) "snapshots" else "releases" }))))
    },
    showTiming := true,
    parallelExecution := true,
    credentials += Credentials(Path.userHome / ".ivy2" / ".novus_nexus"),
    ivyXML := ivyXml,
    parallelExecution in Test := false,
    testFrameworks += TestFrameworks.Specs,
    libraryDependencies += Deps.specs,
    resolvers ++= Seq(Resolvers.novusRels, Resolvers.novusSnaps, Resolvers.akkaRepo),
    offline := true
  )

  lazy val formatSettings = ScalariformPlugin.settings ++ Seq(
    formatPreferences in Compile := formattingPreferences,
    formatPreferences in Test    := formattingPreferences
  )

  lazy val formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences().setPreference(AlignParameters, true).
      setPreference(AlignSingleLineCaseStatements, true).
      setPreference(CompactControlReadability, true). // waiting for CCR patch to go mainstream, patiently patiently
      setPreference(CompactStringConcatenation, true).
      setPreference(DoubleIndentClassDeclaration, true).
      setPreference(FormatXml, true).
      setPreference(IndentLocalDefs, true).
      setPreference(IndentPackageBlocks, true).
      setPreference(IndentSpaces, 2).
      setPreference(MultilineScaladocCommentsStartOnFirstLine, true).
      setPreference(PreserveSpaceBeforeArguments, false).
      setPreference(PreserveDanglingCloseParenthesis, false).
      setPreference(RewriteArrowSymbols, false).
      setPreference(SpaceBeforeColon, false).
      setPreference(SpaceInsideBrackets, false).
      setPreference(SpacesWithinPatternBinders, true)
  }

  lazy val ivyXml = {
    <dependencies>
    <exclude org="se.scalablesolutions.akka" module="akka-persistence"/>
    <exclude org="se.scalablesolutions.akka" module="akka-spring"/>
    <exclude org="se.scalablesolutions.akka" module="akka-amqp"/>
    <exclude org="org.guiceyfruit" module="*"/>
    </dependencies>
  }
}

object Resolvers {
  val akkaRepo = "Akka Repo" at "http://akka.io/repository"
  val novusRels = "Novus Releases" at "http://repo.novus.com/releases"
  val novusSnaps = "Novus Snapshots" at "http://repo.novus.com/snapshots"
}

object Deps {
  import Versions._

  val time = "org.scala-tools.time" %% "time" % ScalaTimeVersion
  val poi = "org.apache.poi" % "poi" % PoiVersion
  val specs = "org.scala-tools.testing" %% "specs" % SpecsVersion % "test"
}

object ProjectDeps {
  import Deps._

  val deps = Seq(time, poi, specs)
}

object TabulaBuild extends Build {
  import BuildSettings._
  import ProjectDeps._

  lazy val util = Project(
    "tabula", file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= deps)
  )
}
