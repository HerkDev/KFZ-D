package de.herk.kfzd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DkfzInputFocusedBorder,
    onPrimary = Color.White,
    background = DkfzMainBackground,
    onBackground = DkfzPrimaryText,
    surface = DkfzBackground,
    onSurface = DkfzPrimaryText,
    onSurfaceVariant = DkfzSecondaryText,
    outline = DkfzInputBorder,
    outlineVariant = DkfzDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DB9E6),
    onPrimary = Color(0xFF07111B),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE3E8EE),
    surface = Color(0xFF171C21),
    onSurface = Color(0xFFE3E8EE),
    surfaceVariant = Color(0xFF252D35),
    onSurfaceVariant = Color(0xFFB8C3CE),
    outline = Color(0xFF8B99A8),
    outlineVariant = Color(0xFF4A5662)
)

@Composable
fun DKFZTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

