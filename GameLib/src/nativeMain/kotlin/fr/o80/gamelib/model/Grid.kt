package fr.o80.gamelib.model

class Grid<T>(
    private val width: Int,
    private val height: Int,
    private val data: Array<Array<T>>
) {
    operator fun get(x: Int, y: Int): T? {
        return data[x][y]
    }

    operator fun set(x: Int, y: Int, value: T) {
        data[x][y] = value
    }

    fun forEach(block: (x: Int, y: Int, value: T?) -> Unit) {
        repeat(height) { y ->
            repeat(width) { x ->
                block(x, y, this[x, y])
            }
        }
    }
}

inline fun <reified T> gridOf(width: Int, height: Int, initialValue: (x: Int, y: Int) -> T): Grid<T> {
    return Grid(
        width,
        height,
        Array(width) { x ->
            Array(height) { y ->
                initialValue(x, y)
            }
        }
    )
}