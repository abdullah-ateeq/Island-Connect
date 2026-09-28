package com.example.islandconnect.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islandconnect.data.GameSettings
import com.example.islandconnect.model.GridPosition
import com.example.islandconnect.model.IslandConnectState
import com.example.islandconnect.model.PuzzleLayout
import com.example.islandconnect.ui.components.BoardView
import com.example.islandconnect.ui.components.GameControls
import com.example.islandconnect.ui.components.GameHud
import com.example.islandconnect.ui.components.TileTrayView
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    state: IslandConnectState,
    settings: GameSettings,
    onCellClick: (GridPosition) -> Unit,
    onTileSelected: (Int) -> Unit,
    onRotate: () -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    onGameFinished: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRestartDialog by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf<String?>(null) }

    // Flash banner when a new island connects
    LaunchedEffect(state.connectedIslandIds.size) {
        if (state.connectedIslandIds.size in 2..3 && !state.isComplete) {
            bannerMessage = "ISLAND CONNECTED (${state.connectedIslandIds.size}/4)"
            delay(1800L)
            bannerMessage = null
        }
    }

    // Auto navigate to result on completion or failure
    LaunchedEffect(state.isComplete, state.isFailed) {
        if (state.isComplete) {
            bannerMessage = "ALL ISLANDS CONNECTED"
            delay(1200L)
            onGameFinished(true)
        } else if (state.isFailed) {
            bannerMessage = "NETWORK INCOMPLETE"
            delay(1200L)
            onGameFinished(false)
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isLandscape) {
            // Landscape / Tablet layout: Board on left, controls on right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Board on left
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    BoardView(
                        board = state.board,
                        islands = PuzzleLayout.islands,
                        connectedIslandIds = state.connectedIslandIds,
                        connectedCellPositions = state.connectedCellPositions,
                        showConnectedNetwork = settings.showConnectedNetwork,
                        onCellClick = onCellClick
                    )
                }

                // Controls & Tray on right
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    GameHud(
                        connectedIslandsCount = state.connectedIslandIds.size,
                        totalIslandsCount = 4,
                        tilesRemaining = state.totalTilesRemaining,
                        moveCount = state.moveCount,
                        elapsedMillis = state.elapsedMillis,
                        showTimer = settings.showTimer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TileTrayView(
                        tray = state.tray,
                        selectedTileId = state.selectedTileId,
                        onTileSelected = onTileSelected
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GameControls(
                        canRotate = state.selectedTileId != null && state.isGameActive,
                        canUndo = state.canUndo,
                        onRotate = onRotate,
                        onUndo = onUndo,
                        onRestart = { showRestartDialog = true },
                        onBack = onBack
                    )
                }
            }
        } else {
            // Portrait layout: Vertical stack
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top controls & HUD
                Column(modifier = Modifier.fillMaxWidth()) {
                    GameControls(
                        canRotate = state.selectedTileId != null && state.isGameActive,
                        canUndo = state.canUndo,
                        onRotate = onRotate,
                        onUndo = onUndo,
                        onRestart = { showRestartDialog = true },
                        onBack = onBack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GameHud(
                        connectedIslandsCount = state.connectedIslandIds.size,
                        totalIslandsCount = 4,
                        tilesRemaining = state.totalTilesRemaining,
                        moveCount = state.moveCount,
                        elapsedMillis = state.elapsedMillis,
                        showTimer = settings.showTimer
                    )
                }

                // Center Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BoardView(
                        board = state.board,
                        islands = PuzzleLayout.islands,
                        connectedIslandIds = state.connectedIslandIds,
                        connectedCellPositions = state.connectedCellPositions,
                        showConnectedNetwork = settings.showConnectedNetwork,
                        onCellClick = onCellClick
                    )
                }

                // Bottom Tray
                TileTrayView(
                    tray = state.tray,
                    selectedTileId = state.selectedTileId,
                    onTileSelected = onTileSelected
                )
            }
        }

        // Floating Connection Notification Banner
        AnimatedVisibility(
            visible = bannerMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
        ) {
            bannerMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Restart Dialog
        if (showRestartDialog) {
            AlertDialog(
                onDismissRequest = { showRestartDialog = false },
                title = { Text("Restart Puzzle") },
                text = { Text("Restart this puzzle with the original board layout and tile bag?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRestartDialog = false
                            onRestart()
                        }
                    ) {
                        Text("Restart", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
