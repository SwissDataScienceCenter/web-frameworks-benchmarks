package io.renku.bench

import cats.effect.Sync

import dev.profunktor.redis4cats.effect.Log
import scribe.Scribe

final class RedisLog[F[_]: Sync](logger: Scribe[F]) extends Log[F]:
  override def debug(msg: => String): F[Unit] = logger.debug(msg)
  override def error(msg: => String): F[Unit] = logger.error(msg)
  override def info(msg: => String): F[Unit] = logger.info(msg)

object RedisLog:
  def apply[F[_]: Sync](scribe: Scribe[F]): RedisLog[F] = new RedisLog[F](scribe)
