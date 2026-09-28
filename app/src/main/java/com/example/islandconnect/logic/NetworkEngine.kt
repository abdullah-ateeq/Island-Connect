package com.example.islandconnect.logic

import com.example.islandconnect.model.BoardCell
import com.example.islandconnect.model.Direction
import com.example.islandconnect.model.GridPosition
import com.example.islandconnect.model.IslandCell
import com.example.islandconnect.model.IslandConnectState
import com.example.islandconnect.model.PuzzleLayout
import com.example.islandconnect.model.Snapshot

object NetworkEngine {
    const val GRID_SIZE = PuzzleLayout.GRID_SIZE
    const val PRIMARY_ISLAND_ID = 1

    /**
     * Resolves the open connection directions for a cell at [position].
     */
    fun getCellConnections(
        board: List<List<BoardCell>>,
        position: GridPosition,
        islandsMap: Map<Int, IslandCell>
    ): Set<Direction> {
        if (position.row !in 0 until GRID_SIZE || position.col !in 0 until GRID_SIZE) {
            return emptySet()
        }
        return when (val cell = board[position.row][position.col]) {
            is BoardCell.Island -> islandsMap[cell.islandId]?.connections ?: emptySet()
            is BoardCell.Tile -> cell.tile.connections
            is BoardCell.Water, is BoardCell.Rock -> emptySet()
        }
    }

    /**
     * Traverses the board starting from Island 1 using BFS.
     * Enforces strict bidirectional matching:
     * Current cell must have DIR towards neighbor, and neighbor must have DIR.opposite().
     * Returns:
     * - Pair(reachableIslandIds, reachableGridPositions)
     */
    fun findConnectedNetwork(
        board: List<List<BoardCell>>,
        islands: List<IslandCell>
    ): Pair<Set<Int>, Set<GridPosition>> {
        val islandsMap = islands.associateBy { it.id }
        val startIsland = islands.find { it.id == PRIMARY_ISLAND_ID }
            ?: return Pair(emptySet(), emptySet())

        val visited = mutableSetOf<GridPosition>()
        val connectedIslands = mutableSetOf<Int>()
        val queue = ArrayDeque<GridPosition>()

        visited.add(startIsland.position)
        connectedIslands.add(startIsland.id)
        queue.add(startIsland.position)

        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()
            val currentConns = getCellConnections(board, currentPos, islandsMap)

            for (dir in currentConns) {
                val neighborPos = currentPos.neighbor(dir, GRID_SIZE, GRID_SIZE) ?: continue
                if (neighborPos in visited) continue

                val neighborConns = getCellConnections(board, neighborPos, islandsMap)
                if (neighborConns.contains(dir.opposite())) {
                    // Valid bridge connection
                    visited.add(neighborPos)
                    val neighborCell = board[neighborPos.row][neighborPos.col]
                    if (neighborCell is BoardCell.Island) {
                        connectedIslands.add(neighborCell.islandId)
                    }
                    queue.add(neighborPos)
                }
            }
        }

        return Pair(connectedIslands, visited)
    }

    /**
     * Checks if a tile can be placed at [position].
     */
    fun canPlaceTile(
        state: IslandConnectState,
        position: GridPosition
    ): Boolean {
        if (!state.isGameActive || state.isAnimating) return false
        if (state.selectedTileId == null) return false
        if (position.row !in 0 until GRID_SIZE || position.col !in 0 until GRID_SIZE) return false
        return state.board[position.row][position.col] is BoardCell.Water
    }

    /**
     * Places the selected tile at [position].
     */
    fun placeTile(
        currentState: IslandConnectState,
        position: GridPosition,
        islands: List<IslandCell> = PuzzleLayout.islands
    ): IslandConnectState {
        if (!canPlaceTile(currentState, position)) return currentState
        val selected = currentState.selectedTile ?: return currentState

        // Capture snapshot before modifying state
        val snapshot = Snapshot(
            board = currentState.board,
            tray = currentState.tray,
            bag = currentState.bag,
            selectedTileId = currentState.selectedTileId,
            moveCount = currentState.moveCount,
            connectedIslandIds = currentState.connectedIslandIds,
            connectedCellPositions = currentState.connectedCellPositions,
            isComplete = currentState.isComplete,
            isFailed = currentState.isFailed
        )

        // 1. Place tile on board
        val newBoard = currentState.board.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                if (r == position.row && c == position.col) {
                    BoardCell.Tile(selected)
                } else {
                    cell
                }
            }
        }

        // 2. Remove placed tile from tray
        val newTray = currentState.tray.filter { it.id != selected.id }.toMutableList()
        val newBag = currentState.bag.toMutableList()

        // 3. Draw replacement from bag if available
        if (newBag.isNotEmpty()) {
            val drawnTile = newBag.removeAt(0)
            newTray.add(drawnTile)
        }

        // 4. Update selection
        val nextSelectedId = newTray.firstOrNull()?.id

        // 5. Recalculate network
        val (connectedIslands, connectedPositions) = findConnectedNetwork(newBoard, islands)
        val newlyConnected = (connectedIslands - currentState.connectedIslandIds).firstOrNull()

        // 6. Check win / failure
        val isComplete = connectedIslands.size == islands.size
        val isFailed = !isComplete && newTray.isEmpty() && newBag.isEmpty()

        return currentState.copy(
            board = newBoard,
            tray = newTray,
            bag = newBag,
            selectedTileId = nextSelectedId,
            moveCount = currentState.moveCount + 1,
            connectedIslandIds = connectedIslands,
            connectedCellPositions = connectedPositions,
            isComplete = isComplete,
            isFailed = isFailed,
            newlyConnectedIsland = newlyConnected,
            undoStack = currentState.undoStack + snapshot
        )
    }

    /**
     * Rotates currently selected tile clockwise by 90 degrees.
     */
    fun rotateSelectedTile(currentState: IslandConnectState): IslandConnectState {
        if (!currentState.isGameActive || currentState.isAnimating) return currentState
        val selectedId = currentState.selectedTileId ?: return currentState
        val newTray = currentState.tray.map { tile ->
            if (tile.id == selectedId) tile.rotatedClockwise() else tile
        }
        return currentState.copy(tray = newTray)
    }

    /**
     * Undoes the last move, restoring previous full state snapshot.
     */
    fun undo(currentState: IslandConnectState): IslandConnectState {
        if (!currentState.canUndo) return currentState
        val lastSnapshot = currentState.undoStack.lastOrNull() ?: return currentState
        val newStack = currentState.undoStack.dropLast(1)

        return currentState.copy(
            board = lastSnapshot.board,
            tray = lastSnapshot.tray,
            bag = lastSnapshot.bag,
            selectedTileId = lastSnapshot.selectedTileId,
            moveCount = lastSnapshot.moveCount,
            connectedIslandIds = lastSnapshot.connectedIslandIds,
            connectedCellPositions = lastSnapshot.connectedCellPositions,
            isComplete = lastSnapshot.isComplete,
            isFailed = lastSnapshot.isFailed,
            newlyConnectedIsland = null,
            undoStack = newStack
        )
    }
}
