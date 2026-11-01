package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BluetoothAudio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.model.Album
import com.example.domain.model.AudioOutputRoute
import com.example.domain.model.Playlist
import com.example.domain.model.RepeatMode
import com.example.domain.model.Song
import com.example.ui.components.AlbumArtworkCard
import com.example.ui.components.AnimatedEqualizerBars
import com.example.ui.components.FrostedGlassContainer
import com.example.ui.components.GlassBackButton
import com.example.ui.components.GlassDialog
import com.example.ui.components.MusicListItem
import com.example.ui.components.OnyxDynamicBackground
import com.example.ui.components.PlaylistArtworkCard
import com.example.ui.components.ScreenBackgroundType
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Full-Screen Now Playing Screen with Dynamic Artwork-Derived Background,
 * Balanced Typography Hierarchy, Large Square Artwork, Interactive Seekbar,
 * Prominent Central Play/Pause Button, and Bottom Lyrics / Audio Output / Queue bar.
 */
@Composable
fun NowPlayingScreen(
    song: Song,
    isPlaying: Boolean,
    currentPositionSeconds: Int,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatMode,
    isLiked: Boolean,
    selectedAudioOutput: String,
    onBackClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeatMode: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenAudioOutput: () -> Unit,
    onOpenMoreOptions: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val artworkScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.0f else 0.88f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "now_playing_art_scale"
    )

    OnyxDynamicBackground(
        screenType = ScreenBackgroundType.NOW_PLAYING,
        artworkRes = song.artworkRes,
        seedColor = song.accentColor,
        modifier = modifier.testTag("now_playing_full_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header: Circular Back Button, Subtle Playing From Album Info, Share & Three-Dot Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassBackButton(
                    onClick = onBackClick,
                    testTag = "now_playing_collapse_button"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PLAYING FROM ALBUM",
                        color = TextPrimaryWhite.copy(alpha = 0.62f),
                        fontFamily = InterFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.9.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = song.albumTitle,
                        color = TextPrimaryWhite.copy(alpha = 0.92f),
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(0.8.dp, GlassBorderSubtle, CircleShape)
                            .clickable(onClick = onShareClick)
                            .testTag("now_playing_share_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share track",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(0.8.dp, GlassBorderSubtle, CircleShape)
                            .clickable(onClick = onOpenMoreOptions)
                            .testTag("now_playing_more_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = "More options",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Center Square Album Artwork with Smooth Crossfade & Dynamic Shadow
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.86f)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .graphicsLayer {
                        scaleX = artworkScale
                        scaleY = artworkScale
                    }
                    .shadow(
                        elevation = 38.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = song.accentColor,
                        spotColor = PureBlack
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .border(0.9.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
            ) {
                Crossfade(
                    targetState = song.artworkRes,
                    animationSpec = tween(420),
                    label = "now_playing_artwork_crossfade"
                ) { artRes ->
                    AsyncImage(
                        model = artRes,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Song Info + Seekbar Section (Refined Typography Hierarchy)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = song.title,
                                color = TextPrimaryWhite,
                                fontFamily = InterFontFamily,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                letterSpacing = (-0.3).sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (song.isExplicit) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.22f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "E",
                                        color = TextPrimaryWhite,
                                        fontFamily = InterFontFamily,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = song.artist,
                            color = TextPrimaryWhite.copy(alpha = 0.78f),
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("now_playing_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favourite",
                                tint = if (isLiked) ElectricBlue else TextPrimaryWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenMoreOptions,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("now_playing_info_more_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreHoriz,
                                contentDescription = "Track options",
                                tint = TextPrimaryWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = currentPositionSeconds.toFloat(),
                    onValueChange = { onSeekTo(it.toInt()) },
                    valueRange = 0f..song.durationSeconds.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = TextPrimaryWhite,
                        activeTrackColor = TextPrimaryWhite,
                        inactiveTrackColor = Color.White.copy(alpha = 0.26f)
                    ),
                    modifier = Modifier.testTag("now_playing_seekbar")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatSec(currentPositionSeconds),
                        color = TextPrimaryWhite.copy(alpha = 0.68f),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.14f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = TextPrimaryWhite.copy(alpha = 0.90f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dolby Atmos • Lossless",
                            color = TextPrimaryWhite.copy(alpha = 0.90f),
                            fontFamily = InterFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp
                        )
                    }

                    Text(
                        text = "-${formatSec((song.durationSeconds - currentPositionSeconds).coerceAtLeast(0))}",
                        color = TextPrimaryWhite.copy(alpha = 0.68f),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Transport Controls: Shuffle, Previous, Visually Prominent Central Play/Pause, Next, Repeat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("now_playing_shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) ElectricBlue else TextPrimaryWhite.copy(alpha = 0.68f),
                        modifier = Modifier.size(23.dp)
                    )
                }

                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("now_playing_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastRewind,
                        contentDescription = "Previous",
                        tint = TextPrimaryWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .shadow(16.dp, CircleShape, ambientColor = Color.White.copy(alpha = 0.35f))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onTogglePlayPause)
                        .testTag("now_playing_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = PureBlack,
                        modifier = Modifier.size(38.dp)
                    )
                }

                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("now_playing_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastForward,
                        contentDescription = "Next",
                        tint = TextPrimaryWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = onCycleRepeatMode,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("now_playing_repeat_button")
                ) {
                    Icon(
                        imageVector = if (repeatMode == RepeatMode.ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                        contentDescription = "Repeat mode",
                        tint = if (repeatMode != RepeatMode.OFF) ElectricBlue else TextPrimaryWhite.copy(alpha = 0.68f),
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            // Bottom Action Bar: Lyrics, Audio Output / Cast, Queue
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenLyrics,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("now_playing_lyrics_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lyrics,
                        contentDescription = "Open Lyrics",
                        tint = TextPrimaryWhite.copy(alpha = 0.88f),
                        modifier = Modifier.size(23.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.14f))
                        .border(0.7.dp, GlassBorderSubtle, RoundedCornerShape(50))
                        .clickable(onClick = onOpenAudioOutput)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("now_playing_audio_output_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (selectedAudioOutput.contains("Phone", ignoreCase = true)) {
                            Icons.Filled.PhoneAndroid
                        } else {
                            Icons.Filled.Headphones
                        },
                        contentDescription = "Audio output",
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedAudioOutput,
                        color = TextPrimaryWhite,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onOpenQueue,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("now_playing_queue_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Open Queue",
                        tint = TextPrimaryWhite.copy(alpha = 0.88f),
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-Screen Playlist & Album Detail Screen with Dynamic Artwork-Derived Background,
 * Top Circular Back Button, Share & Three-Dot Menu, Large Square Artwork,
 * Shuffle, Large Play, and Add to Library buttons, plus complete Track List.
 */
@Composable
fun PlaylistDetailScreen(
    album: Album?,
    playlist: Playlist?,
    allSongs: List<Song>,
    currentPlayingSongId: String,
    isPlaying: Boolean,
    likedSongIds: Set<String>,
    isSavedToLibrary: Boolean,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit,
    onToggleSaveLibrary: () -> Unit,
    onPlaySong: (Song) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMoreOptionsSong: (Song) -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = album?.title ?: playlist?.title ?: ""
    val subtitle = album?.artist ?: playlist?.curator ?: "Onyx Music Editorial"
    val updatedLabel = playlist?.updatedLabel ?: "${album?.releaseYear ?: 2026} • LOSSLESS • DOLBY ATMOS"
    val description = album?.description ?: playlist?.description ?: ""
    val artworkRes = album?.artworkRes ?: playlist?.artworkRes ?: 0
    val accentColor = album?.primaryColor ?: playlist?.primaryColor ?: ElectricBlue

    val tracks = remember(album, playlist, allSongs) {
        when {
            playlist != null -> allSongs.filter { playlist.songIds.contains(it.id) }.ifEmpty { allSongs.take(6) }
            album != null -> {
                val matching = allSongs.filter {
                    it.albumTitle.equals(album.title, ignoreCase = true) || it.artworkRes == album.artworkRes
                }
                if (matching.size >= 4) matching else (matching + allSongs.filterNot { m -> matching.any { it.id == m.id } }).take(6)
            }
            else -> allSongs
        }
    }

    OnyxDynamicBackground(
        screenType = if (playlist != null) ScreenBackgroundType.PLAYLIST_DETAIL else ScreenBackgroundType.ALBUM_DETAIL,
        artworkRes = artworkRes,
        seedColor = accentColor,
        modifier = modifier.testTag("detail_overlay_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .testTag("detail_lazy_column"),
            contentPadding = PaddingValues(bottom = 190.dp)
        ) {
            // Top Bar: Circular Back Button on Left, Share & Three-Dot Menu on Right
            item(key = "detail_top_bar") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassBackButton(
                        onClick = onBack,
                        testTag = "detail_back_button"
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f))
                                .border(0.8.dp, GlassBorderSubtle, CircleShape)
                                .clickable(onClick = onShareClick)
                                .testTag("detail_share_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = TextPrimaryWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f))
                                .border(0.8.dp, GlassBorderSubtle, CircleShape)
                                .clickable { tracks.firstOrNull()?.let(onMoreOptionsSong) }
                                .testTag("detail_more_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreHoriz,
                                contentDescription = "More options",
                                tint = TextPrimaryWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Center Artwork & Playlist/Album Metadata + Shuffle / Play / Add to Library Controls
            item(key = "detail_artwork_header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(
                                elevation = 28.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = accentColor,
                                spotColor = PureBlack
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .border(0.9.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
                    ) {
                        AsyncImage(
                            model = artworkRes,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = title,
                        color = TextPrimaryWhite,
                        fontFamily = InterFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        color = ElectricBlue,
                        fontFamily = InterFontFamily,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$updatedLabel • ${tracks.size} SONGS",
                        color = TextSecondaryGrey,
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.6.sp,
                        textAlign = TextAlign.Center
                    )

                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = description,
                            color = TextPrimaryWhite.copy(alpha = 0.78f),
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Row: Play, Shuffle, and Add to Library
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onPlayAll,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_play_button")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp)
                        }

                        Button(
                            onClick = onShuffleAll,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.14f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_shuffle_button")
                        ) {
                            Icon(Icons.Filled.Shuffle, contentDescription = null, tint = TextPrimaryWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shuffle", color = TextPrimaryWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSavedToLibrary) ElectricBlue.copy(alpha = 0.26f)
                                    else Color.White.copy(alpha = 0.14f)
                                )
                                .border(
                                    0.8.dp,
                                    if (isSavedToLibrary) ElectricBlue else GlassBorderSubtle,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable(onClick = onToggleSaveLibrary)
                                .testTag("detail_add_library_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSavedToLibrary) Icons.Filled.Check else Icons.Filled.Add,
                                contentDescription = if (isSavedToLibrary) "Saved in Library" else "Add to Library",
                                tint = if (isSavedToLibrary) ElectricBlue else TextPrimaryWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                }
            }

            // Track Listing
            items(tracks, key = { "detail_track_${it.id}" }) { song ->
                MusicListItem(
                    song = song,
                    isCurrentlyPlaying = isPlaying && song.id == currentPlayingSongId,
                    isLiked = likedSongIds.contains(song.id),
                    onSongClick = { onPlaySong(song) },
                    onFavoriteClick = { onToggleFavorite(song.id) },
                    onMoreOptionsClick = { onMoreOptionsSong(song) }
                )
            }
        }
    }
}

/**
 * Immersive Full-Screen Lyrics View with Dynamic Artwork-Derived Blurred Background,
 * Real-Time Current Line Highlighting, Tap-to-Seek, Smooth Auto-Scroll, Manual Scroll
 * Resume Control, and Honest Fallback State for Instrumental Tracks.
 */
@Composable
fun LyricsScreen(
    song: Song,
    isPlaying: Boolean,
    currentPositionSeconds: Int,
    onBackClick: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lyrics = song.synchronizedLyrics
    val activeLineIndex = remember(lyrics, currentPositionSeconds) {
        if (lyrics.isEmpty()) -1
        else {
            val idx = lyrics.indexOfLast { it.timestampSec <= currentPositionSeconds }
            if (idx < 0) 0 else idx
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var userManuallyScrolled by remember(song.id) { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            userManuallyScrolled = true
        }
    }

    LaunchedEffect(activeLineIndex, song.id) {
        if (activeLineIndex >= 0 && lyrics.isNotEmpty() && !listState.isScrollInProgress) {
            userManuallyScrolled = false
            runCatching {
                listState.animateScrollToItem((activeLineIndex - 1).coerceAtLeast(0))
            }
        }
    }

    OnyxDynamicBackground(
        screenType = ScreenBackgroundType.LYRICS,
        artworkRes = song.artworkRes,
        seedColor = song.accentColor,
        modifier = modifier.testTag("lyrics_screen")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 22.dp, vertical = 12.dp)
            ) {
                // Top Bar: Back Button + Song Identity + Play/Pause Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GlassBackButton(
                        onClick = onBackClick,
                        testTag = "lyrics_back_button"
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = song.artworkRes,
                            contentDescription = song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = TextPrimaryWhite,
                                fontFamily = InterFontFamily,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = song.artist,
                                color = TextPrimaryWhite.copy(alpha = 0.72f),
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(0.8.dp, GlassBorderSubtle, CircleShape)
                            .clickable(onClick = onTogglePlayPause)
                            .testTag("lyrics_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (song.hasSynchronizedLyrics) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lyrics_list"),
                        contentPadding = PaddingValues(top = 24.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        itemsIndexed(lyrics, key = { idx, _ -> "${song.id}_lyric_$idx" }) { index, line ->
                            val isCurrent = index == activeLineIndex
                            val lineColor by animateColorAsState(
                                targetValue = if (isCurrent) Color.White else Color.White.copy(alpha = 0.42f),
                                animationSpec = tween(280),
                                label = "lyric_color_$index"
                            )
                            val scale by animateFloatAsState(
                                targetValue = if (isCurrent) 1.02f else 0.97f,
                                animationSpec = tween(280),
                                label = "lyric_scale_$index"
                            )

                            Text(
                                text = line.text,
                                color = lineColor,
                                fontFamily = InterFontFamily,
                                fontSize = if (isCurrent) 24.sp else 19.sp,
                                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Medium,
                                lineHeight = if (isCurrent) 31.sp else 26.sp,
                                letterSpacing = (-0.3).sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                                    }
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        userManuallyScrolled = false
                                        onSeekTo(line.timestampSec)
                                    }
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                                    .testTag("lyric_line_$index")
                            )
                        }
                    }
                } else {
                    // Honest Fallback State when Synchronized Lyrics are Not Available
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 48.dp)
                            .testTag("lyrics_fallback_container"),
                        contentAlignment = Alignment.Center
                    ) {
                        FrostedGlassContainer(
                            shape = RoundedCornerShape(24.dp),
                            accentGlowColor = song.accentColor,
                            baseAlpha = 0.72f,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 28.dp, vertical = 36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MusicNote,
                                    contentDescription = null,
                                    tint = TextPrimaryWhite.copy(alpha = 0.85f),
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Instrumental / Unsynced Track",
                                    color = TextPrimaryWhite,
                                    fontFamily = InterFontFamily,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Time-synced lyrics are not available for \"${song.title}\" by ${song.artist}. Enjoy the spatial lossless master.",
                                    color = TextSecondaryGrey,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Floating "Sync to Active Lyric" pill when user scrolls manually
            AnimatedVisibility(
                visible = userManuallyScrolled && song.hasSynchronizedLyrics,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(180)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ElectricBlue)
                        .clickable {
                            userManuallyScrolled = false
                            coroutineScope.launch {
                                runCatching {
                                    listState.animateScrollToItem((activeLineIndex - 1).coerceAtLeast(0))
                                }
                            }
                        }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .testTag("lyrics_resync_button")
                ) {
                    Text(
                        text = "Sync to Active Lyric",
                        color = Color.White,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Full-Screen Playback Queue Screen with Current Playing Song, Upcoming Queue,
 * Track Selection, Move Up / Move Down Reordering, Remove Track, Shuffle Queue, and Clear Queue.
 */
@Composable
fun QueueScreen(
    currentSong: Song,
    queueSongs: List<Song>,
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    onBackClick: () -> Unit,
    onPlaySong: (Song) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    onShuffleQueue: () -> Unit,
    onClearQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val upcomingSongs = remember(queueSongs, currentSong.id) {
        queueSongs.filter { it.id != currentSong.id }
    }

    OnyxDynamicBackground(
        screenType = ScreenBackgroundType.QUEUE,
        artworkRes = currentSong.artworkRes,
        seedColor = currentSong.accentColor,
        modifier = modifier.testTag("queue_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassBackButton(
                    onClick = onBackClick,
                    testTag = "queue_back_button"
                )

                Text(
                    text = "Playing Next",
                    color = TextPrimaryWhite,
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isShuffleEnabled) ElectricBlue.copy(alpha = 0.28f)
                                else Color.White.copy(alpha = 0.14f)
                            )
                            .border(0.8.dp, GlassBorderSubtle, RoundedCornerShape(50))
                            .clickable(onClick = onShuffleQueue)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("queue_shuffle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Shuffle,
                                contentDescription = "Shuffle queue",
                                tint = if (isShuffleEnabled) ElectricBlue else TextPrimaryWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Shuffle",
                                color = TextPrimaryWhite,
                                fontFamily = InterFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (upcomingSongs.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.14f))
                                .border(0.8.dp, GlassBorderSubtle, RoundedCornerShape(50))
                                .clickable(onClick = onClearQueue)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("queue_clear_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Clear",
                                color = ElectricBlue,
                                fontFamily = InterFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Now Playing Card
            Text(
                text = "NOW PLAYING",
                color = TextSecondaryGrey,
                fontFamily = InterFontFamily,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            FrostedGlassContainer(
                shape = RoundedCornerShape(16.dp),
                accentGlowColor = currentSong.accentColor,
                baseAlpha = 0.78f,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentSong.artworkRes,
                        contentDescription = currentSong.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentSong.title,
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currentSong.artist} • ${currentSong.albumTitle}",
                            color = TextSecondaryGrey,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isPlaying) {
                        AnimatedEqualizerBars(color = ElectricBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "UPCOMING (${upcomingSongs.size} TRACKS)",
                color = TextSecondaryGrey,
                fontFamily = InterFontFamily,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (upcomingSongs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Queue is empty. Add songs or tap Shuffle to populate upcoming tracks.",
                        color = TextSecondaryGrey,
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("queue_upcoming_list"),
                    contentPadding = PaddingValues(bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(upcomingSongs, key = { _, s -> "q_${s.id}" }) { idx, song ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(0.6.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                .clickable { onPlaySong(song) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("queue_item_${song.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = song.artworkRes,
                                contentDescription = song.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    color = TextPrimaryWhite,
                                    fontFamily = InterFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist,
                                    color = TextSecondaryGrey,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = { onMoveUp(song.id) },
                                enabled = idx > 0,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Move up",
                                    tint = if (idx > 0) TextPrimaryWhite else TextSecondaryGrey.copy(alpha = 0.35f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { onMoveDown(song.id) },
                                enabled = idx < upcomingSongs.lastIndex,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Move down",
                                    tint = if (idx < upcomingSongs.lastIndex) TextPrimaryWhite else TextSecondaryGrey.copy(alpha = 0.35f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { onRemoveFromQueue(song.id) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("queue_remove_${song.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = "Remove from queue",
                                    tint = TextSecondaryGrey,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-Screen "See All" Destination for any Home or New Discovery Section.
 */
@Composable
fun SectionSeeAllScreen(
    sectionKey: String,
    allAlbums: List<Album>,
    allPlaylists: List<Playlist>,
    allSongs: List<Song>,
    likedSongIds: Set<String>,
    currentPlayingSongId: String,
    isPlaying: Boolean,
    onBackClick: () -> Unit,
    onAlbumClick: (Album) -> Unit,
    onPlayAlbum: (Album) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onPlayPlaylist: (Playlist) -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val sectionTitle = when (sectionKey) {
        "top_picks" -> "Top Picks for You"
        "recently_played" -> "Recently Played"
        "made_for_you" -> "Made For You"
        "top_songs" -> "Top Songs"
        "new_releases" -> "New Releases"
        "recommended_albums" -> "Recommended Albums"
        "popular_artists" -> "Popular Artists"
        "artist_essentials" -> "Artist Essentials"
        "popular_playlists" -> "Popular Playlists"
        "favourite_music" -> "Your Favourite Music"
        "continue_listening" -> "Continue Listening"
        "early_access" -> "Early Access"
        "best_new_songs" -> "Best New Songs"
        "new_this_week" -> "New This Week"
        "recent_releases" -> "Recent Releases"
        "updated_playlists" -> "Updated Playlists"
        else -> "Browse Collection"
    }

    val isPlaylistSection = sectionKey in setOf("made_for_you", "popular_playlists", "updated_playlists", "artist_essentials")
    val isSongSection = sectionKey in setOf("top_songs", "favourite_music", "best_new_songs")

    OnyxDynamicBackground(
        screenType = ScreenBackgroundType.HOME,
        artworkRes = allAlbums.firstOrNull()?.artworkRes ?: 0,
        modifier = modifier.testTag("section_see_all_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(bottom = 188.dp)
        ) {
            item(key = "see_all_top_bar") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassBackButton(
                        onClick = onBackClick,
                        testTag = "see_all_back_button"
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = sectionTitle,
                        color = TextPrimaryWhite,
                        fontFamily = InterFontFamily,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                isSongSection -> {
                    val songsToShow = if (sectionKey == "favourite_music") {
                        allSongs.filter { likedSongIds.contains(it.id) }.ifEmpty { allSongs }
                    } else {
                        allSongs
                    }
                    items(songsToShow, key = { "see_all_song_${it.id}" }) { song ->
                        MusicListItem(
                            song = song,
                            isCurrentlyPlaying = isPlaying && song.id == currentPlayingSongId,
                            isLiked = likedSongIds.contains(song.id),
                            onSongClick = { onSongClick(song) },
                            onFavoriteClick = { onToggleFavorite(song.id) },
                            onMoreOptionsClick = { onMoreOptionsClick(song) }
                        )
                    }
                }

                isPlaylistSection -> {
                    val rows = allPlaylists.chunked(2)
                    items(rows.size, key = { "see_all_pl_row_$it" }) { idx ->
                        val pair = rows[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pair.forEach { playlist ->
                                Box(modifier = Modifier.weight(1f)) {
                                    PlaylistArtworkCard(
                                        playlist = playlist,
                                        onCardClick = { onPlaylistClick(playlist) },
                                        onPlayClick = { onPlayPlaylist(playlist) },
                                        cardWidth = 220.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                else -> {
                    val rows = allAlbums.chunked(2)
                    items(rows.size, key = { "see_all_alb_row_$it" }) { idx ->
                        val pair = rows[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pair.forEach { album ->
                                Box(modifier = Modifier.weight(1f)) {
                                    AlbumArtworkCard(
                                        album = album,
                                        onCardClick = { onAlbumClick(album) },
                                        onPlayClick = { onPlayAlbum(album) },
                                        cardWidth = 220.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Song Three-Dot Bottom Sheet with:
 * TOP SECTION:
 * - Album artwork
 * - Full song title + Explicit badge when applicable
 * - Artist name
 * - Album name
 *
 * SCROLLABLE ACTIONS:
 * 1. Add to Library / Remove from Library
 * 2. Add to Playlist
 * 3. Play Next
 * 4. Add to Queue
 * 5. Favourite
 * 6. Share Song
 * 7. Share Lyrics
 * 8. View Time-Synced Lyrics
 * 9. View Credits
 * 10. Create Station
 * 11. Sleep Timer
 * 12. Suggest Less
 * 13. Report a Concern
 */
@Composable
fun TrackOptionsOverlaySheet(
    song: Song,
    isLiked: Boolean,
    isSavedInLibrary: Boolean = true,
    sleepTimerMinutes: Int? = null,
    onDismiss: () -> Unit,
    onToggleSaveLibrary: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenLyrics: () -> Unit,
    onShareLyrics: () -> Unit = {},
    onViewCredits: () -> Unit = {},
    onCreateStation: () -> Unit = {},
    onOpenSleepTimer: () -> Unit = {},
    onSuggestLess: () -> Unit = {},
    onReportConcern: () -> Unit = {},
    onShare: () -> Unit
) {
    val auraColors = LocalAuraColors.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack.copy(alpha = 0.72f))
            .clickable(onClick = onDismiss)
            .testTag("track_options_sheet"),
        contentAlignment = Alignment.BottomCenter
    ) {
        FrostedGlassContainer(
            shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            accentGlowColor = song.accentColor,
            baseAlpha = 0.97f,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Drag Handle Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)
                        .width(38.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(auraColors.textSecondary.copy(alpha = 0.35f))
                )

                // TOP SECTION: Album Artwork, Full Song Title, Explicit Badge, Artist Name, Album Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = song.artworkRes,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(0.6.dp, auraColors.glassBorder, RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = song.title,
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (song.isExplicit) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(auraColors.textSecondary.copy(alpha = 0.25f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                        .testTag("track_options_explicit_badge"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "E",
                                        color = auraColors.textPrimary,
                                        fontFamily = InterFontFamily,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.artist,
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.albumTitle,
                            color = auraColors.textSecondary,
                            fontFamily = InterFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = auraColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = auraColors.divider, thickness = 0.5.dp)

                // Scrollable Actions List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 430.dp)
                        .verticalScroll(scrollState)
                        .testTag("track_options_scroll_container")
                ) {
                    TrackOptionActionRow(
                        icon = if (isSavedInLibrary) Icons.Filled.Check else Icons.Filled.Add,
                        label = if (isSavedInLibrary) "Remove from Library" else "Add to Library",
                        onClick = onToggleSaveLibrary,
                        testTag = "track_option_library"
                    )
                    TrackOptionActionRow(
                        icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                        label = "Add to Playlist",
                        onClick = onAddToPlaylist,
                        testTag = "track_option_add_playlist"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.PlayArrow,
                        label = "Play Next",
                        onClick = onPlayNext,
                        testTag = "track_option_play_next"
                    )
                    TrackOptionActionRow(
                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                        label = "Add to Queue",
                        onClick = onAddToQueue,
                        testTag = "track_option_add_queue"
                    )

                    HorizontalDivider(color = auraColors.divider, thickness = 0.5.dp)

                    TrackOptionActionRow(
                        icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        label = if (isLiked) "Remove from Favourite" else "Favourite",
                        onClick = {
                            onToggleFavorite()
                            onDismiss()
                        },
                        testTag = "track_option_favorite"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.ThumbDownOffAlt,
                        label = "Suggest Less",
                        onClick = onSuggestLess,
                        testTag = "track_option_suggest_less"
                    )

                    HorizontalDivider(color = auraColors.divider, thickness = 0.5.dp)

                    TrackOptionActionRow(
                        icon = Icons.Filled.Share,
                        label = "Share Song",
                        onClick = {
                            onShare()
                            onDismiss()
                        },
                        testTag = "track_option_share"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.Lyrics,
                        label = "Share Lyrics",
                        onClick = {
                            onShareLyrics()
                            onDismiss()
                        },
                        testTag = "track_option_share_lyrics"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.MusicNote,
                        label = "View Time-Synced Lyrics",
                        onClick = {
                            onDismiss()
                            onOpenLyrics()
                        },
                        testTag = "track_option_lyrics"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.Info,
                        label = "View Credits",
                        onClick = onViewCredits,
                        testTag = "track_option_credits"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.Radio,
                        label = "Create Station",
                        onClick = onCreateStation,
                        testTag = "track_option_station"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.Timer,
                        label = if (sleepTimerMinutes != null) "Sleep Timer ($sleepTimerMinutes min active)" else "Sleep Timer",
                        onClick = onOpenSleepTimer,
                        testTag = "track_option_sleep_timer"
                    )
                    TrackOptionActionRow(
                        icon = Icons.Filled.Flag,
                        label = "Report a Concern",
                        onClick = onReportConcern,
                        testTag = "track_option_report_concern"
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackOptionActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String = ""
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ElectricBlue,
            modifier = Modifier.size(21.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Song Credits Dialog showing Performers, Composer, Producer, and Audio Mastering specs.
 */
@Composable
fun SongCreditsModal(
    song: Song?,
    onDismiss: () -> Unit
) {
    val auraColors = LocalAuraColors.current
    GlassDialog(
        visible = song != null,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("song_credits_modal")
    ) {
        if (song != null) {
            Text(
                text = "Song Credits",
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${song.title} — ${song.artist}",
                color = ElectricBlue,
                fontFamily = InterFontFamily,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CreditDetailRow("Performing Artist", song.artist)
                CreditDetailRow("Album", song.albumTitle)
                CreditDetailRow("Composition & Lyrics", song.composer)
                CreditDetailRow("Production & Engineering", song.producer)
                CreditDetailRow("Audio Quality", song.audioQualityLabel)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CreditDetailRow(label: String, value: String) {
    val auraColors = LocalAuraColors.current
    Column {
        Text(
            text = label.uppercase(),
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
        Text(
            text = value,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Report a Concern Modal for a Selected Song.
 */
@Composable
fun ReportConcernModal(
    song: Song?,
    onSubmitConcern: (Song, String) -> Unit,
    onDismiss: () -> Unit
) {
    val auraColors = LocalAuraColors.current
    val reasons = listOf(
        "Audio Playback or Mastering Issue",
        "Lyrics Synchronization Mismatch",
        "Incorrect Artist or Album Metadata",
        "Explicit Content Tagging"
    )
    GlassDialog(
        visible = song != null,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("report_concern_modal")
    ) {
        if (song != null) {
            Text(
                text = "Report a Concern",
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select the issue for '${song.title}' by ${song.artist}:",
                color = auraColors.textSecondary,
                fontFamily = InterFontFamily,
                fontSize = 13.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(auraColors.cardSurface)
                            .clickable { onSubmitConcern(song, reason) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = reason,
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = auraColors.elevatedSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = auraColors.textPrimary)
            }
        }
    }
}

/**
 * Add to Playlist Picker Modal.
 */
@Composable
fun AddToPlaylistModal(
    song: Song?,
    playlists: List<Playlist>,
    onSelectPlaylist: (Song, Playlist) -> Unit,
    onDismiss: () -> Unit
) {
    val auraColors = LocalAuraColors.current
    GlassDialog(
        visible = song != null,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_to_playlist_modal")
    ) {
        if (song != null) {
            Text(
                text = "Add to Playlist",
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose a playlist for '${song.title}'",
                color = auraColors.textSecondary,
                fontFamily = InterFontFamily,
                fontSize = 13.5.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                playlists.forEach { playlist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(auraColors.cardSurface)
                            .clickable { onSelectPlaylist(song, playlist) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = playlist.artworkRes,
                            contentDescription = playlist.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.title,
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${playlist.songIds.size} songs",
                                color = auraColors.textSecondary,
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = auraColors.elevatedSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = auraColors.textPrimary)
            }
        }
    }
}

/**
 * Interactive Sleep Timer Sheet.
 */
@Composable
fun SleepTimerOverlaySheet(
    visible: Boolean,
    activeMinutes: Int?,
    onSelectMinutes: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val auraColors = LocalAuraColors.current
    val options = listOf(
        null to "Off",
        15 to "15 Minutes",
        30 to "30 Minutes",
        45 to "45 Minutes",
        60 to "1 Hour"
    )
    GlassDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("sleep_timer_modal")
    ) {
        Text(
            text = "Sleep Timer",
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Automatically pause playback after the selected duration.",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 13.5.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { (mins, label) ->
                val isSelected = activeMinutes == mins
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ElectricBlue.copy(alpha = 0.16f) else Color.Transparent)
                        .clickable { onSelectMinutes(mins) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) ElectricBlue else auraColors.textPrimary,
                        fontFamily = InterFontFamily,
                        fontSize = 15.5.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Audio Output Selector Sheet displaying real Android system audio routes
 * detected via Android's [android.media.AudioManager].
 */
@Composable
fun AudioOutputOverlaySheet(
    availableRoutes: List<AudioOutputRoute>,
    selectedOutput: String,
    onSelectOutput: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val auraColors = LocalAuraColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack.copy(alpha = 0.68f))
            .clickable(onClick = onDismiss)
            .testTag("audio_output_sheet"),
        contentAlignment = Alignment.BottomCenter
    ) {
        FrostedGlassContainer(
            shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            accentGlowColor = ElectricBlue,
            baseAlpha = 0.96f,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Audio Output",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Active Android system audio routes",
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                availableRoutes.forEach { route ->
                    val isSelected = selectedOutput == route.name
                    val icon = when (route.routeType) {
                        "BLUETOOTH" -> Icons.Filled.BluetoothAudio
                        "HEADPHONES" -> Icons.Filled.Headphones
                        "USB" -> Icons.Filled.Speaker
                        else -> Icons.Filled.PhoneAndroid
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) ElectricBlue.copy(alpha = 0.18f)
                                else Color.Transparent
                            )
                            .clickable { onSelectOutput(route.name) }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .testTag("audio_route_${route.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = route.name,
                            tint = if (isSelected) ElectricBlue else auraColors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = route.name,
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = route.subtitle,
                                color = auraColors.textSecondary,
                                fontFamily = InterFontFamily,
                                fontSize = 12.5.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Minimalist ONYX MUSIC Splash Screen:
 * - Pure black background (#000000)
 * - Official ONYX MUSIC logo centered
 * - Smooth fade-in and subtle scale animation (~1.8 seconds)
 */
@Composable
fun OnyxSplashOverlay(
    onSplashFinished: () -> Unit
) {
    var startAnim by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.86f,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(1750)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .clickable(onClick = onSplashFinished)
            .testTag("onyx_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Onyx Music Logo",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(164.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                }
        )
    }
}

private fun formatSec(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
