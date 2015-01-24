package me.enkode.scene

trait Sprite extends Physical[Sprite] {
  def visible(width: Double, height: Double): Boolean
  def draw(): Drawable
}
