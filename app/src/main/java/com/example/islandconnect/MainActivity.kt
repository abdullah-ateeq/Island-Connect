package com.example.islandconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.islandconnect.ui.GameViewModel
import com.example.islandconnect.ui.IslandConnectNavGraph
import com.example.islandconnect.ui.theme.IslandConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()

            IslandConnectTheme(
                appThemeMode = settings.themeMode,
                boardThemeMode = settings.boardTheme
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    IslandConnectNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}