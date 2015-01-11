package me.enkode.server.common

import java.util.concurrent.atomic.AtomicLong

class ActorNaming(root: String, separator: String = "_") {
  val id = new AtomicLong()

  def next(): String = (new StringBuilder)
    .append(root)
    .append(separator)
    .append(id.getAndIncrement)
    .toString()
}


