package com.dhyan.malayalamcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkTempleMaroon,
    onPrimary = Color.Black,
    primaryContainer = TempleMaroonDark,
    onPrimaryContainer = Color.White,
    secondary = TempleGold,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = TempleMaroon,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8D8),
    onPrimaryContainer = TempleMaroonDark,
    secondary = TempleGold,
    background = BackgroundCream,
    surface = CardBackground,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun MalayalamCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
