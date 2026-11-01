package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.MicExternalOn
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.Album
import com.example.domain.model.ArtistProfile
import com.example.domain.model.LibrarySortOrder
import com.example.domain.model.Playlist
import com.example.domain.model.Song
import com.example.ui.components.AlbumArtworkCard
import com.example.ui.components.MusicListItem
import com.example.ui.components.MusicSectionHeader
import com.example.ui.components.PlaylistArtworkCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack

private data class LibraryCategoryRow(
    val title: String,
    val icon: ImageVector
)

@Composable
fun LibraryScreen(
    allAlbums: List<Album>,
    allPlaylists: List<Playlist>,
    allSongs: List<Song>,
    allArtists: List<ArtistProfile>,
    likedSongIds: Set<String>,
    savedSongIds: Set<String>,
    selectedCategory: String,
    sortOrder: LibrarySortOrder,
    currentPlayingSongId: String,
    isPlaying: Boolean,
    listState: LazyListState,
    onSelectCategory: (String) -> Unit,
    onSelectSortOrder: (LibrarySortOrder) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onPlayAlbum: (Album) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onPlayPlaylist: (Playlist) -> Unit,
    onArtistClick: (ArtistProfile) -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    onTransferMusicClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    var showTransferBanner by rememberSaveable { mutableStateOf(true) }

    val categories = listOf(
        LibraryCategoryRow("Playlists", Icons.AutoMirrored.Outlined.QueueMusic),
        LibraryCategoryRow("Artists", Icons.Outlined.MicExternalOn),
        LibraryCategoryRow("Albums", Icons.Outlined.Album),
        LibraryCategoryRow("Songs", Icons.Outlined.MusicNote),
        LibraryCategoryRow("Made For You", Icons.Outlined.PersonOutline),
        LibraryCategoryRow("TV & Movies", Icons.Outlined.LiveTv),
        LibraryCategoryRow("Favorite Songs", Icons.Outlined.FavoriteBorder),
        LibraryCategoryRow("Recently Played", Icons.Outlined.History),
        LibraryCategoryRow("Downloaded", Icons.Outlined.DownloadDone)
    )

    val sortedAlbums = remember(allAlbums, sortOrder) {
        when (sortOrder) {
            LibrarySortOrder.RECENTLY_ADDED -> allAlbums
            LibrarySortOrder.TITLE_AZ -> allAlbums.sortedBy { it.title }
            LibrarySortOrder.ARTIST -> allAlbums.sortedBy { it.artist }
        }
    }

    val sortedSongs = remember(allSongs, sortOrder) {
        when (sortOrder) {
            LibrarySortOrder.RECENTLY_ADDED -> allSongs
            LibrarySortOrder.TITLE_AZ -> allSongs.sortedBy { it.title }
            LibrarySortOrder.ARTIST -> allSongs.sortedBy { it.artist }
        }
    }

    val sortedPlaylists = remember(allPlaylists, sortOrder) {
        when (sortOrder) {
            LibrarySortOrder.RECENTLY_ADDED -> allPlaylists
            LibrarySortOrder.TITLE_AZ -> allPlaylists.sortedBy { it.title }
            LibrarySortOrder.ARTIST -> allPlaylists.sortedBy { it.curator }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen_list"),
        contentPadding = PaddingValues(top = 92.dp, bottom = 188.dp)
    ) {
        // 1. Apple Music "Transfer Music" Promotional Card Banner
        if (showTransferBanner) {
            item(key = "library_transfer_banner") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(auraColors.cardSurface)
                        .border(0.75.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .testTag("transfer_music_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElectricBlue.copy(alpha = 0.16f))
                                .border(0.75.dp, ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapHoriz,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Transfer Music",
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Add music and playlists from other services to your Onyx Music library.",
                                color = auraColors.textSecondary,
                                fontFamily = InterFontFamily,
                                fontSize = 12.5.sp,
                                lineHeight = 16.sp,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Get Started",
                                color = ElectricBlue,
                                fontFamily = InterFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        onTransferMusicClick?.invoke()
                                        showTransferBanner = false
                                    }
                                    .testTag("transfer_music_get_started")
                            )
                        }

                        IconButton(
                            onClick = { showTransferBanner = false },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("transfer_music_dismiss")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss transfer banner",
                                tint = auraColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // 2. Apple Music Library Category List
        item(key = "library_categories") {
            Column(modifier = Modifier.fillMaxWidth()) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category.title
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCategory(category.title) }
                            .padding(horizontal = 20.dp)
                            .testTag("library_cat_${category.title.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.title,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = category.title,
                                    color = if (isSelected) ElectricBlue else auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 17.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    letterSpacing = (-0.25).sp
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = auraColors.textSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 38.dp),
                            thickness = 0.6.dp,
                            color = auraColors.divider
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3. Interactive Sort Chips Bar
        item(key = "library_sort_chips") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.testTag("library_sort_row")
            ) {
                items(LibrarySortOrder.entries, key = { it.name }) { option ->
                    val isSelected = sortOrder == option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isSelected) ElectricBlue else auraColors.cardSurface
                            )
                            .border(
                                0.7.dp,
                                if (isSelected) ElectricBlue else auraColors.glassBorder,
                                RoundedCornerShape(50)
                            )
                            .clickable { onSelectSortOrder(option) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("library_sort_${option.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.label,
                            color = if (isSelected) Color.White else auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        when (selectedCategory) {
            "Songs", "Favorite Songs" -> {
                val filteredSongs = if (selectedCategory == "Favorite Songs") {
                    sortedSongs.filter { likedSongIds.contains(it.id) }
                } else {
                    sortedSongs.filter { savedSongIds.contains(it.id) }.ifEmpty { sortedSongs }
                }

                item(key = "lib_songs_header") {
                    MusicSectionHeader(
                        title = selectedCategory,
                        subtitle = "${filteredSongs.size} tracks • Sorted by ${sortOrder.label}"
                    )
                }

                items(filteredSongs, key = { "lib_song_${it.id}" }) { song ->
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

            "Artists" -> {
                item(key = "lib_artists_header") {
                    MusicSectionHeader(
                        title = "Artists",
                        subtitle = "${allArtists.size} artists in your library"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(allArtists, key = { "lib_artist_${it.id}" }) { artist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArtistClick(artist) }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .testTag("library_artist_row_${artist.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = artist.avatarModel,
                                placeholder = painterResource(artist.avatarRes),
                                error = painterResource(artist.avatarRes),
                                contentDescription = artist.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = artist.name,
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 16.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${artist.genre} • ${artist.monthlyListeners}",
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = auraColors.textSecondary
                        )
                    }
                }
            }

            "Playlists", "Made For You" -> {
                item(key = "lib_playlists_header") {
                    MusicSectionHeader(
                        title = if (selectedCategory == "Made For You") "Made For You" else "Saved Playlists",
                        subtitle = "${sortedPlaylists.size} playlists • Sorted by ${sortOrder.label}"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val chunkedPlaylists = sortedPlaylists.chunked(2)
                items(chunkedPlaylists.size, key = { idx -> "lib_pl_row_$idx" }) { rowIndex ->
                    val pair = chunkedPlaylists[rowIndex]
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
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            else -> {
                item(key = "recently_added_header") {
                    MusicSectionHeader(
                        title = "Recently Added",
                        subtitle = "Showing $selectedCategory • Sorted by ${sortOrder.label}"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val chunkedAlbums = sortedAlbums.chunked(2)
                items(chunkedAlbums.size, key = { idx -> "lib_grid_row_$idx" }) { rowIndex ->
                    val pair = chunkedAlbums[rowIndex]
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
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Apple Music-Inspired Create Playlist Modal Sheet.
 * - Dark elevated surface (#1C1C1E)
 * - Top-left circular close button (×), centered "New Playlist" title, top-right circular blue check button (✓)
 * - Centered large square artwork placeholder with circular camera/photo picker button (PickVisualMedia)
 * - Centered large "Playlist Title" input field with subtle underline
 */
@Composable
fun CreatePlaylistModalSheet(
    visible: Boolean,
    onCreatePlaylist: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val auraColors = LocalAuraColors.current
    var playlistTitle by remember { mutableStateOf("") }
    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedCoverUri = uri
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack.copy(alpha = 0.72f))
            .clickable(onClick = onDismiss)
            .testTag("create_playlist_modal")
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF1C1C1E))
                .border(
                    0.8.dp,
                    Color.White.copy(alpha = 0.12f),
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .clickable(enabled = false) {}
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header Row: Close (×) | New Playlist | Confirm (✓)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C2E))
                        .clickable(onClick = onDismiss)
                        .testTag("create_playlist_close_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cancel",
                        tint = auraColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "New Playlist",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .clickable { onCreatePlaylist(playlistTitle) }
                        .testTag("create_playlist_confirm_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Create Playlist",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Large Centered Square Artwork Placeholder with Camera Button
            Box(
                modifier = Modifier
                    .size(188.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF252527))
                    .border(0.8.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("create_playlist_cover_picker"),
                contentAlignment = Alignment.Center
            ) {
                if (selectedCoverUri != null) {
                    AsyncImage(
                        model = selectedCoverUri,
                        contentDescription = "Selected playlist cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PureBlack.copy(alpha = 0.55f))
                        .border(0.8.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Choose cover photo",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Centered "Playlist Title" Input Field with Bottom Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (playlistTitle.isEmpty()) {
                    Text(
                        text = "Playlist Title",
                        color = auraColors.textSecondary.copy(alpha = 0.65f),
                        fontFamily = InterFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                BasicTextField(
                    value = playlistTitle,
                    onValueChange = { playlistTitle = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = auraColors.textPrimary,
                        fontFamily = InterFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(ElectricBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("create_playlist_title_input")
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.8.dp,
                color = Color(0xFF303033)
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
