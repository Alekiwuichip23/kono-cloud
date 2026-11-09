package com.alessandra.konocloud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KonoLightColorScheme = lightColorScheme(
    primary = PrimaryViolet,
    onPrimary = Color.White,
    primaryContainer = SoftLavender,
    onPrimaryContainer = PrimaryViolet,
    secondary = TextSecondary,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun KonoCloudTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KonoLightColorScheme,
        typography = Typography,
        content = content
    )
}
