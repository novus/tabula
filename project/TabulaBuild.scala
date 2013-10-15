import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object Versions {
  val ScalaVersion = "2.10.3"
  val ScalaTimeVersion = "0.6"
  val JodaTimeVersion = "2.3"
  val JodaConvertVersion = "1.2"
  val ShapelessVersion = "1.2.4"
  val SpecsVersion = "2.2"
  val PoiVersion = "3.9"
  val Json4sVersion = "3.2.5"
  val CommonsLangVersion = "3.1"
}

object BuildSettings {
  import Versions._

  def prompt(state: State) =
    "[%s]> ".format(Project.extract(state).currentProject.id)

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.bumnetworks",
    version := "0.0.6-SNAPSHOT",
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq("-deprecation",  "-unchecked", "-feature", "-language:implicitConversions", "-language:reflectiveCalls"),
    shellPrompt := prompt,
    showTiming := true,
    parallelExecution := true,
    parallelExecution in Test := false,
    testFrameworks += TestFrameworks.Specs,
    libraryDependencies += Deps.specs,
    offline := false,
    initialCommands in console in Test := """
    import tabula._
    import Tabula._
    import tabula.test._
    import shapeless._
    import shapeless.HList._
    """) ++ scalariformSettings ++ formatSettings

  lazy val publishSettings = Seq(
    publishTo <<= (version) {
      v =>
      val repo = file(".") / ".." / "repo"
      Some(Resolver.file("repo",
        if (v.trim.endsWith("SNAPSHOT")) repo / "snapshots"
        else repo / "releases"))
    }
  )

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
    setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true).
    setPreference(PreserveSpaceBeforeArguments, false).
    setPreference(PreserveDanglingCloseParenthesis, false).
    setPreference(RewriteArrowSymbols, false).
    setPreference(SpaceBeforeColon, false).
    setPreference(SpaceInsideBrackets, false).
    setPreference(SpacesWithinPatternBinders, true)
  }
}

object Deps {
  import Versions._

  val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.3"
  val joda_time = "joda-time" % "joda-time" % JodaTimeVersion
  val joda_convert = "org.joda" % "joda-convert" % JodaConvertVersion
  val specs = "org.specs2" %% "specs2" % SpecsVersion % "test"
  val commons_lang = "org.apache.commons" % "commons-lang3" % CommonsLangVersion % "test"
  val poi = "org.apache.poi" % "poi" % PoiVersion
  val json4s = "org.json4s" %% "json4s-native" % Json4sVersion
  val shapeless = "com.chuusai" %% "shapeless" % ShapelessVersion

  val CoreDeps = Seq(scalaz, joda_time, joda_convert, specs, commons_lang, shapeless)
  val JsonDeps = Seq(json4s)
  val ExcelDeps = Seq(poi)
}

object TabulaBuild extends Build {
  import BuildSettings._
  import Deps._

  lazy val root = Project(
    id = "tabula", base = file("."),
    settings = buildSettings ++ Seq(publish := {})
  ) aggregate(core, json, excel)

  lazy val core = Project(
    id = "tabula-core", base = file("core"),
    settings = buildSettings ++ publishSettings ++ Seq(libraryDependencies ++= CoreDeps)
  )

  lazy val json = Project(
    id = "tabula-json", base = file("json"),
    settings = buildSettings ++ publishSettings ++ Seq(libraryDependencies ++= JsonDeps)
  ) dependsOn(core % "compile->test")

  lazy val excel = Project(
    id = "tabula-excel", base = file("excel"),
    settings = buildSettings ++ publishSettings ++ Seq(libraryDependencies ++= ExcelDeps)
  ) dependsOn(core % "compile->test")
}
