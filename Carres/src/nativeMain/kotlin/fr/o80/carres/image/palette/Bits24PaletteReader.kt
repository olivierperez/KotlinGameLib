package fr.o80.carres.image.palette

import fr.o80.gamelib.model.Color
import okio.BufferedSource

class Bits24PaletteReader(
    private val width: Int,
    private val height: Int,
): PaletteReader {
    override fun readImage(source: BufferedSource): Array<Color> {
        val skipAtEOL = (4 - (width * 3)) % 4L
        val colors = Array(width * height) { Color(0xFF, 0xFF, 0xFF, 0xFF) }
        repeat(height) { h ->
            val heightIndex = height - h - 1
            repeat(width) { widthIndex ->
                val color = source.readByteArray(3).getColor()
                colors[widthIndex + heightIndex * width] = color
            }
            source.readByteArray(skipAtEOL)
        }

        return colors
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun ByteArray.getColor(): Color {
        val red = getUByteAt(2).toInt()
        val green = getUByteAt(1).toInt()
        val blue = getUByteAt(0).toInt()

        return Color(0xFF, red, green, blue)
    }
}
