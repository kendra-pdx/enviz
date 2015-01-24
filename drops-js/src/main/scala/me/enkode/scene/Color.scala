package me.enkode.scene

sealed trait Color {
  def css: String
}

case class RGB(r: Float, g: Float, b: Float, α: Float = 1.0f) extends Color {
  require(Set(r, g, b, α) forall {_ between (0, 1) }, s"$this must all be between [0…1]")
  override def css: String = s"rgba(${r*255},${g*255},${b*255},$α)"
}

case class HSL(h: Float, s: Float, l: Float, α: Float = 1.0f) extends Color {
  require(Set(h, s, l, α) forall {_ between (0, 1) }, s"$this must all be between [0…1]")
  override def css: String = s"hsla(${h*360},${s*100}%,${l*100}%,$α)"
}

object Black extends HSL(0, 1, 0) {
  override def css: String = "black"
}

object White extends HSL(0, 1, 1) {
  override def css: String = "white"
}

object Red extends RGB(1, 0, 0)
object Yellow extends RGB(1, 1, 0)
object Green extends RGB(0, 1, 0)
object Blue extends RGB(0, 0, 1)

object Color {
  implicit class ColorSugar(color: Color) {
    private def rgb2hsl(rgb: RGB): HSL = {
      import math._
      val (r, g, b) = (rgb.r, rgb.g, rgb.b)
      val colorMax = Array(r, g, b).max
      val colorMin = Array(r, g, b).min
      val chroma = colorMax - colorMin

      val hue = if (chroma == 0) {
        0
      } else {
        (60f * (colorMax match {
          case `r` ⇒ ((g - b) / chroma) % 6f
          case `g` ⇒ ((b - r) / chroma) + 2
          case `b` ⇒ ((r - g) / chroma) + 4
        })) / 360
      }

      val lightness = (colorMax + colorMin) / 2

      val saturation = if (lightness == 0 || lightness == 1) {
        0
      } else {
        chroma / (1 - abs((2*lightness) - 1))
      }

      HSL(hue, saturation, lightness, rgb.α)
    }

    def asHSL: HSL = color match {
      case hsl: HSL ⇒
        hsl

      case rgb: RGB ⇒
        rgb2hsl(rgb)
    }
  }
}

object Test extends App {
  println(Blue.asHSL)
}