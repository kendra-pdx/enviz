package me.enkode.physics

sealed trait Drawable
object Drawable {
  sealed trait Color {
    def css: String
  }
  case class RGB(r: Int, g: Int, b: Int) extends Color {
    require(0 to 255 contains r)
    require(0 to 255 contains g)
    require(0 to 255 contains b)
    override def css: String = s"rgb($r,$g,$b)"
  }

  case class RGBα(r: Int, g: Int, b: Int, α: Float) extends Color {
    require(0 to 255 contains r)
    require(0 to 255 contains g)
    require(0 to 255 contains b)
    require(α <= 100)
    require(α >= 0)
    override def css: String = s"rgba($r,$g,$b,$α)"
  }

  case class HSL(h: Int, s: Int, l: Int) extends Color {
    require(0 to 360 contains h)
    require(0 to 100 contains s)
    require(0 to 100 contains l)
    override def css: String = s"hsl($h,$s%,$l%)"
  }

  case class HSLα(h: Int, s: Int, l: Int, α: Float) extends Color {
    require(0 to 360 contains h)
    require(0 to 100 contains s)
    require(0 to 100 contains l)
    require(α <= 100)
    require(α >= 0)
    override def css: String = s"hsla($h,$s%,$l%,$α)"
  }

  case class FillStyle(color: Color)
  case class StrokeStyle(color: Color, size: Double)

  case class CompoundDrawable(drawables: Drawable*) extends Drawable
  case class Circle(c: Vector, r: Double, fill: FillStyle, stroke: StrokeStyle) extends Drawable
  case class Line(start: Vector, length: Vector, stroke: StrokeStyle) extends Drawable
}