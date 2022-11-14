package fr.o80.carres.scenes

import fr.o80.carres.model.SquarePosition

class ConvertMousePositionToGrid(
    private val width: Int,
    private val height: Int,
    private val margin: Float,
    private val columnWidth: Float,
    private val rowHeight: Float,
) {
    operator fun invoke(x: Double, y: Double): SquarePosition? {
        if (x < margin || y < margin || x >= width - margin || y >= height - margin) {
            return null
        }

        val squareX = ((x - margin) / columnWidth).toInt()
        val squareY = ((y - margin) / rowHeight).toInt()

        return SquarePosition(squareX, squareY)
    }
}
