package fr.o80.carres.scenes

import fr.o80.carres.model.SquarePosition

class ConvertMousePositionToGrid(
    private val width: Int,
    private val height: Int,
    private val margin: Float,
    private val columnWidth: Float,
    private val rowHeight: Float,
) {
    operator fun invoke(
        x: Double,
        y: Double,
        zoom: Float
    ): SquarePosition? {
        val outLeft = width / 2 - (width / 2 - margin) * zoom
        val outRight = width - outLeft
        val outTop = height / 2 - (height / 2 - margin) * zoom
        val outBottom = height - outTop

        if (x < outLeft || y < outTop || x >= outRight || y >= outBottom) {
            return null
        }

        val squareX = ((x - outLeft) / (columnWidth * zoom)).toInt()
        val squareY = ((y - outTop) / (rowHeight * zoom)).toInt()

        return SquarePosition(squareX, squareY)
    }
}
