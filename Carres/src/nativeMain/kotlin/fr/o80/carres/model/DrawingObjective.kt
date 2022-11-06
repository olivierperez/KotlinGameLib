package fr.o80.carres.model

data class DrawingObjective(
    val verticalNumbers: List<List<Int>>,
    val horizontalNumbers: List<List<Int>>,
) {
    val columnsCountInHorizontal = horizontalNumbers.maxOf(List<Int>::size)
    val rowsCountInVertical = verticalNumbers.maxOf(List<Int>::size)
    val columnsCount: Int = columnsCountInHorizontal + verticalNumbers.size
    val rowsCount: Int = verticalNumbers.maxOf(List<Int>::size) + horizontalNumbers.size
}
