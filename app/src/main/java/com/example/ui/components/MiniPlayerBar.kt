package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.Song
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.OnyxDesignTokens
import com.example.ui.theme.PureBlack

/**
 * Floating Apple Music-Inspired Dark Glass Mini-Player positioned directly above the Bottom Navigation Bar.
 * - Strict 1:1 square album artwork thumbnail on the left (46.dp)
 * - Bold white song title + secondary grey artist subtitle with animated equalizer bars when playing
 * - Accessible 48.dp Play/Pause & Next Track buttons
 * - Smooth animated progress bar along the bottom edge
 */
@Composable
fun FloatingGlassMiniPlayer(
    currentSong: Song,
    isPlaying: Boolean,
    currentPositionSeconds: Int,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onExpandNowPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val pillShape = RoundedCornerShape(OnyxDesignTokens.MiniPlayerCornerRadius)
    val rawProgress = if (currentSong.durationSeconds > 0) {
        (currentPositionSeconds.toFloat() / currentSong.durationSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(300),
        label = "mini_player_progress"
    )

    val containerBg = if (auraColors.isDark) Color(0xF21C1C1E) else Color(0xF5FFFFFF)
    val borderTint = if (auraColors.isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.10f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .height(OnyxDesignTokens.MiniPlayerHeight)
                .shadow(
                    elevation = 16.dp,
                    shape = pillShape,
                    ambientColor = PureBlack,
                    spotColor = PureBlack
                )
                .clip(pillShape)
                .background(containerBg)
                .border(1.dp, borderTint, pillShape)
                .clickable(onClick = onExpandNowPlaying)
                .testTag("floating_mini_player")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(OnyxDesignTokens.MiniPlayerArtSize)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(OnyxDesignTokens.TrackThumbCornerRadius))
                                .background(auraColors.cardSurface)
                                .border(
                                    0.6.dp,
                                    borderTint,
                                    RoundedCornerShape(OnyxDesignTokens.TrackThumbCornerRadius)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = currentSong.artworkRes,
                                contentDescription = currentSong.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isPlaying) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PureBlack.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AnimatedEqualizerBars(color = ElectricBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSong.title,
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = currentSong.artist,
                                color = auraColors.textSecondary,
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("mini_player_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = auraColors.textPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("mini_player_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FastForward,
                                contentDescription = "Next Track",
                                tint = auraColors.textPrimary,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(auraColors.textSecondary.copy(alpha = 0.14f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(2.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(ElectricBlue, currentSong.accentColor)
                                )
                            )
                    )
                }
            }
        }
    }
}
