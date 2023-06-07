package io.renku.bench

import cats.effect.*
import cats.syntax.all.*
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import dev.profunktor.redis4cats.connection.{RedisClient, RedisURI}
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log
import org.http4s.ember.server.EmberServerBuilder

object Http4sMain extends IOApp:
  private[this] val logger = scribe.cats.io
  given redisLog: Log[IO] = Log.NoOp.instance[IO]

  val service =
    for {
      cfg <- Resource.eval(Config.load[IO])
      redis <- makeRedisClient(cfg)
      routes = new Routes[IO](redis).all.orNotFound
      server <-
        EmberServerBuilder
          .default[IO]
          .withHost(cfg.bind.host)
          .withPort(cfg.bind.port)
          .withHttp2
          .withHttpApp(routes)
          .build
    } yield server

  override def run(args: List[String]): IO[ExitCode] =
    service.useForever.as(ExitCode.Success)

  private def makeRedisClient(
      cfg: Config
  ): Resource[IO, Option[RedisCommands[IO, String, String]]] =
    cfg.redis.map(_.url).traverse { uri =>
      RedisClient[IO]
        .from(uri.renderString)
        .flatMap(c => Redis[IO].fromClient(c, RedisCodec.Utf8))
    }
