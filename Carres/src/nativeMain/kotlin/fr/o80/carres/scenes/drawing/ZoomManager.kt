package fr.o80.carres.scenes.drawing

import fr.o80.gamelib.loop.ScrollPipeline
import fr.o80.gamelib.loop.Window
import platform.opengl32.*
import kotlin.math.pow

class ZoomManager(
    private val window: Window,
    scrollPipeline: ScrollPipeline,
    private val zoomSpeed: Float
) {
    private var zoom: Float = 1f

    init {
        scrollPipeline.onScroll { _, yOffset -> onScroll(yOffset) }
    }

    private fun onScroll(yOffset: Double) {
        zoom *= zoomSpeed.pow(-yOffset.toFloat())
        glLoadIdentity()
        glOrtho(0.0, window.width * zoom.toDouble(), window.height * zoom.toDouble(), 0.0, 0.0, 1.0)
    }
}
