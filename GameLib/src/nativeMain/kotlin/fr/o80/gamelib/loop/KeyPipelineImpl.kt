package fr.o80.gamelib.loop

import cnames.structs.GLFWwindow
import kotlinx.cinterop.CPointer

internal class KeyPipelineImpl : KeyPipeline {

    private val callbacks = mutableListOf<Triple<Int, Int, () -> Unit>>()

    fun clear() {
        callbacks.clear()
    }

    override fun onKey(key: Int, action: Int, block: () -> Unit) {
        callbacks.add(Triple(key, action, block))
    }

    fun invoke(window: CPointer<GLFWwindow>?, key: Int, scancode: Int, action: Int, mods: Int) {
        callbacks.filter { (k, a, _) -> k == key && a == action }
            .forEach { it.third() }
    }

}
