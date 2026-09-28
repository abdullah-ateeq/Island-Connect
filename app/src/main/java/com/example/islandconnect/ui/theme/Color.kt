package com.example.islandconnect.ui.theme

import androidx.compose.ui.graphics.Color

// Navy & Slate Backgrounds
val SlateDeepNavy = Color(0xFF0B111A)
val SlateNavySurface = Color(0xFF131C2A)
val SlateNavySurfaceVariant = Color(0xFF1A2638)
val SlateNavyBorder = Color(0xFF283950)

// Light Palette
val CoastalLightBg = Color(0xFFF8FAFC)
val CoastalLightSurface = Color(0xFFFFFFFF)
val CoastalLightSurfaceVariant = Color(0xFFE2E8F0)
val CoastalLightBorder = Color(0xFFCBD5E1)

// Accents
val AccentTeal = Color(0xFF38BDF8)
val AccentTealDark = Color(0xFF0284C7)
val AccentTealGlow = Color(0x6638BDF8)
val AccentGold = Color(0xFFD4A373)
val AccentBronze = Color(0xFFB45309)

// Text
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextMutedDark = Color(0xFF64748B)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF94A3B8)

// Board specific colors
data class BoardColors(
    val waterBg: Color,
    val waterGridLine: Color,
    val rockColor: Color,
    val rockTexture: Color,
    val islandBase: Color,
    val islandRim: Color,
    val islandCenter: Color,
    val islandPort: Color,
    val tileBase: Color,
    val tileBevelLight: Color,
    val tileBevelDark: Color,
    val grooveBase: Color,
    val grooveActive: Color,
    val grooveGlow: Color,
    val selectedBorder: Color
)

val ClassicBoardColors = BoardColors(
    waterBg = Color(0xFF152232),
    waterGridLine = Color(0x283B5270),
    rockColor = Color(0xFF28313D),
    rockTexture = Color(0xFF1F2630),
    islandBase = Color(0xFFC79261),
    islandRim = Color(0xFFDEAA7A),
    islandCenter = Color(0xFF7A4A28),
    islandPort = Color(0xFF4F2E18),
    tileBase = Color(0xFFE2E8F0),
    tileBevelLight = Color(0xFFF8FAFC),
    tileBevelDark = Color(0xFFCBD5E1),
    grooveBase = Color(0xFF475569),
    grooveActive = Color(0xFF0EA5E9),
    grooveGlow = Color(0x7738BDF8),
    selectedBorder = Color(0xFF0284C7)
)

val MidnightBoardColors = BoardColors(
    waterBg = Color(0xFF0A0F18),
    waterGridLine = Color(0x221E293B),
    rockColor = Color(0xFF1A2230),
    rockTexture = Color(0xFF111722),
    islandBase = Color(0xFFB07D62),
    islandRim = Color(0xFFDDA15E),
    islandCenter = Color(0xFF6F4E37),
    islandPort = Color(0xFF422E21),
    tileBase = Color(0xFFCBD5E1),
    tileBevelLight = Color(0xFFE2E8F0),
    tileBevelDark = Color(0xFF94A3B8),
    grooveBase = Color(0xFF334155),
    grooveActive = Color(0xFF14B8A6),
    grooveGlow = Color(0x772DD4BF),
    selectedBorder = Color(0xFF0D9488)
)