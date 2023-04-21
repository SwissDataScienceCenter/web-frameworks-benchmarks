package io.renku.bench

import cats.effect.Async
import cats.syntax.all._

import com.comcast.ip4s.{Host, Port}
import org.http4s.Uri

final case class Config(
    bind: Config.Bind,
    redis: Option[Config.RedisConnection]
)

object Config:
  private val cfg =
    (EnvConfig.bind, EnvConfig.redis).mapN(Config.apply)

  def load[F[_]: Async] = cfg.load[F]

  case class RedisConnection(url: Uri)
  case class Bind(host: Host, port: Port)
