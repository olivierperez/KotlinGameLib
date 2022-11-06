package fr.o80.gamelib

import fr.o80.gamelib.loop.GameLoop

abstract class Game {

    abstract val windowName: String
    abstract val updatesPerSecond: Int
    abstract val width: Int
    abstract val height: Int
    abstract val debug: Boolean

    abstract fun createSceneManager(gameLoop: GameLoop): SceneManager

    suspend fun start() {
        val gameLoop = GameLoop(this@Game, width, height, updatesPerSecond, windowName)
        val sceneManager = createSceneManager(gameLoop)

        gameLoop.start(sceneManager.initialScene)
    }

}
