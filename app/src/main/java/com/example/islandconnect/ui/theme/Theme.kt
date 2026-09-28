package com.example.islandconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.islandconnect.data.AppThemeMode
import com.example.islandconnect.data.BoardThemeMode

val LocalBoardColors = staticCompositionLocalOf { ClassicBoardColors }

private val DarkColorScheme = darkColorScheme(
    primary = AccentTeal,
    onPrimary = Color(0xFF032B45),
    primaryContainer = Color(0xFF075985),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = TextSecondaryDark,
    onSecondary = SlateDeepNavy,
    tertiary = AccentGold,
    background = SlateDeepNavy,
    surface = SlateNavySurface,
    surfaceVariant = SlateNavySurfaceVariant,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = SlateNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AccentTealDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = TextSecondaryLight,
    onSecondary = Color.White,
    tertiary = AccentBronze,
    background = CoastalLightBg,
    surface = CoastalLightSurface,
    surfaceVariant = CoastalLightSurfaceVariant,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = CoastalLightBorder
)

@Composable
fun IslandConnectTheme(
    appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    boardThemeMode: BoardThemeMode = BoardThemeMode.CLASSIC,
    content: @Composable () -> Unit
) {
    val isDark = when (appThemeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val boardColors = when (boardThemeMode) {
        BoardThemeMode.CLASSIC -> ClassicBoardColors
        BoardThemeMode.MIDNIGHT -> MidnightBoardColors
    }

    CompositionLocalProvider(LocalBoardColors provides boardColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}