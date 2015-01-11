package me.enkode.server.ui

import akka.actor.ActorSystem
import me.enkode.server.common.Routes
import spray.http.MediaTypes
import spray.routing.Route

class UserInterfaceRoutes(implicit actorSystem: ActorSystem) extends Routes {
  import spray.routing.Directives._

  def index: Route = pathEndOrSingleSlash {
    getFromResource("index.html")
  }

  def resource = path(Segment) { name ⇒
    getFromResource(name)
  }

  override def routes = index :: Nil
}
