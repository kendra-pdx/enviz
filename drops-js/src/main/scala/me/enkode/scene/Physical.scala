package me.enkode.scene

trait Physical[T] { self: T ⇒
  val t: Long   //time
  val a: Vector //acceleration
  val v: Vector //velocity
  val s: Vector //location

  def update(): T
}
