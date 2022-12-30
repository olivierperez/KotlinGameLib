package fr.o80.carres.image

import fr.o80.gamelib.dsl.draw
import platform.opengl32.*

class Texture(
    val width: Int,
    val height: Int,
    val id: GLuint = 0U
) {
    fun render() {
        draw {
            color(1f, 1f, 1f)
            texture2d {
                glBindTexture(GL_TEXTURE_2D, id)

                val margin = 0f
                val scale = 1f

                glBegin(GL_QUADS)
                glTexCoord2f(0f, 0f)
                glVertex2f(margin + 0f * scale, margin + 0f * scale)

                glTexCoord2f(1f, 0f)
                glVertex2f(margin + 26f * scale, margin + 0f * scale)

                glTexCoord2f(1f, 1f)
                glVertex2f(margin + 26f * scale, margin + 16f * scale)

                glTexCoord2f(0f, 1f)
                glVertex2f(margin + 0f * scale, margin + 16f * scale)
                glEnd()
                glBindTexture(GL_TEXTURE_2D, 0)
            }
        }
    }
}
