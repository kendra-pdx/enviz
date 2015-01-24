package me.enkode

package object scene {
  implicit class DoubleTupleSugar(t: (Double, Double)) {
    def toVector = {
      val (x, y) = t
      Vector(x, y)
    }
  }

  implicit class IntTupleSugar(t: (Int, Int)) {
    def toVector = {
      val (x, y) = t
      Vector(x, y)
    }
  }

  implicit class FloatSugar(f: Float) {
    def between(min: Float, max: Float): Boolean = {
      f >= min && f <= max
    }
  }

  import scala.language.implicitConversions
  implicit def intTupleToVector(t: (Int, Int)): Vector = t.toVector
  implicit def doubleTupleToVector(t: (Double, Double)): Vector = t.toVector
}
