package fr.o80.gamelib.image

import okio.BufferedSource
import okio.FileSystem
import okio.Path

data class Image(
    val format: ImageFormat,
    val width: Int,
    val height: Int,
)

enum class ImageFormat { PNG, UNKNOWN }

class ImageIO(
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) {

    fun read(path: Path): Image {
        fileSystem.read(path) {
            return when (getImageFormat()) {
                ImageFormat.PNG -> PngImageReader().read(this)
                else -> UNKNOWN_IMAGE
            }
        }
    }

    companion object {
        val UNKNOWN_IMAGE: Image
            get() = Image(format = ImageFormat.UNKNOWN, -1, -1)
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
            this[3] == 71.toByte()
