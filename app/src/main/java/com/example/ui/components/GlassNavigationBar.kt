package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppTab
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.OnyxDesignTokens

/**
 * Apple Music-inspired Floating Pill Glass Navigation Bar.
 * - Five equally spaced destinations: Home, New, Radio, Library, Search
 * - Balanced across 360dp, 393dp, 412dp, and 430dp screen widths with zero label clipping
 * - Selected state: vibrant electric blue (#0A84FF) icon + label + subtle glass pill highlight
 * - Unselected state: crisp white / subtle gray with consistent 22.dp icon geometry
 */
@Composable
fun GlassNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    accentGlowColor: Color = ElectricBlue,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(50)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        FrostedGlassContainer(
            shape = pillShape,
            accentGlowColor = accentGlowColor,
            elevation = 24.dp,
            baseAlpha = 0.86f,
            borderWidth = 0.85.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .height(OnyxDesignTokens.BottomNavBarHeight)
                .testTag("floating_glass_navigation_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    GlassNavTabItem(
                        tab = tab,
                        isSelected = isSelected,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassNavTabItem(
    tab: AppTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val unselectedColor = if (auraColors.isDark) {
        auraColors.textPrimary.copy(alpha = 0.86f)
    } else {
        auraColors.textPrimary.copy(alpha = 0.78f)
    }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) ElectricBlue else unselectedColor,
        animationSpec = tween(durationMillis = OnyxDesignTokens.FastAnimMs),
        label = "nav_icon_color"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) ElectricBlue else unselectedColor,
        animationSpec = tween(durationMillis = OnyxDesignTokens.FastAnimMs),
        label = "nav_label_color"
    )

    val iconOffsetY by animateDpAsState(
        targetValue = if (isSelected) (-1).dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_icon_offset"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "nav_press_scale"
    )

    val selectionGlowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "nav_glow_alpha"
    )

    val itemHighlightShape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .height(54.dp)
            .padding(horizontal = 2.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(itemHighlightShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("nav_tab_${tab.route}"),
        contentAlignment = Alignment.Center
    ) {
        if (selectionGlowAlpha > 0.01f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = (if (auraColors.isDark) Color.White else ElectricBlue)
                        .copy(alpha = 0.09f * selectionGlowAlpha),
                    cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.20f * selectionGlowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height * 0.42f),
                        radius = size.width * 0.52f
                    ),
                    center = Offset(size.width / 2f, size.height * 0.42f),
                    radius = size.width * 0.52f
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = iconOffsetY.coerceAtLeast(0.dp))
        ) {
            AnimatedContent(
                targetState = isSelected,
                transitionSpec = {
                    fadeIn(tween(150)) togetherWith fadeOut(tween(130))
                },
                label = "nav_icon_transition"
            ) { selectedState ->
                Icon(
                    imageVector = getTabIcon(tab, selectedState),
                    contentDescription = tab.label,
                    tint = iconColor,
                    modifier = Modifier.size(OnyxDesignTokens.BottomNavIconSize)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = tab.label,
                color = labelColor,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                letterSpacing = (-0.15).sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getTabIcon(tab: AppTab, isSelected: Boolean): ImageVector {
    return when (tab) {
        AppTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
        AppTab.NEW -> if (isSelected) Icons.Filled.GridView else Icons.Outlined.GridView
        AppTab.RADIO -> if (isSelected) Icons.Filled.Radio else Icons.Outlined.Radio
        AppTab.LIBRARY -> if (isSelected) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic
        AppTab.SEARCH -> if (isSelected) Icons.Filled.Search else Icons.Outlined.Search
    }
}
