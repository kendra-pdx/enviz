package me.enkode.server.sse

import akka.actor.{Actor, ActorLogging, Props}
import me.enkode.server.common.{ActorNaming, Routes}
import spray.http.CacheDirectives.`no-cache`
import spray.http.HttpHeaders.{Connection, RawHeader, `Cache-Control`}
import spray.http.StatusCodes._
import spray.http._
import spray.routing.{Directives, RequestContext, Route}

object ServerSideEventRoutes {
  object StreamActorNaming extends ActorNaming("stream")
  def nextStreamActorName() = StreamActorNaming.next()
}

abstract class ServerSideEventRoutes
  extends Routes
  with Directives
  with ServerSideEvents {
  import me.enkode.server.sse.ServerSideEventRoutes._

  def stream(streamId: String): Route = { ctx ⇒
    actorSystem.actorOf(StreamActor.props(ctx), nextStreamActorName())
  }

  def sse_get: Route = path("sse") {
    get {
      complete("OK")
    }
  }

  def get_stream: Route = path("stream" / Segment) { streamId ⇒
    get(stream(streamId))
  }

  override def routes = sse_get :: get_stream :: Nil
}
