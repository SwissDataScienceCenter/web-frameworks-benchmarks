import sbt._

object Dependencies {

  object V {
    val scala3 = "3.2.2"
    val http4s = "0.23.19"
    val ciris = "3.1.0"
    val circe = "0.14.5"
    val betterMonadicFor = "0.3.1"
    val organizeImports = "0.6.0"
    val scribe = "3.11.1"
    val redis4Cats = "1.4.1"
  }

  val redis4Cats = Seq(
    "dev.profunktor" %% "redis4cats-effects" % V.redis4Cats
  )
  val ciris = Seq(
    "is.cir" %% "ciris" % V.ciris
  )
  val betterMonadicFor =
    "com.olegpy" %% "better-monadic-for" % V.betterMonadicFor

  val organizeImports = Seq(
    "com.github.liancheng" %% "organize-imports" % V.organizeImports
  )
  val http4s = Seq(
    "org.http4s" %% "http4s-ember-server" % V.http4s,
    "org.http4s" %% "http4s-dsl" % V.http4s,
    "org.http4s" %% "http4s-circe" % V.http4s
  )
  val circe = Seq(
    "io.circe" %% "circe-core" % V.circe,
    "io.circe" %% "circe-generic" % V.circe
  )
  val scribe = Seq(
    "com.outr" %% "scribe" % V.scribe,
    "com.outr" %% "scribe-slf4j" % V.scribe,
    "com.outr" %% "scribe-cats" % V.scribe
  )
}
