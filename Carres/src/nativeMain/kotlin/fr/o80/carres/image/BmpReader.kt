package fr.o80.carres.image

import fr.o80.gamelib.dsl.Vertex3i
import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Path

class BmpReader {
    fun read(path: Path): Image {
        return FileSystem.SYSTEM.read(path) {
            val bm = readByteArray(2)
            println("bm: ${bm.decodeToString()}")

            readByteArray(4) // File size
            readByteArray(4) // Application info
            readByteArray(4) // Header size
            readByteArray(4) // Windows = 0x28

            val width = readByteArray(4).getLittleEndian()
            val height = readByteArray(4).getLittleEndian()
            val colors = Array(width * height) { Vertex3i(0xFF, 0xFF, 0xFF) }

            readByteArray(2) // Plan = 0x01

            val colorBytes = readByteArray(2).debug("color bytes").getLittleEndian()
            check(colorBytes == 24) { "Only 24 bits color is implemented" }

            val compression = readByteArray(4).getLittleEndian()
            check(compression == 0) { "No compression allowed" }

            readByteArray(4) // image data size

            readByteArray(4) // H m/px
            readByteArray(4) // V m/px

            readByteArray(4) // Colors per palette
            readByteArray(4) // Primary colors per palette

            repeat(height) { h ->
                val heightIndex = height - h - 1
                repeat(width) { widthIndex ->
                    val color = readByteArray(3).getColor()
                    colors[widthIndex + heightIndex * width] = color
                }
                readByteArray(4 - (width * 3) % 4L)
            }

            Image(width, height, colors)
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun ByteArray.getColor(): Vertex3i {
    val red = getUByteAt(2).toInt()
    val green = getUByteAt(1).toInt()
    val blue = getUByteAt(0).toInt()

    return Vertex3i(red, green, blue)
}

private fun ByteArray.getLittleEndian(): Int {
    return foldRight(0) { byte, acc -> acc * 256 + byte }
}

private fun ByteArray.debug(name: String): ByteArray {
    println("$name: ${this.toByteString()} => ${this.getLittleEndian()}")
    return this
}
