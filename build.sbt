name := "tabula"

version := "0.0.1-SNAPSHOT"

organization := "tabula"

libraryDependencies ++= Seq(
  "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
  "com.mongodb.casbah" %% "casbah-commons" % "2.1.5-1",
  "com.mongodb.casbah" %% "casbah-core" % "2.1.5-1",
  "com.mongodb.casbah" %% "casbah-query" % "2.1.5-1",
  "org.mongodb" % "mongo-java-driver" % "2.6.3",
  "org.scala-tools.time" %% "time" % "0.4",
  "org.apache.poi" % "poi" % "3.7",
  "org.scala-tools.testing" %% "specs" % "1.6.7" % "test"
)

scalacOptions ++= "-deprecation" :: "-unchecked" :: Nil

scalaVersion := "2.8.1"

resolvers ++= Seq(
  "Novus Release Repository" at "http://repo.novus.com/releases/",
  "Novus Snapshots Repository" at "http://repo.novus.com/snapshots/"
)

publishTo <<= version {
  case v: String =>
    Some(Resolver.file("repo", file("../repo/%s".format({ if (v.endsWith("-SNAPSHOT")) "snapshots" else "releases" }))))
}

showTiming := true

parallelExecution := true

offline := true
