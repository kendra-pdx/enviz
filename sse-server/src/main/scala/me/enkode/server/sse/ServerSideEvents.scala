package me.enkode.server.sse

import akka.actor.ActorSystem

trait ServerSideEvents {
  implicit def actorSystem: ActorSystem
}

trait ServerSideEventsImpl extends ServerSideEvents {

}