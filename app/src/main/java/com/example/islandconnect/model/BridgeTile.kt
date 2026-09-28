package com.example.islandconnect.model

data class BridgeTile(
    val id: Int,
    val kind: TileKind,
    val rotation: Int = 0 // Normalized to 0, 90, 180, 270
) {
    val connections: Set<Direction>
        get() {
            val steps = ((rotation % 360 + 360) % 360) / 90
            return kind.baseConnections.map { dir ->
                var current = dir
                repeat(steps) {
                    current = current.rotateClockwise()
                }
                current
            }.toSet()
        }

    fun rotatedClockwise(): BridgeTile {
        val newRotation = (rotation + 90) % 360
        return copy(rotation = newRotation)
    }

    val accessibleDescription: String
        get() = buildString {
            append(
                when (kind) {
                    TileKind.STRAIGHT -> "Straight bridge tile"
                    TileKind.CORNER -> "Corner bridge tile"
                    TileKind.T_JUNCTION -> "T-junction bridge tile"
                    TileKind.CROSS -> "Cross bridge tile"
                }
            )
            append(" rotated $rotation degrees, openings facing ")
            append(connections.sortedBy { it.ordinal }.joinToString(", ") { it.name.lowercase() })
        }
}
