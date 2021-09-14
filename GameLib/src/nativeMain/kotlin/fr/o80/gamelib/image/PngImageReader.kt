package fr.o80.gamelib.image

import fr.o80.gamelib.image.ImageResult.Image
import kotlinx.cinterop.toKString
import okio.Buffer
import okio.BufferedSource

class Ihdr(
    val width: Int,
    val height: Int
)

class PngImageReader : ImageReader {

    private lateinit var ihdr: Ihdr

    private val imageData = Buffer()

    override fun read(source: BufferedSource): ImageResult {
        do {
            val chunkLength = source.readByteArray(4).toLong()
            val chunkType = source.readByteArray(4).toKString()
            val chunk = source.readByteArray(chunkLength)
            val chunkCRC = source.readByteArray(4)

            println("Chunk type = $chunkType ($chunkLength)")
            when (chunkType) {
                "IHDR" -> chunk.readIhdr()
                "IDAT" -> chunk.addToImageData()
                "IEND" -> break
            }
        } while(chunkLength != 0L)

        return Image(
            format = ImageFormat.PNG,
            width = ihdr.width,
            height = ihdr.height,
            data = imageData
        )
    }

    private fun ByteArray.readIhdr() {
        val width = this.sliceArray(0..3)
        val height = this.sliceArray(4..7)

        ihdr = Ihdr(width.toInt(), height.toInt())
    }

    private fun ByteArray.addToImageData() {
        imageData.write(this)
    }
}


private fun ByteArray.toLong(): Long {
    return fold(0L) { acc, byte ->
        acc * 256 + byte.toUByte().toLong()
    }
}

private fun ByteArray.toInt(): Int {
    return fold(0) { acc, byte ->
        acc * 256 + byte.toUByte().toInt()
    }
}
