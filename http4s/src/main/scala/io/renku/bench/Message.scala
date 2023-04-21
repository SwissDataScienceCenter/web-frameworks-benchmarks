package io.renku.bench

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class Message(message: String)

object Message:
  val helloWorld = Message("Hello World!")
  given encoder: Encoder[Message] = deriveEncoder
