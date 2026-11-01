package com.example.ui.screens

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.data.video.LiveStreamPlayerEngine
import com.example.domain.model.LiveChannel
import com.example.ui.components.MusicSectionHeader
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGrey

private val LiveRed = Color(0xFFEF4444)
private val LiveDarkCard = Color(0xFF141418)

/**
 * Onyx Live TV & Video Streaming Screen.
 *
 * Replaces old static Radio with ultra-low-latency Live TV streaming powered by:
 * - Freelivtv M3U Playlist API (1000+ channels)
 * - ExoPlayer buffer-free low-latency engine
 * - YouTube-style instant playback (Inline + Fullscreen)
 * - Circular channel logos (Popular Artists style)
 * - Horizontal category carousel rows (Music, Entertainment, Movies, Sports, News)
 * - Inbuilt search with live filtering
 */
@Composable
fun LiveTvScreen(
    channels: List<LiveChannel>,
    filteredChannels: List<LiveChannel>,
    categories: List<String>,
    selectedCategory: String,
    searchQuery: String,
    isFetching: Boolean,
    activeChannel: LiveChannel?,
    isFullscreen: Boolean,
    playerEngine: LiveStreamPlayerEngine,
    listState: LazyListState,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onPlayChannel: (LiveChannel) -> Unit,
    onClosePlayer: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    // Group channels by category for horizontal rows
    val musicChannels = remember(channels) {
        channels.filter { it.category.equals("Music", ignoreCase = true) }
            .ifEmpty { channels.filter { it.name.contains("Music", ignoreCase = true) || it.name.contains("9X", ignoreCase = true) || it.name.contains("MTV", ignoreCase = true) } }
    }
    val entertainmentChannels = remember(channels) {
        channels.filter { it.category.equals("Entertainment", ignoreCase = true) }
            .ifEmpty { channels.filter { it.name.contains("Star", ignoreCase = true) || it.name.contains("Zee", ignoreCase = true) || it.name.contains("Colors", ignoreCase = true) } }
    }
    val moviesChannels = remember(channels) {
        channels.filter { it.category.equals("Movies", ignoreCase = true) }
            .ifEmpty { channels.filter { it.name.contains("Cinema", ignoreCase = true) || it.name.contains("MAX", ignoreCase = true) || it.name.contains("Gold", ignoreCase = true) } }
    }
    val sportsChannels = remember(channels) {
        channels.filter { it.category.equals("Sports", ignoreCase = true) }
            .ifEmpty { channels.filter { it.name.contains("Sport", ignoreCase = true) || it.name.contains("Cricket", ignoreCase = true) || it.name.contains("Ten", ignoreCase = true) } }
    }
    val newsChannels = remember(channels) {
        channels.filter { it.category.equals("News", ignoreCase = true) }
            .ifEmpty { channels.filter { it.name.contains("News", ignoreCase = true) || it.name.contains("Tak", ignoreCase = true) || it.name.contains("NDTV", ignoreCase = true) } }
    }
    val topFeaturedChannels = remember(channels) {
        channels.take(16)
    }

    // YouTube Fullscreen Video Modal
    if (isFullscreen && activeChannel != null) {
        FullscreenLivePlayerDialog(
            channel = activeChannel,
            playerEngine = playerEngine,
            onDismiss = onToggleFullscreen
        )
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("live_tv_screen_list"),
        contentPadding = PaddingValues(top = 100.dp, bottom = 190.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // 1. YouTube-style Inline Video Player (Active Stream)
        if (activeChannel != null && !isFullscreen) {
            item(key = "inline_active_live_player") {
                InlineLivePlayer(
                    channel = activeChannel,
                    playerEngine = playerEngine,
                    onClose = onClosePlayer,
                    onFullscreen = onToggleFullscreen
                )
            }
        }

        // 2. Inbuilt Live Search Bar
        item(key = "live_tv_search_bar") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = PureBlack, spotColor = PureBlack)
                        .testTag("live_tv_search_input"),
                    placeholder = {
                        Text(
                            text = "Search 1,000+ Live Channels, 9XM, Sports...",
                            color = TextSecondaryGrey,
                            fontSize = 14.sp,
                            fontFamily = InterFontFamily
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Live",
                            tint = ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = TextSecondaryGrey,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(onClick = onRefresh) {
                                if (isFetching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = ElectricBlue
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh channels",
                                        tint = TextSecondaryGrey,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LiveDarkCard,
                        unfocusedContainerColor = LiveDarkCard,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorderSubtle,
                        focusedTextColor = TextPrimaryWhite,
                        unfocusedTextColor = TextPrimaryWhite
                    )
                )
            }
        }

        // 3. Category Filter Chips (All, Music, Entertainment, Movies, Sports, News...)
        item(key = "live_category_chips") {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories, key = { it }) { category ->
                    val isSelected = category.equals(selectedCategory, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) ElectricBlue else LiveDarkCard
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ElectricBlue else GlassBorderSubtle,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectCategory(category) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("live_chip_$category"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PureBlack)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = category,
                                color = if (isSelected) PureBlack else TextPrimaryWhite,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
            }
        }

        // When a search query is active or category filter is not "All", show filtered grid/list
        if (searchQuery.isNotBlank() || selectedCategory != "All") {
            item(key = "filtered_channels_header") {
                MusicSectionHeader(
                    title = if (searchQuery.isNotBlank()) "Search Results (${filteredChannels.size})" else "$selectedCategory Channels (${filteredChannels.size})",
                    subtitle = "Tap any channel to start instant live streaming"
                )
            }
            if (filteredChannels.isEmpty()) {
                item(key = "filtered_channels_empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No live channels found matching '$searchQuery'",
                            color = TextSecondaryGrey,
                            fontSize = 14.sp,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            } else {
                items(filteredChannels.chunked(2), key = { it.first().id }) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        pair.forEach { channel ->
                            Box(modifier = Modifier.weight(1f)) {
                                LiveChannelGridCard(
                                    channel = channel,
                                    isActive = channel.id == activeChannel?.id,
                                    onPlay = { onPlayChannel(channel) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // 4. Circular Channel Avatars Row ("artist ke jaise gol gol channel ho")
            item(key = "circular_channel_avatars") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MusicSectionHeader(
                        title = "Top Live Channels",
                        subtitle = "Instant buffer-free broadcast feeds"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        items(topFeaturedChannels, key = { "circle_${it.id}" }) { channel ->
                            CircularChannelAvatarItem(
                                channel = channel,
                                isPlaying = channel.id == activeChannel?.id,
                                onClick = { onPlayChannel(channel) }
                            )
                        }
                    }
                }
            }

            // 5. Horizontal Carousel: 24/7 Music Live TV
            if (musicChannels.isNotEmpty()) {
                item(key = "music_channels_section") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MusicSectionHeader(
                            title = "Music Television • Live",
                            subtitle = "9XM, MTV Beats, B4U Music & nonstop hits",
                            onSeeAllClick = { onSelectCategory("Music") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(musicChannels, key = { "music_${it.id}" }) { channel ->
                                LiveChannelHorizontalCard(
                                    channel = channel,
                                    isActive = channel.id == activeChannel?.id,
                                    onPlay = { onPlayChannel(channel) }
                                )
                            }
                        }
                    }
                }
            }

            // 6. Horizontal Carousel: Entertainment Live TV
            if (entertainmentChannels.isNotEmpty()) {
                item(key = "entertainment_channels_section") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MusicSectionHeader(
                            title = "Entertainment & Shows",
                            subtitle = "Star Plus, Zee TV, Sony SAB & Colors HD",
                            onSeeAllClick = { onSelectCategory("Entertainment") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(entertainmentChannels, key = { "ent_${it.id}" }) { channel ->
                                LiveChannelHorizontalCard(
                                    channel = channel,
                                    isActive = channel.id == activeChannel?.id,
                                    onPlay = { onPlayChannel(channel) }
                                )
                            }
                        }
                    }
                }
            }

            // 7. Horizontal Carousel: Blockbuster Movies Live TV
            if (moviesChannels.isNotEmpty()) {
                item(key = "movies_channels_section") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MusicSectionHeader(
                            title = "Blockbuster Movies Live",
                            subtitle = "Sony MAX, Star Gold & Zee Cinema 24/7",
                            onSeeAllClick = { onSelectCategory("Movies") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(moviesChannels, key = { "movie_${it.id}" }) { channel ->
                                LiveChannelHorizontalCard(
                                    channel = channel,
                                    isActive = channel.id == activeChannel?.id,
                                    onPlay = { onPlayChannel(channel) }
                                )
                            }
                        }
                    }
                }
            }

            // 8. Horizontal Carousel: Live Sports & Cricket
            if (sportsChannels.isNotEmpty()) {
                item(key = "sports_channels_section") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MusicSectionHeader(
                            title = "Sports & Live Match TV",
                            subtitle = "Cricket, Football, Tennis & Live Action",
                            onSeeAllClick = { onSelectCategory("Sports") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(sportsChannels, key = { "sports_${it.id}" }) { channel ->
                                LiveChannelHorizontalCard(
                                    channel = channel,
                                    isActive = channel.id == activeChannel?.id,
                                    onPlay = { onPlayChannel(channel) }
                                )
                            }
                        }
                    }
                }
            }

            // 9. Horizontal Carousel: Live 24/7 News
            if (newsChannels.isNotEmpty()) {
                item(key = "news_channels_section") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MusicSectionHeader(
                            title = "24/7 Breaking News",
                            subtitle = "Aaj Tak HD, NDTV India & Global Feeds",
                            onSeeAllClick = { onSelectCategory("News") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(newsChannels, key = { "news_${it.id}" }) { channel ->
                                LiveChannelHorizontalCard(
                                    channel = channel,
                                    isActive = channel.id == activeChannel?.id,
                                    onPlay = { onPlayChannel(channel) }
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
 * Circular Channel Avatar Item (Styled exactly like Popular Artists on Home).
 * 76dp circular container with glowing border when live.
 */
@Composable
private fun CircularChannelAvatarItem(
    channel: LiveChannel,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(82.dp)
            .clickable(onClick = onClick)
            .testTag("channel_circle_${channel.id}")
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(elevation = 10.dp, shape = CircleShape, ambientColor = PureBlack, spotColor = PureBlack)
                .clip(CircleShape)
                .background(Color(0xFF1E1E26))
                .border(
                    width = if (isPlaying) 2.5.dp else 1.dp,
                    color = if (isPlaying) ElectricBlue else Color(0x33FFFFFF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentScale = ContentScale.Fit
            )

            // Red live broadcast indicator dot
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(PureBlack)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(LiveRed)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = channel.name,
            color = if (isPlaying) ElectricBlue else TextPrimaryWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFontFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = channel.category,
            color = TextSecondaryGrey,
            fontSize = 10.sp,
            fontFamily = InterFontFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * YouTube-style Inline Video Player (ExoPlayer + Header Bar + Controls).
 */
@OptIn(UnstableApi::class)
@Composable
private fun InlineLivePlayer(
    channel: LiveChannel,
    playerEngine: LiveStreamPlayerEngine,
    onClose: () -> Unit,
    onFullscreen: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(LiveDarkCard)
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
            .shadow(16.dp, RoundedCornerShape(20.dp), ambientColor = PureBlack, spotColor = PureBlack)
            .testTag("inline_live_player")
    ) {
        // Player Header Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F0F14))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Glowing LIVE Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiveRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = channel.name,
                        color = TextPrimaryWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${channel.category} • Freelivtv Stream",
                        color = TextSecondaryGrey,
                        fontSize = 11.sp,
                        fontFamily = InterFontFamily
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        playerEngine.setMuted(isMuted)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Mute/Unmute",
                        tint = TextPrimaryWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onFullscreen,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = TextPrimaryWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Player",
                        tint = TextSecondaryGrey,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ExoPlayer Video View (16:9 aspect ratio)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = playerEngine.player
                        useController = true
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { view ->
                    view.player = playerEngine.player
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * YouTube-style Fullscreen Live Video Player Dialog.
 */
@OptIn(UnstableApi::class)
@Composable
private fun FullscreenLivePlayerDialog(
    channel: LiveChannel,
    playerEngine: LiveStreamPlayerEngine,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = playerEngine.player
                        useController = true
                        setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { view ->
                    view.player = playerEngine.player
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top Bar Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = channel.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = "LIVE • ${channel.category}",
                            color = LiveRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        }
    }
}

/**
 * Horizontal Carousel Card for Category Rows (Music, Movies, Entertainment, etc.)
 */
@Composable
private fun LiveChannelHorizontalCard(
    channel: LiveChannel,
    isActive: Boolean,
    onPlay: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onPlay)
            .testTag("live_channel_card_${channel.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = PureBlack, spotColor = PureBlack)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B1B22))
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) ElectricBlue else GlassBorderSubtle,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            // Live tag badge at top right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiveRed)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
            }

            // Play overlay icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color(0x33FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Stream",
                    tint = if (isActive) ElectricBlue else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = channel.name,
            color = if (isActive) ElectricBlue else TextPrimaryWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFontFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = channel.category,
            color = TextSecondaryGrey,
            fontSize = 11.sp,
            fontFamily = InterFontFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Grid Card used when searching or filtering by category.
 */
@Composable
private fun LiveChannelGridCard(
    channel: LiveChannel,
    isActive: Boolean,
    onPlay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LiveDarkCard)
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) ElectricBlue else GlassBorderSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onPlay)
            .padding(10.dp)
            .testTag("grid_channel_${channel.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E26)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = channel.name,
                color = if (isActive) ElectricBlue else TextPrimaryWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = channel.category,
                color = TextSecondaryGrey,
                fontSize = 11.sp,
                fontFamily = InterFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (isActive) ElectricBlue else Color(0x22FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = if (isActive) PureBlack else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
