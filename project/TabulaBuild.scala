import sbt._
import Keys._
import com.typesafe.sbtscalariform.ScalariformPlugin
import com.typesafe.sbtscalariform.ScalariformPlugin.ScalariformKeys

object Versions {
  val ScalaVersion = "2.9.1"
  val ScalaTimeVersion = "0.5"
  val SpecsVersion = "1.6.9"
  val PoiVersion = "3.7"
  val LiftVersion = "2.4"
}

object BuildSettings {
  import Versions._

  def prompt(state: State) =
    "[%s]> ".format(Project.extract(state).currentProject.id)

  lazy val buildSettings = Defaults.defaultSettings ++ formatSettings ++ Seq(
    organization := "tabula",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq("-deprecation",  "-unchecked", "-Xexperimental"),
    shellPrompt := prompt,
    showTiming := true,
    parallelExecution := true,
    parallelExecution in Test := false,
    testFrameworks += TestFrameworks.Specs,
    libraryDependencies += Deps.specs,
    resolvers ++= Resolvers.All,
    offline := false
  )

  lazy val formatSettings = ScalariformPlugin.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  lazy val formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences().
    setPreference(AlignParameters, true).
    setPreference(AlignSingleLineCaseStatements, true).
    setPreference(CompactControlReadability, true).
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
}

object Resolvers {
  val All = Seq(
    "Novus Releases" at "http://repo.novus.com/releases",
    "Novus Snapshots" at "http://repo.novus.com/snapshots",
    "Coda Hale's repo" at "http://repo.codahale.com",
    "localRels" at "file:/home/max/work/repo/releases",
    "localSnaps" at "file:/home/max/work/repo/snapshots")
}

object Deps {
  import Versions._

  val time = "org.scala-tools.time" %% "time" % ScalaTimeVersion
  val specs = "org.scala-tools.testing" %% "specs" % SpecsVersion % "test"
  val poi = "org.apache.poi" % "poi" % PoiVersion
  val lift_json = "net.liftweb" %% "lift-json" % LiftVersion

  val TabulaDeps = Seq(time, specs, poi)
  val JsonDeps = Seq(lift_json)
}

object TabulaBuild extends Build {
  import BuildSettings._
  import Deps._

  lazy val tabula = Project(
    id = "tabula", base = file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= TabulaDeps)
  )
  lazy val json = Project(
    id = "json", base = file("json"),
    settings = buildSettings ++ Seq(libraryDependencies ++= (TabulaDeps ++ JsonDeps))
  ) dependsOn(tabula % "compile->test")
}
