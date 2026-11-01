package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.data.preferences.ThemeMode

private val DarkMusicColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = TextPrimaryWhite,
    primaryContainer = CharcoalElevated,
    onPrimaryContainer = ElectricBlueBright,
    secondary = ElectricCyan,
    onSecondary = PureBlack,
    secondaryContainer = CharcoalCard,
    onSecondaryContainer = TextPrimaryWhite,
    tertiary = AmbientViolet,
    background = PureBlack,
    onBackground = TextPrimaryWhite,
    surface = CharcoalSurface,
    onSurface = TextPrimaryWhite,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = TextSecondaryGrey,
    outline = GlassBorderLight,
    outlineVariant = DividerSubtle
)

private val LightMusicColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = PureWhite,
    primaryContainer = LightElevatedSurface,
    onPrimaryContainer = ElectricBlue,
    secondary = ElectricBlue,
    onSecondary = PureWhite,
    secondaryContainer = LightCardSurface,
    onSecondaryContainer = LightTextPrimary,
    tertiary = AmbientViolet,
    background = PureWhite,
    onBackground = LightTextPrimary,
    surface = LightCardSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightElevatedSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0x28000000),
    outlineVariant = LightDivider
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun AuraMusicTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    largerText: Boolean = false,
    increaseContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.FOLLOW_SYSTEM -> systemDark
    }

    val baseAuraColors = if (useDarkTheme) DarkAuraColors else LightAuraColors
    val adjustedAuraColors = if (increaseContrast) {
        baseAuraColors.copy(
            textSecondary = if (useDarkTheme) Color(0xFFD1D1D6) else Color(0xFF2C2C2E),
            divider = if (useDarkTheme) Color(0x48FFFFFF) else Color(0x48000000),
            glassBorder = if (useDarkTheme) Color(0x66FFFFFF) else Color(0x44000000)
        )
    } else {
        baseAuraColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !useDarkTheme
                controller.isAppearanceLightNavigationBars = !useDarkTheme
            }
        }
    }

    val currentDensity = LocalDensity.current
    val effectiveDensity = if (largerText) {
        Density(
            density = currentDensity.density,
            fontScale = (currentDensity.fontScale * 1.14f).coerceAtMost(1.45f)
        )
    } else {
        currentDensity
    }

    CompositionLocalProvider(
        LocalAuraColors provides adjustedAuraColors,
        LocalDensity provides effectiveDensity
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) DarkMusicColorScheme else LightMusicColorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AuraMusicTheme(
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        content = content
    )
}
