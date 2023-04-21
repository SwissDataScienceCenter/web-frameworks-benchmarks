package io.renku.bench

import cats.syntax.all.*

import ciris.*
import com.comcast.ip4s.{Host, Port}
import org.http4s.Uri

object EnvConfig:
  val bind: ConfigValue[Effect, Config.Bind] =
    val host = config("bind_host", "0.0.0.0".some).as[Host]
    val port = config("bind_port", "8181".some).as[Port]
    (host, port).mapN(Config.Bind.apply)

  val redis: ConfigValue[Effect, Option[Config.RedisConnection]] =
    val uri = config("redis_uri", None).as[Uri]
    uri.map(Config.RedisConnection.apply).option

  given hostDecoder: ConfigDecoder[String, Host] =
    ConfigDecoder[String].mapOption("Host")(Host.fromString)

  given portDecoder: ConfigDecoder[String, Port] =
    ConfigDecoder[String].mapOption("Port")(Port.fromString)

  given uriDecoder: ConfigDecoder[String, Uri] =
    ConfigDecoder[String].mapOption("Uri")(str => Uri.fromString(str).toOption)

  private def config(name: String, default: Option[String]) =
    val cfg = env(s"HTTP4S_${name.toUpperCase}")
    default.map(cfg.default(_)).getOrElse(cfg)
