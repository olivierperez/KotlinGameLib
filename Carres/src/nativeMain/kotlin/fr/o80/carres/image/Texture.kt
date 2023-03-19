package fr.o80.carres.image

import fr.o80.gamelib.dsl.draw
import kotlinx.cinterop.refTo
import platform.opengl32.*

class Texture(
    val width: Int,
    val height: Int,
    private val id: GLuint = 0U
) {
    fun render() {
        draw {
            color(1f, 1f, 1f)
            texture2d(withAlpha = true) {
                glBindTexture(GL_TEXTURE_2D, id)
                glColor4f(1.0f, 1.0f, 1.0f, 0.9f)

                glBegin(GL_QUADS)
                glTexCoord2f(0f, 0f)
                glVertex2f(0f, 0f)

                glTexCoord2f(1f, 0f)
                glVertex2f(width.toFloat(), 0f)

                glTexCoord2f(1f, 1f)
                glVertex2f(width.toFloat(), height.toFloat())

                glTexCoord2f(0f, 1f)
                glVertex2f(0f, height.toFloat())
                glEnd()
                glFlush()
            }
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun unload() {
        glDeleteTextures(1, uintArrayOf(id).refTo(0))
    }
}
