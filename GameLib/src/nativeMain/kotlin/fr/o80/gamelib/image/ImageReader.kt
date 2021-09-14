package fr.o80.gamelib.image

import okio.BufferedSource

interface ImageReader {
    fun read(source: BufferedSource) : ImageResult
}
