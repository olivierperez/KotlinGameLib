package fr.o80.carres.image

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import platform.opengl32.*

class TextureLoader {
    fun load(image: Image): Texture {
        val textureId = memScoped {
            val texture = alloc<GLuintVar>()
            glGenTextures(1, texture.ptr)

            glBindTexture(GL_TEXTURE_2D, texture.value)

            val pixels = image.colors
                .map { (red, green, blue) ->
                    blue.shl(16) + green.shl(8) + red
                }
                .toIntArray()

            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGB,
                image.width,
                image.height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                pixels.refTo(0)
            )

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

            glBindTexture(GL_TEXTURE_2D, 0)

            texture.value
        }

        return Texture(
            image.width,
            image.height,
            textureId
        )
    }
}
