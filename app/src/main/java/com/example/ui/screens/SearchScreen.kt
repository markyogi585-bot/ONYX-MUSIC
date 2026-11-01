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
    val gradientColor: Color
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
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current

    val browseTiles = listOf(
        BrowseCategoryTile("Happy Diwali", "Happy Diwali", R.drawable.img_cat_diwali, Color(0xFFB45309)),
        BrowseCategoryTile("Bollywood", "Bollywood", R.drawable.img_cat_bollywood, Color(0xFFBE185D)),
        BrowseCategoryTile("Punjabi", "Punjabi", R.drawable.img_cat_punjabi, Color(0xFFEA580C)),
        BrowseCategoryTile("Tamil", "Tamil", R.drawable.img_cat_tamil, Color(0xFF047857)),
        BrowseCategoryTile("Pop", "Pop", R.drawable.img_cat_pop, Color(0xFFDB2777)),
        BrowseCategoryTile("Telugu", "Telugu", R.drawable.img_cat_telugu, Color(0xFF4338CA)),
        BrowseCategoryTile("Onyx Music Radio", "Radio", R.drawable.img_cat_radio, Color(0xFFE11D48)),
        BrowseCategoryTile("Charts", "Charts", R.drawable.img_cat_charts, Color(0xFF7E22CE)),
        BrowseCategoryTile("Sing", "Sing", R.drawable.img_cat_sing, Color(0xFFF43F5E)),
        BrowseCategoryTile("Spatial Audio", "Spatial Audio", R.drawable.img_cat_spatial, Color(0xFF0284C7))
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
                MusicSectionHeader(title = "Browse Categories")
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
                                model = tile.artworkRes,
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
