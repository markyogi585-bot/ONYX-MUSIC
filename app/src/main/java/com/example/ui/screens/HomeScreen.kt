package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.example.ui.theme.OnyxDesignTokens

/**
 * ONYX MUSIC Home Screen — Apple Music-inspired Complete Editorial & Personalized Hierarchy:
 * 1. Top Picks for You (Large editorial artwork hero cards)
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
    modifier: Modifier = Modifier
) {
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
            items = topSongs.take(4),
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
