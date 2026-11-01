package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.model.Album
import com.example.domain.model.Song
import com.example.ui.components.AlbumArtworkCard
import com.example.ui.components.MusicListItem
import com.example.ui.components.MusicSectionHeader
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextPrimaryWhite

private data class BrowseCategoryTile(
    val title: String,
    val filterKey: String,
    val artworkRes: Int,
    val gradientColor: Color,
    val artworkUrl: String
)

@Composable
fun SearchScreen(
    searchQuery: String,
    selectedCategory: String?,
    searchResultAlbums: List<Album>,
    searchResultSongs: List<Song>,
    recentSearches: List<String>,
    trendingSearches: List<String>,
    currentPlayingSongId: String,
    isPlaying: Boolean,
    likedSongIds: Set<String>,
    listState: LazyListState,
    onQueryChange: (String) -> Unit,
    onSubmitSearchTerm: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onCategoryClick: (String?) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onPlayAlbum: (Album) -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    isSearchingOnline: Boolean = false,
    isLoadingMoreSongs: Boolean = false,
    currentSearchPage: Int = 1,
    onLoadMoreSongs: () -> Unit = {},
    isOfflineMode: Boolean = false,
    downloadedSongsCount: Int = 0,
    onToggleOfflineFilter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current

    val browseTiles = listOf(
        BrowseCategoryTile(
            title = "Happy Diwali",
            filterKey = "Bollywood Festive",
            artworkRes = R.drawable.img_cat_diwali,
            gradientColor = Color(0xFFB45309),
            artworkUrl = "https://c.saavncdn.com/191/Kesariya-From-Brahmastra-Hindi-2022-20220717092820-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Bollywood",
            filterKey = "Bollywood",
            artworkRes = R.drawable.img_cat_bollywood,
            gradientColor = Color(0xFFBE185D),
            artworkUrl = "https://c.saavncdn.com/807/Pathaan-Hindi-2022-20221222104158-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Punjabi",
            filterKey = "Punjabi",
            artworkRes = R.drawable.img_cat_punjabi,
            gradientColor = Color(0xFFEA580C),
            artworkUrl = "https://c.saavncdn.com/734/Champagne-Talk-Punjabi-2022-20221008011951-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Tamil",
            filterKey = "Tamil",
            artworkRes = R.drawable.img_cat_tamil,
            gradientColor = Color(0xFF047857),
            artworkUrl = "https://c.saavncdn.com/026/Chaleya-From-Jawan-Hindi-2023-20230814014337-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Pop",
            filterKey = "Pop",
            artworkRes = R.drawable.img_cat_pop,
            gradientColor = Color(0xFFDB2777),
            artworkUrl = "https://c.saavncdn.com/406/Dhurandhar-Title-Track-From-Dhurandhar-Hindi-2025-20251016190927-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Telugu",
            filterKey = "Telugu",
            artworkRes = R.drawable.img_cat_telugu,
            gradientColor = Color(0xFF4338CA),
            artworkUrl = "https://c.saavncdn.com/584/Heeriye-feat-Arijit-Singh-Hindi-2023-20230925050405-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Onyx Music Radio",
            filterKey = "Arijit Singh",
            artworkRes = R.drawable.img_cat_radio,
            gradientColor = Color(0xFFE11D48),
            artworkUrl = "https://c.saavncdn.com/114/Apna-Bana-Le-From-Bhediya-Hindi-2022-20221105093003-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Charts",
            filterKey = "Top 50 Bollywood",
            artworkRes = R.drawable.img_cat_charts,
            gradientColor = Color(0xFF7E22CE),
            artworkUrl = "https://c.saavncdn.com/000/Stree-2-Hindi-2024-20240809153827-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Sing",
            filterKey = "Shreya Ghoshal",
            artworkRes = R.drawable.img_cat_sing,
            gradientColor = Color(0xFFF43F5E),
            artworkUrl = "https://c.saavncdn.com/238/Satranga-From-ANIMAL-Hindi-2023-20231027131032-500x500.jpg"
        ),
        BrowseCategoryTile(
            title = "Spatial Audio",
            filterKey = "Dhurandhar",
            artworkRes = R.drawable.img_cat_spatial,
            gradientColor = Color(0xFF0284C7),
            artworkUrl = "https://c.saavncdn.com/307/Gehra-Hua-From-Dhurandhar-Hindi-2025-20251127143154-500x500.jpg"
        )
    )

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen_list"),
        contentPadding = PaddingValues(top = 104.dp, bottom = 188.dp)
    ) {
        // Rounded Search Input Bar
        item(key = "search_bar_input") {
            val barShape = RoundedCornerShape(14.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp)
                    .clip(barShape)
                    .background(auraColors.cardSurface)
                    .border(0.7.dp, auraColors.glassBorder.copy(alpha = 0.4f), barShape)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = auraColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = auraColors.textSecondary,
                                fontFamily = InterFontFamily,
                                fontSize = 15.5.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        onSubmitSearchTerm(searchQuery)
                                    }
                                }
                            ),
                            textStyle = TextStyle(
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(ElectricBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_input")
                        )
                    }
                    if (isSearchingOnline) {
                        CircularProgressIndicator(
                            color = ElectricBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    if (searchQuery.isNotEmpty() || selectedCategory != null) {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                                onCategoryClick(null)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear search",
                                tint = auraColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Voice search",
                            tint = auraColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (searchQuery.isNotBlank() || selectedCategory != null) {
            item(key = "search_results_header") {
                MusicSectionHeader(
                    title = if (selectedCategory != null) selectedCategory else "Top Results",
                    subtitle = if (isSearchingOnline) {
                        "Fetching live 320kbps tracks from Onyx Music API..."
                    } else {
                        "${searchResultAlbums.size} albums • ${searchResultSongs.size} songs • Page $currentSearchPage"
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (searchResultAlbums.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(searchResultAlbums, key = { "sr_alb_${it.id}" }) { album ->
                            AlbumArtworkCard(
                                album = album,
                                onCardClick = { onAlbumClick(album) },
                                onPlayClick = { onPlayAlbum(album) },
                                cardWidth = 158.dp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            items(searchResultSongs, key = { "sr_song_${it.id}" }) { song ->
                MusicListItem(
                    song = song,
                    isCurrentlyPlaying = isPlaying && song.id == currentPlayingSongId,
                    isLiked = likedSongIds.contains(song.id),
                    onSongClick = { onSongClick(song) },
                    onFavoriteClick = { onToggleFavorite(song.id) },
                    onMoreOptionsClick = { onMoreOptionsClick(song) }
                )
            }

            item(key = "search_load_more_api_songs") {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElectricBlue.copy(alpha = 0.16f))
                        .border(0.8.dp, ElectricBlue.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                        .clickable(enabled = !isLoadingMoreSongs) { onLoadMoreSongs() }
                        .testTag("search_load_more_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLoadingMoreSongs) {
                            CircularProgressIndicator(
                                color = ElectricBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Loading more songs from API...",
                                color = TextPrimaryWhite,
                                fontFamily = InterFontFamily,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "Load 40 More Songs from API (Page ${currentSearchPage + 1})",
                                color = ElectricBlue,
                                fontFamily = InterFontFamily,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        } else {
            item(key = "browse_categories_header") {
                MusicSectionHeader(
                    title = "Browse Categories",
                    subtitle = "Live 500x500 API covers & 320kbps catalogs"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val rows = browseTiles.chunked(2)
            items(rows.size, key = { "browse_row_$it" }) { idx ->
                val pair = rows[idx]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    pair.forEach { tile ->
                        val shape = RoundedCornerShape(14.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(114.dp)
                                .shadow(
                                    elevation = 10.dp,
                                    shape = shape,
                                    ambientColor = tile.gradientColor.copy(alpha = 0.35f),
                                    spotColor = PureBlack.copy(alpha = 0.4f)
                                )
                                .clip(shape)
                                .background(tile.gradientColor)
                                .border(0.6.dp, Color.White.copy(alpha = 0.16f), shape)
                                .clickable { onCategoryClick(tile.filterKey) }
                                .testTag("search_category_${tile.title.lowercase().replace(" ", "_")}")
                        ) {
                            AsyncImage(
                                model = tile.artworkUrl,
                                placeholder = androidx.compose.ui.res.painterResource(tile.artworkRes),
                                error = androidx.compose.ui.res.painterResource(tile.artworkRes),
                                contentDescription = tile.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                PureBlack.copy(alpha = 0.55f)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = tile.title,
                                    color = TextPrimaryWhite,
                                    fontFamily = InterFontFamily,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                            }
                        }
                    }
                }
            }

            if (isOfflineMode || downloadedSongsCount > 0) {
                item(key = "search_offline_banner_section") {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isOfflineMode) Color(0xFF1E3A8A).copy(alpha = 0.35f)
                                else ElectricBlue.copy(alpha = 0.12f)
                            )
                            .border(
                                0.7.dp,
                                ElectricBlue.copy(alpha = 0.45f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onToggleOfflineFilter() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("search_offline_mode_banner"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isOfflineMode) "Offline Mode Active • Local Playback" else "Offline Ready • $downloadedSongsCount Songs Downloaded",
                                color = TextPrimaryWhite,
                                fontFamily = InterFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tap to toggle offline-only filter for zero-network listening",
                                color = auraColors.textSecondary,
                                fontFamily = InterFontFamily,
                                fontSize = 11.5.sp
                            )
                        }
                        Text(
                            text = if (isOfflineMode) "ONLINE" else "OFFLINE",
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Recent & Trending Searches below Browse Categories so category tiles remain immediately visible
            if (recentSearches.isNotEmpty()) {
                item(key = "recent_searches_section") {
                    Spacer(modifier = Modifier.height(22.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Searches",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clear",
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable(onClick = onClearRecentSearches)
                                .testTag("clear_recent_searches_button")
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(recentSearches, key = { "rec_$it" }) { term ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(auraColors.cardSurface)
                                    .border(0.6.dp, auraColors.glassBorder, RoundedCornerShape(50))
                                    .clickable { onSubmitSearchTerm(term) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    tint = auraColors.textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = term,
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item(key = "trending_searches_section") {
                Spacer(modifier = Modifier.height(22.dp))
                MusicSectionHeader(
                    title = "Trending Searches",
                    subtitle = "Popular across Onyx Music right now"
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trendingSearches, key = { "trend_$it" }) { term ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(ElectricBlue.copy(alpha = 0.14f))
                                .border(0.6.dp, ElectricBlue.copy(alpha = 0.45f), RoundedCornerShape(50))
                                .clickable { onSubmitSearchTerm(term) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = term,
                                color = auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
