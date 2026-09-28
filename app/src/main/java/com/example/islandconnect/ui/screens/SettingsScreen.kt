package com.example.islandconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islandconnect.data.AppThemeMode
import com.example.islandconnect.data.BoardThemeMode
import com.example.islandconnect.data.GameSettings

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onBoardThemeChange: (BoardThemeMode) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit,
    onShowNetworkToggle: (Boolean) -> Unit,
    onShowTimerToggle: (Boolean) -> Unit,
    onResetStatistics: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // App Theme Section
            SettingsGroup(title = "APPLICATION THEME") {
                ThemeOptionRow(
                    label = "System Default",
                    selected = settings.themeMode == AppThemeMode.SYSTEM,
                    onSelect = { onThemeModeChange(AppThemeMode.SYSTEM) }
                )
                ThemeOptionRow(
                    label = "Light Architectural",
                    selected = settings.themeMode == AppThemeMode.LIGHT,
                    onSelect = { onThemeModeChange(AppThemeMode.LIGHT) }
                )
                ThemeOptionRow(
                    label = "Dark Coastal Navy",
                    selected = settings.themeMode == AppThemeMode.DARK,
                    onSelect = { onThemeModeChange(AppThemeMode.DARK) }
                )
            }

            // Board Theme Section
            SettingsGroup(title = "BOARD PALETTE") {
                ThemeOptionRow(
                    label = "Classic Coastal (Slate-Blue)",
                    selected = settings.boardTheme == BoardThemeMode.CLASSIC,
                    onSelect = { onBoardThemeChange(BoardThemeMode.CLASSIC) }
                )
                ThemeOptionRow(
                    label = "Midnight Obsidian (Deep Sea)",
                    selected = settings.boardTheme == BoardThemeMode.MIDNIGHT,
                    onSelect = { onBoardThemeChange(BoardThemeMode.MIDNIGHT) }
                )
            }

            // Game Audio & Tactile Section
            SettingsGroup(title = "AUDIO & HAPTICS") {
                SettingSwitchRow(
                    title = "Sound Effects",
                    subtitle = "Subtle tactile audio for placement & connections",
                    checked = settings.soundEnabled,
                    onCheckedChange = onSoundToggle
                )
                SettingSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Vibrations on tile placement & rotations",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = onHapticsToggle
                )
            }

            // Gameplay Display Section
            SettingsGroup(title = "GAMEPLAY DISPLAY") {
                SettingSwitchRow(
                    title = "Show Connected Network",
                    subtitle = "Highlight active bridge paths in teal",
                    checked = settings.showConnectedNetwork,
                    onCheckedChange = onShowNetworkToggle
                )
                SettingSwitchRow(
                    title = "Show Timer",
                    subtitle = "Display elapsed puzzle duration on HUD",
                    checked = settings.showTimer,
                    onCheckedChange = onShowTimerToggle
                )
            }

            // Reset Statistics
            SettingsGroup(title = "DATA MANAGEMENT") {
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.width(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset Player Statistics",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Statistics") },
                text = { Text("Are you sure you want to clear your game history, completed puzzles, and best scores? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showResetDialog = false
                            onResetStatistics()
                        }
                    ) {
                        Text("Reset", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
