package me.enkode.drops

import me.enkode.physics.{Canvas, Drawable}

class HtmlCanvas(id: String) extends Canvas {
  import scala.scalajs.js
  import scala.scalajs.js.Dynamic._

  val canvas = global.document.getElementById(id)
  val context = canvas.getContext("2d")
  val height = canvas.height.asInstanceOf[js.Number]
  val width = canvas.width.asInstanceOf[js.Number]

  println(s"$width, $height")
  override def clear(): Unit = {
    context.clearRect(0, 0, width, height)
  }

  override def render: PartialFunction[Drawable, Unit] = {
    case drawable: Drawable.CompoundDrawable ⇒
      drawable.drawables foreach { render }

    case Drawable.Circle(c, r, fill, stroke) ⇒
      context.beginPath()
      context.arc(c.x, c.y, r, 0, 2 * global.Math.PI)
      context.fillStyle = fill.color.css
      context.fill()
      context.lineWidth = stroke.size
      context.strokeStyle = stroke.color.css
      context.stroke()

    case Drawable.Line(from, to, stroke) ⇒
      context.beginPath()
      context.lineWidth = stroke.size
      context.strokeStyle = stroke.color.css
      context.moveTo(from.x, from.y)
      context.lineTo(to.x, to.y)
      context.stroke()
  }
}
