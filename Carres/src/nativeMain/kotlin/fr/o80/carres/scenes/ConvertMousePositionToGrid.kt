package fr.o80.carres.scenes

import fr.o80.carres.model.SquarePosition
import fr.o80.carres.scenes.drawing.ZoomManager
import fr.o80.gamelib.loop.Window

class ConvertMousePositionToGrid(
    private val zoomManager: ZoomManager,
    private val window: Window,
    private val margin: Float,
    private val columnWidth: Float,
    private val rowHeight: Float,
) {
    private val width: Int get() = window.width
    private val height: Int get() = window.height
    private val zoom: Float get() = zoomManager.zoom

    operator fun invoke(
        x: Double,
        y: Double
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
