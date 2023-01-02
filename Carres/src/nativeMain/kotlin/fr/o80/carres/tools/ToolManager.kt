package fr.o80.carres.tools

import fr.o80.carres.model.DrawingObjective
import fr.o80.carres.model.SquarePosition
import fr.o80.carres.scenes.drawing.DrawingSettings
import fr.o80.carres.scenes.drawing.MousePositionManager
import fr.o80.carres.tools.line.LineTool
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import interop.*

class ToolManager(
    mousePositionManager: MousePositionManager,
    objective: DrawingObjective,
    drawingSettings: DrawingSettings,
    mouseButtonPipeline: MouseButtonPipeline,
    mouseMovePipeline: MouseMovePipeline,
    private val setColor: (color: Boolean, position: SquarePosition) -> Unit,
    private val getColor: (position: SquarePosition) -> Boolean,
) {

    private val tool: Tool = LineTool(
        objective,
        drawingSettings,
        origin = SquarePosition(objective.columnsCountInHorizontal, objective.rowsCountInVertical),
        setColor = { color, position -> setColor(color, position) }
    ) { position -> getColor(position) }

    init {
        mouseButtonPipeline.onButton(
            GLFW_MOUSE_BUTTON_LEFT,
            GLFW_PRESS
        ) { _, _ -> mousePositionManager.positionInGrid?.let(tool::onMousePress) }
        mouseButtonPipeline.onButton(
            GLFW_MOUSE_BUTTON_LEFT,
            GLFW_RELEASE
        ) { _, _ -> mousePositionManager.positionInGrid?.let(tool::onMouseRelease) }
        mouseMovePipeline.onMove { _, _ -> mousePositionManager.positionInGrid?.let(tool::onMouseMove) }
    }

    fun render(columnWidth: Float, rowHeight: Float) {
        tool.render(columnWidth, rowHeight)
    }
}
