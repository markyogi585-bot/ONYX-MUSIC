package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

@Composable
fun PremiumTopBar(
    title: String,
    isPopupMenuExpanded: Boolean,
    onTogglePopupMenu: () -> Unit,
    onDismissPopupMenu: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onSecondaryActionClick: (() -> Unit)? = null,
    secondaryActionDescription: String = "Create Playlist",
    isScrolled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val baseBg = auraColors.background
    val topGradColor by animateColorAsState(
        targetValue = if (isScrolled) baseBg.copy(alpha = 0.94f) else baseBg.copy(alpha = 0.78f),
        animationSpec = tween(250),
        label = "topbar_grad_color"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseBg.copy(alpha = 0.98f),
                        topGradColor,
                        Color.Transparent
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .testTag("premium_top_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 38.sp,
                    letterSpacing = (-0.8).sp,
                    color = auraColors.textPrimary
                ),
                modifier = Modifier.testTag("top_bar_title")
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onSecondaryActionClick != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C1C1E))
                            .border(0.75.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                            .clickable(onClick = onSecondaryActionClick)
                            .testTag("top_bar_secondary_action_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = secondaryActionDescription,
                            tint = auraColors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                GlassPopupMenu(
                    expanded = isPopupMenuExpanded,
                    onToggleMenu = onTogglePopupMenu,
                    onDismissMenu = onDismissPopupMenu,
                    onSettingsClick = onSettingsClick,
                    onAccountClick = onAccountClick
                )
            }
        }
    }
}
