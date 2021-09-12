package fr.o80.gamelib.drawing

import platform.opengl32.*

class Texture(
    private val image: Image
) {

    fun draw(x: Float, y: Float, width: Float, height: Float) {
        val (outWidth, outHeight) = computeRatio(width, height)

        glBindTexture(GL_TEXTURE_2D, 1 /*image.id*/)

        glBegin(GL_QUADS)
        glTexCoord2f(0f, 0f)
        glVertex2f(x, y)

        glTexCoord2f(1f, 0f)
        glVertex2f(x + outWidth, y)

        glTexCoord2f(1f, 1f)
        glVertex2f(x + outWidth, y + outHeight)

        glTexCoord2f(0f, 1f)
        glVertex2f(x, y + outHeight)
        glEnd()
    }

    private fun computeRatio(desiredWidth: Float, desireHeight: Float): Pair<Float, Float> {
        val imageRatio = 1 /*image.width.toFloat() / image.height.toFloat()*/

        return if (imageRatio > 1) {
            Pair(desiredWidth, desiredWidth / imageRatio)
        } else {
            Pair(desireHeight * imageRatio, desireHeight)
        }
    }
}