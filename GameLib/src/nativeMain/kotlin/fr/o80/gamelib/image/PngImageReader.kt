package fr.o80.gamelib.image

import kotlinx.cinterop.toKString
import okio.BufferedSource

class Ihdr(
    val width: Int,
    val height: Int
)

class PngImageReader : ImageReader {

    private lateinit var ihdr: Ihdr

    override fun read(source: BufferedSource): Image {
        val chunkLength = source.readByteArray(4).toLong()
        val chunkType = source.readByteArray(4)
        val chunk = source.readByteArray(chunkLength)
        val chunkCRC = source.readByteArray(4)

        when (chunkType.toKString()) {
            "IHDR" -> chunk.toIhdr()
        }

        return Image(
            format = ImageFormat.PNG,
            width = ihdr.width,
            height = ihdr.height,
        )
    }

    private fun ByteArray.toIhdr() {
        val width = this.sliceArray(0..3)
        val height = this.sliceArray(4..7)

        ihdr = Ihdr(width.toInt(), height.toInt())
    }
}


private fun ByteArray.toLong(): Long {
    return fold(0L) { acc, byte ->
        acc * 256 + byte
    }
}

private fun ByteArray.toInt(): Int {
    return fold(0) { acc, byte ->
        acc * 256 + byte
    }
}
