package com.example.islandconnect.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.islandconnect.model.BoardCell
import com.example.islandconnect.model.BridgeTile
import com.example.islandconnect.model.Direction
import com.example.islandconnect.model.GridPosition
import com.example.islandconnect.model.IslandCell
import com.example.islandconnect.model.PuzzleLayout
import com.example.islandconnect.ui.theme.BoardColors
import com.example.islandconnect.ui.theme.LocalBoardColors

@Composable
fun BoardView(
    board: List<List<BoardCell>>,
    islands: List<IslandCell>,
    connectedIslandIds: Set<Int>,
    connectedCellPositions: Set<GridPosition>,
    showConnectedNetwork: Boolean,
    onCellClick: (GridPosition) -> Unit,
    modifier: Modifier = Modifier,
    boardColors: BoardColors = LocalBoardColors.current
) {
    val islandsMap = islands.associateBy { it.id }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    ) {
        val boardWidth = constraints.maxWidth.toFloat()
        val boardHeight = constraints.maxHeight.toFloat()
        val gridSize = PuzzleLayout.GRID_SIZE
        val cellSize = boardWidth / gridSize

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(board) {
                    detectTapGestures { offset ->
                        val col = (offset.x / cellSize).toInt().coerceIn(0, gridSize - 1)
                        val row = (offset.y / cellSize).toInt().coerceIn(0, gridSize - 1)
                        onCellClick(GridPosition(row, col))
                    }
                }
                .semantics {
                    role = Role.Button
                    contentDescription = "Island Connect 6 by 6 puzzle board. ${connectedIslandIds.size} of 4 islands connected."
                }
        ) {
            // 1. Water Background
            drawRect(color = boardColors.waterBg)

            // 2. Subtle Water Grid Lines
            val gridStroke = 1.dp.toPx()
            for (i in 0..gridSize) {
                val pos = i * cellSize
                // Vertical lines
                drawLine(
                    color = boardColors.waterGridLine,
                    start = Offset(pos, 0f),
                    end = Offset(pos, boardHeight),
                    strokeWidth = gridStroke
                )
                // Horizontal lines
                drawLine(
                    color = boardColors.waterGridLine,
                    start = Offset(0f, pos),
                    end = Offset(boardWidth, pos),
                    strokeWidth = gridStroke
                )
            }

            // 3. Render Cells
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    val pos = GridPosition(r, c)
                    val cell = board[r][c]
                    val cellLeft = c * cellSize
                    val cellTop = r * cellSize
                    val isConnected = showConnectedNetwork && connectedCellPositions.contains(pos)

                    when (cell) {
                        is BoardCell.Water -> {
                            drawWaterCell(cellLeft, cellTop, cellSize, boardColors)
                        }
                        is BoardCell.Rock -> {
                            drawRockCell(cellLeft, cellTop, cellSize, boardColors)
                        }
                        is BoardCell.Island -> {
                            val island = islandsMap[cell.islandId]
                            val isIslandConnected = connectedIslandIds.contains(cell.islandId)
                            drawIslandCell(
                                left = cellLeft,
                                top = cellTop,
                                size = cellSize,
                                island = island,
                                isConnected = isIslandConnected,
                                pulseAlpha = if (isIslandConnected) pulseAlpha else 1f,
                                boardColors = boardColors
                            )
                        }
                        is BoardCell.Tile -> {
                            drawPlacedTile(
                                left = cellLeft,
                                top = cellTop,
                                size = cellSize,
                                tile = cell.tile,
                                isConnected = isConnected,
                                boardColors = boardColors
                            )
                        }
                    }
                }
            }

            // Outer Board Border
            drawRoundRect(
                color = boardColors.waterGridLine.copy(alpha = 0.6f),
                topLeft = Offset(0f, 0f),
                size = Size(boardWidth, boardHeight),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

/**
 * Draws water cell with calm ripple indicators.
 */
private fun DrawScope.drawWaterCell(
    left: Float,
    top: Float,
    size: Float,
    boardColors: BoardColors
) {
    val cx = left + size / 2f
    val cy = top + size / 2f
    val rippleRadius = size * 0.12f
    // Gentle ripple dot/arc
    drawCircle(
        color = boardColors.waterGridLine.copy(alpha = 0.25f),
        radius = rippleRadius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Draws non-interactive rock cell with dark stone facets.
 */
private fun DrawScope.drawRockCell(
    left: Float,
    top: Float,
    size: Float,
    boardColors: BoardColors
) {
    val inset = size * 0.08f
    val rockRect = Size(size - inset * 2, size - inset * 2)
    val corner = size * 0.22f

    // Base rock body
    drawRoundRect(
        color = boardColors.rockColor,
        topLeft = Offset(left + inset, top + inset),
        size = rockRect,
        cornerRadius = CornerRadius(corner, corner)
    )

    // Subtle cross-hatching texture
    val strokeWidth = 1.2.dp.toPx()
    val texColor = boardColors.rockTexture
    val step = size * 0.2f
    for (i in 1..4) {
        val x = left + inset + i * step * 0.65f
        drawLine(
            color = texColor,
            start = Offset(x, top + inset + size * 0.15f),
            end = Offset(x - size * 0.15f, top + size - inset - size * 0.15f),
            strokeWidth = strokeWidth
        )
    }

    // Rock perimeter highlight
    drawRoundRect(
        color = boardColors.waterGridLine.copy(alpha = 0.4f),
        topLeft = Offset(left + inset, top + inset),
        size = rockRect,
        cornerRadius = CornerRadius(corner, corner),
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Draws an island node with raised stone platform, connection piers/slots, and fortress beacon.
 */
private fun DrawScope.drawIslandCell(
    left: Float,
    top: Float,
    size: Float,
    island: IslandCell?,
    isConnected: Boolean,
    pulseAlpha: Float,
    boardColors: BoardColors
) {
    val cx = left + size / 2f
    val cy = top + size / 2f
    val radius = size * 0.38f
    val grooveWidth = size * 0.22f

    val conns = island?.connections ?: emptySet()
    val pierColor = if (isConnected) boardColors.grooveActive else boardColors.islandPort
    val glowColor = if (isConnected) boardColors.grooveGlow.copy(alpha = pulseAlpha) else Color.Transparent

    // 1. Connection Docks / Piers extending outward to cell edges
    for (dir in conns) {
        val target = when (dir) {
            Direction.UP -> Offset(cx, top)
            Direction.DOWN -> Offset(cx, top + size)
            Direction.LEFT -> Offset(left, cy)
            Direction.RIGHT -> Offset(left + size, cy)
        }

        if (isConnected) {
            drawLine(
                color = glowColor,
                start = Offset(cx, cy),
                end = target,
                strokeWidth = grooveWidth * 1.5f,
                cap = StrokeCap.Square
            )
        }

        drawLine(
            color = pierColor,
            start = Offset(cx, cy),
            end = target,
            strokeWidth = grooveWidth,
            cap = StrokeCap.Square
        )
    }

    // 2. Outer Island Stone Platform
    drawCircle(
        color = boardColors.islandRim,
        radius = radius,
        center = Offset(cx, cy)
    )

    // Inner Plateau
    drawCircle(
        color = boardColors.islandBase,
        radius = radius * 0.82f,
        center = Offset(cx, cy)
    )

    // 3. Central Beacon / Stronghold
    val beaconRadius = radius * 0.45f
    val beaconColor = if (isConnected) {
        boardColors.grooveActive
    } else {
        boardColors.islandCenter
    }

    if (isConnected) {
        drawCircle(
            color = boardColors.grooveGlow.copy(alpha = pulseAlpha),
            radius = beaconRadius * 1.6f,
            center = Offset(cx, cy)
        )
    }

    drawCircle(
        color = beaconColor,
        radius = beaconRadius,
        center = Offset(cx, cy)
    )

    // Beacon inner core
    drawCircle(
        color = if (isConnected) Color.White else boardColors.islandRim,
        radius = beaconRadius * 0.4f,
        center = Offset(cx, cy)
    )
}

/**
 * Draws a placed bridge tile on the board with its correct orientation and active grooves.
 */
private fun DrawScope.drawPlacedTile(
    left: Float,
    top: Float,
    size: Float,
    tile: BridgeTile,
    isConnected: Boolean,
    boardColors: BoardColors
) {
    withTransform({
        translate(left = left, top = top)
        rotate(degrees = tile.rotation.toFloat(), pivot = Offset(size / 2f, size / 2f))
    }) {
        drawTilePiece(
            connections = tile.kind.baseConnections,
            isConnected = isConnected,
            boardColors = boardColors,
            w = size,
            h = size
        )
    }
}
