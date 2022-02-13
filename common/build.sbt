lazy val commonSettings = Seq(
  organization := "edu.berkeley.cs",
  version      := "1.2-SNAPSHOT",
  scalaVersion := "2.12.10",
  parallelExecution in Global := false,
  scalacOptions ++= Seq(
    "-Xsource:2.11",
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit",
    // Enables autoclonetype2 in 3.4.x (on by default in 3.5)
    //"-P:chiselplugin:useBundlePlugin"
    ),
  libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value),
  libraryDependencies ++= Seq("org.json4s" %% "json4s-jackson" % "3.6.1"),
  //libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.0" % "test"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.mavenLocal
  ),
)

//val chiselVersion = "3.4.3"
lazy val chiselSettings = Seq(
  //libraryDependencies ++= Seq("edu.berkeley.cs" %% "chisel3" % chiselVersion),
  //addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full)
)

lazy val zynq = (project in file("."))
  .settings(commonSettings, chiselSettings)
