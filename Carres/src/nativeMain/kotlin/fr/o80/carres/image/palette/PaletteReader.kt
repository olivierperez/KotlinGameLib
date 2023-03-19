package fr.o80.carres.image.palette

import fr.o80.gamelib.model.Color
import okio.BufferedSource

interface PaletteReader {
    fun readImage(source: BufferedSource): Array<Color>
}
