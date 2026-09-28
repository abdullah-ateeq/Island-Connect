package com.example.islandconnect.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.islandconnect.audio.SoundManager
import com.example.islandconnect.data.AppThemeMode
import com.example.islandconnect.data.BoardThemeMode
import com.example.islandconnect.data.GamePreferences
import com.example.islandconnect.data.GameSettings
import com.example.islandconnect.data.GameStatistics
import com.example.islandconnect.haptics.HapticsManager
import com.example.islandconnect.logic.NetworkEngine
import com.example.islandconnect.model.GridPosition
import com.example.islandconnect.model.IslandConnectState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application.applicationContext)
    val soundManager = SoundManager()
    val hapticsManager = HapticsManager(application.applicationContext)

    val settings: StateFlow<GameSettings> = preferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameSettings()
    )

    val statistics: StateFlow<GameStatistics> = preferences.statisticsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameStatistics()
    )

    private val _gameState = MutableStateFlow(IslandConnectState.initial())
    val gameState: StateFlow<IslandConnectState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Sync preferences with audio & haptics managers
        viewModelScope.launch {
            settings.collect { currentSettings ->
                soundManager.isEnabled = currentSettings.soundEnabled
                hapticsManager.isEnabled = currentSettings.hapticsEnabled
            }
        }
    }

    fun startNewGame() {
        timerJob?.cancel()
        _gameState.value = IslandConnectState.initial()
        viewModelScope.launch {
            preferences.recordGameStarted()
        }
        startTimer()
    }

    fun restartGame() {
        timerJob?.cancel()
        _gameState.value = IslandConnectState.initial()
        soundManager.playSelect()
        hapticsManager.onTileSelect()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - _gameState.value.elapsedMillis
            while (isActive && _gameState.value.isGameActive) {
                delay(500L)
                val elapsed = System.currentTimeMillis() - startTime
                _gameState.value = _gameState.value.copy(elapsedMillis = elapsed)
            }
        }
    }

    fun selectTile(tileId: Int) {
        val current = _gameState.value
        if (!current.isGameActive || current.selectedTileId == tileId) return
        _gameState.value = current.copy(selectedTileId = tileId)
        soundManager.playSelect()
        hapticsManager.onTileSelect()
    }

    fun rotateSelectedTile() {
        val current = _gameState.value
        if (!current.isGameActive) return
        _gameState.value = NetworkEngine.rotateSelectedTile(current)
        soundManager.playRotate()
        hapticsManager.onTileRotate()
    }

    fun onCellClick(position: GridPosition) {
        val current = _gameState.value
        if (!NetworkEngine.canPlaceTile(current, position)) return

        val previousConnectedCount = current.connectedIslandIds.size
        val updated = NetworkEngine.placeTile(current, position)

        _gameState.value = updated

        // Sound & Haptic dispatch
        val newlyConnectedCount = updated.connectedIslandIds.size
        if (updated.isComplete) {
            timerJob?.cancel()
            soundManager.playNetworkCompleted()
            hapticsManager.onPuzzleComplete()
            viewModelScope.launch {
                preferences.recordGameCompleted(
                    moves = updated.moveCount,
                    timeMillis = updated.elapsedMillis
                )
            }
        } else if (updated.isFailed) {
            timerJob?.cancel()
            soundManager.playFailure()
            hapticsManager.onTilePlaced()
        } else if (newlyConnectedCount > previousConnectedCount) {
            soundManager.playIslandConnected()
            hapticsManager.onIslandConnected()
        } else {
            soundManager.playPlace()
            hapticsManager.onTilePlaced()
        }
    }

    fun undo() {
        val current = _gameState.value
        if (!current.canUndo) return
        _gameState.value = NetworkEngine.undo(current)
        soundManager.playSelect()
        hapticsManager.onTileSelect()
    }

    // Settings actions
    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { preferences.updateThemeMode(mode) }
    }

    fun setBoardTheme(theme: BoardThemeMode) {
        viewModelScope.launch { preferences.updateBoardTheme(theme) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.updateSoundEnabled(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.updateHapticsEnabled(enabled) }
    }

    fun setShowConnectedNetwork(show: Boolean) {
        viewModelScope.launch { preferences.updateShowConnectedNetwork(show) }
    }

    fun setShowTimer(show: Boolean) {
        viewModelScope.launch { preferences.updateShowTimer(show) }
    }

    fun resetStatistics() {
        viewModelScope.launch { preferences.resetStatistics() }
    }
}
