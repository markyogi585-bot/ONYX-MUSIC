package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.Album
import com.example.domain.model.ArtistProfile
import com.example.domain.model.Playlist
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.OnyxDesignTokens
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextPrimaryWhite

/**
 * Strict 1:1 Square Album Artwork Card with consistent dimensions, rounded corners,
 * high-resolution image rendering, and single-line title + artist hierarchy.
 */
@Composable
fun AlbumArtworkCard(
    album: Album,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit,
    cardWidth: Dp = OnyxDesignTokens.StandardSquareCardWidth,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "album_card_scale"
    )

    val cornerShape = RoundedCornerShape(
        if (cardWidth >= 240.dp) OnyxDesignTokens.HeroCardCornerRadius
        else OnyxDesignTokens.AlbumCardCornerRadius
    )

    Column(
        modifier = modifier
            .width(cardWidth)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onCardClick
            )
            .testTag("album_card_${album.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = if (auraColors.isDark) 16.dp else 8.dp,
                    shape = cornerShape,
                    ambientColor = album.primaryColor.copy(alpha = 0.35f),
                    spotColor = PureBlack.copy(alpha = 0.45f)
                )
                .clip(cornerShape)
                .background(auraColors.cardSurface)
                .border(0.7.dp, auraColors.glassBorder.copy(alpha = 0.4f), cornerShape)
        ) {
            AsyncImage(
                model = album.artworkModel,
                placeholder = painterResource(album.artworkRes),
                error = painterResource(album.artworkRes),
                contentDescription = "${album.title} by ${album.artist}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent,
                            PureBlack.copy(alpha = 0.38f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )
            }

            if (album.isSpatialAudio) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PureBlack.copy(alpha = 0.58f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = TextPrimaryWhite.copy(alpha = 0.85f),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "ATMOS",
                            color = TextPrimaryWhite.copy(alpha = 0.9f),
                            fontFamily = InterFontFamily,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(PureBlack.copy(alpha = 0.62f))
                    .border(0.7.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    .clickable(onClick = onPlayClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play ${album.title}",
                    tint = TextPrimaryWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = album.title,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = if (cardWidth >= 240.dp) 15.5.sp else 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = (-0.15).sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = album.artist,
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = if (cardWidth >= 240.dp) 13.5.sp else 12.5.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Editorial Feature / Top Picks Hero Card with full-bleed high-resolution artwork
 * and a smooth bottom-to-top gradient overlay displaying category/curator,
 * bold headline, artist/year subtitle, and quick play button inside the bottom panel.
 */
@Composable
fun FeaturedAlbumHeroCard(
    album: Album,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "hero_card_scale"
    )
    val cardShape = RoundedCornerShape(OnyxDesignTokens.HeroCardCornerRadius)

    Box(
        modifier = modifier
            .width(OnyxDesignTokens.EditorialHeroCardWidth)
            .height(OnyxDesignTokens.EditorialHeroCardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (auraColors.isDark) 18.dp else 10.dp,
                shape = cardShape,
                ambientColor = album.primaryColor.copy(alpha = 0.35f),
                spotColor = PureBlack.copy(alpha = 0.5f)
            )
            .clip(cardShape)
            .background(auraColors.cardSurface)
            .border(0.75.dp, Color.White.copy(alpha = 0.12f), cardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onCardClick
            )
            .testTag("featured_hero_card_${album.id}")
    ) {
        AsyncImage(
            model = album.artworkModel,
            placeholder = painterResource(album.artworkRes),
            error = painterResource(album.artworkRes),
            contentDescription = album.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Smooth bottom editorial gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to PureBlack.copy(alpha = 0.14f),
                        0.40f to Color.Transparent,
                        0.68f to PureBlack.copy(alpha = 0.72f),
                        1.0f to PureBlack.copy(alpha = 0.94f)
                    )
                )
        )

        if (album.isSpatialAudio) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PureBlack.copy(alpha = 0.56f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = null,
                        tint = TextPrimaryWhite.copy(alpha = 0.9f),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DOLBY ATMOS",
                        color = TextPrimaryWhite.copy(alpha = 0.92f),
                        fontFamily = InterFontFamily,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = (album.badgeText ?: album.categoryTag).uppercase(),
                color = TextPrimaryWhite.copy(alpha = 0.72f),
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = album.title,
                        color = TextPrimaryWhite,
                        fontFamily = InterFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${album.artist} • ${album.releaseYear}",
                        color = TextPrimaryWhite.copy(alpha = 0.80f),
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = album.description,
                        color = TextPrimaryWhite.copy(alpha = 0.68f),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 15.5.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(0.75.dp, Color.White.copy(alpha = 0.32f), CircleShape)
                        .clickable(onClick = onPlayClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play ${album.title}",
                        tint = TextPrimaryWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistArtworkCard(
    playlist: Playlist,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit,
    cardWidth: Dp = OnyxDesignTokens.PlaylistSquareCardWidth,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val cornerShape = RoundedCornerShape(OnyxDesignTokens.PlaylistCardCornerRadius)

    Column(
        modifier = modifier
            .width(cardWidth)
            .clickable(onClick = onCardClick)
            .testTag("playlist_card_${playlist.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = if (auraColors.isDark) 16.dp else 8.dp,
                    shape = cornerShape,
                    ambientColor = playlist.primaryColor.copy(alpha = 0.35f),
                    spotColor = PureBlack.copy(alpha = 0.45f)
                )
                .clip(cornerShape)
                .background(auraColors.cardSurface)
                .border(0.7.dp, auraColors.glassBorder.copy(alpha = 0.4f), cornerShape)
        ) {
            AsyncImage(
                model = playlist.artworkModel,
                placeholder = painterResource(playlist.artworkRes),
                error = painterResource(playlist.artworkRes),
                contentDescription = playlist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PureBlack.copy(alpha = 0.25f),
                                Color.Transparent,
                                PureBlack.copy(alpha = 0.78f)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "ONYX MUSIC",
                    color = TextPrimaryWhite.copy(alpha = 0.85f),
                    fontFamily = InterFontFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(end = 36.dp)
                ) {
                    Text(
                        text = playlist.title,
                        color = TextPrimaryWhite,
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PureBlack.copy(alpha = 0.65f))
                        .border(0.7.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                        .clickable(onClick = onPlayClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play ${playlist.title}",
                        tint = TextPrimaryWhite,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.title,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = playlist.updatedLabel,
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

/**
 * Circular Artist Avatar Card for Popular Artists & Library Artists sections.
 */
@Composable
fun ArtistAvatarCard(
    artist: ArtistProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current

    Column(
        modifier = modifier
            .width(124.dp)
            .clickable(onClick = onClick)
            .testTag("artist_card_${artist.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(OnyxDesignTokens.ArtistAvatarSize)
                .shadow(
                    elevation = if (auraColors.isDark) 14.dp else 6.dp,
                    shape = CircleShape,
                    ambientColor = artist.accentColor.copy(alpha = 0.4f),
                    spotColor = PureBlack
                )
                .clip(CircleShape)
                .background(auraColors.cardSurface)
                .border(0.8.dp, auraColors.glassBorder.copy(alpha = 0.45f), CircleShape)
        ) {
            AsyncImage(
                model = artist.avatarModel,
                placeholder = painterResource(artist.avatarRes),
                error = painterResource(artist.avatarRes),
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = artist.name,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = artist.genre,
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
