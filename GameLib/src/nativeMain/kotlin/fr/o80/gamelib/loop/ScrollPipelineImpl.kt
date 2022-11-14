package fr.o80.gamelib.loop

import cnames.structs.GLFWwindow
import kotlinx.cinterop.CPointer

class ScrollPipelineImpl : ScrollPipeline {

    private val callbacks = mutableListOf<(Double, Double) -> Unit>()

    operator fun invoke(window: CPointer<GLFWwindow>?, xOffset: Double, yOffset: Double) {
        callbacks.forEach { callback ->
            callback(xOffset, yOffset)
        }
    }

    override fun onScroll(block: (Double, Double) -> Unit) {
        callbacks += block
    }
}

interface ScrollPipeline {
    fun onScroll(block: (Double, Double) -> Unit)
}
