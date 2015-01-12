package me.enkode.physics

trait Canvas {
  def width: Double
  def height: Double
  def clear()
  def render: PartialFunction[Drawable, Unit]
}


