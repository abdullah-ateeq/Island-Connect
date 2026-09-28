package com.example.islandconnect.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.islandconnect.ui.screens.AboutScreen
import com.example.islandconnect.ui.screens.GameScreen
import com.example.islandconnect.ui.screens.HomeScreen
import com.example.islandconnect.ui.screens.HowToPlayScreen
import com.example.islandconnect.ui.screens.ResultScreen
import com.example.islandconnect.ui.screens.SettingsScreen
import com.example.islandconnect.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Game : Screen("game")
    data object Result : Screen("result/{isSuccess}") {
        fun createRoute(isSuccess: Boolean) = "result/$isSuccess"
    }
    data object HowToPlay : Screen("how_to_play")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}

@Composable
fun IslandConnectNavGraph(
    viewModel: GameViewModel
) {
    val navController = rememberNavController()
    val gameState by viewModel.gameState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val statistics by viewModel.statistics.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                statistics = statistics,
                onPlayClick = {
                    viewModel.startNewGame()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClick = {
                    navController.navigate(Screen.HowToPlay.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        // 3. Game Screen
        composable(Screen.Game.route) {
            GameScreen(
                state = gameState,
                settings = settings,
                onCellClick = { pos -> viewModel.onCellClick(pos) },
                onTileSelected = { tileId -> viewModel.selectTile(tileId) },
                onRotate = { viewModel.rotateSelectedTile() },
                onUndo = { viewModel.undo() },
                onRestart = { viewModel.restartGame() },
                onBack = { navController.popBackStack() },
                onGameFinished = { isSuccess ->
                    navController.navigate(Screen.Result.createRoute(isSuccess)) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Result Screen
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("isSuccess") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val isSuccess = backStackEntry.arguments?.getBoolean("isSuccess") ?: false
            ResultScreen(
                state = gameState,
                isSuccess = isSuccess,
                onPlayAgain = {
                    viewModel.startNewGame()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // 5. How to Play Screen
        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 6. Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onThemeModeChange = { viewModel.setThemeMode(it) },
                onBoardThemeChange = { viewModel.setBoardTheme(it) },
                onSoundToggle = { viewModel.setSoundEnabled(it) },
                onHapticsToggle = { viewModel.setHapticsEnabled(it) },
                onShowNetworkToggle = { viewModel.setShowConnectedNetwork(it) },
                onShowTimerToggle = { viewModel.setShowTimer(it) },
                onResetStatistics = { viewModel.resetStatistics() },
                onBack = { navController.popBackStack() }
            )
        }

        // 7. About Screen
        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
