package project

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import cats.effect._
import fs2.Stream
import io.circe._
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object JsonService {

  implicit val jsonsEncoder: Encoder[JsonInfo] = deriveEncoder[JsonInfo]
  implicit val timer: Timer[IO]                = IO.timer(global)
  implicit val cs: ContextShift[IO]            = IO.contextShift(global)

  val jsonService: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "json" / msg =>
        val jsonStream = Stream
          .awakeEvery[IO](3.second)
          .map(_ => JsonInfo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), msg).asJson)
          .take(5)
        Ok(jsonStream)
    }
}

final case class JsonInfo(time: String, message: String)
