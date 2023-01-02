package fr.o80.carres.scenes.drawing

import fr.o80.carres.model.SquarePosition
import fr.o80.gamelib.PositionD
import fr.o80.gamelib.loop.MouseMovePipeline

class MousePositionManager(
    private val convertMousePositionToGrid: ConvertMousePositionToGrid,
    mouseMovePipeline: MouseMovePipeline,
) {

    private var mousePosition: PositionD? = null

    var positionInGrid: SquarePosition? = null
        private set

    init {
        mouseMovePipeline.onMove { x, y ->
            mousePosition = PositionD(x, y)
        }
    }

    fun update() {
        positionInGrid = mousePosition?.let { convertMousePositionToGrid(it) }
    }
}