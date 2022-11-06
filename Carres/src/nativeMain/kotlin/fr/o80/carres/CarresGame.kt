package fr.o80.carres

import fr.o80.gamelib.Game
import fr.o80.gamelib.SceneManager
import fr.o80.gamelib.loop.GameLoop

class CarresGame : Game() {

    override val windowName: String = "Carrés"
    override val updatesPerSecond: Int = 30
    override val width: Int = 1120
    override val height: Int = 840
    override val debug: Boolean = true

    override fun createSceneManager(gameLoop: GameLoop): SceneManager = CarresSceneManager(gameLoop)

}