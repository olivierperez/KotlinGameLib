package fr.o80.carres.scenes.drawing

import fr.o80.carres.CarresSceneManager
import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.scenes.background
import fr.o80.carres.scenes.gridColor
import fr.o80.carres.scenes.hoverBackground
import fr.o80.carres.scenes.numbersBackground
import fr.o80.carres.scenes.numbersColor
import fr.o80.carres.tools.ToolManager
import fr.o80.gamelib.Scene
import fr.o80.gamelib.dsl.Draw
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.loop.KeyPipeline
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import fr.o80.gamelib.loop.ScrollPipeline
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.service.Services
import interop.*

class DrawingScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private lateinit var window: Window

    private var columnWidth: Float = 0f
    private var rowHeight: Float = 0f

    private val drawingSettings: DrawingSettings = DrawingSettings()

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

    private lateinit var zoomManager: ZoomManager
    private lateinit var mousePositionManager: MousePositionManager
    private lateinit var currentDrawing: CurrentDrawing
    private lateinit var toolManager: ToolManager

    private val gridWidth = drawingSettings.gridWidth
    private val margin = drawingSettings.margin
    private val numberMargin = drawingSettings.numberMargin
    private val numberWidth = drawingSettings.numberWidth

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

        this.zoomManager = ZoomManager(window, scrollPipeline, drawingSettings.zoomSpeed)
        val convertMousePositionToGrid = ConvertMousePositionToGrid(
            window,
            margin,
            columnWidth,
            rowHeight
        )
        this.mousePositionManager = MousePositionManager(
            convertMousePositionToGrid,
            mouseMovePipeline
        )
        this.currentDrawing = CurrentDrawing(
            drawingObjective,
            drawingSettings
        )
        this.toolManager = ToolManager(
            mousePositionManager,
            drawingObjective,
            drawingSettings,
            mouseButtonPipeline,
            mouseMovePipeline,
            setColor = currentDrawing::setColor,
            getColor = currentDrawing::getColor
        )

        keyPipeline.onKey(GLFW_KEY_ESCAPE, GLFW_PRESS) { sceneManager.quit() }
    }

    override fun close() {
    }

    override suspend fun update() {
        mousePositionManager.update()
    }

    override suspend fun render() {
        draw {
            clear(background)

            mousePositionManager.positionInGrid
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

            currentDrawing.render(
                columnWidth = columnWidth,
                rowHeight = rowHeight,
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

            toolManager.render(
                columnWidth = columnWidth,
                rowHeight = rowHeight,
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
            lineWidth(numberWidth)
            color(numbersColor)
            translate(left, top, 0f)
            translate(numberMargin * width, numberMargin * height, 0f)
            scale(1f - 2 * numberMargin, 1f - 2 * numberMargin, 0f)
            scale(width, height, 0f)

            fr.o80.carres.model.digits[value]!!.forEach { segment ->
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