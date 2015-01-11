package me.enkode.physics

trait World {
  val canvas: Canvas
  var scenes: Seq[Scene]
  def queueNextIn(ms: Long)
  def frameRate: Int

  def run() {
    def clear() {
      canvas.clear()
    }

    def elide() {
      scenes = scenes map { _.elide(canvas.width, canvas.height) }
    }

    def update() {
      scenes = scenes map { _.update() }
    }

    def render() {
      for {
        scene    ← scenes
        sprite   ← scene.sprites
        drawable = sprite.draw()
      } yield {
        canvas render drawable
      }
    }

    def wait(loopTime: Long) {
      queueNextIn( (1 / (frameRate.toDouble / 1000)).toLong - loopTime)
    }

    def t0 = System.currentTimeMillis()
    clear()
    elide()
    update()
    render()
    wait(System.currentTimeMillis() - t0)
  }
}
