package com.dravtv.player.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DravPrimary,
    secondary = DravAccent,
    background = DravBg,
    surface = DravSurface,
    surfaceVariant = DravSurfaceVariant,
    onPrimary = DravTextPrimary,
    onSecondary = DravBg,
    onBackground = DravTextPrimary,
    onSurface = DravTextPrimary
)

@Composable
fun DravTVTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
