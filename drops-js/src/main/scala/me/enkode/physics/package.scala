package me.enkode

package object physics {
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

  import scala.language.implicitConversions
  implicit def intTupleToVector(t: (Int, Int)): Vector = t.toVector
  implicit def doubleTupleToVector(t: (Double, Double)): Vector = t.toVector
}
