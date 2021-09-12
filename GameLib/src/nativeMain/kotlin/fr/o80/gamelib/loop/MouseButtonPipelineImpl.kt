package fr.o80.gamelib.loop

import cnames.structs.GLFWwindow
import kotlinx.cinterop.CPointer

internal class MouseButtonPipelineImpl : MouseButtonPipeline {

    private val callbacks = mutableListOf<Triple<Int, Int, (Double, Double) -> Unit>>()

    private var mouseX: Double = -1.0
    private var mouseY: Double = -1.0

    fun invoke(window: CPointer<GLFWwindow>?, button: Int, action: Int, mods: Int) {
        callbacks.filter { (k, a, _) -> k == button && a == action }
            .forEach { it.third(mouseX, mouseY) }
    }

    override fun onButton(button: Int, action: Int, block: (x: Double, y: Double) -> Unit) {
        callbacks.add(Triple(button, action, block))
    }

    fun onMouseMove(x: Double, y: Double) {
        mouseX = x
        mouseY = y
    }

    fun clear() {
        callbacks.clear()
    }
}
