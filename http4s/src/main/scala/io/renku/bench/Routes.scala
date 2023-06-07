package io.renku.bench

import cats.effect.*
import cats.syntax.all.*
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log
import io.circe.syntax.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.middleware.Logger

final class Routes[F[_]: Async](redisClient: Option[RedisCommands[F, String, String]])
    extends Http4sDsl[F] {
  private[this] val redisKeys = List("test1", "test2", "test3")
  private[this] val helloWorld = Message.helloWorld.asJson
  private[this] val logger = scribe.cats.effect[F]

  val all: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root =>
      Ok(helloWorld)

    case GET -> Root / "redis" =>
      redisClient match
        case None => NotFound()
        case Some(client) =>
          val values = redisKeys.traverse(client.get)
          values.flatMap(vs => Ok(Map("values" -> vs.asJson)))
  }

  val allWithLogging: HttpRoutes[F] =
    Logger.httpRoutes(
      logHeaders = true,
      logBody = false,
      logAction = Some(msg => logger.info(msg))
    )(all)
}
