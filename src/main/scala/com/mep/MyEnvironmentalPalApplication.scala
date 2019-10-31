package com.mep

import com.mep.models.{Footprint, Message, UserId}
import com.mep.services.MyEnvironmentalPalService
import com.twitter.app.Flag
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.filter.Cors
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.{Endpoint, _}
import io.finch.circe._

object MyEnvironmentalPalApplication extends TwitterServer {
  val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")

  val envService = new MyEnvironmentalPalService

  final val hello: Endpoint[Message] = get("hello") {
    envService.getMessage.map(Ok)
  }

  def acceptedMessage: Endpoint[Message] = jsonBody[Message]

  final val accept: Endpoint[Message] = post("accept" :: acceptedMessage) { incomingMessage: Message =>
    envService.acceptMessage(incomingMessage).map(Ok)
  }

  final val footprint: Endpoint[Footprint] = get("footprint" :: param("userId")) { userId: UserId =>
    envService.getFootprint(userId).map(Ok)
  }

  val api = (hello :+: accept :+: footprint).handle {
    case e: Exception => InternalServerError(e)
  }

  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods = _ => Some(Seq("GET", "POST")),
    allowsHeaders = _ => Some(Seq("Accept", "Content-Type"))
  )

  val corsService: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(api.toServiceAs[Application.Json])

  def main(): Unit = {
    log.info(s"Serving the application on port ${port()}")

    val server =
      Http.server
        .withStatsReceiver(statsReceiver)
        .serve(s":${port()}", corsService)
    closeOnExit(server)

    Await.ready(adminHttpServer)
  }
}
