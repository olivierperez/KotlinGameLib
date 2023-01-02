package fr.o80.carres.model

data class SquarePosition(
    val x: Int,
    val y: Int
) {
    operator fun minus(other: Pair<Int, Int>): SquarePosition {
        return SquarePosition(x - other.first, y - other.second)
    }

    operator fun minus(other: SquarePosition): SquarePosition {
        return SquarePosition(x - other.x, y - other.y)
    }
}
