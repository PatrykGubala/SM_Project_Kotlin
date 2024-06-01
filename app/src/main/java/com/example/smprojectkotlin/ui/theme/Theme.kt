package com.example.smprojectkotlin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = Red80,
        tertiary = Teal80,
        background = White80,
        surface = White80,
        onPrimary = White80,
        onSecondary = White80,
        onTertiary = White80,
        onBackground = White100,
        onSurface = White80,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple80,
        secondary = Red80,
        tertiary = Teal80,
        background = White80,
        surface = White80,
        onPrimary = White80,
        onSecondary = White80,
        onTertiary = White80,
        onBackground = White80,
        onSurface = White80,
    )

@Composable
fun SMProjectKotlinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
