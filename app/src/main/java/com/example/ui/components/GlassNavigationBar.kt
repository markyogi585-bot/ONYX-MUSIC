package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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
import com.example.ui.theme.PureBlack
import kotlin.math.cos
import kotlin.math.sin

private val OnyxNavbarBackground = Color(0xF5101014)
private val OnyxNavbarBorder = Color(0x1AFFFFFF) // rgba(255, 255, 255, 0.10)
private val ActiveElectricBlue = Color(0xFF168BFF)
private val InactivePureWhite = Color(0xFFFFFFFF)

/**
 * Apple Music-Inspired Floating Pill Bottom Navigation Bar.
 *
 * Design Specifications:
 * - Floating pill container (#101014 deep dark translucent surface, 1dp rgba(255,255,255,0.10) border)
 * - Corner radius: 44.dp, Height: 70.dp (excluding system insets), Horizontal margin: 18.dp
 * - Five equal-width destinations (20% each): Home, New, Radio, Library, Search
 * - Custom vector icons (27.dp):
 *   1. Home: Premium house icon with roof and doorway details
 *   2. New: Four rounded squares in a balanced 2×2 grid
 *   3. Radio: Apple Music-inspired broadcasting signal with central node and concentric wave arcs
 *   4. Library: Premium music note with collection stack layers
 *   5. Search: Proportional magnifying glass with circular lens and angled handle
 * - Active state: #168BFF icon + #168BFF label ONLY (zero blue circular/square background bubbles)
 * - Inactive state: #FFFFFF icon + #FFFFFF label
 */
@Composable
fun GlassNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    accentGlowColor: Color = ElectricBlue,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val pillShape = RoundedCornerShape(44.dp)
    val containerBackground = if (auraColors.isDark) {
        OnyxNavbarBackground
    } else {
        Color(0xF5F4F4F8)
    }
    val containerBorder = if (auraColors.isDark) {
        OnyxNavbarBorder
    } else {
        Color.Black.copy(alpha = 0.10f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 18.dp)
            .padding(top = 4.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .height(OnyxDesignTokens.BottomNavBarHeight)
                .shadow(
                    elevation = 14.dp,
                    shape = pillShape,
                    ambientColor = PureBlack,
                    spotColor = PureBlack
                )
                .clip(pillShape)
                .background(containerBackground)
                .border(1.dp, containerBorder, pillShape)
                .testTag("floating_glass_navigation_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTab.entries.forEach { tab ->
                    GlassNavTabItem(
                        tab = tab,
                        isSelected = tab == selectedTab,
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
        InactivePureWhite
    } else {
        Color(0xFF1C1C1E)
    }

    val activeColor = ActiveElectricBlue

    val tintColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else unselectedColor,
        animationSpec = tween(durationMillis = OnyxDesignTokens.FastAnimMs),
        label = "nav_item_tint"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_press_scale"
    )

    // Strictly no background bubble/circle/pill behind individual icons:
    // Only the active icon and its text turn #168BFF.
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .semantics {
                role = Role.Tab
                selected = isSelected
                contentDescription = tab.label
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("nav_tab_${tab.route}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            AppleMusicNavIcon(
                tab = tab,
                tint = tintColor,
                isSelected = isSelected,
                modifier = Modifier.size(27.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = tab.label,
                color = tintColor,
                fontFamily = InterFontFamily,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                letterSpacing = (-0.15).sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppleMusicNavIcon(
    tab: AppTab,
    tint: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        when (tab) {
            AppTab.HOME -> drawHomeIcon(tint = tint, isSelected = isSelected)
            AppTab.NEW -> drawNewGridIcon(tint = tint, isSelected = isSelected)
            AppTab.RADIO -> drawRadioBroadcastIcon(tint = tint)
            AppTab.LIBRARY -> drawLibraryMusicIcon(tint = tint, isSelected = isSelected)
            AppTab.SEARCH -> drawSearchMagnifierIcon(tint = tint)
        }
    }
}

/**
 * 1. HOME — Premium house icon with clean roof and doorway details.
 */
private fun DrawScope.drawHomeIcon(tint: Color, isSelected: Boolean) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.082f

    if (isSelected) {
        val housePath = Path().apply {
            fillType = PathFillType.EvenOdd
            // Outer house silhouette with pitched roof
            moveTo(w * 0.50f, h * 0.11f)
            lineTo(w * 0.87f, h * 0.42f)
            lineTo(w * 0.87f, h * 0.86f)
            quadraticTo(w * 0.87f, h * 0.90f, w * 0.83f, h * 0.90f)
            lineTo(w * 0.17f, h * 0.90f)
            quadraticTo(w * 0.13f, h * 0.90f, w * 0.13f, h * 0.86f)
            lineTo(w * 0.13f, h * 0.42f)
            close()

            // Doorway cutout
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        left = w * 0.40f,
                        top = h * 0.56f,
                        right = w * 0.60f,
                        bottom = h * 0.91f
                    ),
                    topLeft = CornerRadius(w * 0.05f, w * 0.05f),
                    topRight = CornerRadius(w * 0.05f, w * 0.05f),
                    bottomLeft = CornerRadius.Zero,
                    bottomRight = CornerRadius.Zero
                )
            )
        }
        drawPath(path = housePath, color = tint, style = Fill)
    } else {
        // Outlined house with roof and doorway details
        val bodyPath = Path().apply {
            moveTo(w * 0.17f, h * 0.44f)
            lineTo(w * 0.17f, h * 0.85f)
            quadraticTo(w * 0.17f, h * 0.89f, w * 0.21f, h * 0.89f)
            lineTo(w * 0.79f, h * 0.89f)
            quadraticTo(w * 0.83f, h * 0.89f, w * 0.83f, h * 0.85f)
            lineTo(w * 0.83f, h * 0.44f)
        }
        val roofPath = Path().apply {
            moveTo(w * 0.10f, h * 0.46f)
            lineTo(w * 0.50f, h * 0.13f)
            lineTo(w * 0.90f, h * 0.46f)
        }
        val doorPath = Path().apply {
            moveTo(w * 0.40f, h * 0.89f)
            lineTo(w * 0.40f, h * 0.59f)
            quadraticTo(w * 0.40f, h * 0.55f, w * 0.44f, h * 0.55f)
            lineTo(w * 0.56f, h * 0.55f)
            quadraticTo(w * 0.60f, h * 0.55f, w * 0.60f, h * 0.59f)
            lineTo(w * 0.60f, h * 0.89f)
        }
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawPath(path = bodyPath, color = tint, style = stroke)
        drawPath(path = roofPath, color = tint, style = stroke)
        drawPath(path = doorPath, color = tint, style = stroke)
    }
}

