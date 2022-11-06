package fr.o80.gamelib.image

import okio.Buffer
import okio.BufferedSource
import okio.FileSystem
import okio.Path

sealed interface ImageResult {

    data class Image(
        val format: ImageFormat,
        val width: Int,
        val height: Int,
        val data: Buffer
    ): ImageResult {
        val rgb: IntArray get() = TODO()
    }

    class Error(
        val cause: String
    ): ImageResult

}

enum class ImageFormat { PNG, UNKNOWN }

class ImageIO(
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) {

    fun read(path: Path): ImageResult {
        fileSystem.read(path) {
            return when (getImageFormat()) {
                ImageFormat.PNG -> PngImageReader().read(this)
                else -> ImageResult.Error(cause = "Image type unknown")
            }
        }
    }
}

private fun BufferedSource.getImageFormat(): ImageFormat {
    val header = readByteArray(8)
    return when {
        header.isPngHeader() -> ImageFormat.PNG
        else -> ImageFormat.UNKNOWN
    }
}

private fun ByteArray.isPngHeader(): Boolean =
    this[0] == 137.toByte() &&
            this[1] == 80.toByte() &&
            this[2] == 78.toByte() &&
            this[3] == 71.toByte() &&
            this[4] == 13.toByte() &&
            this[5] == 10.toByte() &&
            this[6] == 26.toByte() &&
            this[7] == 10.toByte()
