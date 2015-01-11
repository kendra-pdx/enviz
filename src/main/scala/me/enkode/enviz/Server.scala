package me.enkode.enviz

import akka.actor.ActorSystem
import me.enkode.server.sse.ServerSideEventRoutes
import me.enkode.server.ui.UserInterfaceRoutes
import spray.routing.SimpleRoutingApp

object Server extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem("EventDrops")

  val sse = new ServerSideEventRoutes
  val ui = new UserInterfaceRoutes

  val routes = List(sse, ui)
    .map(_.routes)
    .reduce(_ ++ _)
    .reduce(_ ~ _)

  startServer("0.0.0.0", 8080)(routes)
}
