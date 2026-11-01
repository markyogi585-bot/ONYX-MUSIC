package com.example.domain.usecase

import com.example.data.repository.MusicRepository
import com.example.domain.model.Album
import com.example.domain.model.ArtistProfile
import com.example.domain.model.Playlist
import com.example.domain.model.RadioStation
import com.example.domain.model.Song

class GetMusicCatalogUseCase(
    private val repository: MusicRepository = MusicRepository()
) {
    // New Screen Sections
    fun getEarlyAccessAlbums(): List<Album> =
        listOf(repository.albums[0], repository.albums[1], repository.albums[11], repository.albums[2])

    fun getBestNewSongs(): List<Song> =
        repository.songs.take(12)

    fun getNewThisWeekAlbums(): List<Album> =
        listOf(repository.albums[3], repository.albums[4], repository.albums[5], repository.albums[6], repository.albums[7])

    fun getRecentReleases(): List<Album> =
        listOf(repository.albums[9], repository.albums[8], repository.albums[10], repository.albums[0], repository.albums[3])

    fun getUpdatedPlaylists(): List<Playlist> =
        repository.playlists

    // Home Screen Sections
    fun getTopPicksForYou(): List<Album> =
        listOf(repository.albums[0], repository.albums[6], repository.albums[1], repository.albums[2], repository.albums[9], repository.albums[4])

    fun getRecentlyPlayed(): List<Album> =
        listOf(repository.albums[0], repository.albums[2], repository.albums[1], repository.albums[6], repository.albums[9], repository.albums[10])

    fun getMadeForYou(): List<Playlist> =
        listOf(repository.playlists[0], repository.playlists[1], repository.playlists[2], repository.playlists[3], repository.playlists[4])

    fun getTopSongs(): List<Song> =
        listOf(
            repository.songs[0],
            repository.songs[1],
            repository.songs[2],
            repository.songs[3],
            repository.songs[4],
            repository.songs[5],
            repository.songs[6],
            repository.songs[7],
            repository.songs[8],
            repository.songs[11],
            repository.songs[14],
            repository.songs[16]
        )

    fun getNewReleases(): List<Album> =
        listOf(repository.albums[11], repository.albums[3], repository.albums[0], repository.albums[4], repository.albums[9])

    fun getRecommendedAlbums(): List<Album> =
        listOf(repository.albums[4], repository.albums[7], repository.albums[9], repository.albums[5], repository.albums[3])

    fun getPopularArtists(): List<ArtistProfile> =
        repository.artists

    fun getArtistEssentials(): List<Playlist> =
        listOf(repository.playlists[1], repository.playlists[3], repository.playlists[0], repository.playlists[4])

    fun getPopularPlaylists(): List<Playlist> =
        repository.playlists.reversed()

    fun getContinueListening(): List<Album> =
        listOf(repository.albums[8], repository.albums[5], repository.albums[10], repository.albums[2])

    fun getAllAlbums(): List<Album> = repository.albums

    fun getAllSongs(): List<Song> = repository.songs

    fun getAllPlaylists(): List<Playlist> = repository.playlists

    fun getAllArtists(): List<ArtistProfile> = repository.artists

    fun getRadioStations(): List<RadioStation> = repository.radioStations

    fun searchCatalog(query: String, categoryFilter: String?): Pair<List<Album>, List<Song>> {
        val trimmed = query.trim().lowercase()
        val filteredAlbums = repository.albums.filter { album ->
            val matchesQuery = trimmed.isEmpty() ||
                album.title.lowercase().contains(trimmed) ||
                album.artist.lowercase().contains(trimmed) ||
                album.categoryTag.lowercase().contains(trimmed)
            val matchesCat = categoryFilter == null ||
                album.categoryTag.contains(categoryFilter, ignoreCase = true) ||
                album.title.contains(categoryFilter, ignoreCase = true)
            matchesQuery && matchesCat
        }.ifEmpty {
            if (categoryFilter != null && trimmed.isEmpty()) repository.albums.take(6) else emptyList()
        }
        val filteredSongs = repository.songs.filter { song ->
            val matchesQuery = trimmed.isEmpty() ||
                song.title.lowercase().contains(trimmed) ||
                song.artist.lowercase().contains(trimmed) ||
                song.albumTitle.lowercase().contains(trimmed)
            matchesQuery
        }
        return filteredAlbums to filteredSongs
    }

    fun getDefaultProfile() = repository.defaultUserProfile
}
