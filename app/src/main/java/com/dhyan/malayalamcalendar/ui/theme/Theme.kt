package com.dhyan.malayalamcalendar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LiquidGlassColorScheme = darkColorScheme(
    primary = LiquidRose,
    onPrimary = Color.White,
    primaryContainer = Color(0x33E11D48),
    onPrimaryContainer = Color.White,
    secondary = LiquidGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0x33F59E0B),
    onSecondaryContainer = LiquidGold,
    background = LiquidBgBottom,
    surface = LiquidBgMid,
    onBackground = TextLiquidPrimary,
    onSurface = TextLiquidPrimary
)

@Composable
fun MalayalamCalendarTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LiquidGlassColorScheme,
        content = content
    )
}
