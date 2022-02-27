lazy val commonSettings = Seq(
  organization := "edu.berkeley.cs",
  version      := "1.2-SNAPSHOT",
  scalaVersion := "2.12.10",
  parallelExecution in Global := false,
  scalacOptions ++= Seq(
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit",
    "-Xsource:2.11"
    //"-P:chiselplugin:genBundleElements",
    ),
  //libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value),
  //libraryDependencies ++= Seq("org.json4s" %% "json4s-jackson" % "3.6.1"),
  //libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.0" % "test"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.mavenLocal
  ),
)

val chiselVersion = "3.4.1"
lazy val chiselSettings = Seq(
  libraryDependencies ++= Seq("edu.berkeley.cs" %% "firrtl" % "1.4.1"),
  libraryDependencies ++= Seq("edu.berkeley.cs" %% "chisel3" % chiselVersion),
  addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full)
)

lazy val rocketchip = (project in file("rocket-chip"))
  .settings(commonSettings)

lazy val sifiveBlock = (project in file("sifive-blocks"))
  .settings(commonSettings)
  .dependsOn(rocketchip)

lazy val testchipip = (project in file("testchipip"))
  .settings(commonSettings)
  .dependsOn(rocketchip, sifiveBlock)

lazy val `fpga-zynq` = (project in file("."))
  .settings(commonSettings, chiselSettings)
  .dependsOn(rocketchip, testchipip)

