package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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

    // Lightweight static / smooth background without heavy per-frame shader re-allocations
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                when {
                    isAmoled -> Color(0xFF000000)
                    isDark -> Color(0xFF060911)
                    else -> Color(0xFFF8FAFC)
                }
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            if (isAmoled) {
                // AMOLED: Pure 100% OLED Black background with subtle rings
                val center = Offset(canvasWidth * 0.5f, canvasHeight * 0.42f)
                val baseRadius = canvasWidth * 0.45f
                drawCircle(
                    color = Color(0xFF1E293B).copy(alpha = 0.12f),
                    radius = baseRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            } else if (isDark) {
                // Dark Theme: Deep obsidian slate with soft ambient static glows
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF060911),
                            Color(0xFF0B101D),
                            Color(0xFF05080E)
                        )
                    )
                )

                // Ambient orb 1: Electric Cyan Glow (soft & static, zero CPU lag)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.09f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.25f, canvasHeight * 0.32f),
                        radius = canvasWidth * 0.6f
                    ),
                    radius = canvasWidth * 0.6f,
                    center = Offset(canvasWidth * 0.25f, canvasHeight * 0.32f)
                )

                // Ambient orb 2: Deep Indigo Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.75f, canvasHeight * 0.65f),
                        radius = canvasWidth * 0.65f
                    ),
                    radius = canvasWidth * 0.65f,
                    center = Offset(canvasWidth * 0.75f, canvasHeight * 0.65f)
                )

                // Concentric clock arc lines
                val center = Offset(canvasWidth * 0.5f, canvasHeight * 0.44f)
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.05f),
                    radius = canvasWidth * 0.48f,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            } else {
                // Light Theme: Pristine soft porcelain
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFF1F5F9),
                            Color(0xFFE2E8F0)
                        )
                    )
                )

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
