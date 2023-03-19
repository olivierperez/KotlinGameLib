package fr.o80.carres.image

import fr.o80.carres.font.FontTexture
import fr.o80.gamelib.fontatlas.FontAtlas
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import platform.opengl32.*

class TextureLoader {
    fun loadTexture(image: Image): Texture {
        return Texture(
            image.width,
            image.height,
            createTexture(image, GL_NEAREST, GL_NEAREST)
        )
    }

    fun loadFontTexture(fontAtlas: FontAtlas, image: Image): FontTexture {
        return FontTexture(
            image.width,
            image.height,
            createTexture(image, GL_LINEAR, GL_LINEAR),
            fontAtlas
        )
    }

    private fun createTexture(
        image: Image,
        magFilter: Int,
        minFilter: Int
    ) = memScoped {
        val texture = alloc<GLuintVar>()
        glGenTextures(1, texture.ptr)

        glBindTexture(GL_TEXTURE_2D, texture.value)

        val pixels = image.colors.flatMap { (a, r, g, b) ->
            listOf(r.toByte(), g.toByte(), b.toByte(),  a.toByte())
        }.toByteArray()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            image.width,
            image.height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            pixels.refTo(0)
        )

        glBindTexture(GL_TEXTURE_2D, 0)

        texture.value
    }
}
