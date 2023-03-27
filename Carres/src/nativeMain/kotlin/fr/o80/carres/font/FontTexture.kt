package fr.o80.carres.font

import fr.o80.gamelib.model.Color
import fr.o80.gamelib.dsl.Draw
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.fontatlas.FontAtlas
import kotlinx.cinterop.refTo
import platform.opengl32.*

class FontTexture(
    val width: Int,
    val height: Int,
    private val id: GLuint = 0U,
    private val fontAtlas: FontAtlas
) {
    fun render(
        text: String,
        maxWidth: Float,
        maxHeight: Float,
        color: Color,
        textAlign: TextAlign = TextAlign.Left,
        debug: Boolean = false
    ) {
        val prepared = prepareFullText(text, fontAtlas)
        val textHeight = prepared.maxOf { it.height }
        val textWidth = prepared.last().let { it.drawingX + it.width }

        val displayRatio = maxWidth / maxHeight
        val textRatio = textWidth / textHeight

        val (displayWidth, displayHeight) = if (textRatio > displayRatio) {
            Pair(maxWidth, maxWidth / textRatio)
        } else {
            Pair(maxHeight * textRatio, maxHeight)
        }

        draw {
            color(1f, 1f, 1f)
            texture2d {
                color(color)
                glBindTexture(GL_TEXTURE_2D, id)

                glBegin(GL_QUADS)
                val generalXOffset = textAlign.xOffset(maxWidth, displayWidth)

                prepared.forEach { char ->
                    val drawingStartX = generalXOffset + (char.drawingX / textWidth) * displayWidth
                    val drawingStartY = (char.drawingY / textHeight) * displayHeight
                    val drawingEndX = generalXOffset + ((char.drawingX + char.width) / textWidth) * displayWidth
                    val drawingEndY = ((char.drawingY + char.height) / textHeight) * displayHeight

                    glTexCoord2f(char.atlasStartX, char.atlasStartY)
                    glVertex2f(drawingStartX, drawingStartY)

                    glTexCoord2f(char.atlasEndX, char.atlasStartY)
                    glVertex2f(drawingEndX, drawingStartY)

                    glTexCoord2f(char.atlasEndX, char.atlasEndY)
                    glVertex2f(drawingEndX, drawingEndY)

                    glTexCoord2f(char.atlasStartX, char.atlasEndY)
                    glVertex2f(drawingStartX, drawingEndY)
                }
                glEnd()
                glBindTexture(GL_TEXTURE_2D, 0)

                if (debug) {
                    drawOutline(maxWidth, maxHeight)
                }
            }
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun unload() {
        glDeleteTextures(1, uintArrayOf(id).refTo(0))
    }

    private fun prepareFullText(text: String, fontAtlas: FontAtlas): List<PreparedChar> {
        val fullAtlasWidth = fontAtlas.common.scaleW.toFloat()
        val fullAtlasHeight = fontAtlas.common.scaleH.toFloat()

        var x = 0f
        return text.fold(mutableListOf()) { acc, char ->

            val charAtlas = fontAtlas.getChar(char) ?: return@fold acc

            val atlasStartX = charAtlas.x / fullAtlasWidth
            val atlasStartY = charAtlas.y / fullAtlasHeight
            val atlasEndX = atlasStartX + charAtlas.width / fullAtlasWidth
            val atlasEndY = atlasStartY + charAtlas.height / fullAtlasHeight
            val atlasAdvance = charAtlas.xAdvance / fullAtlasWidth
            val atlasXOffset = charAtlas.xOffset / fullAtlasWidth
            val atlasYOffset = charAtlas.yOffset / fullAtlasHeight

            val drawingX = x + atlasXOffset
            x += atlasAdvance

            acc.add(
                PreparedChar(
                    atlasStartX = atlasStartX,
                    atlasEndX = atlasEndX,
                    atlasStartY = atlasStartY,
                    atlasEndY = atlasEndY,
                    drawingX = drawingX,
                    drawingY = atlasYOffset
                )
            )
            acc
        }
    }

    private fun Draw.drawOutline(width: Float, height: Float) {
        color(1f, 0f, 0f)
        rect(0f, 0f, width, height)
    }
}

data class PreparedChar(
    val atlasStartX: Float,
    val atlasEndX: Float,
    val atlasStartY: Float,
    val atlasEndY: Float,
    val drawingX: Float,
    val drawingY: Float
) {
    val width: Float = atlasEndX - atlasStartX
    val height: Float = atlasEndY - atlasStartY
}
