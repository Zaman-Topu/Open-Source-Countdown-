package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Brand accents
val SkyCyan = Color(0xFF38BDF8)
val DeepCyan = Color(0xFF0284C7)
val SoftIndigo = Color(0xFF818CF8)
val WarmAmber = Color(0xFFF59E0B)
val LightAmber = Color(0xFFFDE68A)

// Dark Theme Colors
val DarkBackground = Color(0xFF090D16)
val DarkSurface = Color(0xFF111726)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkOnBackground = Color(0xFFF1F5F9)
val DarkOnSurface = Color(0xFFE2E8F0)
val DarkOutline = Color(0xFF334155)

// AMOLED Theme Colors (True OLED Black)
val AmoledBackground = Color(0xFF000000)
val AmoledSurface = Color(0xFF0A0D12)
val AmoledSurfaceVariant = Color(0xFF121720)
val AmoledOnBackground = Color(0xFFFFFFFF)
val AmoledOnSurface = Color(0xFFF3F4F6)
val AmoledOutline = Color(0xFF1F2937)

// Light Theme Colors
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightOutline = Color(0xFFCBD5E1)

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}
