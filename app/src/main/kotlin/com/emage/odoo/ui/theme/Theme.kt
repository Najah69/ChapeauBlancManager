package com.emage.odoo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Odoo brand colors
private val OdooPurple = Color(0xFF875A7B)
private val OdooPurpleDark = Color(0xFF684657)
private val OdooPurpleLight = Color(0xFFC792AE)

private val LightColorScheme = lightColorScheme(
    primary = OdooPurple,
    onPrimary = Color.White,
    primaryContainer = OdooPurpleLight,
    onPrimaryContainer = Color(0xFF2B1A26),
    secondary = Color(0xFF5C5B6E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0F0),
    tertiary = Color(0xFF7B5A7B),
    background = Color(0xFFFBF8FA),
    surface = Color(0xFFFBF8FA),
    onBackground = Color(0xFF1B1B1F),
    onSurface = Color(0xFF1B1B1F),
    error = Color(0xFFBA1A1A),
    surfaceVariant = Color(0xFFF0E9EE)
)

private val DarkColorScheme = darkColorScheme(
    primary = OdooPurpleLight,
    onPrimary = Color(0xFF3E2D39),
    primaryContainer = OdooPurpleDark,
    onPrimaryContainer = Color(0xFFF8D9F0),
    secondary = Color(0xFFC5C4D7),
    onSecondary = Color(0xFF2E2E3E),
    secondaryContainer = Color(0xFF444455),
    tertiary = Color(0xFFD7BED7),
    background = Color(0xFF1B1B1F),
    surface = Color(0xFF1B1B1F),
    onBackground = Color(0xFFE4E1E6),
    onSurface = Color(0xFFE4E1E6),
    error = Color(0xFFFFB4AB),
    surfaceVariant = Color(0xFF484148)
)

@Composable
fun OdooNativeTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
