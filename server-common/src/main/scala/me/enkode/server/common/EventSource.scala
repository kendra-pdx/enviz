package me.enkode.server.common

import akka.actor.ActorSystem
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.Publish
import me.enkode.logging.LazyLogging

object EventSource {
  type EventData = Map[String, Double]
  case class Event(id: String, data: EventData)
}

trait EventSource { self: LazyLogging ⇒
  import me.enkode.server.common.EventSource._
  def actorSystem: ActorSystem

  lazy val mediator = DistributedPubSubExtension(actorSystem).mediator

  final def publish(topic: String, event: Event): Unit = {
    debug(s"publishing: $topic, $event")
    mediator ! Publish(topic, event)
  }
}
