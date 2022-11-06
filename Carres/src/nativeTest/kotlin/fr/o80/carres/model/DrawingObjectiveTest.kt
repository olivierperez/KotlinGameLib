package fr.o80.carres.model

import kotlin.test.Test
import kotlin.test.assertEquals

class DrawingObjectiveTest {
    @Test
    fun shouldTest() {
        val drawingObjective = DrawingObjective(
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

        assertEquals(10, drawingObjective.columnsCount)
        assertEquals(8, drawingObjective.rowsCount)
    }
}
