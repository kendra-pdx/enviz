package me.enkode.logging

import org.slf4j.Logger

trait LazyLogging {
  def logger: Logger

  def debug(message: ⇒ String) = if (logger.isDebugEnabled) logger.debug(message)
  def info(message: ⇒ String) = if (logger.isInfoEnabled) logger.info(message)
}
