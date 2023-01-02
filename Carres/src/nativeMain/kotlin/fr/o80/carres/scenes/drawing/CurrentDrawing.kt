package fr.o80.carres.scenes.drawing

import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.scenes.coloredCell
import fr.o80.gamelib.dsl.Draw
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.model.Grid
import fr.o80.gamelib.model.gridOf

class CurrentDrawing(
    private val objective: DrawingObjective,
    private val drawingSettings: DrawingSettings
) {

    private val coloredCells: Grid<Boolean> = gridOf(
        objective.width,
        objective.height
    ) { _, _ -> false }

    private val gridWidth = drawingSettings.gridWidth
    private val paddingOfColoredCell = drawingSettings.paddingOfColoredCell

    fun getColor(cellPosition: SquarePosition): Boolean {
        return coloredCells[cellPosition.x, cellPosition.y] ?: false
    }

    fun setColor(newColor: Boolean, cellPosition: SquarePosition) {
        coloredCells[cellPosition.x, cellPosition.y] = newColor
    }

    fun render(
        columnWidth: Float,
        rowHeight: Float,
    ) {
        draw {
            drawColorizedCells(
                columnWidth = columnWidth,
                rowHeight = rowHeight,
                coloredCells = coloredCells
            )
        }
    }

    private fun Draw.drawColorizedCells(
        columnWidth: Float,
        rowHeight: Float,
        coloredCells: Grid<Boolean>
    ) {
        color(coloredCell)
        pushed {
            translate(
                x = drawingSettings.margin + objective.columnsCountInHorizontal * columnWidth,
                y = drawingSettings.margin + objective.rowsCountInVertical * rowHeight,
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