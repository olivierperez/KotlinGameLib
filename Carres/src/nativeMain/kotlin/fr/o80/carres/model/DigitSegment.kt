package fr.o80.carres.model

enum class DigitSegment(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
) {
    TOP(0f, 0f, 1f, 0f),
    MIDDLE(0f, .5f, 1f, .5f),
    BOTTOM(0f, 1f, 1f, 1f),
    TOP_LEFT(0f, 0f, 0f, .5f),
    BOTTOM_LEFT(0f, .5f, 0f, 1f),
    TOP_RIGHT(1f, 0f, 1f, .5f),
    BOTTOM_RIGHT(1f, .5f, 1f, 1f),
}

val digits: Map<Int, List<DigitSegment>> = mapOf(
    0 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_RIGHT,
        DigitSegment.BOTTOM_RIGHT,
        DigitSegment.BOTTOM,
        DigitSegment.BOTTOM_LEFT,
        DigitSegment.TOP_LEFT
    ),
    1 to listOf(
        DigitSegment.TOP_RIGHT,
        DigitSegment.BOTTOM_RIGHT
    ),
    2 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_RIGHT,
        DigitSegment.MIDDLE,
        DigitSegment.BOTTOM_LEFT,
        DigitSegment.BOTTOM
    ),
    3 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_RIGHT,
        DigitSegment.MIDDLE,
        DigitSegment.BOTTOM_RIGHT,
        DigitSegment.BOTTOM
    ),
    4 to listOf(
        DigitSegment.TOP_LEFT,
        DigitSegment.MIDDLE,
        DigitSegment.TOP_RIGHT,
        DigitSegment.BOTTOM_RIGHT
    ),
    5 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_LEFT,
        DigitSegment.MIDDLE,
        DigitSegment.BOTTOM_RIGHT,
        DigitSegment.BOTTOM
    ),
    6 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_LEFT,
        DigitSegment.BOTTOM_LEFT,
        DigitSegment.BOTTOM,
        DigitSegment.BOTTOM_RIGHT,
        DigitSegment.MIDDLE
    ),
    7 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_RIGHT,
        DigitSegment.BOTTOM_RIGHT
    ),
    8 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_LEFT,
        DigitSegment.BOTTOM_LEFT,
        DigitSegment.MIDDLE,
        DigitSegment.TOP_RIGHT,
        DigitSegment.BOTTOM_RIGHT,
        DigitSegment.BOTTOM
    ),
    9 to listOf(
        DigitSegment.TOP,
        DigitSegment.TOP_LEFT,
        DigitSegment.MIDDLE,
        DigitSegment.TOP_RIGHT,
        DigitSegment.BOTTOM_RIGHT,
        DigitSegment.BOTTOM
    )
)