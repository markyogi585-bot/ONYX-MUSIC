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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.model.Album
import com.example.domain.model.ArtistProfile
import com.example.domain.model.Playlist
import com.example.domain.model.Song
import com.example.ui.components.AlbumArtworkCard
import com.example.ui.components.ArtistAvatarCard
import com.example.ui.components.FeaturedAlbumHeroCard
import com.example.ui.components.MusicListItem
import com.example.ui.components.MusicSectionHeader
import com.example.ui.components.PlaylistArtworkCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.OnyxDesignTokens
import com.example.ui.theme.TextPrimaryWhite

/**
 * ONYX MUSIC Home Screen — Apple Music-inspired Complete Editorial & Personalized Hierarchy:
 * 1. Top Picks for You (Large editorial artwork hero cards)
 * 1b. Moods & Gemini AI Smart Suggestions (Interactive Mood Chips + AI Curator Mix)
 * 2. Recently Played (Strict 1:1 square artwork carousel)
 * 3. Made For You (Personalized Spatial Audio mixes)
 * 4. Recommended Albums (Strict 1:1 square artwork carousel)
 * 5. Popular Artists (Circular artist avatars)
 * 6. Artist Essentials (Curated artist essential playlists)
 * 7. Top Songs / Trending Music (Track list with quick play & context menu)
 * 8. New Releases (Fresh studio drops)
 * 9. Popular Playlists (Curated soundtracks)
 * 10. Your Favourite Music
 * 11. Continue Listening
 */
