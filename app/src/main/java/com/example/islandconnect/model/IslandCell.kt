package com.example.islandconnect.model

data class IslandCell(
    val id: Int,
    val name: String,
    val position: GridPosition,
    val connections: Set<Direction>
)
