package me.enkode.server.sse

import me.enkode.server.common.Routes
import spray.http.StatusCodes
import spray.routing.Directives._
import spray.routing.Route

class ServerSideEventRoutes extends Routes {

  import spray.routing.Directives._

  def sse_get: Route = path("sse") {
    get {
      complete("OK")
    }
  }

  override def routes = sse_get :: Nil
}
