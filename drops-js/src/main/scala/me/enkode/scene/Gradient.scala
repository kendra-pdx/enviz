package me.enkode.scene

object Gradient {
  def apply(left: Color, right: Color) = new Gradient {
    override def colorAt(percent: Float): Color = {
      if (percent == 0f) return left
      if (percent == 1f) return right

      val hslLeft = left.asHSL
      val hslRight = right.asHSL

      def split(v: (HSL) ⇒ Float) = {
        val l = v(hslLeft)
        val r = v(hslRight)
        l + (percent * (r - l))
      }

      val h = split(_.h)
      val s = split(_.s)
      val l = split(_.l)
      val α = split(_.α)

      HSL(h,s,l,α)
    }
  }

  def apply(left: Gradient, right: Gradient, split: Float = 0.5f) = new Gradient {
    require(split between (0, 1), s"$split must be [0…1]")

    override def colorAt(percent: Float): Color = {
      require(percent between (0, 1), s"$percent must be [0…1]")

      def percentLeft = 1 - ((1/split) * (split-percent))
      def percentRight = (1 / (1-split)) * (percent-split)

      if (percent <= split) left.colorAt(percentLeft)
      else right.colorAt(percentRight)
    }
  }

  val trafficForwards = Gradient(
    Gradient(Red, Yellow),
    Gradient(Yellow, Green))

  val trafficReverse = Gradient(
    Gradient(Green, Yellow),
    Gradient(Yellow, Red))

  val traffic = Gradient(
    trafficForwards,
    trafficReverse)

}

trait Gradient{
  def colorAt(percent: Float): Color
}
