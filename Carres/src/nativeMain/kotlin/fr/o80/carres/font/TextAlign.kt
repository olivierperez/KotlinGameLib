package fr.o80.carres.font

sealed interface TextAlign {
    fun xOffset(availableWidth: Float, textWidth: Float): Float

    object Left : TextAlign {
        override fun xOffset(availableWidth: Float, textWidth: Float): Float = 0f
    }

    object Center : TextAlign {
        override fun xOffset(availableWidth: Float, textWidth: Float): Float = (availableWidth - textWidth) / 2
    }

    object Right : TextAlign {
        override fun xOffset(availableWidth: Float, textWidth: Float): Float = availableWidth - textWidth
    }
}
