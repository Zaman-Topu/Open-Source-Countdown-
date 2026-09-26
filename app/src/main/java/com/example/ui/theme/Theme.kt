package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SkyCyan,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = SkyCyan,
    secondary = SoftIndigo,
    onSecondary = DarkBackground,
    tertiary = WarmAmber,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
    outline = DarkOutline
)

private val AmoledColorScheme = darkColorScheme(
    primary = SkyCyan,
    onPrimary = AmoledBackground,
    primaryContainer = AmoledSurfaceVariant,
    onPrimaryContainer = SkyCyan,
    secondary = SoftIndigo,
    onSecondary = AmoledBackground,
    tertiary = WarmAmber,
    onTertiary = AmoledBackground,
    background = AmoledBackground,
    onBackground = AmoledOnBackground,
    surface = AmoledSurface,
    onSurface = AmoledOnSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledOnSurface,
    outline = AmoledOutline
)

private val LightColorScheme = lightColorScheme(
    primary = DeepCyan,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = DeepCyan,
    secondary = SoftIndigo,
    onSecondary = LightSurface,
    tertiary = WarmAmber,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    outline = LightOutline
)

@Composable
fun SSC27Theme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val context = LocalContext.current

    val colorScheme = when (themeMode) {
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.DARK -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicDarkColorScheme(context)
            } else {
                DarkColorScheme
            }
        }
        AppThemeMode.LIGHT -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicLightColorScheme(context)
            } else {
                LightColorScheme
            }
        }
        AppThemeMode.SYSTEM -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isSystemDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (isSystemDark) DarkColorScheme else LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
