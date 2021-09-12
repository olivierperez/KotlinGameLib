package fr.o80.gamelib.loop

import fr.o80.gamelib.Game
import fr.o80.gamelib.Scene
import fr.o80.gamelib.service.Services
import fr.o80.gamelib.toKString
import interop.*
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.opengl32.*

private val keyPipeline = KeyPipelineImpl()
private val mouseButtonPipeline = MouseButtonPipelineImpl()
private val mouseMovePipeline = MouseMovePipelineImpl(mouseButtonPipeline)

class GameLoop(
    private val game: Game,
    private val width: Int,
    private val height: Int,
    private val updatesPerSecond: Int,
    private val windowName: String
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var window: Window
    private lateinit var services: Services


    private var currentScene: Scene? = null

    suspend fun start(initialScene: Scene) {
        println("Starting game...")
        scope.launch {
            setup()
            debug()
            open(initialScene)
            loop()
        }.join()

        glfwSetKeyCallback(window.id, null)
        glfwSetMouseButtonCallback(window.id, null)
        glfwSetCursorPosCallback(window.id, null)
        glfwDestroyWindow(window.id)

        glfwTerminate()
        glfwSetErrorCallback(null)
    }

    private fun debug() {
        if (game.debug) {
            println("OpenGL version: " + glGetString(GL_VERSION)?.toKString())
            println("Device: " + glGetString(GL_RENDERER)?.toKString())
        }
    }

    private fun setup() {
        glfwSetErrorCallback(staticCFunction { error, message ->
            println("An error occurred [$error]: ${message?.toKString()}")
        })

        if (glfwInit() != 1) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        window = Window(
            glfwCreateWindow(width, height, windowName, null, null),
            width,
            height
        )
        if (window.id == null) {
            throw IllegalStateException("Failed to create window")
        }

        services = Services(
            game.createCursorManager(window)
        )

        glfwSetKeyCallback(
            window.id,
            staticCFunction { window, key, scancode, action, mods ->
                keyPipeline.invoke(window, key, scancode, action, mods)
            }
        )
        glfwSetMouseButtonCallback(window.id,
            staticCFunction { window, button, action, mods ->
                mouseButtonPipeline.invoke(window, button, action, mods)
            }
        )
        glfwSetCursorPosCallback(window.id,
            staticCFunction { window, xpos, ypos ->
                mouseMovePipeline.invoke(window, xpos, ypos)
            }
        )

        memScoped {
            val widthBuffer = alloc<IntVar>()
            val heightBuffer = alloc<IntVar>()
            glfwGetWindowSize(window.id, widthBuffer.ptr, heightBuffer.ptr)

            val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

            glfwSetWindowPos(
                window.id,
                (videoMode.pointed.width - widthBuffer.value) / 2,
                (videoMode.pointed.height - heightBuffer.value) / 2
            )
        }

        glfwMakeContextCurrent(window.id)
        glfwSwapInterval(1)

        glfwShowWindow(window.id)

        // TODO OPZ
//        createCapabilities()

        ortho(Ortho.TOP_LEFT, GL_MODELVIEW.toUInt())
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun ortho(ortho: Ortho, mode: GLenum) {
        val width = width.toDouble()
        val height = height.toDouble()

        when (ortho) {
            Ortho.TOP_LEFT -> {
                glMatrixMode(mode)
                glOrtho(0.0, width, height, 0.0, 0.0, 1.0)
            }
            Ortho.BOTTOM_CENTER -> {
                glMatrixMode(mode)
                glOrtho(-width / 2, width / 2, 0.0, height, 0.0, 1.0)
            }
            Ortho.CENTER -> {
                glMatrixMode(mode)
                glOrtho(-width / 2, width / 2, -height / 2, height / 2, 0.0, 1.0)
            }
        }
    }

    private suspend fun loop() {
        var lastTime = glfwGetTime()
        var timer = lastTime
        var delta = 0.0
        var now: Double
        var frames = 0
        var updates = 0
        val limitFPS = 1f / updatesPerSecond

        while (glfwWindowShouldClose(window.id) != 1) {
            if (currentScene == null && game.debug) println("Not yet any scene !!!")
            now = glfwGetTime()
            delta += (now - lastTime) / limitFPS

            lastTime = now

            while (delta > 1.0) {
                currentScene?.update()
                updates++
                delta--
            }

            glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())
            currentScene?.render()
            glfwPollEvents()
            glfwSwapBuffers(window.id)
            frames++

            if (glfwGetTime() - timer > 1) {
                timer++
                if (game.debug) {
                    println("FPS: $frames Updates: $updates")
                }
                frames = 0
                updates = 0
            }
        }
    }

    fun open(scene: Scene) {
        val oldScene = currentScene
        keyPipeline.clear()
        mouseButtonPipeline.clear()
        mouseMovePipeline.clear()
        scene.open(window, services, keyPipeline, mouseButtonPipeline, mouseMovePipeline)
        currentScene = scene
        oldScene?.close()
    }

    fun stop() {
        glfwSetWindowShouldClose(window.id, 1)
    }

}

enum class Ortho {
    TOP_LEFT,
    BOTTOM_CENTER,
    CENTER
}
