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
import com.example.domain.model.Playlist
import com.example.domain.model.Song
import com.example.ui.components.AlbumArtworkCard
import com.example.ui.components.FeaturedAlbumHeroCard
import com.example.ui.components.MusicListItem
import com.example.ui.components.MusicSectionHeader
import com.example.ui.components.PlaylistArtworkCard

/**
 * New Screen — Matches the Apple Music New reference:
 * 1. Early Access editorial banner (284.dp cards)
 * 2. Best New Songs section
 * 3. New This Week section (large 276.dp square artwork cards with title & secondary grey artist)
 * 4. Recent Releases section (168.dp square artwork cards)
 * 5. Updated Playlists section (176.dp playlist cards)
 */
@Composable
fun NewScreen(
    earlyAccessAlbums: List<Album>,
    bestNewSongs: List<Song>,
    newThisWeekAlbums: List<Album>,
    recentReleases: List<Album>,
    updatedPlaylists: List<Playlist>,
    currentPlayingSongId: String,
    isPlaying: Boolean,
    likedSongIds: Set<String>,
    listState: LazyListState,
    onAlbumClick: (Album) -> Unit,
    onPlayAlbum: (Album) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onPlayPlaylist: (Playlist) -> Unit,
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
            .testTag("new_screen_list"),
        contentPadding = PaddingValues(top = 104.dp, bottom = 188.dp)
    ) {
        // 1. Early Access Editorial Banner Section
        item(key = "header_early_access") {
            MusicSectionHeader(
                title = stringResource(R.string.section_early_access),
                subtitle = "Pre-release spatial audio editions",
                onSeeAllClick = { onSeeAllClick("early_access") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.testTag("row_early_access")
            ) {
                items(earlyAccessAlbums, key = { "early_${it.id}" }) { album ->
                    FeaturedAlbumHeroCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // 2. Best New Songs Section
        item(key = "header_best_new_songs") {
            MusicSectionHeader(
                title = stringResource(R.string.section_best_new_songs),
                subtitle = "Essential tracks updated daily",
                onSeeAllClick = { onSeeAllClick("best_new_songs") }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(
            items = bestNewSongs.take(5),
            key = { "best_song_${it.id}" }
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

        // 3. New This Week Section (Large 276.dp Square Artwork Showcase Cards)
        item(key = "section_new_this_week") {
            Spacer(modifier = Modifier.height(26.dp))
            MusicSectionHeader(
                title = stringResource(R.string.section_new_this_week),
                subtitle = "Handpicked albums from around the world",
                onSeeAllClick = { onSeeAllClick("new_this_week") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.testTag("row_new_this_week")
            ) {
                items(newThisWeekAlbums, key = { "ntw_${it.id}" }) { album ->
                    AlbumArtworkCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) },
                        cardWidth = 276.dp
                    )
                }
            }
        }

        // 4. Recent Releases Section
        item(key = "section_recent_releases") {
            Spacer(modifier = Modifier.height(28.dp))
            MusicSectionHeader(
                title = stringResource(R.string.section_recent_releases),
                onSeeAllClick = { onSeeAllClick("recent_releases") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.testTag("row_recent_releases")
            ) {
                items(recentReleases, key = { "rec_${it.id}" }) { album ->
                    AlbumArtworkCard(
                        album = album,
                        onCardClick = { onAlbumClick(album) },
                        onPlayClick = { onPlayAlbum(album) },
                        cardWidth = 166.dp
                    )
                }
            }
        }

        // 5. Updated Playlists Section
        item(key = "section_updated_playlists") {
            Spacer(modifier = Modifier.height(28.dp))
            MusicSectionHeader(
                title = stringResource(R.string.section_updated_playlists),
                subtitle = "Freshly curated mixes for every mood",
                onSeeAllClick = { onSeeAllClick("updated_playlists") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.testTag("row_updated_playlists")
            ) {
                items(updatedPlaylists, key = { "upl_${it.id}" }) { playlist ->
                    PlaylistArtworkCard(
                        playlist = playlist,
                        onCardClick = { onPlaylistClick(playlist) },
                        onPlayClick = { onPlayPlaylist(playlist) },
                        cardWidth = 176.dp
                    )
                }
            }
        }
    }
}
