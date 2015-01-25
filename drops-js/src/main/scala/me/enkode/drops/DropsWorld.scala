package me.enkode.drops

import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.annotation.JSExport

import me.enkode.scene._
import me.enkode.scene.html.HtmlCanvas

object DropsWorld {
  val gravity = 1.4
  val minR = 1.0
  val maxR = 3.5

  case class Raindrop(
    s: Vector,
    v: Vector = (0, 0),
    a: Vector = (0d, gravity),
    r: Double = 2,
    t: Long = System.currentTimeMillis(),
    stroke: Drawable.StrokeStyle = Drawable.StrokeStyle(HSL(210, 60, 60, 0.5f), 1),
    fill: Drawable.FillStyle = Drawable.FillStyle(HSL(210, 60, 60, 0.5f)))
    extends Sprite {
    override def visible(width: Double, height: Double): Boolean = s.x < width && s.y < height

    override def draw(): Drawable = {
      val head = {
        Drawable.Circle(s, r, fill, stroke)
      }

      val tail = {
        val length = v * 2.9
        Drawable.Line(s, s - Vector(length.x, length.y), stroke.copy(size = r))
      }

      Drawable.CompoundDrawable(head, tail)
    }

    override def update(): Sprite = {
      val now = System.currentTimeMillis().toDouble
      val vScale = .975 + (r/maxR) * .025
      val (s0, v0, a0, t0) = (this.s, this.v, this.a, this.t)
      val Δt = (now - t0) / 1000
      val Δs = (a0 * (Δt * Δt)) / 2
      val Δv = a0 * Δt
      val v = (Δv + v0) * vScale
      val s =  s0 + v - Δs
      copy(
        s = s,
        v = v,
        t = now.toLong
      )
    }
  }
}

@JSExport("DropsWorld")
class DropsWorld(canvasId: String) extends World {
  import me.enkode.drops.DropsWorld._

  override def frameRate = 100

  val drops = new Scene(sprites = Seq.empty[Sprite])

  override var scenes = Seq(drops)
  override val canvas = new HtmlCanvas(canvasId)

  var running = false
  override def queueNextIn(ms: Long): Unit = {
    if (running) global.window.setTimeout(this.run _, ms)
  }

  @JSExport
  def start() {
    running = true
    queueNextIn(0)
  }

  @JSExport
  def stop() {
    running = false
  }

  @JSExport
  def createDrop() {
    import scala.util.Random
    val randomX = Random.nextDouble * canvas.width
    val randomR = minR + Random.nextDouble() * (maxR - minR)
    val colorAt = System.currentTimeMillis().toFloat / 10000 % 1
    val color = Gradient.traffic.colorAt(colorAt)
    scenes = scenes map { scene ⇒
      val sprite = Raindrop(
        s = (randomX, 10.0),
        r = randomR,
        stroke = Drawable.StrokeStyle(color, 1),
        fill = Drawable.FillStyle(color))
      scene.copy(sprites = scene.sprites :+ sprite)
    }
  }

  @JSExport
  def startRaining(interval: Double, clutter: Double) {
    global.window.setInterval(() ⇒ {
      val delay =  scala.util.Random.nextDouble() * interval * clutter
      global.window.setTimeout(createDrop _, delay)
    }, interval)
  }
}
