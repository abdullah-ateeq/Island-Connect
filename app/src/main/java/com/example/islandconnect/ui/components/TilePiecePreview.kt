package com.example.islandconnect.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.islandconnect.model.BridgeTile
import com.example.islandconnect.model.Direction
import com.example.islandconnect.ui.theme.BoardColors
import com.example.islandconnect.ui.theme.LocalBoardColors

@Composable
fun TilePiecePreview(
    tile: BridgeTile,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    isSelected: Boolean = false,
    isConnected: Boolean = false,
    boardColors: BoardColors = LocalBoardColors.current,
    onClick: (() -> Unit)? = null
) {
    val animatedRotation by animateFloatAsState(
        targetValue = tile.rotation.toFloat(),
        animationSpec = tween(durationMillis = 200),
        label = "tile_rotation"
    )

    val yOffset = if (isSelected) (-4).dp else 0.dp
    val elevation = if (isSelected) 8.dp else 2.dp

    Box(
        modifier = modifier
            .offset(y = yOffset)
            .size(size)
            .shadow(elevation, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, boardColors.selectedBorder, RoundedCornerShape(12.dp))
                } else {
                    Modifier.border(1.dp, boardColors.waterGridLine, RoundedCornerShape(12.dp))
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .semantics {
                contentDescription = if (isSelected) {
                    "Selected ${tile.accessibleDescription}"
                } else {
                    tile.accessibleDescription
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Base tile Canvas
        Canvas(
            modifier = Modifier
                .size(size)
                .rotate(animatedRotation)
        ) {
            drawTilePiece(
                connections = tile.kind.baseConnections,
                isConnected = isConnected,
                boardColors = boardColors
            )
        }
    }
}

/**
 * Draws the base bridge tile geometry on Canvas.
 */
fun DrawScope.drawTilePiece(
    connections: Set<Direction>,
    isConnected: Boolean,
    boardColors: BoardColors,
    w: Float = size.width,
    h: Float = size.height
) {
    val cornerRadius = w * 0.16f
    val inset = w * 0.05f

    // 1. Tile stone background
    drawRoundRect(
        color = boardColors.tileBevelDark,
        topLeft = Offset(inset, inset),
        size = Size(w - inset * 2, h - inset * 2),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    // Inner lighter face
    val faceInset = w * 0.09f
    drawRoundRect(
        color = boardColors.tileBase,
        topLeft = Offset(faceInset, faceInset),
        size = Size(w - faceInset * 2, h - faceInset * 2),
        cornerRadius = CornerRadius(cornerRadius * 0.8f, cornerRadius * 0.8f)
    )

    // 2. Grooves / Channels
    val grooveWidth = w * 0.22f
    val cx = w / 2f
    val cy = h / 2f

    val grooveColor = if (isConnected) boardColors.grooveActive else boardColors.grooveBase
    val glowColor = if (isConnected) boardColors.grooveGlow else Color.Transparent

    // Draw glow if connected
    if (isConnected) {
        val glowWidth = grooveWidth * 1.6f
        for (dir in connections) {
            val endOffset = when (dir) {
                Direction.UP -> Offset(cx, 0f)
                Direction.DOWN -> Offset(cx, h)
                Direction.LEFT -> Offset(0f, cy)
                Direction.RIGHT -> Offset(w, cy)
            }
            drawLine(
                color = glowColor,
                start = Offset(cx, cy),
                end = endOffset,
                strokeWidth = glowWidth,
                cap = StrokeCap.Square
            )
        }
        drawCircle(
            color = glowColor,
            radius = glowWidth * 0.55f,
            center = Offset(cx, cy)
        )
    }

    // Draw main channel grooves
    for (dir in connections) {
        val endOffset = when (dir) {
            Direction.UP -> Offset(cx, 0f)
            Direction.DOWN -> Offset(cx, h)
            Direction.LEFT -> Offset(0f, cy)
            Direction.RIGHT -> Offset(w, cy)
        }
        drawLine(
            color = grooveColor,
            start = Offset(cx, cy),
            end = endOffset,
            strokeWidth = grooveWidth,
            cap = StrokeCap.Square
        )
    }

    // Central circular hub
    drawCircle(
        color = grooveColor,
        radius = grooveWidth * 0.52f,
        center = Offset(cx, cy)
    )

    // Subtle center rivet / metallic fastener
    drawCircle(
        color = if (isConnected) Color.White else boardColors.tileBevelLight,
        radius = grooveWidth * 0.22f,
        center = Offset(cx, cy)
    )
}
