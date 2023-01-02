package fr.o80.carres.tools.line

import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.scenes.coloredPreviewCell
import fr.o80.carres.scenes.drawing.DrawingSettings
import fr.o80.carres.tools.Tool
import fr.o80.gamelib.dsl.draw

class LineTool(
    private val drawingObjective: DrawingObjective,
    private val drawingSettings: DrawingSettings,
    private val origin: SquarePosition,
    private val setColor: (color: Boolean, position: SquarePosition) -> Unit,
    private val getColor: (position: SquarePosition) -> Boolean,
) : Tool {

    private val gridWidth = drawingSettings.gridWidth
    private val paddingOfColoredCell = drawingSettings.paddingOfColoredCell

    private var pressPosition: SquarePosition? = null
    private var positionToPreview: List<SquarePosition> = emptyList()

    override fun render(columnWidth: Float, rowHeight: Float) {
        draw {
            pushed {
                color(coloredPreviewCell)
                translate(
                    x = drawingSettings.margin + drawingObjective.columnsCountInHorizontal * columnWidth,
                    y = drawingSettings.margin + drawingObjective.rowsCountInVertical * rowHeight,
                    z = 0f
                )
                positionToPreview.forEach { (x, y) ->
                    pushed {
                        translate(
                            x = columnWidth * x + gridWidth / 2,
                            y = rowHeight * y + gridWidth / 2,
                            z = 0f
                        )
                        scale(1 - paddingOfColoredCell, 1 - paddingOfColoredCell, 0f)
                        translate(
                            x = (paddingOfColoredCell / 2) * columnWidth,
                            y = (paddingOfColoredCell / 2) * rowHeight,
                            z = 0f
                        )
                        quad(
                            x1 = 0f,
                            y1 = 0f,
                            x2 = columnWidth - gridWidth / 2,
                            y2 = rowHeight - gridWidth / 2,
                        )
                    }
                }
            }
        }
    }

    override fun onMousePress(positionInGrid: SquarePosition) {
        pressPosition = positionInGrid - origin
    }

    override fun onMouseRelease(positionInGrid: SquarePosition) {
        val pressPosition = pressPosition ?: return
        val releasePosition = positionInGrid - origin

        val newColor = !getColor(pressPosition)
        getPositionsBetween(pressPosition, releasePosition)
            ?.filter(::isInDrawing)
            ?.forEach { cellPosition -> setColor(newColor, cellPosition) }

        this.pressPosition = null
        this.positionToPreview = emptyList()
    }

    override fun onMouseMove(positionInGrid: SquarePosition) {
        val pressPosition = pressPosition ?: return
        val currentPositionInGrid = positionInGrid - origin

        positionToPreview = getPositionsBetween(pressPosition, currentPositionInGrid)
            ?.filter(::isInDrawing)
            ?: emptyList()
    }

    private fun getPositionsBetween(
        pressPosition: SquarePosition,
        releasePosition: SquarePosition
    ): List<SquarePosition>? {
        return if (releasePosition.x == pressPosition.x || releasePosition.y == pressPosition.y) {
            (pressPosition..releasePosition)
        } else {
            null
        }
    }

    private fun isInDrawing(squarePosition: SquarePosition): Boolean {
        return squarePosition.x in 0 until drawingObjective.width &&
                squarePosition.y in 0 until drawingObjective.height
    }
}

private operator fun SquarePosition.rangeTo(other: SquarePosition): List<SquarePosition> {
    return when {
        this.y == other.y && this.x < other.x -> (this.x..other.x).map { SquarePosition(it, this.y) }
        this.y == other.y && this.x >= other.x -> (other.x..this.x).map { SquarePosition(it, this.y) }
        this.x == other.x && this.y < other.y -> (this.y..other.y).map { SquarePosition(this.x, it) }
        this.x == other.x && this.y >= other.y -> (other.y..this.y).map { SquarePosition(this.x, it) }
        else -> {
            println("!! Inputs can't create a range $this / $other !!")
            emptyList()
        }
    }
}
