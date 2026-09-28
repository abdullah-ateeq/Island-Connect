package com.example.islandconnect.model

enum class TileKind {
    STRAIGHT,
    CORNER,
    T_JUNCTION,
    CROSS;

    /**
     * Base open connection directions at rotation = 0:
     * STRAIGHT: LEFT + RIGHT
     * CORNER: UP + RIGHT
     * T_JUNCTION: UP + LEFT + RIGHT
     * CROSS: UP + RIGHT + DOWN + LEFT
     */
    val baseConnections: Set<Direction>
        get() = when (this) {
            STRAIGHT -> setOf(Direction.LEFT, Direction.RIGHT)
            CORNER -> setOf(Direction.UP, Direction.RIGHT)
            T_JUNCTION -> setOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            CROSS -> setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        }
}
