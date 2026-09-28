package com.example.islandconnect.model

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        RIGHT -> LEFT
        DOWN -> UP
        LEFT -> RIGHT
    }

    fun rotateClockwise(): Direction = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }
}
