package me.enkode.physics

trait Sprite extends Physical[Sprite] {
  def visible(width: Double, height: Double): Boolean
  def draw(): Drawable
}
