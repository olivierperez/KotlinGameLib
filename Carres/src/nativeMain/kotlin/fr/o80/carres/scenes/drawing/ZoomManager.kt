package fr.o80.carres.scenes.drawing

import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.ScrollPipeline
import fr.o80.gamelib.loop.Window
import kotlin.math.pow

class ZoomManager(
    private val window: Window,
    scrollPipeline: ScrollPipeline,
    private val zoomSpeed: Float
) {
    var zoom: Float = 1f
        private set

    init {
        scrollPipeline.onScroll { _, yOffset -> onScroll(yOffset) }
    }

    private fun onScroll(yOffset: Double) {
        zoom *= zoomSpeed.pow(-yOffset.toFloat())
    }

    fun pushed(function: () -> Unit) {
        draw {
            pushed {
                translate(
                    x = window.width / 2 * (1 - zoom),
                    y = window.height / 2 * (1 - zoom),
                    z = 0f
                )
                scale(zoom, zoom, 0f)
                function()
            }
        }
    }
}
