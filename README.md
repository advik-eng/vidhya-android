package com.vidhya.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E3A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0EAFF),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    background = Color(0xFFF8F7F4),
    onBackground = Color(0xFF111827),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF0F172A),
    error = Color(0xFFDC2626),
    onError = Color.White,
    tertiary = Color(0xFF0F766E),
    tertiaryContainer = Color(0xFFCCFBF1)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFE2E8F0),
    error = Color(0xFFF87171),
    onError = Color(0xFF111827),
    tertiary = Color(0xFF5EEAD4),
    tertiaryContainer = Color(0xFF134E4A)
)

@Composable
fun VidhyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
