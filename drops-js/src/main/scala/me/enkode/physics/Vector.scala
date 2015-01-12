package me.enkode.physics

case class Vector(x: Double, y: Double) {
  import scala.math._

  def + (that: Vector) = Vector(this.x + that.x, this.y + that.y)
  def - (that: Vector) = Vector(this.x - that.x, this.y - that.y)
  def * (that: Vector) = Vector(this.x * that.x, this.y * that.y)
  def / (that: Vector) = Vector(this.x / that.x, this.y / that.y)
  def squared = Vector(x * x, y * y)

  def * (scalar: Double) = Vector(x * scalar, y * scalar)
  def / (scalar: Double) = Vector(x / scalar, y / scalar)

  def plus(that: Vector) = this + that

  lazy val r = sqrt(x*x + y*y)
  lazy val θ = ???
}
