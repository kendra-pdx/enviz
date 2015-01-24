package me.enkode.scene

case class Scene(sprites: Seq[Sprite]) {
  def elide(width: Double, height: Double): Scene = {
    copy(sprites filter { _.visible(width, height)})
  }

  def update(): Scene = {
    copy(
      sprites = sprites map { _.update() }
    )
  }
}
