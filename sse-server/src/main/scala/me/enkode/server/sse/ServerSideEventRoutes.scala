package me.enkode.server.sse

import akka.actor.ActorSystem
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.{Directives, Route}

import me.enkode.server.common.EventSource.Event
import me.enkode.server.common.{ActorNaming, Routes}

object ServerSideEventRoutes {

  object StreamActorNaming extends ActorNaming("stream")
  with ActorNaming.SequentialNamingStrategy

  def nextStreamActorName() = StreamActorNaming.next()
}

abstract class ServerSideEventRoutes(val actorSystem: ActorSystem)
  extends Routes with Directives
  with SprayJsonSupport with DefaultJsonProtocol
  with ServerSideEvents {

  import me.enkode.server.sse.ServerSideEventRoutes._

  implicit val eventFormat = jsonFormat2(Event)

  def stream(streamId: String): Route = { ctx ⇒
    actorSystem.actorOf(StreamActor.props(ctx, streamId), nextStreamActorName())
  }

  def subscribe: Route = path("stream" / Segment) { topic ⇒
    get(stream(topic))
  }

  def publish: Route = path("stream" / Segment) { topic ⇒
    post {
      entity(as[Event]) { event ⇒
        publish(topic, event)
        complete(StatusCodes.Accepted)
      }
    }
  }

  override def routes = subscribe :: publish :: Nil
}
