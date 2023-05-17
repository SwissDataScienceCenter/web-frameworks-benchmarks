import Dependencies.V

addCommandAlias("ci", "; lint; +test; +publishLocal")
addCommandAlias(
  "lint",
  "; scalafmtSbtCheck; scalafmtCheckAll; Compile/scalafix --check; Test/scalafix --check"
)
addCommandAlias("fix", "; Compile/scalafix; Test/scalafix; scalafmtSbt; scalafmtAll")
addCommandAlias("make-package", "; Universal/packageBin")
addCommandAlias("make-stage", "; Universal/stage")

val sharedSettings = Seq(
  organization := "io.renku",
  scalaVersion := V.scala3,
  scalacOptions ++=
    Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-language:higherKinds",
      "-explain",
      "-explain-types",
      "-Xfatal-warnings"
    ),
  Compile / console / scalacOptions := Seq(),
  Test / console / scalacOptions := Seq(),
  licenses := Seq(
    "GPL-3.0-or-later" -> url("https://spdx.org/licenses/GPL-3.0-or-later")
  ),
  homepage := Some(
    url("https://github.com/SwissDataScienceCenter/web-frameworks-benchmarks")
  ),
  versionScheme := Some("early-semver")
)

val scalafixSettings = Seq(
  semanticdbEnabled := true, // enable SemanticDB
  semanticdbVersion := scalafixSemanticdb.revision, // use Scalafix compatible version
  ThisBuild / scalafixDependencies ++= Dependencies.organizeImports
)

val root = project
  .in(file("."))
  .enablePlugins(JavaServerAppPackaging, GraalVMNativeImagePlugin)
  .settings(sharedSettings)
  .settings(scalafixSettings)
  .settings(
    name := "http4s-test-app",
    libraryDependencies ++=
      Dependencies.http4s ++
        Dependencies.circe ++
        Dependencies.ciris ++
        Dependencies.scribe ++
        Dependencies.redis4Cats,
    assembly / mainClass := Some("io.renku.bench.Http4sMain"),
    assembly / assemblyMergeStrategy := {
      case "MANIFEST.MF"                           => MergeStrategy.discard
      case "NOTICE"                                => MergeStrategy.discard
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case "reference.conf" => MergeStrategy.concat
      case entry =>
        val prev = (ThisBuild / assemblyMergeStrategy).value
        prev(entry)
    }
  )
