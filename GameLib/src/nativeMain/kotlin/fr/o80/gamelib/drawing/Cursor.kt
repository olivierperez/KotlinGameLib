package fr.o80.gamelib.drawing

import cnames.structs.GLFWcursor
import fr.o80.gamelib.image.ImageIO
import fr.o80.gamelib.image.ImageResult
import interop.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import okio.Buffer
import okio.Path

// TODO OPZ
class Cursor(
    path: Path,
    hotspotX: Int,
    hotspotY: Int
) {
    var id: CPointer<GLFWcursor>? = null
        private set
    val width: Int
    val height: Int

    init {
        val image: ImageResult = ImageIO().read(path)

        when (image) {
            is ImageResult.Error -> {
                error("Can't load cursor because: ${image.cause}")
            }
            is ImageResult.Image -> {
                width = image.width
                height = image.height

                val raw: IntArray = image.rgb
                val buffer = Buffer()

                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val index = y * width + x
                        val pixel = raw[index]
                        buffer.writeByte(pixel shr 16 and 0xFF) // red
                        buffer.writeByte(pixel shr 8 and 0xFF) // green
                        buffer.writeByte(pixel and 0xFF) // blue
                        buffer.writeByte(pixel shr 24 and 0xFF) // alpha
                    }
                }
                // TODO buffer.flip() // this will flip the cursor image vertically

                memScoped {
                    val cursorImg = GLFWimage(alloc())
                    val pixels = alloc<UByteVar>().apply { value = buffer[0].toUByte() } // TODO OPZ Aller le buffer, et le remplir ici !
                    cursorImg.width = width
                    cursorImg.height = height
                    cursorImg.pixels = pixels.ptr
                    id = glfwCreateCursor(cursorImg.ptr, hotspotX, hotspotY)
                }
            }
        }

//        val raw = image.getRGB(0, 0, width, height, IntArray(width * height), 0, width)
//        val buffer = ByteArray(width * height * 4)
//
//        for (y in 0 until height) {
//            for (x in 0 until width) {
//                val index = y * width + x
//                val pixel = raw[index]
//                buffer.push( (pixel shr 16 and 0xFF).toByte() ) // red
//                buffer.push( (pixel shr 8 and 0xFF).toByte() ) // green
//                buffer.push( (pixel and 0xFF).toByte() ) // blue
//                buffer.push( (pixel shr 24 and 0xFF).toByte() ) // alpha
//            }
//        }
//        //buffer.flip() // this will flip the cursor image vertically
//
//        memScoped {
//            val cursorImg = GLFWimage(alloc())
//            val pixels = alloc<UByteVar>().apply { value = buffer.getUByteAt(0) }
//
//            cursorImg.width = width
//            cursorImg.height = height
//            cursorImg.pixels = pixels.ptr
//            id = glfwCreateCursor(cursorImg.ptr, hotspotX, hotspotY)
//        }

    }

    fun unload() {
        glfwDestroyCursor(id)
        id = null
    }
}
