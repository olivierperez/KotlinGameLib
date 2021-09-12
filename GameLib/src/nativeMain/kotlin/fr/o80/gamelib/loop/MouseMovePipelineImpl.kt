package fr.o80.gamelib.loop

import cnames.structs.GLFWwindow
import kotlinx.cinterop.CPointer

internal class MouseMovePipelineImpl(
    private val mouseButtonPipeline: MouseButtonPipelineImpl
) : MouseMovePipeline {

    private val callbacks = mutableListOf<(Double, Double) -> Unit>()

    fun invoke(window: CPointer<GLFWwindow>?, xpos: Double, ypos: Double) {
        mouseButtonPipeline.onMouseMove(xpos, ypos)
        callbacks.forEach { it(xpos, ypos) }
    }

    override fun onMove(block: (Double, Double) -> Unit) {
        callbacks.add(block)
    }

    fun clear() {
        callbacks.clear()
    }
}