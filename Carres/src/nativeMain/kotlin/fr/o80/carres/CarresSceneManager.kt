package fr.o80.carres

import fr.o80.carres.scenes.DrawingScene
import fr.o80.gamelib.Scene
import fr.o80.gamelib.SceneManager
import fr.o80.gamelib.loop.GameLoop
import fr.o80.carres.scenes.MainScene

class CarresSceneManager(
    private val gameLoop: GameLoop
) : SceneManager {

    override val initialScene: Scene
        get() = MainScene(this)

    fun openDrawing() {
        gameLoop.open(DrawingScene(this))
    }

    fun quit() {
        gameLoop.stop()
    }

}
