package me.enkode.server.sse

import akka.actor.{ActorLogging, Actor, Props}
import akka.event.LoggingReceive
import spray.http.CacheDirectives.`no-cache`
import spray.http.HttpHeaders.{RawHeader, Connection, `Cache-Control`}
import spray.http.StatusCodes._
import spray.http._
import spray.routing.RequestContext

import scala.concurrent.duration._

object StreamActor {
  def props(ctx: RequestContext) = Props(new StreamActor(ctx))

  val terminate = "\r\n\r\n"

  case class SSEComment(msg: String) {
    override def toString = ": " + msg + terminate
  }

  def initResponse(comment: String): HttpResponse = {
    val entity = HttpEntity(contentType = MediaType.custom("text/event-stream"), SSEComment(comment).toString)
    val headers = List(
      `Cache-Control`(`no-cache`),
      `Connection`("Keep-Alive"),
      RawHeader("Access-Control-Allow-Origin", "*"))
    HttpResponse(OK, entity, headers)
  }

  sealed trait Protocol
  object Timeout extends Protocol
  object Close extends Protocol
  case class Send(message: String) extends Protocol

  type ProtocolReceive = Protocol ⇒ Unit
}

class StreamActor(requestContext: RequestContext) extends Actor with ActorLogging {
  import StreamActor._

  import context.dispatcher

  override def receive: Receive = LoggingReceive {
    case p: Protocol ⇒ protocol(p)
  }

  def send(response: AnyRef) = {
    requestContext.responder ! response
  }

  def protocol: ProtocolReceive = {
    case Timeout | Close⇒
      send(ChunkedMessageEnd)
      context stop self

    case Send(message) ⇒
      send(MessageChunk(message + terminate))
      context.system.scheduler.scheduleOnce(2.seconds, self, Send("test"))

  }

  override def preStart(): Unit = {
    send(ChunkedResponseStart(initResponse("begin stream")))
    context.system.scheduler.scheduleOnce(2.seconds, self, Send("test"))
    context.system.scheduler.scheduleOnce(6.seconds, self, Timeout)
  }
}