@Composable
fun HomeScreen(
    topPicksForYou: List<Album>,
    recentlyPlayed: List<Album>,
    madeForYou: List<Playlist>,
    topSongs: List<Song>,
    newReleases: List<Album>,
    recommendedAlbums: List<Album>,
    popularArtists: List<ArtistProfile>,
    artistEssentials: List<Playlist>,
    popularPlaylists: List<Playlist>,
    favouriteSongs: List<Song>,
    continueListening: List<Album>,
    currentPlayingSongId: String,
    isPlaying: Boolean,
    likedSongIds: Set<String>,
    listState: LazyListState,
    onAlbumClick: (Album) -> Unit,
    onPlayAlbum: (Album) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onPlayPlaylist: (Playlist) -> Unit,
    onArtistClick: (ArtistProfile) -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    onSeeAllClick: (String) -> Unit,
    availableMoods: List<String> = emptyList(),
    selectedMood: String = "🔥 Mass & Action",
    aiMoodCuratorNote: String = "",
    aiMoodTargetBpm: String = "124 BPM",
    aiSuggestedSongs: List<Song> = emptyList(),
    isGeneratingMoodMix: Boolean = false,
    onSelectMood: (String) -> Unit = {},
    onPlayAiMoodMix: () -> Unit = {},
    isOfflineMode: Boolean = false,
    offlineOnlyFilterEnabled: Boolean = false,
    onToggleOfflineFilter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(top = 104.dp, bottom = 188.dp)
    ) {
        // 1. Top Picks for You (Large Editorial Hero Carousel)
        item(key = "home_top_picks") {
            MusicSectionHeader(
                title = "Top Picks for You",
                subtitle = "Handpicked spatial masters & pre-releases",
                onSeeAllClick = { onSeeAllClick("top_picks") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.HeroCardHorizontalGap),
                modifier = Modifier.testTag("row_top_picks")
            ) {
                items(topPicksForYou, key = { "tp_${it.id}" }) { album ->
                    FeaturedAlbumHeroCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 2. Recently Played (Strict 1:1 Square Artwork Carousel)
        item(key = "home_recently_played") {
            MusicSectionHeader(
                title = stringResource(R.string.section_recently_played),
                subtitle = "Jump right back into your rotation",
                onSeeAllClick = { onSeeAllClick("recently_played") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.CardHorizontalGap),
                modifier = Modifier.testTag("row_recently_played")
            ) {
                items(recentlyPlayed, key = { "rp_${it.id}" }) { album ->
                    AlbumArtworkCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) },
                        cardWidth = OnyxDesignTokens.StandardSquareCardWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 3. Made For You
        item(key = "home_made_for_you") {
            MusicSectionHeader(
                title = stringResource(R.string.section_made_for_you),
                subtitle = "Personalized Spatial Audio mixes",
                onSeeAllClick = { onSeeAllClick("made_for_you") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.HeroCardHorizontalGap),
                modifier = Modifier.testTag("row_made_for_you")
            ) {
                items(madeForYou, key = { "mfy_${it.id}" }) { playlist ->
                    PlaylistArtworkCard(
                        playlist = playlist,
                        onCardClick = { onPlaylistClick(playlist) },
                        onPlayClick = { onPlayPlaylist(playlist) },
                        cardWidth = OnyxDesignTokens.PlaylistSquareCardWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 3b. Moods & Gemini AI Smart Suggestions + Offline Mode Control
        if (availableMoods.isNotEmpty()) {
            item(key = "home_ai_moods") {
                if (isOfflineMode || offlineOnlyFilterEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = OnyxDesignTokens.ScreenHorizontalPadding)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E3A8A).copy(alpha = 0.42f))
                            .border(0.8.dp, ElectricBlue.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                            .clickable { onToggleOfflineFilter() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("home_offline_mode_banner"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Filled.CloudOff,
                                contentDescription = "Offline Mode",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Offline Listening Mode Active",
                                    color = TextPrimaryWhite,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Playing downloaded 320kbps AAC & synthesized spatial tracks without network",
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                        Text(
                            text = "Switch",
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                MusicSectionHeader(
                    title = "Moods & AI Smart Suggestions",
                    subtitle = "Gemini AI-curated sonic vibes & instant mixes"
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.testTag("row_ai_mood_chips")
                ) {
                    items(availableMoods, key = { "mood_$it" }) { mood ->
                        val isSelected = mood == selectedMood
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) ElectricBlue
                                    else auraColors.cardSurface
                                )
                                .border(
                                    0.8.dp,
                                    if (isSelected) ElectricBlue else auraColors.glassBorder,
                                    RoundedCornerShape(50)
                                )
                                .clickable { onSelectMood(mood) }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                                .testTag("mood_chip_${mood.lowercase().replace(" ", "_")}")
                        ) {
                            Text(
                                text = mood,
                                color = if (isSelected) Color.White else auraColors.textPrimary,
                                fontFamily = InterFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gemini AI Mood Curator Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = OnyxDesignTokens.ScreenHorizontalPadding)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    ElectricBlue.copy(alpha = 0.22f),
                                    Color(0xFF7C3AED).copy(alpha = 0.22f)
                                )
                            )
                        )
                        .border(0.8.dp, ElectricBlue.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                        .testTag("ai_mood_curator_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "Gemini AI",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GEMINI AI VIBE • $aiMoodTargetBpm",
                                    color = ElectricBlue,
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp
                                )
                            }

                            if (isGeneratingMoodMix) {
                                CircularProgressIndicator(
                                    color = ElectricBlue,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(ElectricBlue)
                                        .clickable { onPlayAiMoodMix() }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                        .testTag("play_ai_mood_mix_button"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Play AI Mix",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Play Mix",
                                        color = Color.White,
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (aiMoodCuratorNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = aiMoodCuratorNote,
                                color = TextPrimaryWhite.copy(alpha = 0.90f),
                                fontFamily = InterFontFamily,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        }

                        if (aiSuggestedSongs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.testTag("row_ai_suggested_songs")
                            ) {
                                items(aiSuggestedSongs.take(8), key = { "aisug_${it.id}" }) { song ->
                                    Row(
                                        modifier = Modifier
                                            .width(220.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.Black.copy(alpha = 0.35f))
                                            .border(0.6.dp, auraColors.glassBorder, RoundedCornerShape(12.dp))
                                            .clickable { onSongClick(song) }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = song.artworkModel,
                                            placeholder = painterResource(song.artworkRes),
                                            error = painterResource(song.artworkRes),
                                            contentDescription = song.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = song.title,
                                                color = TextPrimaryWhite,
                                                fontFamily = InterFontFamily,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = song.artist,
                                                color = auraColors.textSecondary,
                                                fontFamily = InterFontFamily,
                                                fontSize = 11.5.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
            }
        }

        // 4. Recommended Albums (Strict 1:1 Square Artwork Carousel)
        item(key = "home_recommended_albums") {
            MusicSectionHeader(
                title = stringResource(R.string.section_recommended_albums),
                subtitle = "Inspired by your recent listening",
                onSeeAllClick = { onSeeAllClick("recommended_albums") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.CardHorizontalGap),
                modifier = Modifier.testTag("row_recommended_albums")
            ) {
                items(recommendedAlbums, key = { "recalb_${it.id}" }) { album ->
                    AlbumArtworkCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) },
                        cardWidth = OnyxDesignTokens.StandardSquareCardWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 5. Popular Artists
        item(key = "home_popular_artists") {
            MusicSectionHeader(
                title = "Popular Artists",
                subtitle = "Artists defining the Onyx sound",
                onSeeAllClick = { onSeeAllClick("popular_artists") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.testTag("row_popular_artists")
            ) {
                items(popularArtists, key = { "art_${it.id}" }) { artist ->
                    ArtistAvatarCard(
                        artist = artist,
                        onClick = { onArtistClick(artist) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 6. Artist Essentials
        item(key = "home_artist_essentials") {
            MusicSectionHeader(
                title = "Artist Essentials",
                subtitle = "Definitive collections from top creators",
                onSeeAllClick = { onSeeAllClick("artist_essentials") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.CardHorizontalGap),
                modifier = Modifier.testTag("row_artist_essentials")
            ) {
                items(artistEssentials, key = { "ess_${it.id}" }) { playlist ->
                    PlaylistArtworkCard(
                        playlist = playlist,
                        onCardClick = { onPlaylistClick(playlist) },
                        onPlayClick = { onPlayPlaylist(playlist) },
                        cardWidth = OnyxDesignTokens.StandardSquareCardWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 7. Top Songs / Trending Music
        item(key = "home_top_songs_header") {
            MusicSectionHeader(
                title = stringResource(R.string.section_top_songs),
                subtitle = "Global lossless chart leaders",
                onSeeAllClick = { onSeeAllClick("top_songs") }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(
            items = topSongs.take(8),
            key = { "top_song_${it.id}" }
        ) { song ->
            MusicListItem(
                song = song,
                isCurrentlyPlaying = isPlaying && song.id == currentPlayingSongId,
                isLiked = likedSongIds.contains(song.id),
                onSongClick = { onSongClick(song) },
                onFavoriteClick = { onToggleFavorite(song.id) },
                onMoreOptionsClick = { onMoreOptionsClick(song) }
            )
        }

        // 8. New Releases
        item(key = "home_new_releases") {
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
            MusicSectionHeader(
                title = stringResource(R.string.section_new_releases),
                subtitle = "Fresh studio drops this week",
                onSeeAllClick = { onSeeAllClick("new_releases") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.CardHorizontalGap),
                modifier = Modifier.testTag("row_new_releases")
            ) {
                items(newReleases, key = { "newrel_${it.id}" }) { album ->
                    AlbumArtworkCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) },
                        cardWidth = OnyxDesignTokens.StandardSquareCardWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 9. Popular Playlists
        item(key = "home_popular_playlists") {
            MusicSectionHeader(
                title = stringResource(R.string.section_popular_playlists),
                subtitle = "Essential curated soundtracks",
                onSeeAllClick = { onSeeAllClick("popular_playlists") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.CardHorizontalGap),
                modifier = Modifier.testTag("row_popular_playlists")
            ) {
                items(popularPlaylists, key = { "poppl_${it.id}" }) { playlist ->
                    PlaylistArtworkCard(
                        playlist = playlist,
                        onCardClick = { onPlaylistClick(playlist) },
                        onPlayClick = { onPlayPlaylist(playlist) },
                        cardWidth = OnyxDesignTokens.PlaylistSquareCardWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
        }

        // 10. Your Favourite Music
        item(key = "home_favourite_music_header") {
            MusicSectionHeader(
                title = stringResource(R.string.section_favourite_music),
                subtitle = "Tracks you've loved in Onyx Music",
                onSeeAllClick = { onSeeAllClick("favourite_music") }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(
            items = favouriteSongs.take(4),
            key = { "fav_song_${it.id}" }
        ) { song ->
            MusicListItem(
                song = song,
                isCurrentlyPlaying = isPlaying && song.id == currentPlayingSongId,
                isLiked = likedSongIds.contains(song.id),
                onSongClick = { onSongClick(song) },
                onFavoriteClick = { onToggleFavorite(song.id) },
                onMoreOptionsClick = { onMoreOptionsClick(song) }
            )
        }

        // 11. Continue Listening
        item(key = "home_continue_listening") {
            Spacer(modifier = Modifier.height(OnyxDesignTokens.SectionVerticalSpacing))
            MusicSectionHeader(
                title = stringResource(R.string.section_continue_listening),
                subtitle = "Pick up where you left off",
                onSeeAllClick = { onSeeAllClick("continue_listening") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = OnyxDesignTokens.ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(OnyxDesignTokens.CardHorizontalGap),
                modifier = Modifier.testTag("row_continue_listening")
            ) {
                items(continueListening, key = { "cont_${it.id}" }) { album ->
                    AlbumArtworkCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) },
                        cardWidth = OnyxDesignTokens.StandardSquareCardWidth
                    )
                }
            }
        }
    }
}
