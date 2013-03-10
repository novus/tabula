import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object Versions {
  val ScalaVersion = "2.10.0"
  val ScalaTimeVersion = "0.6"
  val NScalaTimeVersion = "0.2.0"
  val ShapelessVersion = "1.2.4"
  val SpecsVersion = "1.6.9"
  val PoiVersion = "3.7"
  val LiftVersion = "2.4"
}

object BuildSettings {
  import Versions._

  def prompt(state: State) =
    "[%s]> ".format(Project.extract(state).currentProject.id)

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "tabula",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq("-deprecation",  "-unchecked", "-feature", "-language:implicitConversions", "-language:reflectiveCalls"),
    shellPrompt := prompt,
    showTiming := true,
    parallelExecution := true,
    parallelExecution in Test := false,
    testFrameworks += TestFrameworks.Specs,
    libraryDependencies += Deps.specs,
    resolvers ++= Resolvers.All,
    offline := false,
    initialCommands in console := """
    import tabula._
    import Tabula._
    import tabula.test._
    import shapeless._
    import shapeless.HList._
    """
  ) ++ scalariformSettings ++ formatSettings

  lazy val formatSettings = Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  lazy val formattingPreferences = {
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
    ScalaToolsSnapshots,
    "Novus Releases" at "http://repo.novus.com/releases",
    "Novus Snapshots" at "http://repo.novus.com/snapshots",
    "Coda Hale's repo" at "http://repo.codahale.com",
    "localRels" at "file:/home/max/work/repo/releases",
    "localSnaps" at "file:/home/max/work/repo/snapshots")
}

object Deps {
  import Versions._

  val nscala_time = "com.github.nscala-time" %% "nscala-time" % NScalaTimeVersion
  val specs = "org.scala-tools.testing" %% "specs" % SpecsVersion % "test"
  val commons_lang = "org.apache.commons" % "commons-lang3" % "3.1" % "test"
  val poi = "org.apache.poi" % "poi" % PoiVersion
  val lift_json = "net.liftweb" %% "lift-json" % LiftVersion
  val shapeless = "com.chuusai" %% "shapeless" % ShapelessVersion

  val TabulaDeps = Seq(nscala_time, specs, commons_lang, poi, shapeless)
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
