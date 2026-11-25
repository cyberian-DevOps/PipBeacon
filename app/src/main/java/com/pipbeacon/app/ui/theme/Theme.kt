package com.pipbeacon.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalThemeIsDark = compositionLocalOf { true }

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF5B731),
    onPrimary = Color(0xFF060D1A),
    primaryContainer = Color(0xFF2B210B),
    onPrimaryContainer = Color(0xFFFACC15),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF060D1A),
    secondaryContainer = Color(0xFF0B2542),
    onSecondaryContainer = Color(0xFF38BDF8),
    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFF060D1A),
    background = Color(0xFF060D1A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F1C33),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF091324),
    onSurfaceVariant = Color(0xFF9FB0C7),
    outline = Color(0xFF1E3454),
    outlineVariant = Color(0xFF152540)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF0284C7),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF075985),
    tertiary = Color(0xFF059669),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFD6E0EC),
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun PipBeaconTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PipBeaconTypography,
            content = content
        )
    }
}
