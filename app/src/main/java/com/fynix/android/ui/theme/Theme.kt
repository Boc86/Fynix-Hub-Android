package com.fynix.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = android.graphics.Color.parseColor("#1976D2"),
    secondary = android.graphics.Color.parseColor("#03DAC6"),
    tertiary = android.graphics.Color.parseColor("#C0CA33"),
    background = android.graphics.Color.parseColor("#121212"),
    surface = android.graphics.Color.parseColor("#1E1E1E"),
    onPrimary = android.graphics.Color.parseColor("#FFFFFF"),
    onSecondary = android.graphics.Color.parseColor("#000000"),
    onTertiary = android.graphics.Color.parseColor("#000000"),
    onBackground = android.graphics.Color.parseColor("#FFFFFF"),
    onSurface = android.graphics.Color.parseColor("#FFFFFF")
)

private val LightColorScheme = lightColorScheme(
    primary = android.graphics.Color.parseColor("#1976D2"),
    secondary = android.graphics.Color.parseColor("#03DAC6"),
    tertiary = android.graphics.Color.parseColor("#C0CA33"),
    background = android.graphics.Color.parseColor("#FFFFFF"),
    surface = android.graphics.Color.parseColor("#FFFFFF"),
    onPrimary = android.graphics.Color.parseColor("#FFFFFF"),
    onSecondary = android.graphics.Color.parseColor("#000000"),
    onTertiary = android.graphics.Color.parseColor("#000000"),
    onBackground = android.graphics.Color.parseColor("#000000"),
    onSurface = android.graphics.Color.parseColor("#000000")
)

@Composable
fun FynixHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context as Activity
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
