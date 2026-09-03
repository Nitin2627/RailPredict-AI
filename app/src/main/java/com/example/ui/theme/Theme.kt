package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RailBlue400,
    onPrimary = RailNavy900,
    primaryContainer = RailBlue500,
    onPrimaryContainer = Color.White,
    secondary = RailTealAccent,
    onSecondary = RailNavy900,
    secondaryContainer = RailNavy700,
    onSecondaryContainer = RailTealAccent,
    tertiary = RailPurpleAI,
    onTertiary = Color.White,
    background = RailNavy900,
    onBackground = RailTextPrimary,
    surface = RailNavy800,
    onSurface = RailTextPrimary,
    surfaceVariant = RailNavy700,
    onSurfaceVariant = RailTextSecondary,
    outline = RailNavy600,
    error = RailRedSevere,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Railway control room always uses high-fidelity dark operations theme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

