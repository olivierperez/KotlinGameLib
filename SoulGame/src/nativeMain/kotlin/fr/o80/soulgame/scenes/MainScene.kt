package fr.o80.soulgame.scenes

import fr.o80.gamelib.Scene
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.KeyPipeline
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.service.Services
import fr.o80.soulgame.SoulSceneManager
import interop.*

class MainScene(
    private val sceneManager: SoulSceneManager
) : Scene {

    override fun open(
        window: Window,
        services: Services,
        keyPipeline: KeyPipeline,
        mouseButtonPipeline: MouseButtonPipeline,
        mouseMovePipeline: MouseMovePipeline
    ) {
        keyPipeline.onKey(GLFW_KEY_ESCAPE, GLFW_PRESS) { sceneManager.quit() }
    }

    override fun close() {
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            clear(0.3f)
        }
    }
}
