package com.example.islandconnect.model

data class Snapshot(
    val board: List<List<BoardCell>>,
    val tray: List<BridgeTile>,
    val bag: List<BridgeTile>,
    val selectedTileId: Int?,
    val moveCount: Int,
    val connectedIslandIds: Set<Int>,
    val connectedCellPositions: Set<GridPosition>,
    val isComplete: Boolean,
    val isFailed: Boolean
)

data class IslandConnectState(
    val board: List<List<BoardCell>>,
    val tray: List<BridgeTile>,
    val bag: List<BridgeTile>,
    val selectedTileId: Int? = null,
    val moveCount: Int = 0,
    val connectedIslandIds: Set<Int> = emptySet(),
    val connectedCellPositions: Set<GridPosition> = emptySet(),
    val isComplete: Boolean = false,
    val isFailed: Boolean = false,
    val elapsedMillis: Long = 0L,
    val isAnimating: Boolean = false,
    val newlyConnectedIsland: Int? = null,
    val undoStack: List<Snapshot> = emptyList()
) {
    val totalTilesRemaining: Int get() = tray.size + bag.size
    val selectedTile: BridgeTile? get() = tray.find { it.id == selectedTileId }
    val canUndo: Boolean get() = undoStack.isNotEmpty() && !isComplete && !isFailed && !isAnimating
    val isGameActive: Boolean get() = !isComplete && !isFailed

    companion object {
        fun initial(): IslandConnectState {
            val board = PuzzleLayout.createInitialBoard()
            val (tray, bag) = PuzzleLayout.createInitialTrayAndBag()
            // Initial network check (Island 1 starts connected to itself)
            val initialIslandIds = setOf(1)
            val initialPositions = setOf(GridPosition(1, 1))

            return IslandConnectState(
                board = board,
                tray = tray,
                bag = bag,
                selectedTileId = tray.firstOrNull()?.id,
                moveCount = 0,
                connectedIslandIds = initialIslandIds,
                connectedCellPositions = initialPositions,
                isComplete = false,
                isFailed = false,
                elapsedMillis = 0L,
                isAnimating = false,
                newlyConnectedIsland = null,
                undoStack = emptyList()
            )
        }
    }
}
