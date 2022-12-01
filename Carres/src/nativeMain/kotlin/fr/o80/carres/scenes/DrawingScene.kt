package fr.o80.carres.scenes

import fr.o80.carres.CarresSceneManager
import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.model.digits
import fr.o80.gamelib.Scene
import fr.o80.gamelib.dsl.Draw
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.KeyPipeline
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import fr.o80.gamelib.loop.ScrollPipeline
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.model.Grid
import fr.o80.gamelib.model.gridOf
import fr.o80.gamelib.service.Services
import interop.*
import kotlin.math.pow

private const val gridWidth: Float = 3f

private const val margin: Float = 20f

private const val numberWidth: Float = 3f
private const val numberMargin = .3f

private const val paddingOfColoredCell = .15f

private const val zoomSpeed: Float = .9f

class DrawingScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private lateinit var window: Window

    private var columnWidth: Float = 0f
    private var rowHeight: Float = 0f
    private var zoom: Float = 1f

    private var mousePosition: SquarePosition? = null
    private var mousePositionInDrawing: SquarePosition? = null

    private lateinit var convertMousePositionToGrid: ConvertMousePositionToGrid
    private lateinit var coloredCells: Grid<Boolean>

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
        mouseMovePipeline: MouseMovePipeline,
        scrollPipeline: ScrollPipeline
    ) {
        this.window = window
        this.columnWidth = (window.width - 2 * margin) / drawingObjective.columnsCount
        this.rowHeight = (window.height - 2 * margin) / drawingObjective.rowsCount
        this.convertMousePositionToGrid = ConvertMousePositionToGrid(
            window.width, window.height, margin, columnWidth, rowHeight
        )
        this.coloredCells = gridOf(
            drawingObjective.width,
            drawingObjective.height
        ) { _, _ -> false }

        keyPipeline.onKey(GLFW_KEY_ESCAPE, GLFW_PRESS) { sceneManager.quit() }
        mouseMovePipeline.onMove { x, y ->
            mousePosition = convertMousePositionToGrid.invoke(x = x, y = y, zoom = zoom)
            mousePositionInDrawing = mousePosition?.minus(
                Pair(
                    drawingObjective.columnsCountInHorizontal,
                    drawingObjective.rowsCountInVertical
                )
            )
        }
        mouseButtonPipeline.onButton(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS) { _, _ ->
            mousePositionInDrawing
                ?.takeIf { it.x in 0 until drawingObjective.width && it.y in 0 until drawingObjective.height }
                ?.let(::toggleColor)
        }
        scrollPipeline.onScroll { _, yOffset ->
            zoom *= zoomSpeed.pow(-yOffset.toFloat())
        }
    }

    private fun toggleColor(position: SquarePosition) {
        coloredCells[position.x, position.y] = !(coloredCells[position.x, position.y] ?: false)
    }

    override fun close() {
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            pushed {
                translate(
                    x = window.width / 2 * (1 - zoom),
                    y = window.height / 2 * (1 - zoom),
                    z = 0f
                )
                scale(zoom, zoom, 0f)
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

                drawColorizedCells(
                    margin = margin,
                    columnWidth = columnWidth,
                    rowHeight = rowHeight,
                    columnsCountInHorizontal = drawingObjective.columnsCountInHorizontal,
                    rowsCountInVertical = drawingObjective.rowsCountInVertical,
                    coloredCells = coloredCells
                )

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
            lineWidth(numberWidth * zoom)
            color(numbersColor)
            translate(left, top, 0f)
            translate(numberMargin * width, numberMargin * height, 0f)
            scale(1f - 2 * numberMargin, 1f - 2 * numberMargin, 0f)
            scale(width, height, 0f)

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
        lineWidth(gridWidth * zoom)
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
