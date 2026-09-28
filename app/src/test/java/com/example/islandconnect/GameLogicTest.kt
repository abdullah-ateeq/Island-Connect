package com.example.islandconnect

import com.example.islandconnect.logic.NetworkEngine
import com.example.islandconnect.model.BoardCell
import com.example.islandconnect.model.BridgeTile
import com.example.islandconnect.model.Direction
import com.example.islandconnect.model.GridPosition
import com.example.islandconnect.model.IslandConnectState
import com.example.islandconnect.model.PuzzleLayout
import com.example.islandconnect.model.TileKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun testStraightConnections() {
        val straight0 = BridgeTile(1, TileKind.STRAIGHT, 0)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), straight0.connections)

        val straight90 = straight0.rotatedClockwise()
        assertEquals(setOf(Direction.UP, Direction.DOWN), straight90.connections)

        val straight180 = straight90.rotatedClockwise()
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), straight180.connections)

        val straight270 = straight180.rotatedClockwise()
        assertEquals(setOf(Direction.UP, Direction.DOWN), straight270.connections)
    }

    @Test
    fun testCornerRotation() {
        val corner0 = BridgeTile(2, TileKind.CORNER, 0)
        assertEquals(setOf(Direction.UP, Direction.RIGHT), corner0.connections)

        val corner90 = corner0.rotatedClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.DOWN), corner90.connections)

        val corner180 = corner90.rotatedClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.LEFT), corner180.connections)

        val corner270 = corner180.rotatedClockwise()
        assertEquals(setOf(Direction.LEFT, Direction.UP), corner270.connections)
    }

    @Test
    fun testTJunctionRotation() {
        val t0 = BridgeTile(3, TileKind.T_JUNCTION, 0)
        assertEquals(setOf(Direction.UP, Direction.LEFT, Direction.RIGHT), t0.connections)

        val t90 = t0.rotatedClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.UP, Direction.DOWN), t90.connections)

        val t180 = t90.rotatedClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.RIGHT, Direction.LEFT), t180.connections)

        val t270 = t180.rotatedClockwise()
        assertEquals(setOf(Direction.LEFT, Direction.DOWN, Direction.UP), t270.connections)
    }

    @Test
    fun testCrossConnections() {
        val cross = BridgeTile(4, TileKind.CROSS, 0)
        val allFour = setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        assertEquals(allFour, cross.connections)
        assertEquals(allFour, cross.rotatedClockwise().connections)
        assertEquals(allFour, cross.rotatedClockwise().rotatedClockwise().connections)
        assertEquals(allFour, cross.rotatedClockwise().rotatedClockwise().rotatedClockwise().connections)
    }

    @Test
    fun testOppositeDirectionMatching() {
        assertEquals(Direction.DOWN, Direction.UP.opposite())
        assertEquals(Direction.UP, Direction.DOWN.opposite())
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite())
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite())
    }

    @Test
    fun testMismatchedNeighboringEdgesDoNotConnect() {
        var state = IslandConnectState.initial()
        // Select Straight tile and rotate it to 90 degrees (UP + DOWN)
        // Island 1 at (1,1) has { RIGHT, DOWN }
        // Cell (1,2) is to the RIGHT of (1,1).
        // Since tile has UP + DOWN, it lacks LEFT opening. Connection must NOT form.
        state = state.copy(
            tray = listOf(BridgeTile(1, TileKind.STRAIGHT, 90)),
            selectedTileId = 1
        )
        val updated = NetworkEngine.placeTile(state, GridPosition(1, 2))
        // Position (1,2) should not be reachable from Island 1
        assertFalse(updated.connectedCellPositions.contains(GridPosition(1, 2)))
        assertEquals(setOf(1), updated.connectedIslandIds)
    }

    @Test
    fun testRockPlacementRejection() {
        val state = IslandConnectState.initial()
        val rockPos = GridPosition(2, 2)
        assertTrue(state.board[rockPos.row][rockPos.col] is BoardCell.Rock)

        assertFalse(NetworkEngine.canPlaceTile(state, rockPos))
        val afterPlacementAttempt = NetworkEngine.placeTile(state, rockPos)
        // State remains unchanged
        assertEquals(state.moveCount, afterPlacementAttempt.moveCount)
        assertTrue(afterPlacementAttempt.board[rockPos.row][rockPos.col] is BoardCell.Rock)
    }

    @Test
    fun testIslandPlacementRejection() {
        val state = IslandConnectState.initial()
        val islandPos = GridPosition(1, 1)
        assertTrue(state.board[islandPos.row][islandPos.col] is BoardCell.Island)

        assertFalse(NetworkEngine.canPlaceTile(state, islandPos))
        val afterPlacementAttempt = NetworkEngine.placeTile(state, islandPos)
        // State remains unchanged
        assertEquals(state.moveCount, afterPlacementAttempt.moveCount)
        assertTrue(afterPlacementAttempt.board[islandPos.row][islandPos.col] is BoardCell.Island)
    }

    @Test
    fun testSuccessfulIslandToIslandConnection() {
        var state = IslandConnectState.initial()

        // Place Straight 0° (LEFT, RIGHT) at (1,2)
        state = state.copy(
            tray = listOf(BridgeTile(101, TileKind.STRAIGHT, 0)),
            selectedTileId = 101
        )
        state = NetworkEngine.placeTile(state, GridPosition(1, 2))
        assertTrue(state.connectedCellPositions.contains(GridPosition(1, 2)))
        assertEquals(setOf(1), state.connectedIslandIds)

        // Place Straight 0° (LEFT, RIGHT) at (1,3)
        state = state.copy(
            tray = listOf(BridgeTile(102, TileKind.STRAIGHT, 0)),
            selectedTileId = 102
        )
        state = NetworkEngine.placeTile(state, GridPosition(1, 3))
        assertTrue(state.connectedCellPositions.contains(GridPosition(1, 3)))

        // Island 1 and Island 2 are now connected!
        assertTrue(state.connectedIslandIds.contains(1))
        assertTrue(state.connectedIslandIds.contains(2))
        assertEquals(2, state.connectedIslandIds.size)
        assertFalse(state.isComplete)
    }

    @Test
    fun testAllFourIslandsConnected() {
        var state = IslandConnectState.initial()

        // Link Island 1 (1,1) to Island 2 (1,4) horizontally
        state = state.copy(tray = listOf(BridgeTile(1, TileKind.STRAIGHT, 0)), selectedTileId = 1)
        state = NetworkEngine.placeTile(state, GridPosition(1, 2))

        state = state.copy(tray = listOf(BridgeTile(2, TileKind.STRAIGHT, 0)), selectedTileId = 2)
        state = NetworkEngine.placeTile(state, GridPosition(1, 3))

        // Link Island 1 (1,1) to Island 3 (4,1) vertically
        state = state.copy(tray = listOf(BridgeTile(3, TileKind.STRAIGHT, 90)), selectedTileId = 3)
        state = NetworkEngine.placeTile(state, GridPosition(2, 1))

        state = state.copy(tray = listOf(BridgeTile(4, TileKind.STRAIGHT, 90)), selectedTileId = 4)
        state = NetworkEngine.placeTile(state, GridPosition(3, 1))

        // Link Island 2 (1,4) to Island 4 (4,4) vertically
        state = state.copy(tray = listOf(BridgeTile(5, TileKind.STRAIGHT, 90)), selectedTileId = 5)
        state = NetworkEngine.placeTile(state, GridPosition(2, 4))

        state = state.copy(tray = listOf(BridgeTile(6, TileKind.STRAIGHT, 90)), selectedTileId = 6)
        state = NetworkEngine.placeTile(state, GridPosition(3, 4))

        // All 4 islands connected!
        assertEquals(setOf(1, 2, 3, 4), state.connectedIslandIds)
        assertTrue(state.isComplete)
        assertFalse(state.isFailed)
    }

    @Test
    fun testDisconnectedNetworkNotWinning() {
        var state = IslandConnectState.initial()
        // Place tile in bottom corner (5,5) not connected to Island 1
        state = state.copy(tray = listOf(BridgeTile(1, TileKind.CROSS, 0)), selectedTileId = 1)
        state = NetworkEngine.placeTile(state, GridPosition(5, 5))

        assertFalse(state.isComplete)
        assertEquals(setOf(1), state.connectedIslandIds)
    }

    @Test
    fun testTileExhaustionFailure() {
        // State with only 1 tile left and no bag
        var state = IslandConnectState.initial().copy(
            tray = listOf(BridgeTile(99, TileKind.STRAIGHT, 0)),
            bag = emptyList(),
            selectedTileId = 99
        )

        // Place the last tile at an isolated cell (0,0)
        state = NetworkEngine.placeTile(state, GridPosition(0, 0))

        assertTrue(state.tray.isEmpty())
        assertTrue(state.bag.isEmpty())
        assertFalse(state.isComplete)
        assertTrue(state.isFailed)
    }

    @Test
    fun testUndoRestoresExactSnapshot() {
        var state = IslandConnectState.initial()
        val originalTray = state.tray
        val originalMoveCount = state.moveCount

        // Place tile at (1,2)
        state = NetworkEngine.placeTile(state, GridPosition(1, 2))
        assertEquals(originalMoveCount + 1, state.moveCount)
        assertTrue(state.board[1][2] is BoardCell.Tile)
        assertTrue(state.canUndo)

        // Undo
        state = NetworkEngine.undo(state)
        assertEquals(originalMoveCount, state.moveCount)
        assertTrue(state.board[1][2] is BoardCell.Water)
        assertEquals(originalTray.map { it.id }, state.tray.map { it.id })
        assertFalse(state.canUndo)
    }
}
