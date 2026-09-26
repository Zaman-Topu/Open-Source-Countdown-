package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Google UI Kit Signature Palette
val GoogleBlue = Color(0xFF4285F4)
val GoogleRed = Color(0xFFEA4335)
val GoogleYellow = Color(0xFFFBBC05)
val GoogleGreen = Color(0xFF34A853)

@Composable
fun SplashScreen(
    loadingProgress: Float = 0.5f,
    loadingStatus: String = "Google UI Kits · কুষ্টিয়া আবহাওয়া ও ডেটা সিঙ্ক হচ্ছে...",
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Pulse animation for the emblem
    val infiniteTransition = rememberInfiniteTransition(label = "google_loader_infinite")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Wave animation for the 4 Google dots
    val dotOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot_wave"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0B132B),
                        Color(0xFF020617)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkip
            )
            .testTag("google_loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Google Material 3 Expressive Squircle Emblem
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Brush.linearGradient(
                        listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen)
                    )
                ),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .size(108.dp)
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(28.dp))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "27",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 46.sp,
                                letterSpacing = (-1.5).sp
                            ),
                            color = Color(0xFFF8FAFC)
                        )
                        Text(
                            text = "SSC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp,
                                fontSize = 11.sp
                            ),
                            color = GoogleBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Title in Google Sans Modern Style
            Text(
                text = "SSC 27",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFFF8FAFC)
            )

            Text(
                text = "Kushtia Active · Google Material 3",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Google 4-Color Wave Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val colors = listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen)
                colors.forEachIndexed { index, color ->
                    val phase = (dotOffset + (index * 0.25f)) % 1f
                    val yTranslation = (kotlin.math.sin(phase * 2 * Math.PI) * 6).toFloat()
                    Box(
                        modifier = Modifier
                            .offset(y = yTranslation.dp)
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Material 3 Expressive Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF334155))
            ) {
                LinearProgressIndicator(
                    progress = { loadingProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = GoogleBlue,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Loading Status
            Text(
                text = loadingStatus,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tap hint
            Text(
                text = "ট্যাপ করে সরাসরি প্রবেশ করুন",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            )
        }
    }
}
