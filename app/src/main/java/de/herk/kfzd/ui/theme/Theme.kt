package de.herk.kfzd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DkfzInputFocusedBorder,
    onPrimary = Color.White,
    background = DkfzBackground,
    onBackground = DkfzPrimaryText,
    surface = DkfzBackground,
    onSurface = DkfzPrimaryText,
    onSurfaceVariant = DkfzSecondaryText,
    outline = DkfzInputBorder,
    outlineVariant = DkfzDivider
)

@Composable
fun DKFZTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

