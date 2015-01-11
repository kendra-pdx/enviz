package me.enkode.enviz

import akka.actor.ActorSystem
import me.enkode.server.sse.{ServerSideEventsImpl, ServerSideEventRoutes}
import me.enkode.server.ui.UserInterfaceRoutes
import spray.routing.SimpleRoutingApp

object Server extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem("EnViz")
  val config = actorSystem.settings.config

  val sse = new ServerSideEventRoutes with ServerSideEventsImpl {
    override implicit val actorSystem: ActorSystem = Server.this.actorSystem
  }

  val ui = new UserInterfaceRoutes

  val routes = List(sse, ui)
    .map(_.routes)
    .reduce(_ ++ _)
    .reduce(_ ~ _)

  val httpConfig = config.getConfig("enviz.http")
  startServer(httpConfig.getString("bind"), httpConfig.getInt("port"))(routes)
}
