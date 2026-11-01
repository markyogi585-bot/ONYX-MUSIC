package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.AndroidAudioOutputManager
import com.example.data.audio.OnyxMediaNotificationHelper
import com.example.data.audio.SynthesizedAudioEngine
import com.example.data.download.DownloadedTrackRecord
import com.example.data.download.MusicFolderInfo
import com.example.data.download.OnyxDownloadManager
import com.example.data.preferences.PersistedSettings
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.userPreferencesDataStore
import com.example.data.remote.OnyxSongApiService
import com.example.domain.model.Album
import com.example.domain.model.AppTab
import com.example.domain.model.ArtistProfile
import com.example.domain.model.AudioOutputRoute
import com.example.domain.model.LibrarySortOrder
import com.example.domain.model.Playlist
import com.example.domain.model.RadioStation
import com.example.domain.model.RepeatMode
import com.example.domain.model.Song
import com.example.domain.model.UserProfile
import com.example.domain.usecase.GetMusicCatalogUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MusicUiState(
    val selectedTab: AppTab = AppTab.HOME,
    val isPopupMenuOpen: Boolean = false,
    val showSplashOverlay: Boolean = true,

    // New Tab Data
    val earlyAccessAlbums: List<Album> = emptyList(),
    val bestNewSongs: List<Song> = emptyList(),
    val newThisWeekAlbums: List<Album> = emptyList(),
    val recentReleases: List<Album> = emptyList(),
    val updatedPlaylists: List<Playlist> = emptyList(),

    // Home Tab Data (Complete Editorial & Personalized Hierarchy)
    val topPicksForYou: List<Album> = emptyList(),
    val recentlyPlayed: List<Album> = emptyList(),
    val madeForYou: List<Playlist> = emptyList(),
    val topSongs: List<Song> = emptyList(),
    val newReleases: List<Album> = emptyList(),
    val recommendedAlbums: List<Album> = emptyList(),
    val popularArtists: List<ArtistProfile> = emptyList(),
    val artistEssentials: List<Playlist> = emptyList(),
    val popularPlaylists: List<Playlist> = emptyList(),
    val continueListening: List<Album> = emptyList(),
    val popularSongs: List<Song> = emptyList(),
    val featuredPlaylists: List<Playlist> = emptyList(),

    // Full Catalog
    val allAlbums: List<Album> = emptyList(),
    val allSongs: List<Song> = emptyList(),
    val allPlaylists: List<Playlist> = emptyList(),
    val allArtists: List<ArtistProfile> = emptyList(),
    val radioStations: List<RadioStation> = emptyList(),
    val recentlyPlayedStations: List<RadioStation> = emptyList(),

    // Playback, Queue & Sleep Timer State
    val currentSong: Song,
    val queueSongs: List<Song> = emptyList(),
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Int = 0,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val isMuted: Boolean = false,
    val sleepTimerMinutes: Int? = null,
    val likedSongIds: Set<String> = setOf("sng_1", "sng_2", "sng_4", "sng_8"),
    val savedSongIds: Set<String> = setOf("sng_1", "sng_2", "sng_3", "sng_4", "sng_5", "sng_8"),
    val savedPlaylistIds: Set<String> = setOf("pl_midnight_drive", "pl_velvet_lounge", "pl_deep_focus"),
    val savedAlbumIds: Set<String> = setOf("alb_neon_horizons", "alb_velvet_echoes", "alb_solar_flare"),
    val selectedAudioOutput: String = "This Android Phone",
    val availableAudioOutputs: List<AudioOutputRoute> = emptyList(),
    val isAudioOutputSheetOpen: Boolean = false,
    val isSleepTimerSheetOpen: Boolean = false,
    val activeTrackOptionsSong: Song? = null,
    val activeCreditsSong: Song? = null,
    val activeAddToPlaylistSong: Song? = null,
    val activeReportConcernSong: Song? = null,

    // Search, Pagination, Download & Folder System State
    val searchQuery: String = "",
    val selectedSearchCategory: String? = null,
    val isSearchingOnline: Boolean = false,
    val currentSearchPage: Int = 1,
    val isLoadingMoreSongs: Boolean = false,
    val searchResultAlbums: List<Album> = emptyList(),
    val searchResultSongs: List<Song> = emptyList(),
    val recentSearches: List<String> = listOf(
        "Dhurandhar",
        "Saiyaara",
        "Karan Aujla",
        "Arijit Singh"
    ),
    val trendingSearches: List<String> = listOf(
        "Dhurandhar",
        "Aaj Ki Raat",
        "Tauba Tauba",
        "Millionaire",
        "Raanjhan",
        "Bhool Bhulaiyaa 3"
    ),
    val selectedLibraryCategory: String = "Albums",
    val librarySortOrder: LibrarySortOrder = LibrarySortOrder.RECENTLY_ADDED,
    val downloadedTracks: List<DownloadedTrackRecord> = emptyList(),
    val musicFolders: List<MusicFolderInfo> = emptyList(),
    val selectedFolderFilter: String? = null,
    val downloadingSongIds: Set<String> = emptySet(),
    val downloadProgressMap: Map<String, Int> = emptyMap(),
    val activeSaveToFolderSong: Song? = null,
    val isCreateFolderModalOpen: Boolean = false,

    // Account & Persisted Settings State
    val userProfile: UserProfile,
    val persistedSettings: PersistedSettings = PersistedSettings(),
    val isEditProfileModalOpen: Boolean = false,
    val isCreatePlaylistModalOpen: Boolean = false,
    val activeAccountSubModal: String? = null,
    val toastMessage: String? = null
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogUseCase = GetMusicCatalogUseCase()
    private val preferencesRepository = UserPreferencesRepository(
        runCatching { application.applicationContext.userPreferencesDataStore }.getOrNull()
    )
    private val downloadManager = OnyxDownloadManager(application.applicationContext)
    private val audioEngine = SynthesizedAudioEngine(
        scope = viewModelScope,
        context = application.applicationContext,
        offlineFileResolver = { song -> downloadManager.getDownloadedFileForSong(song) }
    )
    private val audioOutputManager = AndroidAudioOutputManager(application.applicationContext)
    private val mediaNotificationHelper = OnyxMediaNotificationHelper(application.applicationContext)
    private var progressJob: Job? = null
    private var toastJob: Job? = null
    private var searchJob: Job? = null

    private val initialDownloadedRecords = downloadManager.getDownloadedTracks()
    private val initialDownloadedSongs = initialDownloadedRecords.map { downloadManager.toSong(it) }
    private val initialSongs = (initialDownloadedSongs + catalogUseCase.getAllSongs())
        .distinctBy { it.audioUrl ?: it.id }
    private val initialAlbums = catalogUseCase.getAllAlbums()
    private val initialPlaylists = catalogUseCase.getAllPlaylists()
    private val initialArtists = catalogUseCase.getAllArtists()
    private val initialStations = catalogUseCase.getRadioStations()
    private val initialRoutes = audioOutputManager.getAvailableOutputRoutes()

    private val _uiState = MutableStateFlow(
        MusicUiState(
            selectedTab = AppTab.HOME,
            earlyAccessAlbums = catalogUseCase.getEarlyAccessAlbums(),
            bestNewSongs = catalogUseCase.getBestNewSongs(),
            newThisWeekAlbums = catalogUseCase.getNewThisWeekAlbums(),
            recentReleases = catalogUseCase.getRecentReleases(),
            updatedPlaylists = catalogUseCase.getUpdatedPlaylists(),
            topPicksForYou = catalogUseCase.getTopPicksForYou(),
            recentlyPlayed = catalogUseCase.getRecentlyPlayed(),
            madeForYou = catalogUseCase.getMadeForYou(),
            topSongs = catalogUseCase.getTopSongs(),
            newReleases = catalogUseCase.getNewReleases(),
            recommendedAlbums = catalogUseCase.getRecommendedAlbums(),
            popularArtists = catalogUseCase.getPopularArtists(),
            artistEssentials = catalogUseCase.getArtistEssentials(),
            popularPlaylists = catalogUseCase.getPopularPlaylists(),
            continueListening = catalogUseCase.getContinueListening(),
            popularSongs = catalogUseCase.getTopSongs(),
            featuredPlaylists = catalogUseCase.getPopularPlaylists(),
            allAlbums = initialAlbums,
            allSongs = initialSongs,
            allPlaylists = initialPlaylists,
            allArtists = initialArtists,
            radioStations = initialStations,
            recentlyPlayedStations = initialStations.take(3),
            currentSong = initialSongs.first(),
            queueSongs = initialSongs,
            selectedAudioOutput = initialRoutes.firstOrNull()?.name ?: "This Android Phone",
            availableAudioOutputs = initialRoutes,
            searchResultAlbums = initialAlbums,
            searchResultSongs = initialSongs,
            downloadedTracks = initialDownloadedRecords,
            musicFolders = downloadManager.getMusicFolders(),
            userProfile = catalogUseCase.getDefaultProfile()
        )
    )
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.settingsFlow.collect { prefs ->
                audioEngine.applyEqualizerPreset(prefs.equalizerPreset)
                _uiState.update { state ->
                    state.copy(
                        persistedSettings = prefs,
                        userProfile = state.userProfile.copy(
                            notificationsNewMusic = prefs.notificationsNewMusic,
                            notificationsFriendActivity = prefs.notificationsFriendActivity,
                            dolbyAtmosEnabled = prefs.spatialAudioEnabled,
                            crossfadeSeconds = prefs.crossfadeSeconds
                        )
                    )
                }
            }
        }
        refreshLiveCatalogFromApi()
    }

    /**
     * Pre-buffers the active catalog tracks and fetches trending 2026 Bollywood, Dhurandhar & Punjabi songs
     * in parallel on startup so the library is packed with instant-play songs.
     */
    private fun refreshLiveCatalogFromApi() {
        audioEngine.preloadCatalogSongs(initialSongs.take(6))
        viewModelScope.launch {
            val apiSongs = OnyxSongApiService.fetchTrending2026Catalog()
            if (apiSongs.isNotEmpty()) {
                audioEngine.preloadCatalogSongs(apiSongs.take(4))
                val apiAlbums = OnyxSongApiService.buildAlbumsFromSongs(apiSongs, "2026 BLOCKBUSTER • LIVE")
                _uiState.update { state ->
                    val mergedSongs = (state.allSongs + apiSongs)
                        .distinctBy { it.audioUrl ?: it.id }
                    val mergedAlbums = (state.allAlbums + apiAlbums)
                        .distinctBy { it.title.lowercase().trim() }
                    state.copy(
                        allSongs = mergedSongs,
                        allAlbums = mergedAlbums,
                        bestNewSongs = (state.bestNewSongs + apiSongs.take(8)).distinctBy { it.audioUrl ?: it.id }.take(16),
                        topSongs = (state.topSongs + apiSongs.take(6)).distinctBy { it.audioUrl ?: it.id }.take(14),
                        popularSongs = (state.popularSongs + apiSongs.take(6)).distinctBy { it.audioUrl ?: it.id }.take(14),
                        queueSongs = if (!state.isPlaying) mergedSongs else state.queueSongs,
                        searchResultSongs = if (state.searchQuery.isBlank() && state.selectedSearchCategory == null) {
                            mergedSongs
                        } else state.searchResultSongs,
                        searchResultAlbums = if (state.searchQuery.isBlank() && state.selectedSearchCategory == null) {
                            mergedAlbums
                        } else state.searchResultAlbums
                    )
                }
            }
        }
    }

    fun dismissSplashOverlay() {
        _uiState.update { it.copy(showSplashOverlay = false) }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                isPopupMenuOpen = false
            )
        }
    }

    fun togglePopupMenu() {
        _uiState.update { it.copy(isPopupMenuOpen = !it.isPopupMenuOpen) }
    }

    fun dismissPopupMenu() {
        _uiState.update { it.copy(isPopupMenuOpen = false) }
    }

    fun playSong(song: Song, customQueue: List<Song>? = null) {
        _uiState.update { state ->
            val updatedQueue = when {
                customQueue != null && customQueue.isNotEmpty() -> customQueue
                state.queueSongs.any { it.id == song.id } -> state.queueSongs
                else -> listOf(song) + state.queueSongs
            }
            val updatedAllSongs = if (state.allSongs.any { it.id == song.id }) {
                state.allSongs
            } else {
                listOf(song) + state.allSongs
            }
            state.copy(
                currentSong = song,
                allSongs = updatedAllSongs,
                queueSongs = updatedQueue,
                isPlaying = true,
                currentPositionSeconds = 0
            )
        }
        audioEngine.playSong(
            song = song,
            onPreparedDurationSec = { realDurationSec ->
                _uiState.update { state ->
                    if (state.currentSong.id == song.id && realDurationSec > 10) {
                        state.copy(currentSong = state.currentSong.copy(durationSeconds = realDurationSec))
                    } else state
                }
            },
            onCompletion = {
                viewModelScope.launch {
                    handleTrackCompletion()
                }
            }
        )
        audioEngine.updateFrequency(song.baseFreqHz)
        mediaNotificationHelper.updatePlaybackNotification(song, true)
        startProgressTicker()

        // Pre-buffer the next song in queue for zero-latency track transitions
        val activeQueue = _uiState.value.queueSongs
        val currentIdx = activeQueue.indexOfFirst { it.id == song.id }
        if (activeQueue.size > 1 && currentIdx >= 0) {
            val nextInQueue = activeQueue[(currentIdx + 1) % activeQueue.size]
            audioEngine.prebufferSong(nextInQueue)
        }
    }

    fun playAlbum(album: Album, shuffle: Boolean = false) {
        val albumSongs = _uiState.value.allSongs.filter {
            it.albumTitle.equals(album.title, ignoreCase = true) ||
                (album.artworkUrl != null && it.artworkUrl == album.artworkUrl) ||
                it.artworkRes == album.artworkRes
        }.ifEmpty { _uiState.value.allSongs }
        val ordered = if (shuffle) albumSongs.shuffled() else albumSongs
        _uiState.update { it.copy(isShuffleEnabled = shuffle) }
        playSong(ordered.first(), ordered)
        showToast(if (shuffle) "Shuffling ${album.title}" else "Playing ${album.title}")
    }

    fun playPlaylist(playlist: Playlist, shuffle: Boolean = false) {
        val playlistSongs = _uiState.value.allSongs.filter { playlist.songIds.contains(it.id) }
            .ifEmpty { _uiState.value.allSongs }
        val ordered = if (shuffle) playlistSongs.shuffled() else playlistSongs
        _uiState.update { it.copy(isShuffleEnabled = shuffle) }
        playSong(ordered.first(), ordered)
        showToast(if (shuffle) "Shuffling ${playlist.title}" else "Playing ${playlist.title}")
    }

    fun playRadioStation(station: RadioStation) {
        val matchingSong = _uiState.value.allSongs.firstOrNull { it.id == station.featuredSongId }
            ?: _uiState.value.allSongs.first()
        _uiState.update { state ->
            val updatedRecent = listOf(station) + state.recentlyPlayedStations.filterNot { it.id == station.id }
            state.copy(recentlyPlayedStations = updatedRecent.take(4))
        }
        playSong(matchingSong)
        showToast("Tuned into ${station.name}")
    }

    fun startStationFromSong(song: Song) {
        val stationQueue = listOf(song) + _uiState.value.allSongs.filterNot { it.id == song.id }.shuffled()
        _uiState.update { it.copy(activeTrackOptionsSong = null) }
        playSong(song, stationQueue)
        showToast("Started '${song.title}' Radio Station")
    }

    fun togglePlayPause() {
        val willPlay = !_uiState.value.isPlaying
        val currentSong = _uiState.value.currentSong
        _uiState.update { it.copy(isPlaying = willPlay) }
        mediaNotificationHelper.updatePlaybackNotification(currentSong, willPlay)
        if (willPlay) {
            audioEngine.resume(
                song = currentSong,
                onPreparedDurationSec = { realDurationSec ->
                    _uiState.update { state ->
                        if (state.currentSong.id == currentSong.id && realDurationSec > 10) {
                            state.copy(currentSong = state.currentSong.copy(durationSeconds = realDurationSec))
                        } else state
                    }
                },
                onCompletion = {
                    viewModelScope.launch {
                        handleTrackCompletion()
                    }
                }
            )
            startProgressTicker()
        } else {
            audioEngine.pause()
            progressJob?.cancel()
        }
    }

    fun skipNext() {
        val queue = _uiState.value.queueSongs.ifEmpty { _uiState.value.allSongs }
        val currentIndex = queue.indexOfFirst { it.id == _uiState.value.currentSong.id }
        val nextSong = if (_uiState.value.isShuffleEnabled && queue.size > 1) {
            queue.filter { it.id != _uiState.value.currentSong.id }.randomOrNull() ?: queue.first()
        } else {
            queue[(currentIndex + 1).coerceAtLeast(0) % queue.size]
        }
        playSong(nextSong)
    }

    fun skipPrevious() {
        if (_uiState.value.currentPositionSeconds > 5) {
            seekTo(0)
            return
        }
        val queue = _uiState.value.queueSongs.ifEmpty { _uiState.value.allSongs }
        val currentIndex = queue.indexOfFirst { it.id == _uiState.value.currentSong.id }
        val prevIndex = if (currentIndex <= 0) queue.lastIndex else currentIndex - 1
        playSong(queue[prevIndex])
    }

    fun seekTo(seconds: Int) {
        val maxSec = _uiState.value.currentSong.durationSeconds
        val targetSec = seconds.coerceIn(0, maxSec)
        _uiState.update { it.copy(currentPositionSeconds = targetSec) }
        audioEngine.seekTo(targetSec)
    }

    fun toggleFavorite(songId: String) {
        _uiState.update { state ->
            val updated = state.likedSongIds.toMutableSet()
            val added = if (updated.contains(songId)) {
                updated.remove(songId)
                false
            } else {
                updated.add(songId)
                true
            }
            state.copy(
                likedSongIds = updated,
                toastMessage = if (added) "Added to Favourite Music" else "Removed from Favourites"
            )
        }
        scheduleToastClear()
    }

    fun toggleSaveSongToLibrary(song: Song) {
        _uiState.update { state ->
            val updated = state.savedSongIds.toMutableSet()
            val added = if (updated.contains(song.id)) {
                updated.remove(song.id)
                false
            } else {
                updated.add(song.id)
                true
            }
            state.copy(
                savedSongIds = updated,
                activeTrackOptionsSong = null,
                toastMessage = if (added) "Added '${song.title}' to Library" else "Removed '${song.title}' from Library"
            )
        }
        scheduleToastClear()
    }

    fun openAddToPlaylistModal(song: Song) {
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                activeAddToPlaylistSong = song
            )
        }
    }

    fun dismissAddToPlaylistModal() {
        _uiState.update { it.copy(activeAddToPlaylistSong = null) }
    }

    fun addSongToPlaylist(song: Song, playlist: Playlist) {
        _uiState.update { state ->
            val updatedPlaylists = state.allPlaylists.map { pl ->
                if (pl.id == playlist.id && !pl.songIds.contains(song.id)) {
                    pl.copy(songIds = pl.songIds + song.id)
                } else pl
            }
            state.copy(
                allPlaylists = updatedPlaylists,
                activeAddToPlaylistSong = null,
                toastMessage = "Added '${song.title}' to ${playlist.title}"
            )
        }
        scheduleToastClear()
    }

    fun openSongCreditsModal(song: Song) {
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                activeCreditsSong = song
            )
        }
    }

    fun dismissSongCreditsModal() {
        _uiState.update { it.copy(activeCreditsSong = null) }
    }

    fun suggestLessLikeThis(song: Song) {
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                toastMessage = "We'll suggest less like '${song.title}'"
            )
        }
        scheduleToastClear()
    }

    fun shareSongLyrics(song: Song) {
        val snippet = song.synchronizedLyrics.firstOrNull()?.text
            ?: "Instrumental master from ${song.albumTitle}"
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                toastMessage = "Shared lyrics: \"$snippet\" — ${song.artist}"
            )
        }
        scheduleToastClear()
    }

    fun openReportConcernModal(song: Song) {
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                activeReportConcernSong = song
            )
        }
    }

    fun dismissReportConcernModal() {
        _uiState.update { it.copy(activeReportConcernSong = null) }
    }

    fun submitReportConcern(song: Song, reason: String) {
        _uiState.update {
            it.copy(
                activeReportConcernSong = null,
                toastMessage = "Concern reported for '${song.title}': $reason"
            )
        }
        scheduleToastClear()
    }

    fun setSleepTimerSheetOpen(open: Boolean) {
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                isSleepTimerSheetOpen = open
            )
        }
    }

    fun setSleepTimer(minutes: Int?) {
        _uiState.update {
            it.copy(
                sleepTimerMinutes = minutes,
                isSleepTimerSheetOpen = false,
                toastMessage = if (minutes != null) "Sleep timer set for $minutes minutes" else "Sleep timer turned off"
            )
        }
        scheduleToastClear()
    }

    fun toggleSavePlaylist(playlistId: String) {
        _uiState.update { state ->
            val updated = state.savedPlaylistIds.toMutableSet()
            val added = if (updated.contains(playlistId)) {
                updated.remove(playlistId)
                false
            } else {
                updated.add(playlistId)
                true
            }
            state.copy(
                savedPlaylistIds = updated,
                toastMessage = if (added) "Added Playlist to Library" else "Removed from Library"
            )
        }
        scheduleToastClear()
    }

    fun toggleSaveAlbum(albumId: String) {
        _uiState.update { state ->
            val updated = state.savedAlbumIds.toMutableSet()
            val added = if (updated.contains(albumId)) {
                updated.remove(albumId)
                false
            } else {
                updated.add(albumId)
                true
            }
            state.copy(
                savedAlbumIds = updated,
                toastMessage = if (added) "Added Album to Library" else "Removed from Library"
            )
        }
        scheduleToastClear()
    }

    fun toggleShuffle() {
        val nextShuffle = !_uiState.value.isShuffleEnabled
        _uiState.update { state ->
            val updatedQueue = if (nextShuffle) {
                val current = state.currentSong
                listOf(current) + state.queueSongs.filter { it.id != current.id }.shuffled()
            } else {
                state.allSongs
            }
            state.copy(
                isShuffleEnabled = nextShuffle,
                queueSongs = updatedQueue
            )
        }
        showToast(if (nextShuffle) "Shuffle On" else "Shuffle Off")
    }

    fun cycleRepeatMode() {
        val nextMode = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _uiState.update { it.copy(repeatMode = nextMode) }
        val label = when (nextMode) {
            RepeatMode.OFF -> "Repeat Off"
            RepeatMode.ALL -> "Repeat All"
            RepeatMode.ONE -> "Repeat One"
        }
        showToast(label)
    }

    fun toggleMute() {
        val nextMuted = !_uiState.value.isMuted
        audioEngine.setMuted(nextMuted)
        _uiState.update { it.copy(isMuted = nextMuted) }
    }

    // Queue Management Actions
    fun moveQueueItemUp(songId: String) {
        _uiState.update { state ->
            val list = state.queueSongs.toMutableList()
            val idx = list.indexOfFirst { it.id == songId }
            if (idx > 0) {
                val item = list.removeAt(idx)
                list.add(idx - 1, item)
            }
            state.copy(queueSongs = list)
        }
    }

    fun moveQueueItemDown(songId: String) {
        _uiState.update { state ->
            val list = state.queueSongs.toMutableList()
            val idx = list.indexOfFirst { it.id == songId }
            if (idx in 0 until list.lastIndex) {
                val item = list.removeAt(idx)
                list.add(idx + 1, item)
            }
            state.copy(queueSongs = list)
        }
    }

    fun removeFromQueue(songId: String) {
        _uiState.update { state ->
            val updated = state.queueSongs.filterNot { it.id == songId }
            state.copy(
                queueSongs = updated.ifEmpty { listOf(state.currentSong) },
                toastMessage = "Removed from queue"
            )
        }
        scheduleToastClear()
    }

    fun clearQueue() {
        _uiState.update { state ->
            state.copy(
                queueSongs = listOf(state.currentSong),
                toastMessage = "Upcoming queue cleared"
            )
        }
        scheduleToastClear()
    }

    fun shuffleQueue() {
        _uiState.update { state ->
            val current = state.currentSong
            val rest = state.queueSongs.filter { it.id != current.id }.ifEmpty {
                state.allSongs.filter { it.id != current.id }
            }.shuffled()
            state.copy(
                isShuffleEnabled = true,
                queueSongs = listOf(current) + rest,
                toastMessage = "Queue shuffled"
            )
        }
        scheduleToastClear()
    }

    fun playNextInQueue(song: Song) {
        _uiState.update { state ->
            val current = state.currentSong
            val list = state.queueSongs.filterNot { it.id == song.id }.toMutableList()
            val curIdx = list.indexOfFirst { it.id == current.id }.coerceAtLeast(0)
            list.add((curIdx + 1).coerceAtMost(list.size), song)
            state.copy(
                queueSongs = list,
                activeTrackOptionsSong = null,
                toastMessage = "Playing '${song.title}' next"
            )
        }
        scheduleToastClear()
    }

    fun addToQueue(song: Song) {
        _uiState.update { state ->
            val updated = if (state.queueSongs.any { it.id == song.id }) {
                state.queueSongs
            } else {
                state.queueSongs + song
            }
            state.copy(
                queueSongs = updated,
                activeTrackOptionsSong = null,
                toastMessage = "Added '${song.title}' to queue"
            )
        }
        scheduleToastClear()
    }

    fun openTrackOptions(song: Song) {
        _uiState.update { it.copy(activeTrackOptionsSong = song) }
    }

    fun dismissTrackOptions() {
        _uiState.update { it.copy(activeTrackOptionsSong = null) }
    }

    fun setAudioOutputSheetOpen(open: Boolean) {
        val refreshedRoutes = if (open) audioOutputManager.getAvailableOutputRoutes() else _uiState.value.availableAudioOutputs
        _uiState.update {
            it.copy(
                isAudioOutputSheetOpen = open,
                availableAudioOutputs = refreshedRoutes
            )
        }
    }

    fun selectAudioOutput(deviceName: String) {
        _uiState.update {
            it.copy(
                selectedAudioOutput = deviceName,
                isAudioOutputSheetOpen = false,
                toastMessage = "Audio output: $deviceName"
            )
        }
        scheduleToastClear()
    }

    fun updateSearchQuery(query: String) {
        val (localAlbums, localSongs) = filterInMemoryCatalog(query, _uiState.value.selectedSearchCategory)
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResultAlbums = localAlbums,
                searchResultSongs = localSongs
            )
        }
        val clean = query.trim()
        if (clean.length >= 2) {
            triggerOnlineSearch(clean, debounceMs = 160L)
        } else {
            searchJob?.cancel()
            _uiState.update { it.copy(isSearchingOnline = false) }
        }
    }

    fun submitSearchTerm(term: String) {
        val clean = term.trim()
        if (clean.isEmpty()) return
        val (localAlbums, localSongs) = filterInMemoryCatalog(clean, _uiState.value.selectedSearchCategory)
        _uiState.update { state ->
            val updatedRecent = listOf(clean) + state.recentSearches.filterNot { it.equals(clean, ignoreCase = true) }
            state.copy(
                searchQuery = clean,
                searchResultAlbums = localAlbums,
                searchResultSongs = localSongs,
                recentSearches = updatedRecent.take(6)
            )
        }
        triggerOnlineSearch(clean, debounceMs = 0L)
    }

    fun clearRecentSearches() {
        _uiState.update {
            it.copy(
                recentSearches = emptyList(),
                toastMessage = "Recent searches cleared"
            )
        }
        scheduleToastClear()
    }

    fun selectSearchCategory(category: String?) {
        val nextCategory = if (_uiState.value.selectedSearchCategory == category) null else category
        val (localAlbums, localSongs) = filterInMemoryCatalog(_uiState.value.searchQuery, nextCategory)
        _uiState.update {
            it.copy(
                selectedSearchCategory = nextCategory,
                searchResultAlbums = localAlbums,
                searchResultSongs = localSongs
            )
        }
        val termToQuery = _uiState.value.searchQuery.trim().ifEmpty { nextCategory.orEmpty() }
        if (termToQuery.isNotBlank()) {
            triggerOnlineSearch(termToQuery, debounceMs = 0L)
        }
    }

    private fun filterInMemoryCatalog(query: String, categoryFilter: String?): Pair<List<Album>, List<Song>> {
        val trimmed = query.trim().lowercase()
        val state = _uiState.value
        val filteredAlbums = state.allAlbums.filter { album ->
            val matchesQuery = trimmed.isEmpty() ||
                album.title.lowercase().contains(trimmed) ||
                album.artist.lowercase().contains(trimmed) ||
                album.categoryTag.lowercase().contains(trimmed)
            val matchesCat = categoryFilter == null ||
                album.categoryTag.contains(categoryFilter, ignoreCase = true) ||
                album.title.contains(categoryFilter, ignoreCase = true)
            matchesQuery && matchesCat
        }.ifEmpty {
            if (categoryFilter != null && trimmed.isEmpty()) state.allAlbums.take(8) else emptyList()
        }
        val filteredSongs = state.allSongs.filter { song ->
            trimmed.isEmpty() ||
                song.title.lowercase().contains(trimmed) ||
                song.artist.lowercase().contains(trimmed) ||
                song.albumTitle.lowercase().contains(trimmed)
        }
        return filteredAlbums to filteredSongs
    }

    private fun triggerOnlineSearch(query: String, debounceMs: Long) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (debounceMs > 0) delay(debounceMs)
            _uiState.update { it.copy(isSearchingOnline = true, currentSearchPage = 1) }
            val liveSongs = OnyxSongApiService.searchSongs(query, resolveArtwork = true, page = 1, pageSize = 40)
            if (liveSongs.isNotEmpty()) {
                val liveAlbums = OnyxSongApiService.buildAlbumsFromSongs(liveSongs, "${query.uppercase()} • LIVE")
                _uiState.update { state ->
                    val mergedAllSongs = (state.allSongs + liveSongs).distinctBy { it.audioUrl ?: it.id }
                    val mergedAllAlbums = (state.allAlbums + liveAlbums).distinctBy { it.title.lowercase().trim() }
                    state.copy(
                        isSearchingOnline = false,
                        currentSearchPage = 1,
                        allSongs = mergedAllSongs,
                        allAlbums = mergedAllAlbums,
                        searchResultSongs = (liveSongs + state.searchResultSongs).distinctBy { it.audioUrl ?: it.id },
                        searchResultAlbums = (liveAlbums + state.searchResultAlbums).distinctBy { it.title.lowercase().trim() }
                    )
                }
            } else {
                _uiState.update { it.copy(isSearchingOnline = false) }
            }
        }
    }

    /**
     * Loads the next page (40 more songs per click) from the live API so the user can browse unlimited songs.
     */
    fun loadMoreSongsFromApi(customQuery: String? = null) {
        if (_uiState.value.isLoadingMoreSongs) return
        val nextPage = _uiState.value.currentSearchPage + 1
        val query = (customQuery ?: _uiState.value.searchQuery.ifBlank {
            _uiState.value.selectedSearchCategory ?: "Bollywood 2025"
        }).trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreSongs = true) }
            val moreSongs = OnyxSongApiService.searchSongs(
                query = query,
                resolveArtwork = false,
                page = nextPage,
                pageSize = 40
            )
            if (moreSongs.isNotEmpty()) {
                val moreAlbums = OnyxSongApiService.buildAlbumsFromSongs(moreSongs, "${query.uppercase()} • PAGE $nextPage")
                _uiState.update { state ->
                    val updatedAllSongs = (state.allSongs + moreSongs).distinctBy { it.audioUrl ?: it.id }
                    val updatedAllAlbums = (state.allAlbums + moreAlbums).distinctBy { it.title.lowercase().trim() }
                    val updatedSearchSongs = (state.searchResultSongs + moreSongs).distinctBy { it.audioUrl ?: it.id }
                    val updatedSearchAlbums = (state.searchResultAlbums + moreAlbums).distinctBy { it.title.lowercase().trim() }
                    state.copy(
                        isLoadingMoreSongs = false,
                        currentSearchPage = nextPage,
                        allSongs = updatedAllSongs,
                        allAlbums = updatedAllAlbums,
                        searchResultSongs = updatedSearchSongs,
                        searchResultAlbums = updatedSearchAlbums,
                        toastMessage = "Loaded ${moreSongs.size} more songs from API (Page $nextPage)"
                    )
                }
                scheduleToastClear()
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingMoreSongs = false,
                        toastMessage = "All available API songs for '$query' loaded"
                    )
                }
                scheduleToastClear()
            }
        }
    }

    /**
     * Fetches full album tracklist from the API when opening an album detail view.
     */
    fun ensureAlbumTracksLoaded(album: Album) {
        viewModelScope.launch {
            val existingCount = _uiState.value.allSongs.count {
                it.albumTitle.equals(album.title, ignoreCase = true)
            }
            if (existingCount >= 6) return@launch
            val apiTracks = OnyxSongApiService.fetchAlbumTracksFromApi(album.title, album.artist)
            if (apiTracks.isNotEmpty()) {
                _uiState.update { state ->
                    val merged = (state.allSongs + apiTracks).distinctBy { it.audioUrl ?: it.id }
                    state.copy(allSongs = merged)
                }
            }
        }
    }

    // =========================================================================
    // DOWNLOAD SYSTEM & LOCAL FOLDER MANAGEMENT ACTIONS
    // =========================================================================

    fun downloadSong(song: Song, folderName: String? = null) {
        if (_uiState.value.downloadingSongIds.contains(song.id)) return
        val targetFolder = folderName?.takeIf { it.isNotBlank() }
            ?: _uiState.value.selectedFolderFilter
            ?: downloadManager.suggestFolderForSong(song)

        _uiState.update { state ->
            state.copy(
                activeTrackOptionsSong = null,
                activeSaveToFolderSong = null,
                downloadingSongIds = state.downloadingSongIds + song.id,
                downloadProgressMap = state.downloadProgressMap + (song.id to 5),
                toastMessage = "Downloading '${song.title}' (320kbps AAC) to '$targetFolder'..."
            )
        }
        scheduleToastClear()

        viewModelScope.launch {
            val result = downloadManager.downloadSongToFolder(
                song = song,
                folderName = targetFolder,
                onProgress = { pct ->
                    _uiState.update { state ->
                        state.copy(downloadProgressMap = state.downloadProgressMap + (song.id to pct))
                    }
                }
            )
            result.onSuccess { record ->
                _uiState.update { state ->
                    state.copy(
                        downloadingSongIds = state.downloadingSongIds - song.id,
                        downloadProgressMap = state.downloadProgressMap - song.id,
                        savedSongIds = state.savedSongIds + song.id,
                        downloadedTracks = downloadManager.getDownloadedTracks(),
                        musicFolders = downloadManager.getMusicFolders(),
                        toastMessage = "Downloaded '${song.title}' (${record.fileSizeFormatted}) in folder '${record.folderName}'"
                    )
                }
                scheduleToastClear()
            }.onFailure { err ->
                _uiState.update { state ->
                    state.copy(
                        downloadingSongIds = state.downloadingSongIds - song.id,
                        downloadProgressMap = state.downloadProgressMap - song.id,
                        toastMessage = "Download failed: ${err.message ?: "Network error"}"
                    )
                }
                scheduleToastClear()
            }
        }
    }

    fun deleteDownloadedSong(songId: String) {
        val removed = downloadManager.deleteDownloadedSong(songId)
        if (removed) {
            _uiState.update { state ->
                state.copy(
                    downloadedTracks = downloadManager.getDownloadedTracks(),
                    musicFolders = downloadManager.getMusicFolders(),
                    toastMessage = "Removed offline download from folder"
                )
            }
            scheduleToastClear()
        }
    }

    fun createMusicFolder(folderName: String) {
        val created = downloadManager.createFolder(folderName)
        if (created != null) {
            _uiState.update { state ->
                state.copy(
                    musicFolders = downloadManager.getMusicFolders(),
                    selectedFolderFilter = created.name,
                    isCreateFolderModalOpen = false,
                    toastMessage = "Created folder '${created.name}'"
                )
            }
            scheduleToastClear()
        }
    }

    fun moveSongToFolder(song: Song, targetFolderName: String) {
        if (downloadManager.isSongDownloaded(song.id)) {
            val moved = downloadManager.moveSongToFolder(song.id, targetFolderName)
            if (moved != null) {
                _uiState.update { state ->
                    state.copy(
                        activeTrackOptionsSong = null,
                        activeSaveToFolderSong = null,
                        downloadedTracks = downloadManager.getDownloadedTracks(),
                        musicFolders = downloadManager.getMusicFolders(),
                        toastMessage = "Moved '${song.title}' to folder '${moved.folderName}'"
                    )
                }
                scheduleToastClear()
                return
            }
        }
        // If not yet downloaded, download directly into targetFolderName
        downloadSong(song, targetFolderName)
    }

    fun selectMusicFolder(folderName: String?) {
        _uiState.update { state ->
            val next = if (state.selectedFolderFilter == folderName) null else folderName
            state.copy(selectedFolderFilter = next)
        }
    }

    fun openSaveToFolderModal(song: Song) {
        _uiState.update {
            it.copy(
                activeTrackOptionsSong = null,
                activeSaveToFolderSong = song
            )
        }
    }

    fun dismissSaveToFolderModal() {
        _uiState.update { it.copy(activeSaveToFolderSong = null) }
    }

    fun setCreateFolderModalOpen(open: Boolean) {
        _uiState.update { it.copy(isCreateFolderModalOpen = open) }
    }

    fun selectLibraryCategory(category: String) {
        _uiState.update { it.copy(selectedLibraryCategory = category) }
    }

    fun setLibrarySortOrder(order: LibrarySortOrder) {
        _uiState.update {
            it.copy(
                librarySortOrder = order,
                toastMessage = "Sorted by ${order.label}"
            )
        }
        scheduleToastClear()
    }

    fun setCreatePlaylistModalOpen(open: Boolean) {
        _uiState.update { it.copy(isCreatePlaylistModalOpen = open) }
    }

    fun createCustomPlaylist(title: String) {
        val cleanTitle = title.trim().ifEmpty { "My Onyx Mix" }
        val newId = "pl_custom_${System.currentTimeMillis()}"
        val sampleArtwork = _uiState.value.allAlbums.firstOrNull()?.artworkRes ?: 0
        val newPlaylist = Playlist(
            id = newId,
            title = cleanTitle,
            curator = _uiState.value.userProfile.displayName,
            updatedLabel = "Updated Just Now",
            description = "Custom personal playlist created in Onyx Music.",
            artworkRes = sampleArtwork,
            primaryColor = com.example.ui.theme.ElectricBlue,
            secondaryColor = com.example.ui.theme.AmbientViolet,
            songIds = _uiState.value.allSongs.take(5).map { it.id }
        )
        _uiState.update { state ->
            state.copy(
                allPlaylists = listOf(newPlaylist) + state.allPlaylists,
                savedPlaylistIds = state.savedPlaylistIds + newId,
                selectedLibraryCategory = "Playlists",
                isCreatePlaylistModalOpen = false,
                toastMessage = "Created playlist '$cleanTitle'"
            )
        }
        scheduleToastClear()
    }

    // Account & Profile Actions
    fun setEditProfileModalOpen(open: Boolean) {
        _uiState.update { it.copy(isEditProfileModalOpen = open) }
    }

    fun saveProfileEdits(displayName: String, handle: String) {
        val cleanName = displayName.trim().ifEmpty { "Alex Rivera" }
        val cleanHandle = handle.trim().let { if (it.startsWith("@")) it else "@$it" }
        _uiState.update { state ->
            state.copy(
                userProfile = state.userProfile.copy(
                    displayName = cleanName,
                    handle = cleanHandle,
                    isProfileSetUp = true
                ),
                isEditProfileModalOpen = false
            )
        }
        showToast("Profile updated")
    }

    fun completeProfileSetup() {
        _uiState.update { state ->
            state.copy(
                userProfile = state.userProfile.copy(isProfileSetUp = !state.userProfile.isProfileSetUp)
            )
        }
        val isSetUp = _uiState.value.userProfile.isProfileSetUp
        showToast(if (isSetUp) "Profile sharing enabled with friends" else "Profile set to private")
    }

    fun openAccountSubModal(modalType: String?) {
        _uiState.update { it.copy(activeAccountSubModal = modalType) }
    }

    fun upgradeToFamilyPlan() {
        _uiState.update { state ->
            val nextFamily = !state.userProfile.isFamilyPlan
            state.copy(
                userProfile = state.userProfile.copy(
                    isFamilyPlan = nextFamily,
                    currentPlan = if (nextFamily) "Family Plan • $16.99/mo" else "Individual Plan • $10.99/mo"
                ),
                activeAccountSubModal = null
            )
        }
        showToast(
            if (_uiState.value.userProfile.isFamilyPlan) "Upgraded to Onyx Music Family Plan"
            else "Switched to Individual Plan"
        )
    }

    fun updateCountryRegion(country: String) {
        _uiState.update { state ->
            state.copy(
                userProfile = state.userProfile.copy(countryRegion = country),
                activeAccountSubModal = null
            )
        }
        showToast("Country/Region updated to $country")
    }

    fun redeemPromoCode(code: String) {
        _uiState.update { it.copy(activeAccountSubModal = null) }
        if (code.isNotBlank()) {
            showToast("Code '${code.uppercase()}' redeemed: 3 Months Spatial Audio")
        }
    }

    fun signOutAccount() {
        _uiState.update { state ->
            state.copy(
                userProfile = state.userProfile.copy(isProfileSetUp = false),
                activeAccountSubModal = null
            )
        }
        showToast("Signed out of cloud profile session")
    }

    // DataStore Persisted Settings Actions
    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(themeMode = mode)) }
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
        showToast("Theme set to ${mode.label}")
    }

    fun setAddDeletePlaylistSongs(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(addDeletePlaylistSongs = enabled)) }
        viewModelScope.launch { preferencesRepository.setAddDeletePlaylistSongs(enabled) }
    }

    fun setAddDeleteFavouriteSongs(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(addDeleteFavouriteSongs = enabled)) }
        viewModelScope.launch { preferencesRepository.setAddDeleteFavouriteSongs(enabled) }
    }

    fun setSpatialAudio(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(spatialAudioEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setSpatialAudio(enabled) }
    }

    fun setStreamingQuality(quality: String) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(streamingQuality = quality)) }
        viewModelScope.launch { preferencesRepository.setStreamingQuality(quality) }
    }

    fun setDownloadQuality(quality: String) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(downloadQuality = quality)) }
        viewModelScope.launch { preferencesRepository.setDownloadQuality(quality) }
    }

    fun setEqualizerPreset(preset: String) {
        audioEngine.applyEqualizerPreset(preset)
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(equalizerPreset = preset)) }
        viewModelScope.launch { preferencesRepository.setEqualizerPreset(preset) }
        showToast("DSP Equalizer: $preset")
    }

    fun setCrossfadeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(crossfadeEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setCrossfadeEnabled(enabled) }
    }

    fun updateCrossfadeSeconds(seconds: Int) {
        val clamped = seconds.coerceIn(1, 12)
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(crossfadeSeconds = clamped)) }
        viewModelScope.launch { preferencesRepository.setCrossfadeSeconds(clamped) }
    }

    fun setSoundCheck(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(soundCheckEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setSoundCheck(enabled) }
    }

    fun setAutoPlay(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(autoPlayEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setAutoPlay(enabled) }
    }

    fun setWifiOnlyDownloads(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(wifiOnlyDownloads = enabled)) }
        viewModelScope.launch { preferencesRepository.setWifiOnlyDownloads(enabled) }
    }

    fun setCellularStreaming(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(cellularStreamingEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setCellularStreaming(enabled) }
    }

    fun setLargerText(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(largerTextEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setLargerText(enabled) }
    }

    fun setIncreaseContrast(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(increaseContrastEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setIncreaseContrast(enabled) }
    }

    fun setDiagnostics(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(diagnosticsEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setDiagnostics(enabled) }
    }

    fun setListeningHistory(enabled: Boolean) {
        _uiState.update { it.copy(persistedSettings = it.persistedSettings.copy(listeningHistoryEnabled = enabled)) }
        viewModelScope.launch { preferencesRepository.setListeningHistory(enabled) }
    }

    fun toggleNotificationsNewMusic() {
        val next = !_uiState.value.persistedSettings.notificationsNewMusic
        setNotificationsNewMusic(next)
    }

    fun setNotificationsNewMusic(enabled: Boolean) {
        _uiState.update {
            it.copy(
                persistedSettings = it.persistedSettings.copy(notificationsNewMusic = enabled),
                userProfile = it.userProfile.copy(notificationsNewMusic = enabled)
            )
        }
        viewModelScope.launch { preferencesRepository.setNotificationsNewMusic(enabled) }
    }

    fun toggleNotificationsFriendActivity() {
        val next = !_uiState.value.persistedSettings.notificationsFriendActivity
        setNotificationsFriendActivity(next)
    }

    fun setNotificationsFriendActivity(enabled: Boolean) {
        _uiState.update {
            it.copy(
                persistedSettings = it.persistedSettings.copy(notificationsFriendActivity = enabled),
                userProfile = it.userProfile.copy(notificationsFriendActivity = enabled)
            )
        }
        viewModelScope.launch { preferencesRepository.setNotificationsFriendActivity(enabled) }
    }

    fun clearCache() {
        val freedMb = audioEngine.clearDiskCache()
        _uiState.update {
            it.copy(
                persistedSettings = it.persistedSettings.copy(cacheSizeMb = 0),
                toastMessage = if (freedMb > 0) "Cleared $freedMb MB audio preload cache" else "Audio cache cleared"
            )
        }
        scheduleToastClear()
        viewModelScope.launch { preferencesRepository.clearCache() }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
        scheduleToastClear()
    }

    private fun scheduleToastClear() {
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(2500)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    private fun handleTrackCompletion() {
        when (_uiState.value.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0)
                audioEngine.resume(_uiState.value.currentSong)
            }
            RepeatMode.ALL -> skipNext()
            RepeatMode.OFF -> {
                val queue = _uiState.value.queueSongs
                val idx = queue.indexOfFirst { it.id == _uiState.value.currentSong.id }
                if (idx in 0 until queue.lastIndex) {
                    skipNext()
                } else {
                    _uiState.update { it.copy(isPlaying = false, currentPositionSeconds = 0) }
                    audioEngine.stop()
                    mediaNotificationHelper.updatePlaybackNotification(_uiState.value.currentSong, false)
                }
            }
        }
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && _uiState.value.isPlaying) {
                delay(1000)
                if (!_uiState.value.isPlaying) break
                val duration = _uiState.value.currentSong.durationSeconds.coerceAtLeast(1)
                val prevSec = _uiState.value.currentPositionSeconds
                val streamSec = audioEngine.getCurrentPositionSeconds()
                // Use hardware MediaPlayer position when advancing, or smoothly increment by 1s
                val nextSec = if (streamSec != null && streamSec >= prevSec) {
                    streamSec
                } else {
                    prevSec + 1
                }
                if (nextSec >= duration) {
                    handleTrackCompletion()
                } else {
                    _uiState.update { it.copy(currentPositionSeconds = nextSec.coerceIn(0, duration)) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
        mediaNotificationHelper.cancelNotification()
    }
}
