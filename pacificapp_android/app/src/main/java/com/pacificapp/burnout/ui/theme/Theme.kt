package com.pacificapp.burnout.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Brand colors
val PacificBlue = Color(0xFF0077B6)
val PacificBlueLight = Color(0xFF48CAE4)
val PacificBlueDark = Color(0xFF023E8A)

val StressLow = Color(0xFF4CAF50)
val StressMedium = Color(0xFFFFC107)
val StressHigh = Color(0xFFFF9800)
val StressCritical = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = PacificBlueLight,
    secondary = PacificBlue,
    tertiary = PacificBlueDark,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = StressCritical,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PacificBlue,
    secondary = PacificBlueLight,
    tertiary = PacificBlueDark,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    error = StressCritical,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun BurnoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
