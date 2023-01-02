package fr.o80.carres.tools

import fr.o80.carres.model.SquarePosition

interface Tool {
    fun render(columnWidth: Float, rowHeight: Float)
    fun onMouseRelease(positionInGrid: SquarePosition)
    fun onMousePress(positionInGrid: SquarePosition)
    fun onMouseMove(positionInGrid: SquarePosition)
}
