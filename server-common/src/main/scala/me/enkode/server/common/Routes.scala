package me.enkode.server.common

import spray.routing.Route

trait Routes {
  def routes: Seq[Route]
}
