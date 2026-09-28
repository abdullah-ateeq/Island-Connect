package com.example.islandconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "island_connect_prefs")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class BoardThemeMode {
    CLASSIC,
    MIDNIGHT
}

data class GameSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val boardTheme: BoardThemeMode = BoardThemeMode.CLASSIC,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showConnectedNetwork: Boolean = true,
    val showTimer: Boolean = true
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val puzzlesCompleted: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeMillis: Long = 0L
)

class GamePreferences(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_CONNECTED_NETWORK = booleanPreferencesKey("show_connected_network")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val PUZZLES_COMPLETED = intPreferencesKey("puzzles_completed")
        val BEST_MOVES = intPreferencesKey("best_moves")
        val BEST_TIME_MILLIS = longPreferencesKey("best_time_millis")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        val themeModeStr = prefs[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val boardThemeStr = prefs[PreferencesKeys.BOARD_THEME] ?: BoardThemeMode.CLASSIC.name
        GameSettings(
            themeMode = try { AppThemeMode.valueOf(themeModeStr) } catch (_: Exception) { AppThemeMode.SYSTEM },
            boardTheme = try { BoardThemeMode.valueOf(boardThemeStr) } catch (_: Exception) { BoardThemeMode.CLASSIC },
            soundEnabled = prefs[PreferencesKeys.SOUND_ENABLED] ?: true,
            hapticsEnabled = prefs[PreferencesKeys.HAPTICS_ENABLED] ?: true,
            showConnectedNetwork = prefs[PreferencesKeys.SHOW_CONNECTED_NETWORK] ?: true,
            showTimer = prefs[PreferencesKeys.SHOW_TIMER] ?: true
        )
    }

    val statisticsFlow: Flow<GameStatistics> = context.dataStore.data.map { prefs ->
        GameStatistics(
            gamesPlayed = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0,
            puzzlesCompleted = prefs[PreferencesKeys.PUZZLES_COMPLETED] ?: 0,
            bestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0,
            bestTimeMillis = prefs[PreferencesKeys.BEST_TIME_MILLIS] ?: 0L
        )
    }

    suspend fun updateThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun updateBoardTheme(theme: BoardThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.BOARD_THEME] = theme.name
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun updateShowConnectedNetwork(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_CONNECTED_NETWORK] = show
        }
    }

    suspend fun updateShowTimer(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_TIMER] = show
        }
    }

    suspend fun recordGameStarted() {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0
            prefs[PreferencesKeys.GAMES_PLAYED] = current + 1
        }
    }

    suspend fun recordGameCompleted(moves: Int, timeMillis: Long) {
        context.dataStore.edit { prefs ->
            val completed = (prefs[PreferencesKeys.PUZZLES_COMPLETED] ?: 0) + 1
            prefs[PreferencesKeys.PUZZLES_COMPLETED] = completed

            val bestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0
            if (bestMoves == 0 || moves < bestMoves) {
                prefs[PreferencesKeys.BEST_MOVES] = moves
            }

            val bestTime = prefs[PreferencesKeys.BEST_TIME_MILLIS] ?: 0L
            if (bestTime == 0L || timeMillis < bestTime) {
                prefs[PreferencesKeys.BEST_TIME_MILLIS] = timeMillis
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GAMES_PLAYED] = 0
            prefs[PreferencesKeys.PUZZLES_COMPLETED] = 0
            prefs[PreferencesKeys.BEST_MOVES] = 0
            prefs[PreferencesKeys.BEST_TIME_MILLIS] = 0L
        }
    }
}
