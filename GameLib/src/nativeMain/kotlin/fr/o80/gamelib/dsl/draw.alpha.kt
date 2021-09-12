package fr.o80.gamelib.dsl

import platform.opengl32.*

@Drawer
fun Draw.alpha(block: Draw.() -> Unit) {
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    block()
    glDisable(GL_BLEND)
}
