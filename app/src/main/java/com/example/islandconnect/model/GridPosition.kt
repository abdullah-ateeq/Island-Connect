package com.example.islandconnect.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun neighbor(direction: Direction, maxRows: Int = 6, maxCols: Int = 6): GridPosition? {
        val nextRow = when (direction) {
            Direction.UP -> row - 1
            Direction.DOWN -> row + 1
            else -> row
        }
        val nextCol = when (direction) {
            Direction.LEFT -> col - 1
            Direction.RIGHT -> col + 1
            else -> col
        }
        return if (nextRow in 0 until maxRows && nextCol in 0 until maxCols) {
            GridPosition(nextRow, nextCol)
        } else {
            null
        }
    }
}
