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
        listOf(
            repository.albums[1], // Saiyaara
            repository.albums[2], // Stree 2
            repository.albums[9], // AUJLA SZN 1 (Karan Aujla)
            repository.albums[3], // GLORY (Honey Singh)
            repository.albums[10], // Pushpa 2
            repository.albums[4], // Bhool Bhulaiyaa 3
            repository.albums[0]  // Dhurandhar
        )

    fun getBestNewSongs(): List<Song> =
        listOf(
            repository.songs[3], // Saiyaara
            repository.songs[4], // Aaj Ki Raat (Stree 2)
            repository.songs[5], // Tauba Tauba (Karan Aujla)
            repository.songs[6], // Millionaire (Honey Singh)
            repository.songs[7], // Raanjhan (Do Patti)
            repository.songs[8], // Bhool Bhulaiyaa 3 (Diljit Dosanjh)
            repository.songs[16], // Sajni (Arijit Singh)
            repository.songs[17], // Peelings (Pushpa 2)
            repository.songs[14], // Ashke (Karan Aujla)
            repository.songs[21], // Kesariya (Arijit Singh)
            repository.songs[23], // G.O.A.T. (Diljit Dosanjh)
            repository.songs[0],  // Dhurandhar Title Track
            repository.songs[1]   // Gehra Hua
        )

    fun getNewThisWeekAlbums(): List<Album> =
        listOf(repository.albums[3], repository.albums[4], repository.albums[5], repository.albums[6], repository.albums[7])

    fun getRecentReleases(): List<Album> =
        listOf(repository.albums[9], repository.albums[8], repository.albums[10], repository.albums[0], repository.albums[3])

    fun getUpdatedPlaylists(): List<Playlist> =
        repository.playlists

    // Home Screen Sections
    fun getTopPicksForYou(): List<Album> =
        listOf(
            repository.albums[1], // Saiyaara
            repository.albums[2], // Stree 2
            repository.albums[9], // AUJLA SZN 1 (Karan Aujla)
            repository.albums[3], // GLORY (Honey Singh)
            repository.albums[10], // Pushpa 2 The Rule
            repository.albums[4], // Bhool Bhulaiyaa 3 (Diljit Dosanjh & Pitbull)
            repository.albums[0]  // Dhurandhar
        )

    fun getRecentlyPlayed(): List<Album> =
        listOf(
            repository.albums[2], // Stree 2
            repository.albums[1], // Saiyaara
            repository.albums[9], // AUJLA SZN 1
            repository.albums[10], // Pushpa 2
            repository.albums[4], // Bhool Bhulaiyaa 3
            repository.albums[0]  // Dhurandhar
        )

    fun getMadeForYou(): List<Playlist> =
        listOf(repository.playlists[1], repository.playlists[2], repository.playlists[3], repository.playlists[4], repository.playlists[0])

    fun getTopSongs(): List<Song> =
        listOf(
            repository.songs[3], // Saiyaara
            repository.songs[4], // Aaj Ki Raat (Stree 2)
            repository.songs[5], // Tauba Tauba (Karan Aujla)
            repository.songs[6], // Millionaire (Honey Singh)
            repository.songs[7], // Raanjhan (Do Patti)
            repository.songs[8], // Bhool Bhulaiyaa 3 (Diljit Dosanjh)
            repository.songs[16], // Sajni (Arijit Singh)
            repository.songs[17], // Peelings (Pushpa 2)
            repository.songs[14], // Ashke (Karan Aujla)
            repository.songs[21], // Kesariya (Arijit Singh)
            repository.songs[23], // G.O.A.T. (Diljit Dosanjh)
            repository.songs[0]  // Dhurandhar Title Track
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

    fun getDecadeBlockbusters(): List<Song> =
        listOf(
            repository.songs[24], // Channa Mereya (2016)
            repository.songs[31], // Tum Hi Ho (2013)
            repository.songs[25], // Raataan Lambiyan (2021)
            repository.songs[32], // Bekhayali (2019)
            repository.songs[26], // Apna Bana Le (2022)
            repository.songs[27], // Arjan Vailly (2023)
            repository.songs[28], // Pehle Bhi Main (2023)
            repository.songs[29], // Chaleya (2023)
            repository.songs[30], // Lover (2021)
            repository.songs[21], // Kesariya (2022)
            repository.songs[23], // G.O.A.T. (2020)
            repository.songs[5],  // Tauba Tauba (2024)
            repository.songs[6],  // Millionaire (2024)
            repository.songs[4],  // Aaj Ki Raat (2024)
            repository.songs[3]   // Saiyaara (2025)
        )

    fun getDecadeAlbums(): List<Album> =
        listOf(
            repository.albums[12], // Ae Dil Hai Mushkil (2016)
            repository.albums[13], // Kabir Singh (2019)
            repository.albums[14], // Shershaah (2021)
            repository.albums[15], // Animal (2023)
            repository.albums[16], // MoonChild Era (2021)
            repository.albums[2],  // Stree 2 (2024)
            repository.albums[1],  // Saiyaara (2025)
            repository.albums[3]   // GLORY (2024)
        )

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