/**
 * 2. NEW — Four rounded squares arranged in a 2×2 grid with equal dimensions and spacing.
 */
private fun DrawScope.drawNewGridIcon(tint: Color, isSelected: Boolean) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.080f
    val margin = w * 0.14f
    val gap = w * 0.11f
    val boxSize = (w - margin * 2f - gap) / 2f
    val corner = CornerRadius(w * 0.085f, w * 0.085f)

    val positions = listOf(
        Offset(margin, margin),
        Offset(margin + boxSize + gap, margin),
        Offset(margin, margin + boxSize + gap),
        Offset(margin + boxSize + gap, margin + boxSize + gap)
    )

    positions.forEach { topLeft ->
        if (isSelected) {
            drawRoundRect(
                color = tint,
                topLeft = topLeft,
                size = Size(boxSize, boxSize),
                cornerRadius = corner,
                style = Fill
            )
        } else {
            drawRoundRect(
                color = tint,
                topLeft = topLeft,
                size = Size(boxSize, boxSize),
                cornerRadius = corner,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

/**
 * 3. RADIO — Apple Music-inspired broadcasting/radio waves icon.
 * Central transmitter dot with symmetric concentric circular wave arcs on left and right.
 */
private fun DrawScope.drawRadioBroadcastIcon(tint: Color) {
    val w = size.width
    val h = size.height
    val center = Offset(w * 0.50f, h * 0.50f)
    val strokeWidth = w * 0.082f
    val arcStroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

    // Central broadcast node
    drawCircle(
        color = tint,
        radius = w * 0.095f,
        center = center
    )

    // Inner left & right wave arcs
    val innerRadius = w * 0.24f
    val innerRect = Rect(center = center, radius = innerRadius)
    drawArc(
        color = tint,
        startAngle = -42f,
        sweepAngle = 84f,
        useCenter = false,
        topLeft = innerRect.topLeft,
        size = innerRect.size,
        style = arcStroke
    )
    drawArc(
        color = tint,
        startAngle = 138f,
        sweepAngle = 84f,
        useCenter = false,
        topLeft = innerRect.topLeft,
        size = innerRect.size,
        style = arcStroke
    )

    // Outer left & right wave arcs
    val outerRadius = w * 0.41f
    val outerRect = Rect(center = center, radius = outerRadius)
    drawArc(
        color = tint,
        startAngle = -48f,
        sweepAngle = 96f,
        useCenter = false,
        topLeft = outerRect.topLeft,
        size = outerRect.size,
        style = arcStroke
    )
    drawArc(
        color = tint,
        startAngle = 132f,
        sweepAngle = 96f,
        useCenter = false,
        topLeft = outerRect.topLeft,
        size = outerRect.size,
        style = arcStroke
    )
}

/**
 * 4. LIBRARY — Premium music note with collection/library layers (Apple Music style).
 */
private fun DrawScope.drawLibraryMusicIcon(tint: Color, isSelected: Boolean) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.078f
    val corner = CornerRadius(w * 0.10f, w * 0.10f)

    // Top-left stacked library card accent line
    drawLine(
        color = tint,
        start = Offset(w * 0.26f, h * 0.12f),
        end = Offset(w * 0.74f, h * 0.12f),
        strokeWidth = strokeWidth * 0.9f,
        cap = StrokeCap.Round
    )

    // Main rounded square album/collection frame
    val cardTop = h * 0.22f
    val cardLeft = w * 0.15f
    val cardSize = w * 0.70f

    drawRoundRect(
        color = tint,
        topLeft = Offset(cardLeft, cardTop),
        size = Size(cardSize, cardSize),
        cornerRadius = corner,
        style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
    )

    // Beamed eighth note inside the library frame
    val noteColor = tint
    val leftHead = Offset(w * 0.39f, h * 0.69f)
    val rightHead = Offset(w * 0.62f, h * 0.64f)
    val headRadius = w * 0.068f

    drawCircle(color = noteColor, radius = headRadius, center = leftHead)
    drawCircle(color = noteColor, radius = headRadius, center = rightHead)

    val stemStroke = strokeWidth * 0.85f
    // Left stem
    drawLine(
        color = noteColor,
        start = Offset(leftHead.x + headRadius * 0.7f, leftHead.y),
        end = Offset(leftHead.x + headRadius * 0.7f, h * 0.42f),
        strokeWidth = stemStroke,
        cap = StrokeCap.Round
    )
    // Right stem
    drawLine(
        color = noteColor,
        start = Offset(rightHead.x + headRadius * 0.7f, rightHead.y),
        end = Offset(rightHead.x + headRadius * 0.7f, h * 0.37f),
        strokeWidth = stemStroke,
        cap = StrokeCap.Round
    )
    // Top angled beam
    drawLine(
        color = noteColor,
        start = Offset(leftHead.x + headRadius * 0.7f, h * 0.42f),
        end = Offset(rightHead.x + headRadius * 0.7f, h * 0.37f),
        strokeWidth = stemStroke * 1.25f,
        cap = StrokeCap.Round
    )
}

/**
 * 5. SEARCH — Clean magnifying glass icon with proportional circular lens and angled handle.
 */
private fun DrawScope.drawSearchMagnifierIcon(tint: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.088f
    val lensCenter = Offset(w * 0.43f, h * 0.43f)
    val lensRadius = w * 0.26f

    drawCircle(
        color = tint,
        radius = lensRadius,
        center = lensCenter,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    val angleRad = Math.toRadians(45.0)
    val handleStart = Offset(
        x = lensCenter.x + (lensRadius * cos(angleRad)).toFloat(),
        y = lensCenter.y + (lensRadius * sin(angleRad)).toFloat()
    )
    val handleEnd = Offset(w * 0.84f, h * 0.84f)

    drawLine(
        color = tint,
        start = handleStart,
        end = handleEnd,
        strokeWidth = strokeWidth * 1.08f,
        cap = StrokeCap.Round
    )
}
