package com.example.islandconnect.model

sealed class BoardCell {
    data object Water : BoardCell()
    data object Rock : BoardCell()
    data class Island(val islandId: Int) : BoardCell()
    data class Tile(val tile: BridgeTile) : BoardCell()

    val isWater: Boolean get() = this is Water
    val isRock: Boolean get() = this is Rock
    val isIsland: Boolean get() = this is Island
    val isTile: Boolean get() = this is Tile
}
