package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.AppThemeMode
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CinematicBackground(
    themeMode: AppThemeMode,
    isAmbientMotionEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.AMOLED -> true
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemDark
    }
    val isAmoled = themeMode == AppThemeMode.AMOLED

    // Lightweight infinite transition for subtle ambient motion (paused if disabled)
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_motion")
    val pulse by if (isAmbientMotionEnabled && !isAmoled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 28000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ambient_angle"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "ambient_static"
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            if (isAmoled) {
                // AMOLED: Pure 100% OLED Black background to maximize battery savings
                drawRect(color = Color(0xFF000000))

                // Faint elegant concentric circles for subtle stopwatch / countdown depth
                val center = Offset(canvasWidth * 0.5f, canvasHeight * 0.42f)
                val baseRadius = canvasWidth * 0.45f
                drawCircle(
                    color = Color(0xFF1E293B).copy(alpha = 0.12f),
                    radius = baseRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = Color(0xFF0F172A).copy(alpha = 0.25f),
                    radius = baseRadius * 0.72f,
                    center = center,
                    style = Stroke(width = 1.0f)
                )
            } else if (isDark) {
                // Dark Theme: Deep obsidian slate with soft ambient cyan and indigo glows
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF060911),
                            Color(0xFF0B101D),
                            Color(0xFF05080E)
                        )
                    )
                )

                // Ambient orb 1: Electric Cyan
                val rad1 = Math.toRadians(pulse.toDouble())
                val orb1X = canvasWidth * 0.2f + (cos(rad1) * 35).toFloat()
                val orb1Y = canvasHeight * 0.32f + (sin(rad1) * 45).toFloat()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = canvasWidth * 0.65f
                    ),
                    radius = canvasWidth * 0.65f,
                    center = Offset(orb1X, orb1Y)
                )

                // Ambient orb 2: Deep Indigo
                val rad2 = Math.toRadians((pulse + 180).toDouble())
                val orb2X = canvasWidth * 0.78f + (cos(rad2) * 40).toFloat()
                val orb2Y = canvasHeight * 0.68f + (sin(rad2) * 50).toFloat()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.07f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = canvasWidth * 0.7f
                    ),
                    radius = canvasWidth * 0.7f,
                    center = Offset(orb2X, orb2Y)
                )

                // Elegant clock arc lines
                val center = Offset(canvasWidth * 0.5f, canvasHeight * 0.44f)
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.04f),
                    radius = canvasWidth * 0.48f,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            } else {
                // Light Theme: Pristine soft porcelain with subtle sky morning aura
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFF1F5F9),
                            Color(0xFFE2E8F0)
                        )
                    )
                )

                // Gentle light glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0284C7).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.3f),
                        radius = canvasWidth * 0.6f
                    ),
                    radius = canvasWidth * 0.6f,
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.3f)
                )
            }
        }

        // Overlay Content
        content()
    }
}
