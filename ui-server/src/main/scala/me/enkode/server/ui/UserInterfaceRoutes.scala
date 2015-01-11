package me.enkode.server.ui

import me.enkode.server.common.Routes
import spray.routing.Route

class UserInterfaceRoutes extends Routes {
  import spray.routing.Directives._

  def index: Route = pathEndOrSingleSlash {
    get {
      complete {
        <html>
          <head>
            <title>Event Drops</title>
          </head>
          <body>
            <p>It Works!</p>
          </body>
        </html>
      }
    }
  }

  override def routes = index :: Nil
}
