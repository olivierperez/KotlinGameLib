package fr.o80.carres.scenes

import fr.o80.gamelib.Scene
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.KeyPipeline
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.service.Services
import fr.o80.carres.CarresSceneManager
import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.model.digits
import fr.o80.gamelib.dsl.Draw
import interop.*
import platform.opengl32.*

private const val margin: Float = 20f
private const val gridWidth: Float = 3f

class DrawingScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private lateinit var window: Window

    private var columnWidth: Float = 0f
    private var rowHeight: Float = 0f

    private var mousePosition: SquarePosition? = null

    private val drawingObjective = DrawingObjective(
        verticalNumbers = listOf(
            listOf(4),
            listOf(1, 2),
            listOf(1, 3),
            listOf(1, 1, 1),
            listOf(2, 1),
            listOf(1, 3),
            listOf(3),
        ),
        horizontalNumbers = listOf(
            listOf(4),
            listOf(2, 1),
            listOf(1, 2, 2),
            listOf(3, 2),
            listOf(7),
        )
    )

    override fun open(
        window: Window,
        services: Services,
        keyPipeline: KeyPipeline,
        mouseButtonPipeline: MouseButtonPipeline,
        mouseMovePipeline: MouseMovePipeline
    ) {
        this.window = window
        keyPipeline.onKey(GLFW_KEY_ESCAPE, GLFW_PRESS) { sceneManager.quit() }
        mouseMovePipeline.onMove { x, y ->
            mousePosition = globalToSquarePosition(
                width = window.width,
                height = window.height,
                margin = margin,
                columnWidth = columnWidth,
                rowHeight = rowHeight,
                x = x,
                y = y
            )
        }
        columnWidth = (window.width - 2 * margin) / drawingObjective.columnsCount
        rowHeight = (window.height - 2 * margin) / drawingObjective.rowsCount
    }

    private fun globalToSquarePosition(
        width: Int,
        height: Int,
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        x: Double,
        y: Double
    ): SquarePosition? {
        if (x < margin || y < margin || x >= width - margin || y >= height - margin) {
            return null
        }

        val squareX = ((x - margin) / columnWidth).toInt()
        val squareY = ((y - margin) / rowHeight).toInt()

        return SquarePosition(squareX, squareY)
    }

    override fun close() {
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            clear(background)

            mousePosition
                ?.takeIf {
                    it.x >= drawingObjective.columnsCountInHorizontal && it.y >= drawingObjective.rowsCountInVertical
                }
                ?.let {
                    drawHover(
                        margin = margin,
                        columnWidth = columnWidth,
                        rowHeight = rowHeight,
                        position = it
                    )
                }

            drawNumbersBackground(
                width = window.width,
                height = window.height,
                margin = margin,
                columnWidth = columnWidth,
                rowHeight = rowHeight,
                columnsCountInHorizontal = drawingObjective.columnsCountInHorizontal,
                rowsCountInVertical = drawingObjective.rowsCountInVertical,
            )

            drawGrid(
                width = window.width,
                height = window.height,
                margin = margin,
                columnWidth = columnWidth,
                rowHeight = rowHeight,
                columnsCounts = drawingObjective.columnsCount,
                rowsCounts = drawingObjective.rowsCount,
                columnsCountInHorizontal = drawingObjective.columnsCountInHorizontal,
                rowsCountInVertical = drawingObjective.rowsCountInVertical,
            )

            drawNumbers(
                margin = margin,
                columnWidth = columnWidth,
                rowHeight = rowHeight,
                verticalNumbers = drawingObjective.verticalNumbers,
                horizontalNumbers = drawingObjective.horizontalNumbers,
                columnsCountInHorizontal = drawingObjective.columnsCountInHorizontal,
                rowsCountInVertical = drawingObjective.rowsCountInVertical,
            )
        }
    }

    private fun Draw.drawHover(
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        position: SquarePosition
    ) {
        color(hoverBackground)
        quad(
            x1 = margin + columnWidth * position.x + gridWidth / 2,
            y1 = margin + rowHeight * position.y + gridWidth / 2,
            x2 = margin + columnWidth * (position.x + 1) - gridWidth / 2,
            y2 = margin + rowHeight * (position.y + 1) - gridWidth / 2,
        )
    }

    private fun Draw.drawNumbersBackground(
        width: Int,
        height: Int,
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        columnsCountInHorizontal: Int,
        rowsCountInVertical: Int
    ) {
        color(numbersBackground)
        quad(
            x1 = margin + columnsCountInHorizontal * columnWidth,
            y1 = margin,
            x2 = width - margin,
            y2 = margin + rowsCountInVertical * rowHeight
        )
        quad(
            x1 = margin,
            y1 = margin + rowsCountInVertical * rowHeight,
            x2 = margin + columnsCountInHorizontal * columnWidth,
            y2 = height - margin
        )
    }

    private fun Draw.drawNumbers(
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        verticalNumbers: List<List<Int>>,
        horizontalNumbers: List<List<Int>>,
        columnsCountInHorizontal: Int,
        rowsCountInVertical: Int
    ) {
        verticalNumbers.forEachIndexed { columnIndex, column ->
            val columnLeft = margin + (columnIndex + columnsCountInHorizontal) * columnWidth
            column.forEachIndexed { numberIndex, number ->
                val numberTop = margin + (rowsCountInVertical - column.size + numberIndex) * rowHeight
                drawNumber(
                    top = numberTop,
                    left = columnLeft,
                    width = columnWidth,
                    height = rowHeight,
                    value = number
                )
            }
        }

        horizontalNumbers.forEachIndexed { rowIndex, row ->
            val rowTop = margin + (rowIndex + rowsCountInVertical) * rowHeight
            row.forEachIndexed { numberIndex, number ->
                val numberLeft = margin + (columnsCountInHorizontal - row.size + numberIndex) * columnWidth
                drawNumber(
                    top = rowTop,
                    left = numberLeft,
                    width = columnWidth,
                    height = rowHeight,
                    value = number
                )
            }
        }
    }

    private fun Draw.drawNumber(top: Float, left: Float, width: Float, height: Float, value: Int) {
        pushed {
            color(numbersColor)
            translate(left, top, 0f)
            translate(.2f * width, .2f * height, 0f)
            glScalef(.6f, .6f, 0f)
            glScalef(width, height, 0f)

            digits[value]!!.forEach { segment ->
                line(segment.x1, segment.y1, segment.x2, segment.y2)
            }
        }
    }

    private fun Draw.drawGrid(
        width: Int,
        height: Int,
        margin: Float,
        columnWidth: Float,
        rowHeight: Float,
        columnsCounts: Int,
        rowsCounts: Int,
        columnsCountInHorizontal: Int,
        rowsCountInVertical: Int
    ) {
        lineWidth(gridWidth)
        color(gridColor)

        val horizontalNumbersWidth = columnsCountInHorizontal * columnWidth
        val verticalNumbersHeight = rowsCountInVertical * rowHeight

        repeat(columnsCounts + 1) { columnIndex ->
            val x = margin + columnIndex * columnWidth
            val top = if (columnIndex in 1 until columnsCountInHorizontal) {
                margin + verticalNumbersHeight
            } else {
                margin
            }
            line(x, top, x, height - margin)
        }

        repeat(rowsCounts + 1) { rowIndex ->
            val y = margin + rowIndex * rowHeight
            val left = if (rowIndex in 1 until rowsCountInVertical) {
                margin + horizontalNumbersWidth
            } else {
                margin
            }
            line(left, y, width - margin, y)
        }
    }
}
