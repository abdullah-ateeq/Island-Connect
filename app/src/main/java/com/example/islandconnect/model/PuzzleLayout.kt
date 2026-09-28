package com.example.islandconnect.model

object PuzzleLayout {
    const val GRID_SIZE = 6

    val islands: List<IslandCell> = listOf(
        IslandCell(
            id = 1,
            name = "North Haven",
            position = GridPosition(row = 1, col = 1),
            connections = setOf(Direction.RIGHT, Direction.DOWN)
        ),
        IslandCell(
            id = 2,
            name = "East Lookout",
            position = GridPosition(row = 1, col = 4),
            connections = setOf(Direction.LEFT, Direction.DOWN)
        ),
        IslandCell(
            id = 3,
            name = "West Sanctuary",
            position = GridPosition(row = 4, col = 1),
            connections = setOf(Direction.RIGHT, Direction.UP)
        ),
        IslandCell(
            id = 4,
            name = "South Refuge",
            position = GridPosition(row = 4, col = 4),
            connections = setOf(Direction.LEFT, Direction.UP)
        )
    )

    val rockPositions: Set<GridPosition> = setOf(
        GridPosition(row = 0, col = 2),
        GridPosition(row = 2, col = 2),
        GridPosition(row = 3, col = 3),
        GridPosition(row = 5, col = 3),
        GridPosition(row = 2, col = 5)
    )

    fun createInitialBoard(): List<List<BoardCell>> {
        val islandMap = islands.associateBy { it.position }
        return List(GRID_SIZE) { row ->
            List(GRID_SIZE) { col ->
                val pos = GridPosition(row, col)
                when {
                    islandMap.containsKey(pos) -> BoardCell.Island(islandMap.getValue(pos).id)
                    rockPositions.contains(pos) -> BoardCell.Rock
                    else -> BoardCell.Water
                }
            }
        }
    }

    /**
     * Deterministic, balanced tile collection of 16 pieces:
     * 6 Straight, 5 Corner, 3 T-Junction, 2 Cross.
     */
    fun createTileCollection(): List<BridgeTile> = listOf(
        BridgeTile(id = 1, kind = TileKind.STRAIGHT),
        BridgeTile(id = 2, kind = TileKind.CORNER),
        BridgeTile(id = 3, kind = TileKind.T_JUNCTION),
        BridgeTile(id = 4, kind = TileKind.STRAIGHT),
        BridgeTile(id = 5, kind = TileKind.CROSS),
        BridgeTile(id = 6, kind = TileKind.CORNER),
        BridgeTile(id = 7, kind = TileKind.STRAIGHT),
        BridgeTile(id = 8, kind = TileKind.T_JUNCTION),
        BridgeTile(id = 9, kind = TileKind.CORNER),
        BridgeTile(id = 10, kind = TileKind.STRAIGHT),
        BridgeTile(id = 11, kind = TileKind.CROSS),
        BridgeTile(id = 12, kind = TileKind.CORNER),
        BridgeTile(id = 13, kind = TileKind.STRAIGHT),
        BridgeTile(id = 14, kind = TileKind.T_JUNCTION),
        BridgeTile(id = 15, kind = TileKind.CORNER),
        BridgeTile(id = 16, kind = TileKind.STRAIGHT)
    )

    fun createInitialTrayAndBag(): Pair<List<BridgeTile>, List<BridgeTile>> {
        val allTiles = createTileCollection()
        val tray = allTiles.take(3)
        val bag = allTiles.drop(3)
        return Pair(tray, bag)
    }
}
