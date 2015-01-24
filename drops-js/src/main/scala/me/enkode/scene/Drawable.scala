package me.enkode.scene

sealed trait Drawable
object Drawable {
  case class FillStyle(color: Color)
  case class StrokeStyle(color: Color, size: Double)

  case class CompoundDrawable(drawables: Drawable*) extends Drawable
  case class Circle(c: Vector, r: Double, fill: FillStyle, stroke: StrokeStyle) extends Drawable
  case class Line(start: Vector, length: Vector, stroke: StrokeStyle) extends Drawable
}