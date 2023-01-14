package fr.o80.carres.scenes.drawing

import fr.o80.carres.model.SquarePosition
import fr.o80.gamelib.PositionD
import fr.o80.gamelib.PositionF
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.pointer.getPointedPosition
import kotlinx.cinterop.refTo
import platform.opengl32.*
import platform.posix.fabsf

class ConvertMousePositionToGrid(
    private val window: Window,
    private val margin: Float,
    private val columnWidth: Float,
    private val rowHeight: Float,
) {
    private val width: Int get() = window.width
    private val height: Int get() = window.height

    operator fun invoke(
        mousePosition: PositionD
    ): SquarePosition? {
        val (x, y) = getPointedPosition(mousePosition) ?: return null

        val outLeft = width / 2 - (width / 2 - margin)
        val outRight = width - outLeft
        val outTop = height / 2 - (height / 2 - margin)
        val outBottom = height - outTop

        if (x < outLeft || y < outTop || x >= outRight || y >= outBottom) {
            return null
        }

        val squareX = ((x - outLeft) / columnWidth).toInt()
        val squareY = ((y - outTop) / rowHeight).toInt()

        return SquarePosition(squareX, squareY)
    }
}
