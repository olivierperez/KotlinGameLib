package fr.o80.carres.image

import fr.o80.carres.image.palette.PaletteReader
import fr.o80.carres.image.palette.PaletteReaderFactory
import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Path

class BmpReader {
    fun read(path: Path): Image {
        return FileSystem.SYSTEM.read(path) {
            readByteArray(2) // "BM"

            readByteArray(4) // File size
            readByteArray(4) // Application info
            readByteArray(4) // Header size
            readByteArray(4) // Windows = 0x28

            val width = readByteArray(4).getULittleEndian()
            val height = readByteArray(4).getULittleEndian()

            readByteArray(2) // Plan = 0x01

            val colorBytes = readByteArray(2).getULittleEndian()
            check(colorBytes == 24 || colorBytes == 32) { "Only 24 bits & 32 bits color is implemented, found: $colorBytes" }

            val compression = readByteArray(4).getULittleEndian()
            check(compression == 0 || compression == 3) { "No compression allowed, found: $compression" }

            readByteArray(4) // image data size

            readByteArray(4) // H m/px
            readByteArray(4) // V m/px

            readByteArray(4) // Colors per palette
            readByteArray(4) // Primary colors per palette

            val paletteReader: PaletteReader = PaletteReaderFactory().create(colorBytes, width, height)
            val colors = paletteReader.readImage(this)

            Image(width, height, colors)
        }
    }
}



private fun ByteArray.getULittleEndian(): Int {
    return foldRight(0) { byte, acc -> acc * 256 + byte.toUByte().toInt() }
}

private fun ByteArray.getLittleEndian(): Int {
    return foldRight(0) { byte, acc -> acc * 256 + byte }
}

private fun ByteArray.debug(name: String): ByteArray {
    println("$name: ${this.toByteString()} => ${this.getLittleEndian()}")
    return this
}
