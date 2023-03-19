package fr.o80.carres.image.palette

class PaletteReaderFactory {
    fun create(bitsPerPixel: Int, width: Int, height: Int): PaletteReader {
        return when(bitsPerPixel) {
            24 -> Bits24PaletteReader(width, height)
            32 -> Bits32PaletteReader(width, height)
            else -> error("Bits per pixel not handled: $bitsPerPixel")
        }
    }
}
