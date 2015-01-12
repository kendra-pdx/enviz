package me.enkode.server.sse

import akka.actor.ActorSystem
import me.enkode.logging.LazyLogging
import me.enkode.server.common.EventSource
import me.enkode.server.common.EventSource.Event

trait ServerSideEvents {
  implicit def actorSystem: ActorSystem
  def publish(streamId: String, event: Event): Unit
}

trait ServerSideEventsImpl extends ServerSideEvents with EventSource with LazyLogging {
}