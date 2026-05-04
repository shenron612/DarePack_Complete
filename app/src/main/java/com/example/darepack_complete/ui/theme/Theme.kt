package com.example.darepack_complete.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    secondary = Teal,
    tertiary = Pink,
    background = LightBg,
    surface = LightCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

// We define a similar scheme for Dark to keep the app bright even if the system is in Dark mode
private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    secondary = Teal,
    tertiary = Pink,
    background = LightBg,
    surface = LightCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun DarePackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Force light theme colors even if darkTheme is true
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Status bar text should be dark since background is light
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}