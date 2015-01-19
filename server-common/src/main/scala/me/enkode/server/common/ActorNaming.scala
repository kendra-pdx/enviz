package me.enkode.server.common

import java.util.concurrent.atomic.AtomicLong

object ActorNaming {
  sealed trait SuffixStrategy {
    def nextSuffix(): String
  }

  trait SequentialNamingStrategy extends SuffixStrategy {
    val id = new AtomicLong()
    override def nextSuffix(): String = id.getAndIncrement.toString
  }

  trait RandomNamingStrategy extends SuffixStrategy {
    def suffixLength = 32

    override def nextSuffix(): String = util.Random.alphanumeric.take(suffixLength).mkString
  }
}

abstract class ActorNaming(root: String, separator: String = "_")
  extends ActorNaming.SuffixStrategy {
  def next(): String = (new StringBuilder)
    .append(root)
    .append(separator)
    .append(nextSuffix())
    .toString()
}

