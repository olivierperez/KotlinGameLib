package fr.o80.carres.image

import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.fontatlas.FontAtlas
import platform.opengl32.*

class FontTexture(
    val width: Int,
    val height: Int,
    val id: GLuint = 0U,
    val fontAtlas: FontAtlas
) {
    fun render(char: Char, hSize: Float) {
        val scaleW = fontAtlas.common.scaleW.toFloat()
        val scaleH = fontAtlas.common.scaleH.toFloat()

        val wSize = hSize * scaleW / scaleH
        println("wSize=$wSize")

        val charAtlas = fontAtlas.getChar(char) ?: return

        val startX = charAtlas.x / scaleW
        val endX = startX + charAtlas.width / scaleW
        val startY = charAtlas.y / scaleH
        val endY = startY + charAtlas.height / scaleH

        val xOffset = wSize * charAtlas.xOffset / charAtlas.width
        val yOffset = hSize * charAtlas.yOffset / charAtlas.height

        println("charAtlas.xAdvance = ${charAtlas.xAdvance}")
        println("scaleW = $scaleW")
        val xAdvance = wSize * charAtlas.xAdvance / charAtlas.width

        println("===============================================")
        draw {
            color(1f, 1f, 1f)
            texture2d {
                glBindTexture(GL_TEXTURE_2D, id)

                glBegin(GL_QUADS)
                glTexCoord2f(startX, startY)
                glVertex2f(0f + xOffset, 0f + yOffset)

                glTexCoord2f(endX, startY)
                glVertex2f(wSize + xOffset, 0f + yOffset)

                glTexCoord2f(endX, endY)
                glVertex2f(wSize + xOffset, hSize + yOffset)

                glTexCoord2f(startX, endY)
                glVertex2f(0f + xOffset, hSize + yOffset)

                glTexCoord2f(startX, startY)
                glVertex2f(0f + xOffset + xAdvance, 0f + yOffset)

                glTexCoord2f(endX, startY)
                glVertex2f(wSize + xOffset + xAdvance, 0f + yOffset)

                glTexCoord2f(endX, endY)
                glVertex2f(wSize + xOffset + xAdvance, hSize + yOffset)

                glTexCoord2f(startX, endY)
                glVertex2f(0f + xOffset + xAdvance, hSize + yOffset)
                glEnd()
                glBindTexture(GL_TEXTURE_2D, 0)
            }
        }
    }

    fun unload() {

    }
}
