package fr.o80.carres.scenes.drawing

import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.scenes.coloredCell
import fr.o80.gamelib.dsl.Draw
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.model.Grid
import fr.o80.gamelib.model.gridOf
import interop.*

class CurrentDrawing(
    private val mousePositionManager: MousePositionManager,
    private val objective: DrawingObjective,
    drawingSettings: DrawingSettings,
    mouseButtonPipeline: MouseButtonPipeline
) {
    private val coloredCells: Grid<Boolean> = gridOf(
        objective.width,
        objective.height
    ) { _, _ -> false }

    private val gridWidth = drawingSettings.gridWidth
    private val paddingOfColoredCell = drawingSettings.paddingOfColoredCell

    init {
        mouseButtonPipeline.onButton(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS) { _, _ ->
            mousePositionManager.inGrid?.let(::onClick)
        }
    }

    private fun onClick(squarePosition: SquarePosition) {
        val cellClicked = squarePosition - Pair(objective.columnsCountInHorizontal, objective.rowsCountInVertical)
        cellClicked
            .takeIf(::isInDrawing)
            ?.let(::toggleColor)
    }

    private fun isInDrawing(squarePosition: SquarePosition): Boolean {
        return squarePosition.x in 0 until objective.width &&
                squarePosition.y in 0 until objective.height
    }

    private fun toggleColor(position: SquarePosition) {
        coloredCells[position.x, position.y] = !(coloredCells[position.x, position.y] ?: false)
    }

    fun render(
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        columnsCountInHorizontal: Int,
        rowsCountInVertical: Int
    ) {
        draw {
            drawColorizedCells(
                margin = margin,
                columnWidth = columnWidth,
                rowHeight = rowHeight,
                columnsCountInHorizontal = columnsCountInHorizontal,
                rowsCountInVertical = rowsCountInVertical,
                coloredCells = coloredCells
            )
        }
    }

    private fun Draw.drawColorizedCells(
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        columnsCountInHorizontal: Int,
        rowsCountInVertical: Int,
        coloredCells: Grid<Boolean>
    ) {
        color(coloredCell)
        pushed {
            translate(
                x = margin + columnsCountInHorizontal * columnWidth,
                y = margin + rowsCountInVertical * rowHeight,
                z = 0f
            )
            coloredCells
                .forEachNotNull { x, y, value ->
                    if (!value) return@forEachNotNull

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
