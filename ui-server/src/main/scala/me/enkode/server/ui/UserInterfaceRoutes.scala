package me.enkode.server.ui

import akka.actor.ActorSystem
import me.enkode.logging.LazyLogging
import me.enkode.server.common.Routes
import org.slf4j.LoggerFactory
import spray.routing.{Directives, Route}

class UserInterfaceRoutes(implicit actorSystem: ActorSystem)
  extends Routes
  with Directives
  with LazyLogging {

  val logger = LoggerFactory.getLogger(classOf[UserInterfaceRoutes])

  def index: Route = pathEndOrSingleSlash {
    getFromResource("index.html")
  }

  def resource = path(Segment) { name ⇒
    debug(s"GET $name")
    getFromResource(name)
  }

  override def routes = index :: resource :: Nil
}
