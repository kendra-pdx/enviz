package me.enkode.server.sse

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Subscribe, SubscribeAck, Unsubscribe, UnsubscribeAck}
import akka.event.LoggingReceive
import akka.io.Tcp.PeerClosed
import spray.http.CacheDirectives.`no-cache`
import spray.http.HttpHeaders.{Connection, RawHeader, `Cache-Control`}
import spray.http.StatusCodes._
import spray.http._
import spray.json.DefaultJsonProtocol
import spray.routing.RequestContext

import me.enkode.server.common.EventSource.Event

object StreamActor {
  def props(ctx: RequestContext, streamId: String) = Props(new StreamActor(ctx, streamId))

  val terminate = "\r\n\r\n"

  trait SSEEvent {
    def sseContent: String
  }
  case class SSEComment(msg: String) extends SSEEvent {
    override def sseContent = ": " + msg + terminate
  }

  case class SSEData(message: String) extends SSEEvent{
    override def sseContent: String = message + terminate
  }

  def initResponse(comment: String): HttpResponse = {
    val entity = HttpEntity(contentType = MediaType.custom("text/event-stream"), SSEComment(comment).sseContent)
    val headers = List(
      `Cache-Control`(`no-cache`),
      `Connection`("Keep-Alive"),
      RawHeader("Access-Control-Allow-Origin", "*"))
    HttpResponse(OK, entity, headers)
  }

  sealed trait Protocol
  object Timeout extends Protocol
  object Close extends Protocol
  case class SendData(message: SSEEvent) extends Protocol

  type ProtocolReceive = Protocol ⇒ Unit
}

class StreamActor(
  requestContext: RequestContext,
  topic: String,
  keepAliveFrequency: FiniteDuration = 5.seconds)
  extends Actor
  with ActorLogging
  with DefaultJsonProtocol {
  import spray.json._

import me.enkode.server.sse.StreamActor._

  val mediator = DistributedPubSubExtension(context.system).mediator
  var keepAlives: Option[Cancellable] = None
  implicit val eventFormat = jsonFormat2(Event)

  override def receive: Receive = LoggingReceive {
    case p: Protocol             ⇒ protocol(p)

    case event: Event            ⇒ self forward SendData(SSEData(event.toJson.toString()))
    case PeerClosed              ⇒ self forward Close

    case SubscribeAck(subscribe) ⇒ log.debug("subscribe ack: {}", subscribe)
    case UnsubscribeAck(_)       ⇒ context stop self
  }

  def send(response: AnyRef) = {
    requestContext.responder ! response
  }

  def protocol: ProtocolReceive = {
    case Timeout | Close⇒
      keepAlives map { _.cancel() }
      keepAlives = None
      mediator ! Unsubscribe(topic, self)

    case SendData(sseEvent) ⇒
      send(MessageChunk(sseEvent.sseContent))
  }

  override def preStart(): Unit = {
    import context.dispatcher
    keepAlives = Some(context.system.scheduler
      .schedule(keepAliveFrequency, keepAliveFrequency, self, SendData(SSEComment("keep-alive")))
    )
    send(ChunkedResponseStart(initResponse("begin stream")))
    mediator ! Subscribe(topic, self)
  }
}
