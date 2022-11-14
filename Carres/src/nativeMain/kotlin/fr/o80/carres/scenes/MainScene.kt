package fr.o80.carres.scenes

import fr.o80.gamelib.Scene
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.KeyPipeline
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.service.Services
import fr.o80.carres.CarresSceneManager
import fr.o80.gamelib.loop.ScrollPipeline
import interop.*
import platform.opengl32.*

class MainScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private lateinit var size: Pair<Int, Int>

    override fun open(
        window: Window,
        services: Services,
        keyPipeline: KeyPipeline,
        mouseButtonPipeline: MouseButtonPipeline,
        mouseMovePipeline: MouseMovePipeline,
        scrollPipeline: ScrollPipeline
    ) {
        size = Pair(window.width, window.height)
        keyPipeline.onKey(GLFW_KEY_ESCAPE, GLFW_PRESS) { sceneManager.quit() }
        keyPipeline.onKey(GLFW_KEY_SPACE, GLFW_PRESS) { sceneManager.openDrawing() }
        keyPipeline.onKey(GLFW_KEY_ENTER, GLFW_PRESS) { sceneManager.openDrawing() }
    }

    override fun close() {
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            clear(0.3f)
            glPointSize(20f)
            color(1f, 0f, 0f)
            point(size.first / 2.0, size.second / 2.0, .0)
        }
    }
}
