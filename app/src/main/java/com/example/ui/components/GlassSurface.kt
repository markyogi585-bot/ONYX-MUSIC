package com.example.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack

/**
 * Applies hardware RenderEffect blur on Android 12+ (API 31+) to blur underlying content.
 */
fun Modifier.realBackdropBlur(radiusPx: Float): Modifier {
    if (radiusPx <= 0.5f) return this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.graphicsLayer {
            renderEffect = RenderEffect.createBlurEffect(
                radiusPx,
                radiusPx,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        this
    }
}

/**
 * Core translucent glass container supporting both Dark Mode and Light Mode.
 */
@Composable
fun FrostedGlassContainer(
    shape: Shape,
    modifier: Modifier = Modifier,
    accentGlowColor: Color = ElectricBlue,
    elevation: Dp = 20.dp,
    baseAlpha: Float = 0.80f,
    borderWidth: Dp = 0.8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val auraColors = LocalAuraColors.current

    val glassFillBrush = Brush.verticalGradient(
        colors = listOf(
            auraColors.glassSurfaceTop.copy(alpha = baseAlpha),
            auraColors.glassSurfaceBottom.copy(alpha = (baseAlpha + 0.08f).coerceAtMost(0.95f))
        )
    )

    val specularBorderBrush = Brush.linearGradient(
        colors = if (auraColors.isDark) {
            listOf(
                Color.White.copy(alpha = 0.30f),
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.16f)
            )
        } else {
            listOf(
                Color.Black.copy(alpha = 0.12f),
                Color.Black.copy(alpha = 0.06f),
                Color.Black.copy(alpha = 0.10f)
            )
        },
        start = Offset(0f, 0f),
        end = Offset(600f, 400f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (auraColors.isDark) PureBlack else Color.Black.copy(alpha = 0.14f),
                spotColor = if (auraColors.isDark) PureBlack else Color.Black.copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(brush = glassFillBrush)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            auraColors.glassHighlight,
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.42f
                    )
                )
            }
            .border(width = borderWidth, brush = specularBorderBrush, shape = shape)
    ) {
        content()
    }
}

/**
 * Reusable GlassCard component for grouped settings/account/content containers.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val auraColors = LocalAuraColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(auraColors.cardSurface)
            .border(0.6.dp, auraColors.glassBorder.copy(alpha = 0.35f), shape),
        content = content
    )
}

/**
 * Reusable Circular Floating Glass Back Button matching the Apple Music Settings & Account screenshots.
 */
@Composable
fun GlassBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "circular_glass_back_button"
) {
    val auraColors = LocalAuraColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "back_btn_scale"
    )

    FrostedGlassContainer(
        shape = CircleShape,
        elevation = 10.dp,
        baseAlpha = 0.82f,
        borderWidth = 0.8.dp,
        modifier = modifier
            .size(44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = "Navigate back",
            tint = auraColors.textPrimary,
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.Center)
                .padding(start = 4.dp)
        )
    }
}

/**
 * Reusable GlassButton with subtle press scale and glass surface.
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = ElectricBlue,
    accentColor: Color = ElectricBlue,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "glass_btn_scale"
    )

    FrostedGlassContainer(
        shape = RoundedCornerShape(14.dp),
        accentGlowColor = accentColor,
        elevation = 8.dp,
        baseAlpha = if (enabled) 0.85f else 0.45f,
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (enabled) textColor else textColor.copy(alpha = 0.45f),
                fontFamily = InterFontFamily,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Reusable GlassDialog with smooth scale + fade animation.
 */
@Composable
fun GlassDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)) + scaleIn(
            initialScale = 0.90f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ),
        exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.92f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack.copy(alpha = 0.65f))
                .clickable(onClick = onDismissRequest)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            FrostedGlassContainer(
                shape = RoundedCornerShape(22.dp),
                baseAlpha = 0.94f,
                elevation = 28.dp,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    content = content
                )
            }
        }
    }
}

/**
 * Reusable GlassBottomSheet with smooth vertical slide + fade animation.
 */
@Composable
fun GlassBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut(tween(160)) + slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack.copy(alpha = 0.60f))
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.BottomCenter
        ) {
            FrostedGlassContainer(
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                baseAlpha = 0.94f,
                elevation = 30.dp,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    content = content
                )
            }
        }
    }
}

/**
 * Standard Full-Screen Top Navigation Bar with Circular Back Button at top-left
 * and centered screen title, matching the Settings & Account screenshots.
 */
@Composable
fun SubpageTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backButtonTestTag: String = "subpage_back_button"
) {
    val auraColors = LocalAuraColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(auraColors.groupedBackground.copy(alpha = 0.92f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        GlassBackButton(
            onClick = onBackClick,
            testTag = backButtonTestTag,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = title,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
