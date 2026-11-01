package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Phase 2 Core Apple Music Dark & Light Mode Tokens
val PureBlack = Color(0xFF000000)
val DeepObsidian = Color(0xFF101010)
val CharcoalSurface = Color(0xFF161618)
val CharcoalElevated = Color(0xFF1C1C1E)
val CharcoalCard = Color(0xFF202124)
val CharcoalHighlight = Color(0xFF2C2C2E)

// Light Mode Tokens
val PureWhite = Color(0xFFFFFFFF)
val LightGroupedBackground = Color(0xFFF2F2F7)
val LightCardSurface = Color(0xFFFFFFFF)
val LightElevatedSurface = Color(0xFFE5E5EA)
val LightTextPrimary = Color(0xFF000000)
val LightTextSecondary = Color(0xFF6C6C70)
val LightDivider = Color(0x26000000)

// Electric Blue Accents (Selected Tabs, Links, Interactive Highlights)
val ElectricBlue = Color(0xFF0A84FF)
val ElectricBlueBright = Color(0xFF409CFF)
val ElectricBlueGlow = Color(0x440A84FF)
val ElectricCyan = Color(0xFF00F5D4)
val DestructiveRed = Color(0xFFFF453A)

// Glassmorphic Tokens
val GlassSurfaceDark = Color(0xB818181C)
val GlassSurfaceElevated = Color(0xCC222228)
val GlassPopupSurface = Color(0xD91E1E24)
val GlassBorderLight = Color(0x38FFFFFF)
val GlassBorderSubtle = Color(0x1EFFFFFF)
val GlassInnerHighlight = Color(0x14FFFFFF)

// Typography & Iconography Colors (Phase 2: Primary #FFFFFF, Secondary #A1A1AA)
val TextPrimaryWhite = Color(0xFFFFFFFF)
val TextSecondaryGrey = Color(0xFFA1A1AA)
val TextTertiaryMuted = Color(0xFF71717A)
val DividerSubtle = Color(0x22FFFFFF)

// Ambient Artwork Glow Colors
val AmbientViolet = Color(0xFF7209B7)
val AmbientCrimson = Color(0xFFFF2A5F)
val AmbientAmber = Color(0xFFFF9E00)
val AmbientEmerald = Color(0xFF10B981)

/**
 * Reusable global design tokens for spacing, corner radii, icon dimensions,
 * artwork sizes, and animation timings across ONYX MUSIC.
 */
object OnyxDesignTokens {
    // Spacing tokens
    val ScreenHorizontalPadding = 20.dp
    val SectionVerticalSpacing = 28.dp
    val CardHorizontalGap = 14.dp
    val HeroCardHorizontalGap = 16.dp
    val ListItemVerticalPadding = 8.dp

    // Corner radius tokens
    val HeroCardCornerRadius = 18.dp
    val AlbumCardCornerRadius = 14.dp
    val PlaylistCardCornerRadius = 16.dp
    val MiniPlayerCornerRadius = 18.dp
    val TrackThumbCornerRadius = 10.dp
    val SheetTopCornerRadius = 26.dp

    // Artwork & Card dimensions
    val EditorialHeroCardWidth = 274.dp
    val EditorialHeroCardHeight = 274.dp
    val StandardSquareCardWidth = 164.dp
    val PlaylistSquareCardWidth = 174.dp
    val ArtistAvatarSize = 114.dp
    val MiniPlayerHeight = 58.dp
    val MiniPlayerArtSize = 44.dp
    val BottomNavBarHeight = 64.dp
    val BottomNavIconSize = 22.dp

    // Animation durations (ms)
    const val FastAnimMs = 180
    const val MediumAnimMs = 260
    const val SlowAnimMs = 420
}

@Immutable
data class AuraColors(
    val isDark: Boolean,
    val background: Color,
    val secondaryBackground: Color,
    val groupedBackground: Color,
    val cardSurface: Color,
    val elevatedSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val accentBlue: Color,
    val glassSurfaceTop: Color,
    val glassSurfaceBottom: Color,
    val glassBorder: Color,
    val glassHighlight: Color,
    val unselectedNavTint: Color
)

val DarkAuraColors = AuraColors(
    isDark = true,
    background = PureBlack,
    secondaryBackground = DeepObsidian,
    groupedBackground = PureBlack,
    cardSurface = CharcoalCard,
    elevatedSurface = CharcoalElevated,
    textPrimary = TextPrimaryWhite,
    textSecondary = TextSecondaryGrey,
    divider = DividerSubtle,
    accentBlue = ElectricBlue,
    glassSurfaceTop = Color(0xFF26262C),
    glassSurfaceBottom = Color(0xFF121215),
    glassBorder = Color(0x34FFFFFF),
    glassHighlight = Color(0x18FFFFFF),
    unselectedNavTint = TextPrimaryWhite
)

val LightAuraColors = AuraColors(
    isDark = false,
    background = PureWhite,
    secondaryBackground = LightGroupedBackground,
    groupedBackground = LightGroupedBackground,
    cardSurface = LightCardSurface,
    elevatedSurface = LightElevatedSurface,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    divider = LightDivider,
    accentBlue = ElectricBlue,
    glassSurfaceTop = Color(0xFFF9F9FB),
    glassSurfaceBottom = Color(0xFFEAEAF0),
    glassBorder = Color(0x22000000),
    glassHighlight = Color(0x66FFFFFF),
    unselectedNavTint = LightTextPrimary
)

val LocalAuraColors = staticCompositionLocalOf { DarkAuraColors }
