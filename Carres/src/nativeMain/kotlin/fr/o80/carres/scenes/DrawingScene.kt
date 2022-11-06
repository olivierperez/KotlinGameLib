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
import fr.o80.gamelib.dsl.Draw
import interop.*
import platform.opengl32.*

private const val margin: Float = 20f

class DrawingScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private lateinit var window: Window

    private val drawingObjective = DrawingObjective(
        verticalNumbers = listOf(
            listOf(4),
            listOf(1, 2),
            listOf(1, 3),
            listOf(1, 1, 3),
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
    }

    override fun close() {
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            clear(0.3f)

            drawGrid(
                width = window.width,
                height = window.height,
                margin = margin,
                columnsCounts = drawingObjective.columnsCount,
                rowsCounts = drawingObjective.rowsCount,
                columnsCountInHorizontal = drawingObjective.columnsCountInHorizontal,
                rowsCountInVertical = drawingObjective.rowsCountInVertical,
            )
        }
    }

    private fun Draw.drawGrid(
        width: Int,
        height: Int,
        margin: Float,
        columnsCounts: Int,
        rowsCounts: Int,
        columnsCountInHorizontal: Int,
        rowsCountInVertical: Int
    ) {
        lineWidth(3f)
        color(0f, 0f, 0f)

        val columnSize = (width - (2 * margin)) / columnsCounts
        val rowSize = (height - (2 * margin)) / rowsCounts
        val horizontalNumbersWidth = columnsCountInHorizontal * columnSize
        val verticalNumbersHeight = rowsCountInVertical * rowSize

        repeat(columnsCounts + 1) { columnIndex ->
            val x = margin + columnIndex * columnSize
            val top = if (columnIndex >= columnsCountInHorizontal) {
                margin
            } else {
                margin + verticalNumbersHeight
            }
            line(x, top, x, height - margin)
        }

        repeat(rowsCounts + 1) { rowIndex ->
            val y = margin + rowIndex * rowSize
            val left = if (rowIndex >= rowsCountInVertical) {
                margin
            } else {
                margin + horizontalNumbersWidth
            }
            line(left, y, width - margin, y)
        }
    }
}
