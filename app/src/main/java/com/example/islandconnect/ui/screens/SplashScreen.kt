package com.example.islandconnect.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islandconnect.ui.theme.LocalBoardColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val boardColors = LocalBoardColors.current

    val islandAlpha = remember { Animatable(0f) }
    val bridgeProgress = remember { Animatable(0f) }
    val networkGlow = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Island nodes appear
        islandAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        // 2. Bridges draw progressively
        bridgeProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        // 3. Network glows
        networkGlow.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        // 4. Title appears
        textAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        // Hold briefly then navigate (~1.7s total)
        delay(350L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Splash Graphic: 4 islands with connecting bridges
            Canvas(
                modifier = Modifier.size(160.dp)
            ) {
                val w = size.width
                val h = size.height
                val pad = w * 0.22f

                val p1 = Offset(pad, pad)             // NW
                val p2 = Offset(w - pad, pad)         // NE
                val p3 = Offset(pad, h - pad)         // SW
                val p4 = Offset(w - pad, h - pad)     // SE

                val pVal = bridgeProgress.value
                val glowVal = networkGlow.value

                // Glowing connecting bridges
                val activeBridgeColor = boardColors.grooveActive
                val glowBridgeColor = boardColors.grooveGlow.copy(alpha = glowVal * 0.8f)

                if (pVal > 0f) {
                    val strokeW = 10.dp.toPx()

                    // Glow line behind bridges
                    if (glowVal > 0f) {
                        drawLine(
                            color = glowBridgeColor,
                            start = p1,
                            end = Offset(p1.x + (p2.x - p1.x) * pVal, p1.y),
                            strokeWidth = strokeW * 2f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = glowBridgeColor,
                            start = p1,
                            end = Offset(p1.x, p1.y + (p3.y - p1.y) * pVal),
                            strokeWidth = strokeW * 2f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = glowBridgeColor,
                            start = p2,
                            end = Offset(p2.x, p2.y + (p4.y - p2.y) * pVal),
                            strokeWidth = strokeW * 2f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Bridge 1: NW to NE
                    drawLine(
                        color = activeBridgeColor,
                        start = p1,
                        end = Offset(p1.x + (p2.x - p1.x) * pVal, p1.y),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                    // Bridge 2: NW to SW
                    drawLine(
                        color = activeBridgeColor,
                        start = p1,
                        end = Offset(p1.x, p1.y + (p3.y - p1.y) * pVal),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                    // Bridge 3: NE to SE
                    drawLine(
                        color = activeBridgeColor,
                        start = p2,
                        end = Offset(p2.x, p2.y + (p4.y - p2.y) * pVal),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // 4 Island nodes
                val iAlpha = islandAlpha.value
                val islandRadius = 18.dp.toPx()
                val islandPoints = listOf(p1, p2, p3, p4)

                for (pt in islandPoints) {
                    // Outer rim
                    drawCircle(
                        color = boardColors.islandRim.copy(alpha = iAlpha),
                        radius = islandRadius,
                        center = pt
                    )
                    // Inner stone
                    drawCircle(
                        color = boardColors.islandBase.copy(alpha = iAlpha),
                        radius = islandRadius * 0.75f,
                        center = pt
                    )
                    // Beacon
                    val beaconColor = if (glowVal > 0f) activeBridgeColor else boardColors.islandCenter
                    drawCircle(
                        color = beaconColor.copy(alpha = iAlpha),
                        radius = islandRadius * 0.4f,
                        center = pt
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title & Subtitle with fade
            Text(
                text = "ISLAND CONNECT",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 28.sp,
                    letterSpacing = 4.sp
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ARCHITECTURAL PUZZLE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = textAlpha.value)
            )
        }
    }
}
