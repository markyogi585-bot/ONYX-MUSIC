package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.R
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

/**
 * Circular three-dot menu button at top-right with compact floating glass popup
 * displaying Settings and Account options.
 */
@Composable
fun GlassPopupMenu(
    expanded: Boolean,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed || expanded) 0.92f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "three_dot_button_scale"
    )

    Box(modifier = modifier) {
        FrostedGlassContainer(
            shape = CircleShape,
            accentGlowColor = ElectricBlue,
            elevation = 14.dp,
            baseAlpha = if (expanded) 0.90f else 0.78f,
            borderWidth = 0.85.dp,
            modifier = Modifier
                .size(44.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggleMenu
                )
                .testTag("floating_three_dot_menu_button")
        ) {
            val dotColor = auraColors.textPrimary
            Canvas(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.Center)
            ) {
                val dotRadius = 2.0.dp.toPx()
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val spacing = 5.4.dp.toPx()

                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(centerX, centerY - spacing)
                )
                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(centerX, centerY + spacing)
                )
            }
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(x = 0, y = 128),
                onDismissRequest = onDismissMenu,
                properties = PopupProperties(focusable = true)
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(150)) + scaleIn(
                        initialScale = 0.86f,
                        transformOrigin = TransformOrigin(0.9f, 0.05f),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
                    exit = fadeOut(tween(120)) + scaleOut(
                        targetScale = 0.9f,
                        transformOrigin = TransformOrigin(0.9f, 0.05f),
                        animationSpec = tween(120)
                    )
                ) {
                    FrostedGlassContainer(
                        shape = RoundedCornerShape(18.dp),
                        accentGlowColor = ElectricBlue,
                        elevation = 28.dp,
                        baseAlpha = 0.90f,
                        borderWidth = 0.9.dp,
                        modifier = Modifier
                            .width(214.dp)
                            .testTag("glass_popup_menu_container")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            GlassPopupMenuRow(
                                label = stringResource(R.string.menu_settings),
                                icon = Icons.Outlined.Settings,
                                onClick = onSettingsClick,
                                testTag = "popup_menu_settings"
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = auraColors.divider
                            )
                            GlassPopupMenuRow(
                                label = stringResource(R.string.menu_account),
                                icon = Icons.Outlined.AccountCircle,
                                onClick = onAccountClick,
                                testTag = "popup_menu_account"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassPopupMenuRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.1).sp
        )
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = auraColors.textPrimary.copy(alpha = 0.90f),
            modifier = Modifier.size(21.dp)
        )
    }
}
