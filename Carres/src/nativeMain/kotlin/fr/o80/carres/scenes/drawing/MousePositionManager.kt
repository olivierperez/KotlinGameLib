package fr.o80.carres.scenes.drawing

import fr.o80.carres.model.SquarePosition
import fr.o80.gamelib.PositionD
import fr.o80.gamelib.loop.MouseMovePipeline

class MousePositionManager(
    mouseMovePipeline: MouseMovePipeline,
    private val convertMousePositionToGrid: ConvertMousePositionToGrid,
) {

    private var mousePosition: PositionD? = null

    var inGrid: SquarePosition? = null
        private set

    init {
        mouseMovePipeline.onMove { x, y ->
            mousePosition = PositionD(x, y)
        }
    }

    fun update() {
        inGrid = mousePosition?.let { convertMousePositionToGrid(it) }
    }
}