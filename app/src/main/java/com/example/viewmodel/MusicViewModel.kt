package com.example.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiMusicAiService
import com.example.data.audio.AndroidAudioOutputManager
import com.example.data.audio.OnyxMediaNotificationHelper
import com.example.data.audio.OnyxPlaybackService
import com.example.data.audio.SynthesizedAudioEngine
import com.example.data.download.DownloadedTrackRecord
import com.example.data.download.MusicFolderInfo
import com.example.data.download.OnyxDownloadManager
import com.example.data.firebase.OnyxFirebaseRepository
import com.example.data.preferences.PersistedSettings
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.userPreferencesDataStore
import com.example.data.remote.LiveTvService
import com.example.data.remote.OnyxSongApiService
import com.example.data.video.LiveStreamPlayerEngine
import com.example.domain.model.Album
import com.example.domain.model.AppTab
import com.example.domain.model.ArtistProfile
import com.example.domain.model.AudioOutputRoute
import com.example.domain.model.LibrarySortOrder
import com.example.domain.model.LiveChannel
import com.example.domain.model.Playlist
import com.example.domain.model.RadioStation
import com.example.domain.model.RepeatMode
import com.example.domain.model.Song
import com.example.domain.model.UserProfile
import com.example.domain.usecase.GetMusicCatalogUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    val decadeBlockbusters: List<Song> = emptyList(),
    val decadeAlbums: List<Album> = emptyList(),

    // Full Catalog
    val allAlbums: List<Album> = emptyList(),
    val allSongs: List<Song> = emptyList(),
    val allPlaylists: List<Playlist> = emptyList(),
    val allArtists: List<ArtistProfile> = emptyList(),
    val radioStations: List<RadioStation> = emptyList(),
    val recentlyPlayedStations: List<RadioStation> = emptyList(),

    // Live TV Streaming State (ExoPlayer & Freelivtv M3U API)
    val liveChannels: List<LiveChannel> = emptyList(),
    val filteredLiveChannels: List<LiveChannel> = emptyList(),
    val liveCategories: List<String> = listOf("All", "Music", "Entertainment", "Movies", "Sports", "News", "Punjabi"),
    val selectedLiveCategory: String = "All",
    val liveSearchQuery: String = "",
    val isFetchingLiveChannels: Boolean = false,
    val activeLiveChannel: LiveChannel? = null,
    val isLiveFullscreen: Boolean = false,

    // Moods & Gemini AI Smart Suggestions State
    val availableMoods: List<String> = listOf(
        "Mass & Action",
        "Sad & Heartbreak",
        "Late Night Romance",
        "Workout & Gym",
        "Long Drive Punjabi",
        "Desi Party",
        "Soulful Lo-Fi",
        "Deep Focus"
    ),
    val selectedMood: String = "Mass & Action",
    val aiMoodCuratorNote: String = "High-adrenaline theater anthems, explosive rap verses, and heavy basslines curated by Gemini AI.",
    val aiMoodTargetBpm: Int = 134,
    val isGeneratingMoodMix: Boolean = false,
    val aiSuggestedSongs: List<Song> = emptyList(),

    // Gemini AI Accurate Lyrics & Bilingual Translation State
    val isGeneratingAiLyrics: Boolean = false,
    val showLyricsTranslation: Boolean = true,
    val aiLyricsMeaningMap: Map<String, String> = emptyMap(),
    val aiLyricsMoodTagMap: Map<String, String> = emptyMap(),

    // Firebase Auth & Cloud Firestore Sync State
    val firebaseUserUid: String? = null,
    val firebaseUserEmail: String? = null,
    val firebaseUserDisplayName: String? = null,
    val cloudSyncedItemsCount: Int = 0,
    val isCloudSyncing: Boolean = false,

    // Playback, Queue, Headphone/Earbuds, Background Engine & DSP Sound Quality State
    val currentSong: Song,
    val queueSongs: List<Song> = emptyList(),
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Int = 0,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val isMuted: Boolean = false,
    val sleepTimerMinutes: Int? = null,
    val likedSongIds: Set<String> = emptySet(),
    val savedSongIds: Set<String> = emptySet(),
    val savedPlaylistIds: Set<String> = setOf("pl_midnight_drive", "pl_velvet_lounge", "pl_deep_focus"),
    val savedAlbumIds: Set<String> = setOf("alb_neon_horizons", "alb_velvet_echoes", "alb_solar_flare"),
    val selectedAudioOutput: String = "This Android Phone",
    val availableAudioOutputs: List<AudioOutputRoute> = emptyList(),
    val hardwareVolumePercent: Int = 75,
    val earbudNoiseControlMode: String = "Adaptive ANC",
    val autoPauseOnEarbudDisconnect: Boolean = true,
    val isBackgroundPlaybackEnabled: Boolean = true,
    val bassBoostPercent: Int = 65,
    val loudnessBoostPercent: Int = 50,
    val spatialSurroundPercent: Int = 70,
    val playbackSpeed: Float = 1.0f,
    val isAudioOutputSheetOpen: Boolean = false,
    val isSleepTimerSheetOpen: Boolean = false,
    val activeTrackOptionsSong: Song? = null,
    val activeCreditsSong: Song? = null,
    val activeAddToPlaylistSong: Song? = null,
    val activeReportConcernSong: Song? = null,

    // Offline Mode & Search / Pagination / Download / Folder System State
    val isOfflineMode: Boolean = false,
    val offlineOnlyFilterEnabled: Boolean = false,
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
    private val firebaseRepository: OnyxFirebaseRepository? by lazy {
        runCatching { OnyxFirebaseRepository.getInstance(application.applicationContext) }.getOrNull()
    }

    private var progressJob: Job? = null
    private var toastJob: Job? = null
    private var searchJob: Job? = null
    private var cloudSyncJob: Job? = null
    private var moodJob: Job? = null

    private val initialDownloadedRecords = downloadManager.getDownloadedTracks()
    private val initialDownloadedSongs = initialDownloadedRecords.map { downloadManager.toSong(it) }
    private val initialSongs = (initialDownloadedSongs + catalogUseCase.getAllSongs())
        .distinctBy { it.audioUrl ?: it.id }
    private val initialAlbums = catalogUseCase.getAllAlbums()
    private val initialPlaylists = catalogUseCase.getAllPlaylists()
    private val initialArtists = catalogUseCase.getAllArtists()
    private val initialStations = catalogUseCase.getRadioStations()
    private val initialRoutes = audioOutputManager.getAvailableOutputRoutes()
    val livePlayerEngine = LiveStreamPlayerEngine(application.applicationContext)
    private val initialLiveChannels = LiveTvService.getFallbackChannels()

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
            decadeBlockbusters = catalogUseCase.getDecadeBlockbusters(),
            decadeAlbums = catalogUseCase.getDecadeAlbums(),
            allAlbums = initialAlbums,
            allSongs = initialSongs,
            allPlaylists = initialPlaylists,
            allArtists = initialArtists,
            radioStations = initialStations,
            recentlyPlayedStations = initialStations.take(3),
            liveChannels = initialLiveChannels,
            filteredLiveChannels = initialLiveChannels,
            aiSuggestedSongs = initialSongs.take(8),
            currentSong = initialSongs.first(),
            queueSongs = initialSongs,
            selectedAudioOutput = initialRoutes.firstOrNull()?.name ?: "This Android Phone",
            availableAudioOutputs = initialRoutes,
            hardwareVolumePercent = audioOutputManager.getSystemVolumePercent(),
            searchResultAlbums = initialAlbums,
            searchResultSongs = initialSongs,
            downloadedTracks = initialDownloadedRecords,
            musicFolders = downloadManager.getMusicFolders(),
            userProfile = catalogUseCase.getDefaultProfile()
        )
    )
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        // Restore user's custom username & profile choice from local SharedPreferences
        runCatching {
            val prefs = getApplication<Application>().getSharedPreferences("onyx_user_profile_prefs", Context.MODE_PRIVATE)
            val savedName = prefs.getString("display_name", null)
            val savedHandle = prefs.getString("username_handle", null)
            val savedEmail = prefs.getString("user_email", null)
            val savedBgEnabled = prefs.getBoolean("bg_playback_enabled", true)
            if (!savedName.isNullOrBlank() || !savedHandle.isNullOrBlank()) {
                _uiState.update { state ->
                    state.copy(
                        isBackgroundPlaybackEnabled = savedBgEnabled,
                        userProfile = state.userProfile.copy(
                            displayName = savedName ?: state.userProfile.displayName,
                            handle = savedHandle ?: state.userProfile.handle,
                            email = savedEmail ?: state.userProfile.email,
                            isProfileSetUp = true
                        )
                    )
                }
            }
        }

        // Connect Foreground Media Playback Service callbacks
        OnyxPlaybackService.onTogglePlayCallback = { togglePlayPause() }
        OnyxPlaybackService.onNextCallback = { skipNext() }
        OnyxPlaybackService.onPrevCallback = { skipPrevious() }

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

        // Register headphone/earbud disconnect & connect hardware receiver
        audioOutputManager.registerAudioDisconnectListener(
            onEarbudsDisconnected = {
                val routes = audioOutputManager.getAvailableOutputRoutes()
                val shouldAutoPause = _uiState.value.autoPauseOnEarbudDisconnect && _uiState.value.isPlaying
                if (shouldAutoPause) {
                    audioEngine.pause()
                    progressJob?.cancel()
                }
                _uiState.update { state ->
                    state.copy(
                        isPlaying = if (shouldAutoPause) false else state.isPlaying,
                        availableAudioOutputs = routes,
                        selectedAudioOutput = "This Android Phone",
                        toastMessage = if (shouldAutoPause) {
                            "Earbuds disconnected — playback auto-paused"
                        } else {
                            "Switched output to Phone Speaker"
                        }
                    )
                }
                scheduleToastClear()
            },
            onHeadsetConnected = { deviceName ->
                val routes = audioOutputManager.getAvailableOutputRoutes()
                _uiState.update { state ->
                    state.copy(
                        availableAudioOutputs = routes,
                        selectedAudioOutput = deviceName,
                        toastMessage = "Connected: $deviceName • Spatial Audio Ready"
                    )
                }
                scheduleToastClear()
            }
        )

        // Observe Firebase Auth state & only attach Firestore listener when signed in with Google
        viewModelScope.launch {
            val repo = firebaseRepository ?: return@launch
            repo.observeAuthState().collect { user ->
                cloudSyncJob?.cancel()
                if (user != null) {
                    _uiState.update { state ->
                        state.copy(
                            firebaseUserUid = user.uid,
                            firebaseUserEmail = user.email,
                            firebaseUserDisplayName = user.displayName ?: state.userProfile.displayName,
                            userProfile = state.userProfile.copy(
                                displayName = user.displayName ?: state.userProfile.displayName,
                                email = user.email ?: state.userProfile.email,
                                isProfileSetUp = true
                            )
                        )
                    }
                    cloudSyncJob = viewModelScope.launch {
                        repo.observeCloudItems(user.uid).collect { cloudItems ->
                            val cloudFavIds = cloudItems
                                .filter { it.category == "favorite" }
                                .map { it.songId }
                                .toSet()
                            _uiState.update { state ->
                                state.copy(
                                    likedSongIds = state.likedSongIds + cloudFavIds,
                                    cloudSyncedItemsCount = cloudItems.size
                                )
                            }
                        }
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            firebaseUserUid = null,
                            firebaseUserEmail = null,
                            firebaseUserDisplayName = null,
                            cloudSyncedItemsCount = 0
                        )
                    }
                }
            }
        }

        refreshLiveCatalogFromApi()
        loadLiveChannels()
    }

    private fun checkNetworkAvailable(): Boolean {
        return runCatching {
            val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return true
            val net = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(net) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }.getOrDefault(true)
    }

    /**
     * Fetches trending 2026 Bollywood, Dhurandhar & Punjabi songs in parallel on startup
     * and populates inbuilt playlists with 35–50+ real API songs.
     */
    fun refreshLiveCatalogFromApi() {
        val online = checkNetworkAvailable()
        _uiState.update { it.copy(isOfflineMode = !online) }
        audioEngine.preloadCatalogSongs(initialSongs.take(3))
        viewModelScope.launch {
            val apiSongs = OnyxSongApiService.fetchTrending2026Catalog()
            if (apiSongs.isNotEmpty()) {
                audioEngine.preloadCatalogSongs(apiSongs.take(2))
                val apiAlbums = OnyxSongApiService.buildAlbumsFromSongs(apiSongs, "2026 BLOCKBUSTER • LIVE")
                _uiState.update { state ->
                    val mergedSongs = (state.allSongs + apiSongs)
                        .distinctBy { it.audioUrl ?: it.id }
                    val mergedAlbums = (state.allAlbums + apiAlbums)
                        .distinctBy { it.title.lowercase().trim() }

                    // Enrich inbuilt playlists so each playlist has 30-50+ live API songs
                    val enrichedPlaylists = state.allPlaylists.map { pl ->
                        val extraIds = when (pl.id) {
                            "pl_midnight_drive" -> mergedSongs.filter { s ->
                                val t = "${s.title} ${s.albumTitle} ${s.artist}".lowercase()
                                t.contains("dhurandhar") || t.contains("shashwat") || t.contains("raid") ||
                                    t.contains("pushpa") || t.contains("stree") || t.contains("animal")
                            }.map { it.id }
                            "pl_velvet_lounge" -> mergedSongs.filter { s ->
                                val t = "${s.title} ${s.albumTitle} ${s.artist}".lowercase()
                                t.contains("arijit") || t.contains("vishal") || t.contains("saiyaara") ||
                                    t.contains("do patti") || t.contains("ishq") || t.contains("sajni") || t.contains("kesariya")
                            }.map { it.id }
                            "pl_deep_focus" -> mergedSongs.filter { s ->
                                val t = "${s.title} ${s.albumTitle} ${s.artist}".lowercase()
                                t.contains("aujla") || t.contains("diljit") || t.contains("honey singh") ||
                                    t.contains("punjabi") || t.contains("tauba") || t.contains("glory")
                            }.map { it.id }
                            "pl_new_wave" -> mergedSongs.filter { s ->
                                val t = "${s.title} ${s.albumTitle} ${s.artist}".lowercase()
                                t.contains("stree") || t.contains("bhool") || t.contains("aaj ki raat") ||
                                    t.contains("sachin") || t.contains("pritam") || t.contains("tanishk")
                            }.map { it.id }
                            else -> mergedSongs.take(35).map { it.id }
                        }
                        val combinedIds = (pl.songIds + extraIds + mergedSongs.take(35).map { it.id }).distinct().take(50)
                        pl.copy(songIds = combinedIds)
                    }

                    state.copy(
                        isOfflineMode = false,
                        allSongs = mergedSongs,
                        allAlbums = mergedAlbums,
                        allPlaylists = enrichedPlaylists,
                        madeForYou = enrichedPlaylists,
                        popularPlaylists = enrichedPlaylists,
                        featuredPlaylists = enrichedPlaylists,
                        updatedPlaylists = enrichedPlaylists,
                        bestNewSongs = (state.bestNewSongs + apiSongs.take(12)).distinctBy { it.audioUrl ?: it.id }.take(20),
                        topSongs = (state.topSongs + apiSongs.take(10)).distinctBy { it.audioUrl ?: it.id }.take(18),
                        popularSongs = (state.popularSongs + apiSongs.take(10)).distinctBy { it.audioUrl ?: it.id }.take(18),
                        aiSuggestedSongs = (state.aiSuggestedSongs + apiSongs.take(8)).distinctBy { it.audioUrl ?: it.id }.take(14),
                        queueSongs = if (!state.isPlaying) mergedSongs else state.queueSongs,
                        searchResultSongs = if (state.searchQuery.isBlank() && state.selectedSearchCategory == null) {
                            mergedSongs
                        } else state.searchResultSongs,
                        searchResultAlbums = if (state.searchQuery.isBlank() && state.selectedSearchCategory == null) {
                            mergedAlbums
                        } else state.searchResultAlbums
                    )
                }
            } else {
                _uiState.update { it.copy(isOfflineMode = !checkNetworkAvailable()) }
            }
        }
    }

    // =========================================================================
    // GEMINI AI MOODS, SMART SUGGESTIONS & ACCURATE SYNCED LYRICS
    // =========================================================================

    fun selectMood(moodLabel: String, autoPlayMix: Boolean = false) {
        moodJob?.cancel()
        val preset = GeminiMusicAiService.getCuratedMoodPreset(moodLabel, _uiState.value.allSongs)
        val initialMatched = _uiState.value.allSongs.filter { preset.recommendedSongIds.contains(it.id) }
            .ifEmpty { _uiState.value.allSongs.take(10) }

        _uiState.update { state ->
            state.copy(
                selectedMood = moodLabel,
                aiMoodCuratorNote = preset.aiCuratorNote,
                aiMoodTargetBpm = preset.suggestedTempoBpm,
                aiSuggestedSongs = initialMatched,
                isGeneratingMoodMix = true
            )
        }

        if (autoPlayMix && initialMatched.isNotEmpty()) {
            playSong(initialMatched.first(), initialMatched)
        }

        moodJob = viewModelScope.launch {
            val aiResult = GeminiMusicAiService.generateMoodSuggestions(moodLabel, _uiState.value.allSongs)
            val liveMoodSongs = coroutineScope {
                aiResult.recommendedQueries.take(3).map { q ->
                    async { runCatching { OnyxSongApiService.searchSongs(q, resolveArtwork = false, page = 1, pageSize = 15) }.getOrDefault(emptyList()) }
                }.awaitAll().flatten()
            }

            _uiState.update { state ->
                val mergedAll = (state.allSongs + liveMoodSongs).distinctBy { it.audioUrl ?: it.id }
                val moodTracks = (
                    mergedAll.filter { aiResult.recommendedSongIds.contains(it.id) } +
                        liveMoodSongs +
                        initialMatched
                    ).distinctBy { it.audioUrl ?: it.id }.take(24)

                state.copy(
                    allSongs = mergedAll,
                    aiMoodCuratorNote = aiResult.aiCuratorNote,
                    aiMoodTargetBpm = aiResult.suggestedTempoBpm,
                    aiSuggestedSongs = moodTracks.ifEmpty { state.allSongs.take(12) },
                    isGeneratingMoodMix = false
                )
            }

            // Sync selected mood to Cloud Firestore if authenticated with Google
            val uid = _uiState.value.firebaseUserUid
            if (uid != null) {
                runCatching {
                    val safeMoodId = "mood_${moodLabel.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')}"
                    firebaseRepository?.createCloudItem(
                        userId = uid,
                        itemId = safeMoodId,
                        songId = safeMoodId,
                        title = moodLabel,
                        artist = aiResult.aiCuratorNote,
                        category = "mood"
                    )
                }
            }
        }
    }

    fun playAiMoodMix() {
        val mix = _uiState.value.aiSuggestedSongs.ifEmpty { _uiState.value.allSongs }
        if (mix.isNotEmpty()) {
            playSong(mix.first(), mix)
            showToast("Playing Gemini AI Mix: ${_uiState.value.selectedMood}")
        }
    }

    /**
     * Uses Gemini 3.8 Flash (Firebase AI Logic) + LRCLIB to generate/verify accurate time-synced lyrics
     * and bilingual English translations for the currently playing song.
     */
    fun verifyLyricsWithGeminiAi(targetSong: Song = _uiState.value.currentSong) {
        if (_uiState.value.isGeneratingAiLyrics) return
        _uiState.update {
            it.copy(
                isGeneratingAiLyrics = true,
                toastMessage = "Fetching synchronized lyrics for '${targetSong.title}'..."
            )
        }
        scheduleToastClear()

        viewModelScope.launch {
            val lrcLines = OnyxSongApiService.fetchLiveSyncedLyrics(targetSong)
            val aiResult = GeminiMusicAiService.generateAccurateSyncedLyrics(
                song = targetSong,
                existingLyrics = lrcLines
            )

            _uiState.update { state ->
                val updatedSong = state.currentSong.let { cur ->
                    if (cur.id == targetSong.id) cur.copy(synchronizedLyrics = aiResult.lines) else cur
                }
                val updatedAll = state.allSongs.map { s ->
                    if (s.id == targetSong.id) s.copy(synchronizedLyrics = aiResult.lines) else s
                }
                state.copy(
                    isGeneratingAiLyrics = false,
                    currentSong = updatedSong,
                    allSongs = updatedAll,
                    aiLyricsMeaningMap = state.aiLyricsMeaningMap + (targetSong.id to aiResult.meaningSummary),
                    aiLyricsMoodTagMap = state.aiLyricsMoodTagMap + (targetSong.id to aiResult.moodTag),
                    toastMessage = "Synchronized ${aiResult.lines.size} lyrics lines & translation"
                )
            }
            scheduleToastClear()
        }
    }

    fun toggleLyricsTranslation() {
        _uiState.update { state ->
            val next = !state.showLyricsTranslation
            state.copy(
                showLyricsTranslation = next,
                toastMessage = if (next) "Bilingual English translation enabled" else "Showing original lyrics only"
            )
        }
        scheduleToastClear()
    }

    // =========================================================================
    // FIREBASE GOOGLE SIGN-IN & CLOUD FIRESTORE SYNC ACTIONS
    // =========================================================================

    fun signInWithGoogle(activity: Activity) {
        val repo = firebaseRepository
        if (repo == null) {
            showToast("Firebase Cloud service initializing...")
            return
        }
        _uiState.update { it.copy(isCloudSyncing = true) }
        viewModelScope.launch {
            val res = repo.signInWithGoogle(activity)
            res.onSuccess { user ->
                _uiState.update { state ->
                    state.copy(
                        isCloudSyncing = false,
                        firebaseUserUid = user.uid,
                        firebaseUserEmail = user.email,
                        firebaseUserDisplayName = user.displayName ?: state.userProfile.displayName,
                        userProfile = state.userProfile.copy(
                            displayName = user.displayName ?: state.userProfile.displayName,
                            email = user.email ?: state.userProfile.email,
                            isProfileSetUp = true
                        ),
                        toastMessage = "Signed in as ${user.displayName ?: user.email} • Cloud Sync Active"
                    )
                }
                scheduleToastClear()
                syncAllFavoritesToCloud(user.uid)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isCloudSyncing = false,
                        toastMessage = "Google Sign-In: ${err.localizedMessage ?: "Select a Google account on device"}"
                    )
                }
                scheduleToastClear()
            }
        }
    }

    private fun syncAllFavoritesToCloud(userId: String) {
        val repo = firebaseRepository ?: return
        viewModelScope.launch {
            val favSongs = _uiState.value.allSongs.filter { _uiState.value.likedSongIds.contains(it.id) }
            for (song in favSongs.take(10)) {
                val safeId = "fav_${song.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}"
                runCatching {
                    repo.createCloudItem(
                        userId = userId,
                        itemId = safeId,
                        songId = song.id,
                        title = song.title,
                        artist = song.artist,
                        category = "favorite"
                    )
                }
            }
        }
    }

    // =========================================================================
    // HEADPHONE / EARBUDS / BLUETOOTH HARDWARE CONTROLS & OFFLINE MODE
    // =========================================================================

    fun setHardwareVolumePercent(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        audioOutputManager.setSystemVolumePercent(clamped)
        if (clamped > 0 && _uiState.value.isMuted) {
            audioEngine.setMuted(false)
        }
        _uiState.update {
            it.copy(
                hardwareVolumePercent = clamped,
                isMuted = if (clamped > 0) false else it.isMuted
            )
        }
    }

    fun setEarbudNoiseControlMode(mode: String) {
        _uiState.update {
            it.copy(
                earbudNoiseControlMode = mode,
                toastMessage = "Earbuds Mode: $mode"
            )
        }
        scheduleToastClear()
    }

    fun setAutoPauseOnEarbudDisconnect(enabled: Boolean) {
        _uiState.update {
            it.copy(
                autoPauseOnEarbudDisconnect = enabled,
                toastMessage = if (enabled) "Auto-pause on earbud disconnect enabled" else "Auto-pause on disconnect disabled"
            )
        }
        scheduleToastClear()
    }

    fun setBackgroundPlaybackEnabled(enabled: Boolean) {
        runCatching {
            getApplication<Application>()
                .getSharedPreferences("onyx_user_profile_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("bg_playback_enabled", enabled)
                .apply()
        }
        _uiState.update {
            it.copy(
                isBackgroundPlaybackEnabled = enabled,
                toastMessage = if (enabled) "24/7 Background Playback & WakeLock Active" else "Background Playback Paused"
            )
        }
        val state = _uiState.value
        OnyxPlaybackService.syncPlaybackState(
            context = getApplication(),
            title = state.currentSong.title,
            artist = state.currentSong.artist,
            qualityBadge = "320kbps AAC • Dolby Atmos",
            isPlaying = state.isPlaying,
            enabled = enabled
        )
        scheduleToastClear()
    }

    fun setBassBoostPercent(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        audioEngine.setBassBoostPercent(clamped)
        _uiState.update { it.copy(bassBoostPercent = clamped) }
    }

    fun setLoudnessBoostPercent(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        audioEngine.setLoudnessGainPercent(clamped)
        _uiState.update { it.copy(loudnessBoostPercent = clamped) }
    }

    fun setSpatialSurroundPercent(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        audioEngine.setSpatialSurroundPercent(clamped)
        _uiState.update { it.copy(spatialSurroundPercent = clamped) }
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.75f, 1.5f)
        audioEngine.setPlaybackSpeed(clamped)
        _uiState.update {
            it.copy(
                playbackSpeed = clamped,
                toastMessage = when {
                    clamped < 0.95f -> "Playback: ${clamped}x (Slowed + Reverb)"
                    clamped > 1.05f -> "Playback: ${clamped}x (Upbeat Tempo)"
                    else -> "Playback: 1.0x Studio Original"
                }
            )
        }
        scheduleToastClear()
    }

    fun toggleOfflineOnlyFilter() {
        _uiState.update { state ->
            val next = !state.offlineOnlyFilterEnabled
            state.copy(
                offlineOnlyFilterEnabled = next,
                toastMessage = if (next) "Offline Mode: Showing cached & downloaded songs" else "Online Streaming Mode active"
            )
        }
        scheduleToastClear()
    }

    /**
     * Processes natural voice microphone commands (Hindi/Hinglish/English) via AI & Live API.
     */
    fun processVoiceSearchCommand(spokenCommand: String) {
        val raw = spokenCommand.trim()
        if (raw.isEmpty()) return
        val lower = raw.lowercase()
            .replace("play ", "")
            .replace("chala do", "")
            .replace("lagao", "")
            .replace("sunao", "")
            .trim()
            .ifEmpty { raw }

        // Automatically adapt mood feed if voice command mentions a mood
        when {
            lower.contains("sad") || lower.contains("heartbreak") || lower.contains("dard") ->
                selectMood("Sad & Heartbreak", autoPlayMix = true)
            lower.contains("punjabi") || lower.contains("drive") ->
                selectMood("Long Drive Punjabi", autoPlayMix = true)
            lower.contains("party") || lower.contains("dance") || lower.contains("diwali") ->
                selectMood("Desi Party", autoPlayMix = true)
            lower.contains("workout") || lower.contains("gym") ->
                selectMood("Workout & Gym", autoPlayMix = true)
            lower.contains("romantic") || lower.contains("love") ->
                selectMood("Late Night Romance", autoPlayMix = true)
        }

        updateSearchQuery(lower)
        submitSearchTerm(lower)
        viewModelScope.launch {
            val apiSongs = OnyxSongApiService.searchSongs(lower, resolveArtwork = true, page = 1, pageSize = 30)
            if (apiSongs.isNotEmpty()) {
                playSong(apiSongs.first(), apiSongs)
                showToast("Playing: ${apiSongs.first().title}")
            } else {
                showToast("Searching: $lower")
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
        OnyxPlaybackService.syncPlaybackState(
            context = getApplication(),
            title = song.title,
            artist = song.artist,
            qualityBadge = "320kbps AAC • Dolby Atmos",
            isPlaying = true,
            enabled = _uiState.value.isBackgroundPlaybackEnabled
        )
        startProgressTicker()

        // Fetch live synced LRC lyrics in the background if available
        viewModelScope.launch {
            val liveLyrics = OnyxSongApiService.fetchLiveSyncedLyrics(song)
            if (liveLyrics.isNotEmpty()) {
                _uiState.update { state ->
                    val updatedCurrent = if (state.currentSong.id == song.id) {
                        state.currentSong.copy(synchronizedLyrics = liveLyrics)
                    } else state.currentSong
                    state.copy(currentSong = updatedCurrent)
                }
            }
        }

        // Automatically adapt the AI Mood Feed to the song the user is listening to (no manual click needed)
        val autoDetectedMood = OnyxSongApiService.detectSongMoodAutomatic(song)
        if (autoDetectedMood != _uiState.value.selectedMood) {
            selectMood(autoDetectedMood, autoPlayMix = false)
        }

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
        OnyxPlaybackService.syncPlaybackState(
            context = getApplication(),
            title = currentSong.title,
            artist = currentSong.artist,
            qualityBadge = "320kbps AAC • Dolby Atmos",
            isPlaying = willPlay,
            enabled = _uiState.value.isBackgroundPlaybackEnabled
        )
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
        var wasAdded = false
        _uiState.update { state ->
            val updated = state.likedSongIds.toMutableSet()
            wasAdded = if (updated.contains(songId)) {
                updated.remove(songId)
                false
            } else {
                updated.add(songId)
                true
            }
            state.copy(
                likedSongIds = updated,
                toastMessage = if (wasAdded) "Added to Favourite Music" else "Removed from Favourites"
            )
        }
        scheduleToastClear()

        // Sync to Firebase Firestore if authenticated with Google
        val uid = _uiState.value.firebaseUserUid
        val repo = firebaseRepository
        if (uid != null && repo != null) {
            val safeItemId = "fav_${songId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}"
            val song = _uiState.value.allSongs.firstOrNull { it.id == songId } ?: _uiState.value.currentSong
            viewModelScope.launch {
                runCatching {
                    if (wasAdded) {
                        repo.createCloudItem(
                            userId = uid,
                            itemId = safeItemId,
                            songId = songId,
                            title = song.title,
                            artist = song.artist,
                            category = "favorite"
                        )
                    } else {
                        repo.deleteCloudItem(uid, safeItemId)
                    }
                }
            }
        }
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
        val currentVol = if (open) audioOutputManager.getSystemVolumePercent() else _uiState.value.hardwareVolumePercent
        _uiState.update {
            it.copy(
                isAudioOutputSheetOpen = open,
                availableAudioOutputs = refreshedRoutes,
                hardwareVolumePercent = currentVol
            )
        }
    }

    fun selectAudioOutput(deviceName: String) {
        val matchedRoute = _uiState.value.availableAudioOutputs.firstOrNull {
            it.name.equals(deviceName, ignoreCase = true)
        }
        if (matchedRoute != null) {
            audioEngine.setPreferredAudioRoute(matchedRoute.id)
        }
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
                        isOfflineMode = false,
                        currentSearchPage = 1,
                        allSongs = mergedAllSongs,
                        allAlbums = mergedAllAlbums,
                        searchResultSongs = (liveSongs + state.searchResultSongs).distinctBy { it.audioUrl ?: it.id },
                        searchResultAlbums = (liveAlbums + state.searchResultAlbums).distinctBy { it.title.lowercase().trim() }
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSearchingOnline = false,
                        isOfflineMode = !checkNetworkAvailable()
                    )
                }
            }
        }
    }

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
            songIds = _uiState.value.allSongs.take(8).map { it.id },
            artworkUrl = _uiState.value.allAlbums.firstOrNull()?.artworkUrl
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

        // Sync custom playlist to Cloud Firestore if authenticated with Google
        val uid = _uiState.value.firebaseUserUid
        if (uid != null) {
            viewModelScope.launch {
                runCatching {
                    firebaseRepository?.createCloudItem(
                        userId = uid,
                        itemId = newId,
                        songId = newId,
                        title = cleanTitle,
                        artist = "${newPlaylist.songIds.size} tracks • Custom Playlist",
                        category = "ai_playlist"
                    )
                }
            }
        }
    }

    // Account & Profile Actions
    fun setEditProfileModalOpen(open: Boolean) {
        _uiState.update { it.copy(isEditProfileModalOpen = open) }
    }

    fun saveProfileEdits(displayName: String, handle: String, email: String? = null) {
        val cleanName = displayName.trim().ifEmpty { "Alex Rivera" }
        val cleanHandle = handle.trim().ifEmpty { "onyx_listener" }.let { if (it.startsWith("@")) it else "@$it" }
        val cleanEmail = email?.trim()?.takeIf { it.isNotEmpty() } ?: _uiState.value.userProfile.email
        runCatching {
            getApplication<Application>()
                .getSharedPreferences("onyx_user_profile_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("display_name", cleanName)
                .putString("username_handle", cleanHandle)
                .putString("user_email", cleanEmail)
                .apply()
        }
        _uiState.update { state ->
            state.copy(
                userProfile = state.userProfile.copy(
                    displayName = cleanName,
                    handle = cleanHandle,
                    email = cleanEmail,
                    isProfileSetUp = true
                ),
                isEditProfileModalOpen = false
            )
        }
        val uid = _uiState.value.firebaseUserUid
        val repo = firebaseRepository
        if (uid != null && repo != null) {
            viewModelScope.launch {
                runCatching {
                    repo.createCloudItem(
                        userId = uid,
                        itemId = "user_profile_handle",
                        songId = cleanHandle,
                        title = cleanName,
                        artist = cleanHandle,
                        category = "profile"
                    )
                }
            }
        }
        showToast("Username saved: $cleanHandle ($cleanName)")
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
        viewModelScope.launch {
            firebaseRepository?.signOut(getApplication())
        }
        _uiState.update { state ->
            state.copy(
                firebaseUserUid = null,
                firebaseUserEmail = null,
                firebaseUserDisplayName = null,
                cloudSyncedItemsCount = 0,
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
                // Strict '>' check so streamSec == 0 at startup never freezes the seekbar at 0:00
                val nextSec = if (streamSec != null && streamSec > prevSec) {
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

    fun loadLiveChannels(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingLiveChannels = true) }
            val channels = LiveTvService.fetchLiveChannels(getApplication<Application>().applicationContext, forceRefresh)
            val categories = mutableListOf("All")
            channels.map { it.category }.distinct().forEach { cat ->
                if (cat.isNotBlank() && !categories.contains(cat)) {
                    categories.add(cat)
                }
            }
            _uiState.update { state ->
                val filtered = applyLiveFilter(channels, state.selectedLiveCategory, state.liveSearchQuery)
                state.copy(
                    liveChannels = channels,
                    filteredLiveChannels = filtered,
                    liveCategories = if (categories.size > 1) categories else state.liveCategories,
                    isFetchingLiveChannels = false
                )
            }
        }
    }

    fun selectLiveCategory(category: String) {
        _uiState.update { state ->
            val filtered = applyLiveFilter(state.liveChannels, category, state.liveSearchQuery)
            state.copy(
                selectedLiveCategory = category,
                filteredLiveChannels = filtered
            )
        }
    }

    fun updateLiveSearchQuery(query: String) {
        _uiState.update { state ->
            val filtered = applyLiveFilter(state.liveChannels, state.selectedLiveCategory, query)
            state.copy(
                liveSearchQuery = query,
                filteredLiveChannels = filtered
            )
        }
    }

    private fun applyLiveFilter(channels: List<LiveChannel>, category: String, query: String): List<LiveChannel> {
        return channels.filter { channel ->
            val matchesCat = category == "All" || channel.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                channel.name.contains(query, ignoreCase = true) ||
                channel.category.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    fun playLiveChannel(channel: LiveChannel) {
        if (_uiState.value.isPlaying) {
            audioEngine.pause()
            progressJob?.cancel()
            _uiState.update { it.copy(isPlaying = false) }
            mediaNotificationHelper.updatePlaybackNotification(_uiState.value.currentSong, false)
        }
        _uiState.update { it.copy(activeLiveChannel = channel) }
        livePlayerEngine.playChannel(channel)
    }

    fun closeLivePlayer() {
        livePlayerEngine.stop()
        _uiState.update { it.copy(activeLiveChannel = null, isLiveFullscreen = false) }
    }

    fun toggleLiveFullscreen() {
        _uiState.update { it.copy(isLiveFullscreen = !it.isLiveFullscreen) }
    }

    fun setLiveFullscreen(fullscreen: Boolean) {
        _uiState.update { it.copy(isLiveFullscreen = fullscreen) }
    }

    override fun onCleared() {
        super.onCleared()
        audioOutputManager.unregisterAudioDisconnectListener()
        audioEngine.stop()
        livePlayerEngine.release()
        mediaNotificationHelper.cancelNotification()
    }
}
