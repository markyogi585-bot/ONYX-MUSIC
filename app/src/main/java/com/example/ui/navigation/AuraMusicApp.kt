package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domain.model.AppTab
import com.example.ui.components.FloatingGlassMiniPlayer
import com.example.ui.components.FrostedGlassContainer
import com.example.ui.components.GlassNavigationBar
import com.example.ui.components.OnyxDynamicBackground
import com.example.ui.components.PremiumTopBar
import com.example.ui.components.ScreenBackgroundType
import com.example.ui.components.realBackdropBlur
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AddToPlaylistModal
import com.example.ui.screens.AudioOutputOverlaySheet
import com.example.ui.screens.CreateFolderModalSheet
import com.example.ui.screens.CreatePlaylistModalSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LyricsScreen
import com.example.ui.screens.NewScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.OnyxSplashOverlay
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.QueueScreen
import com.example.ui.screens.RadioScreen
import com.example.ui.screens.ReportConcernModal
import com.example.ui.screens.SaveToFolderModalSheet
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SectionSeeAllScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SettingsSubpageScreen
import com.example.ui.screens.SleepTimerOverlaySheet
import com.example.ui.screens.SongCreditsModal
import com.example.ui.screens.TrackOptionsOverlaySheet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.viewmodel.MusicViewModel

private fun NavHostController.safeNavigateBack() {
    if (previousBackStackEntry != null) {
        popBackStack()
    }
}

@Composable
fun AuraMusicApp(
    musicViewModel: MusicViewModel = viewModel()
) {
    val uiState by musicViewModel.uiState.collectAsStateWithLifecycle()
    val auraColors = LocalAuraColors.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppTab.HOME.route

    // Keep selectedTab synchronized when returning to a main tab via back navigation
    LaunchedEffect(currentRoute) {
        AppTab.entries.firstOrNull { it.route == currentRoute }?.let { matchedTab ->
            if (uiState.selectedTab != matchedTab) {
                musicViewModel.selectTab(matchedTab)
            }
        }
    }

    val isMainTabRoute = remember(currentRoute) {
        AppTab.entries.any { it.route == currentRoute }
    }

    val showMiniPlayer = remember(currentRoute) {
        isMainTabRoute ||
            currentRoute.startsWith("playlist/") ||
            currentRoute.startsWith("album/") ||
            currentRoute.startsWith("section_all/")
    }

    val homeListState = rememberLazyListState()
    val newListState = rememberLazyListState()
    val radioListState = rememberLazyListState()
    val libraryListState = rememberLazyListState()
    val searchListState = rememberLazyListState()

    val isCurrentTabScrolled by remember(uiState.selectedTab) {
        derivedStateOf {
            val state = when (uiState.selectedTab) {
                AppTab.HOME -> homeListState
                AppTab.NEW -> newListState
                AppTab.RADIO -> radioListState
                AppTab.LIBRARY -> libraryListState
                AppTab.SEARCH -> searchListState
            }
            state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 24
        }
    }

    val dynamicAccentColor by animateColorAsState(
        targetValue = uiState.currentSong.accentColor,
        animationSpec = tween(700),
        label = "ambient_accent_glow"
    )

    val backgroundBlurRadius by animateFloatAsState(
        targetValue = if (uiState.isPopupMenuOpen) 14f else 0f,
        animationSpec = tween(240),
        label = "backdrop_blur_radius"
    )

    val favouriteSongs = remember(uiState.allSongs, uiState.likedSongIds) {
        uiState.allSongs.filter { uiState.likedSongIds.contains(it.id) }.ifEmpty { uiState.allSongs.take(4) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(auraColors.background)
            .testTag("aura_music_root")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .realBackdropBlur(backgroundBlurRadius)
        ) {
            // Navigation Host for all Primary Tabs, Playlist/Album Details, Now Playing, Lyrics, Queue, Settings & Account
            NavHost(
                navController = navController,
                startDestination = AppTab.HOME.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it / 5 }, animationSpec = tween(250)) +
                        fadeIn(tween(220))
                },
                exitTransition = {
                    fadeOut(tween(180))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it / 5 }, animationSpec = tween(240)) +
                        fadeIn(tween(220))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it / 5 }, animationSpec = tween(220)) +
                        fadeOut(tween(180))
                },
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. HOME SCREEN — Deep Black with Subtle Artwork Accent Glow & Complete Discovery Sections
                composable(AppTab.HOME.route) {
                    OnyxDynamicBackground(
                        screenType = ScreenBackgroundType.HOME,
                        artworkRes = uiState.currentSong.artworkRes,
                        seedColor = uiState.currentSong.accentColor
                    ) {
                        HomeScreen(
                            topPicksForYou = uiState.topPicksForYou,
                            recentlyPlayed = uiState.recentlyPlayed,
                            madeForYou = uiState.madeForYou,
                            topSongs = uiState.topSongs,
                            newReleases = uiState.newReleases,
                            recommendedAlbums = uiState.recommendedAlbums,
                            popularArtists = uiState.popularArtists,
                            artistEssentials = uiState.artistEssentials,
                            popularPlaylists = uiState.popularPlaylists,
                            favouriteSongs = favouriteSongs,
                            continueListening = uiState.continueListening,
                            currentPlayingSongId = uiState.currentSong.id,
                            isPlaying = uiState.isPlaying,
                            likedSongIds = uiState.likedSongIds,
                            listState = homeListState,
                            onAlbumClick = { album ->
                                navController.navigate("album/${album.id}") { launchSingleTop = true }
                            },
                            onPlayAlbum = { album -> musicViewModel.playAlbum(album) },
                            onPlaylistClick = { playlist ->
                                navController.navigate("playlist/${playlist.id}") { launchSingleTop = true }
                            },
                            onPlayPlaylist = { playlist -> musicViewModel.playPlaylist(playlist) },
                            onArtistClick = { artist ->
                                navController.navigate("album/${artist.topAlbumId}") { launchSingleTop = true }
                            },
                            onSongClick = { song -> musicViewModel.playSong(song) },
                            onToggleFavorite = musicViewModel::toggleFavorite,
                            onMoreOptionsClick = musicViewModel::openTrackOptions,
                            onSeeAllClick = { sectionKey ->
                                navController.navigate("section_all/$sectionKey") { launchSingleTop = true }
                            },
                            availableMoods = uiState.availableMoods,
                            selectedMood = uiState.selectedMood,
                            aiMoodCuratorNote = uiState.aiMoodCuratorNote,
                            aiMoodTargetBpm = "${uiState.aiMoodTargetBpm} BPM",
                            aiSuggestedSongs = uiState.aiSuggestedSongs,
                            isGeneratingMoodMix = uiState.isGeneratingMoodMix,
                            onSelectMood = musicViewModel::selectMood,
                            onPlayAiMoodMix = musicViewModel::playAiMoodMix,
                            isOfflineMode = uiState.isOfflineMode,
                            offlineOnlyFilterEnabled = uiState.offlineOnlyFilterEnabled,
                            onToggleOfflineFilter = musicViewModel::toggleOfflineOnlyFilter
                        )
                    }
                }

                // 2. NEW SCREEN — Deep Black & Midnight Navy with Blue Illumination
                composable(AppTab.NEW.route) {
                    OnyxDynamicBackground(
                        screenType = ScreenBackgroundType.NEW,
                        artworkRes = uiState.earlyAccessAlbums.firstOrNull()?.artworkRes ?: 0
                    ) {
                        NewScreen(
                            earlyAccessAlbums = uiState.earlyAccessAlbums,
                            bestNewSongs = uiState.bestNewSongs,
                            newThisWeekAlbums = uiState.newThisWeekAlbums,
                            recentReleases = uiState.recentReleases,
                            updatedPlaylists = uiState.updatedPlaylists,
                            currentPlayingSongId = uiState.currentSong.id,
                            isPlaying = uiState.isPlaying,
                            likedSongIds = uiState.likedSongIds,
                            listState = newListState,
                            onAlbumClick = { album ->
                                navController.navigate("album/${album.id}") { launchSingleTop = true }
                            },
                            onPlayAlbum = { album -> musicViewModel.playAlbum(album) },
                            onPlaylistClick = { playlist ->
                                navController.navigate("playlist/${playlist.id}") { launchSingleTop = true }
                            },
                            onPlayPlaylist = { playlist -> musicViewModel.playPlaylist(playlist) },
                            onSongClick = { song -> musicViewModel.playSong(song) },
                            onToggleFavorite = musicViewModel::toggleFavorite,
                            onMoreOptionsClick = musicViewModel::openTrackOptions,
                            onSeeAllClick = { sectionKey ->
                                navController.navigate("section_all/$sectionKey") { launchSingleTop = true }
                            }
                        )
                    }
                }

                // 3. RADIO SCREEN — Dark Cinematic Deep Purple & Midnight Blue Ambient Gradients
                composable(AppTab.RADIO.route) {
                    OnyxDynamicBackground(
                        screenType = ScreenBackgroundType.RADIO,
                        artworkRes = uiState.radioStations.firstOrNull()?.artworkRes ?: 0
                    ) {
                        RadioScreen(
                            stations = uiState.radioStations,
                            recentlyPlayedStations = uiState.recentlyPlayedStations,
                            listState = radioListState,
                            onPlayStation = musicViewModel::playRadioStation
                        )
                    }
                }

                // 4. LIBRARY SCREEN — Deep Brown & Warm Amber Background
                composable(AppTab.LIBRARY.route) {
                    OnyxDynamicBackground(
                        screenType = ScreenBackgroundType.LIBRARY,
                        artworkRes = uiState.allAlbums.firstOrNull()?.artworkRes ?: 0
                    ) {
                        LibraryScreen(
                            allAlbums = uiState.allAlbums,
                            allPlaylists = uiState.allPlaylists,
                            allSongs = uiState.allSongs,
                            allArtists = uiState.allArtists,
                            likedSongIds = uiState.likedSongIds,
                            savedSongIds = uiState.savedSongIds,
                            selectedCategory = uiState.selectedLibraryCategory,
                            sortOrder = uiState.librarySortOrder,
                            currentPlayingSongId = uiState.currentSong.id,
                            isPlaying = uiState.isPlaying,
                            listState = libraryListState,
                            onSelectCategory = musicViewModel::selectLibraryCategory,
                            onSelectSortOrder = musicViewModel::setLibrarySortOrder,
                            onAlbumClick = { album ->
                                navController.navigate("album/${album.id}") { launchSingleTop = true }
                            },
                            onPlayAlbum = { album -> musicViewModel.playAlbum(album) },
                            onPlaylistClick = { playlist ->
                                navController.navigate("playlist/${playlist.id}") { launchSingleTop = true }
                            },
                            onPlayPlaylist = { playlist -> musicViewModel.playPlaylist(playlist) },
                            onArtistClick = { artist ->
                                navController.navigate("album/${artist.topAlbumId}") { launchSingleTop = true }
                            },
                            onSongClick = { song -> musicViewModel.playSong(song) },
                            onToggleFavorite = musicViewModel::toggleFavorite,
                            onMoreOptionsClick = musicViewModel::openTrackOptions,
                            downloadedTracks = uiState.downloadedTracks,
                            musicFolders = uiState.musicFolders,
                            selectedFolderFilter = uiState.selectedFolderFilter,
                            downloadingSongIds = uiState.downloadingSongIds,
                            downloadProgressMap = uiState.downloadProgressMap,
                            onSelectFolder = musicViewModel::selectMusicFolder,
                            onOpenCreateFolder = { musicViewModel.setCreateFolderModalOpen(true) },
                            onDownloadSong = { song -> musicViewModel.downloadSong(song) },
                            onDeleteDownloadedSong = musicViewModel::deleteDownloadedSong
                        )
                    }
                }

                // 5. SEARCH SCREEN — Pure Premium Black with Subtle Charcoal Gradients
                composable(AppTab.SEARCH.route) {
                    OnyxDynamicBackground(
                        screenType = ScreenBackgroundType.SEARCH
                    ) {
                        SearchScreen(
                            searchQuery = uiState.searchQuery,
                            selectedCategory = uiState.selectedSearchCategory,
                            searchResultAlbums = uiState.searchResultAlbums,
                            searchResultSongs = uiState.searchResultSongs,
                            recentSearches = uiState.recentSearches,
                            trendingSearches = uiState.trendingSearches,
                            currentPlayingSongId = uiState.currentSong.id,
                            isPlaying = uiState.isPlaying,
                            likedSongIds = uiState.likedSongIds,
                            listState = searchListState,
                            onQueryChange = musicViewModel::updateSearchQuery,
                            onSubmitSearchTerm = musicViewModel::submitSearchTerm,
                            onClearRecentSearches = musicViewModel::clearRecentSearches,
                            onCategoryClick = musicViewModel::selectSearchCategory,
                            onAlbumClick = { album ->
                                navController.navigate("album/${album.id}") { launchSingleTop = true }
                            },
                            onPlayAlbum = { album -> musicViewModel.playAlbum(album) },
                            onSongClick = { song -> musicViewModel.playSong(song) },
                            onToggleFavorite = musicViewModel::toggleFavorite,
                            onMoreOptionsClick = musicViewModel::openTrackOptions,
                            isSearchingOnline = uiState.isSearchingOnline,
                            isLoadingMoreSongs = uiState.isLoadingMoreSongs,
                            currentSearchPage = uiState.currentSearchPage,
                            onLoadMoreSongs = { musicViewModel.loadMoreSongsFromApi() },
                            isOfflineMode = uiState.isOfflineMode || uiState.offlineOnlyFilterEnabled,
                            downloadedSongsCount = uiState.downloadedTracks.size,
                            onToggleOfflineFilter = musicViewModel::toggleOfflineOnlyFilter
                        )
                    }
                }

                // 6. PLAYLIST DETAIL ROUTE — Full-Screen Dynamic Artwork-Extracted Background
                composable(
                    route = "playlist/{playlistId}",
                    arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                ) { entry ->
                    val playlistId = entry.arguments?.getString("playlistId").orEmpty()
                    val playlist = uiState.allPlaylists.firstOrNull { it.id == playlistId }
                        ?: uiState.allPlaylists.first()
                    val playlistTracks = uiState.allSongs.filter { playlist.songIds.contains(it.id) }
                        .ifEmpty { uiState.allSongs }

                    PlaylistDetailScreen(
                        album = null,
                        playlist = playlist,
                        allSongs = uiState.allSongs,
                        currentPlayingSongId = uiState.currentSong.id,
                        isPlaying = uiState.isPlaying,
                        likedSongIds = uiState.likedSongIds,
                        isSavedToLibrary = uiState.savedPlaylistIds.contains(playlist.id),
                        onBack = { navController.safeNavigateBack() },
                        onPlayAll = { musicViewModel.playPlaylist(playlist, shuffle = false) },
                        onShuffleAll = { musicViewModel.playPlaylist(playlist, shuffle = true) },
                        onToggleSaveLibrary = { musicViewModel.toggleSavePlaylist(playlist.id) },
                        onPlaySong = { song ->
                            musicViewModel.playSong(song, playlistTracks)
                        },
                        onToggleFavorite = musicViewModel::toggleFavorite,
                        onMoreOptionsSong = musicViewModel::openTrackOptions,
                        onShareClick = { musicViewModel.showToast("Shared '${playlist.title}' link") }
                    )
                }

                // 7. ALBUM DETAIL ROUTE — Full-Screen Dynamic Artwork-Extracted Background
                composable(
                    route = "album/{albumId}",
                    arguments = listOf(navArgument("albumId") { type = NavType.StringType })
                ) { entry ->
                    val albumId = entry.arguments?.getString("albumId").orEmpty()
                    val album = uiState.allAlbums.firstOrNull { it.id == albumId }
                        ?: uiState.allAlbums.first()
                    LaunchedEffect(album.id) {
                        musicViewModel.ensureAlbumTracksLoaded(album)
                    }
                    val albumTracks = uiState.allSongs.filter {
                        it.albumTitle.equals(album.title, ignoreCase = true) || it.artworkRes == album.artworkRes
                    }.ifEmpty { uiState.allSongs }

                    PlaylistDetailScreen(
                        album = album,
                        playlist = null,
                        allSongs = uiState.allSongs,
                        currentPlayingSongId = uiState.currentSong.id,
                        isPlaying = uiState.isPlaying,
                        likedSongIds = uiState.likedSongIds,
                        isSavedToLibrary = uiState.savedAlbumIds.contains(album.id),
                        onBack = { navController.safeNavigateBack() },
                        onPlayAll = { musicViewModel.playAlbum(album, shuffle = false) },
                        onShuffleAll = { musicViewModel.playAlbum(album, shuffle = true) },
                        onToggleSaveLibrary = { musicViewModel.toggleSaveAlbum(album.id) },
                        onPlaySong = { song ->
                            musicViewModel.playSong(song, albumTracks)
                        },
                        onToggleFavorite = musicViewModel::toggleFavorite,
                        onMoreOptionsSong = musicViewModel::openTrackOptions,
                        onShareClick = { musicViewModel.showToast("Shared '${album.title}' link") }
                    )
                }

                // 8. SECTION SEE ALL ROUTE
                composable(
                    route = "section_all/{sectionKey}",
                    arguments = listOf(navArgument("sectionKey") { type = NavType.StringType })
                ) { entry ->
                    val sectionKey = entry.arguments?.getString("sectionKey") ?: "recently_played"
                    SectionSeeAllScreen(
                        sectionKey = sectionKey,
                        allAlbums = uiState.allAlbums,
                        allPlaylists = uiState.allPlaylists,
                        allSongs = uiState.allSongs,
                        likedSongIds = uiState.likedSongIds,
                        currentPlayingSongId = uiState.currentSong.id,
                        isPlaying = uiState.isPlaying,
                        onBackClick = { navController.safeNavigateBack() },
                        onAlbumClick = { album ->
                            navController.navigate("album/${album.id}") { launchSingleTop = true }
                        },
                        onPlayAlbum = { album -> musicViewModel.playAlbum(album) },
                        onPlaylistClick = { playlist ->
                            navController.navigate("playlist/${playlist.id}") { launchSingleTop = true }
                        },
                        onPlayPlaylist = { playlist -> musicViewModel.playPlaylist(playlist) },
                        onSongClick = { song -> musicViewModel.playSong(song) },
                        onToggleFavorite = musicViewModel::toggleFavorite,
                        onMoreOptionsClick = musicViewModel::openTrackOptions,
                        isLoadingMoreSongs = uiState.isLoadingMoreSongs,
                        onLoadMoreSongs = { musicViewModel.loadMoreSongsFromApi() }
                    )
                }

                // 9. NOW PLAYING ROUTE — Full-Screen Dynamic Artwork-Extracted Background
                composable(
                    route = "now_playing",
                    enterTransition = {
                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) +
                            fadeIn(tween(220))
                    },
                    exitTransition = {
                        fadeOut(tween(180))
                    },
                    popEnterTransition = {
                        fadeIn(tween(200))
                    },
                    popExitTransition = {
                        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(260)) +
                            fadeOut(tween(180))
                    }
                ) {
                    NowPlayingScreen(
                        song = uiState.currentSong,
                        isPlaying = uiState.isPlaying,
                        currentPositionSeconds = uiState.currentPositionSeconds,
                        isShuffleEnabled = uiState.isShuffleEnabled,
                        repeatMode = uiState.repeatMode,
                        isLiked = uiState.likedSongIds.contains(uiState.currentSong.id),
                        selectedAudioOutput = uiState.selectedAudioOutput,
                        onBackClick = { navController.safeNavigateBack() },
                        onTogglePlayPause = musicViewModel::togglePlayPause,
                        onSkipNext = musicViewModel::skipNext,
                        onSkipPrevious = musicViewModel::skipPrevious,
                        onSeekTo = musicViewModel::seekTo,
                        onToggleShuffle = musicViewModel::toggleShuffle,
                        onCycleRepeatMode = musicViewModel::cycleRepeatMode,
                        onToggleFavorite = { musicViewModel.toggleFavorite(uiState.currentSong.id) },
                        onOpenLyrics = {
                            navController.navigate("lyrics") { launchSingleTop = true }
                        },
                        onOpenQueue = {
                            navController.navigate("queue") { launchSingleTop = true }
                        },
                        onOpenAudioOutput = { musicViewModel.setAudioOutputSheetOpen(true) },
                        onOpenMoreOptions = { musicViewModel.openTrackOptions(uiState.currentSong) },
                        onShareClick = {
                            musicViewModel.showToast("Shared '${uiState.currentSong.title}' link")
                        }
                    )
                }

                // 10. LYRICS ROUTE — Full-Screen Artwork-Derived Blurred Background
                composable("lyrics") {
                    LyricsScreen(
                        song = uiState.currentSong,
                        isPlaying = uiState.isPlaying,
                        currentPositionSeconds = uiState.currentPositionSeconds,
                        onBackClick = { navController.safeNavigateBack() },
                        onSeekTo = musicViewModel::seekTo,
                        onTogglePlayPause = musicViewModel::togglePlayPause,
                        isGeneratingAiLyrics = uiState.isGeneratingAiLyrics,
                        showTranslation = uiState.showLyricsTranslation,
                        aiLyricsMeaning = uiState.aiLyricsMeaningMap[uiState.currentSong.id].orEmpty(),
                        aiLyricsMoodTag = uiState.aiLyricsMoodTagMap[uiState.currentSong.id].orEmpty(),
                        onVerifyWithAi = musicViewModel::verifyLyricsWithGeminiAi,
                        onToggleTranslation = musicViewModel::toggleLyricsTranslation
                    )
                }

                // 11. QUEUE ROUTE — Full-Screen Artwork-Derived Background
                composable("queue") {
                    QueueScreen(
                        currentSong = uiState.currentSong,
                        queueSongs = uiState.queueSongs,
                        isPlaying = uiState.isPlaying,
                        isShuffleEnabled = uiState.isShuffleEnabled,
                        onBackClick = { navController.safeNavigateBack() },
                        onPlaySong = { song -> musicViewModel.playSong(song) },
                        onMoveUp = musicViewModel::moveQueueItemUp,
                        onMoveDown = musicViewModel::moveQueueItemDown,
                        onRemoveFromQueue = musicViewModel::removeFromQueue,
                        onShuffleQueue = musicViewModel::shuffleQueue,
                        onClearQueue = musicViewModel::clearQueue
                    )
                }

                // 12. SETTINGS ROUTE
                composable("settings") {
                    SettingsScreen(
                        settings = uiState.persistedSettings,
                        onBackClick = { navController.safeNavigateBack() },
                        onNavigateSubpage = { subpage ->
                            navController.navigate("settings/$subpage") {
                                launchSingleTop = true
                            }
                        },
                        onNavigateAccount = {
                            navController.navigate("account") {
                                launchSingleTop = true
                            }
                        },
                        onToggleAddDeletePlaylist = musicViewModel::setAddDeletePlaylistSongs,
                        onToggleAddDeleteFavourite = musicViewModel::setAddDeleteFavouriteSongs,
                        onToggleSpatialAudio = musicViewModel::setSpatialAudio,
                        onToggleLargerText = musicViewModel::setLargerText,
                        onToggleIncreaseContrast = musicViewModel::setIncreaseContrast,
                        onToggleDiagnostics = musicViewModel::setDiagnostics
                    )
                }

                // 13. SETTINGS SUBPAGE ROUTE
                composable(
                    route = "settings/{subpage}",
                    arguments = listOf(navArgument("subpage") { type = NavType.StringType })
                ) { entry ->
                    val subpageKey = entry.arguments?.getString("subpage") ?: "appearance"
                    SettingsSubpageScreen(
                        subpageKey = subpageKey,
                        settings = uiState.persistedSettings,
                        onBackClick = { navController.safeNavigateBack() },
                        onSelectThemeMode = musicViewModel::setThemeMode,
                        onSelectStreamingQuality = musicViewModel::setStreamingQuality,
                        onSelectDownloadQuality = musicViewModel::setDownloadQuality,
                        onSelectEqualizerPreset = musicViewModel::setEqualizerPreset,
                        onToggleCrossfade = musicViewModel::setCrossfadeEnabled,
                        onUpdateCrossfadeSeconds = musicViewModel::updateCrossfadeSeconds,
                        onToggleSoundCheck = musicViewModel::setSoundCheck,
                        onToggleAutoPlay = musicViewModel::setAutoPlay,
                        onToggleWifiOnlyDownloads = musicViewModel::setWifiOnlyDownloads,
                        onToggleCellularStreaming = musicViewModel::setCellularStreaming,
                        onToggleNewMusicNotif = musicViewModel::setNotificationsNewMusic,
                        onToggleFriendNotif = musicViewModel::setNotificationsFriendActivity,
                        onClearCache = musicViewModel::clearCache,
                        onToggleListeningHistory = musicViewModel::setListeningHistory,
                        onToggleLargerText = musicViewModel::setLargerText,
                        onToggleIncreaseContrast = musicViewModel::setIncreaseContrast,
                        onShowToast = musicViewModel::showToast
                    )
                }

                // 14. ACCOUNT ROUTE
                composable("account") {
                    val activityContext = androidx.compose.ui.platform.LocalContext.current
                    AccountScreen(
                        userProfile = uiState.userProfile,
                        streamingQualityLabel = uiState.persistedSettings.streamingQuality,
                        isEditProfileModalOpen = uiState.isEditProfileModalOpen,
                        activeAccountSubModal = uiState.activeAccountSubModal,
                        onCloseAccount = { navController.safeNavigateBack() },
                        onOpenEditProfile = { musicViewModel.setEditProfileModalOpen(true) },
                        onCloseEditProfile = { musicViewModel.setEditProfileModalOpen(false) },
                        onSaveProfileEdits = musicViewModel::saveProfileEdits,
                        onCompleteProfileSetup = musicViewModel::completeProfileSetup,
                        onUpgradeFamilyClick = musicViewModel::upgradeToFamilyPlan,
                        onOpenSubModal = musicViewModel::openAccountSubModal,
                        onUpdateCountry = musicViewModel::updateCountryRegion,
                        onRedeemCode = musicViewModel::redeemPromoCode,
                        onToggleNewMusicNotif = musicViewModel::toggleNotificationsNewMusic,
                        onToggleFriendNotif = musicViewModel::toggleNotificationsFriendActivity,
                        onSelectStreamingQuality = musicViewModel::setStreamingQuality,
                        onToggleSpatialAudio = musicViewModel::setSpatialAudio,
                        onSignOutConfirm = musicViewModel::signOutAccount,
                        firebaseUserEmail = uiState.firebaseUserEmail,
                        cloudSyncedItemsCount = uiState.cloudSyncedItemsCount,
                        onGoogleSignInClick = {
                            (activityContext as? android.app.Activity)?.let { act ->
                                musicViewModel.signInWithGoogle(act)
                            }
                        }
                    )
                }
            }

            // Pinned Floating Top Bar with Three-Dot Glass Button & Anchored Popup Menu (ONLY on main 5 tabs)
            if (isMainTabRoute) {
                PremiumTopBar(
                    title = uiState.selectedTab.label,
                    isPopupMenuExpanded = uiState.isPopupMenuOpen,
                    onTogglePopupMenu = musicViewModel::togglePopupMenu,
                    onDismissPopupMenu = musicViewModel::dismissPopupMenu,
                    onSettingsClick = {
                        musicViewModel.dismissPopupMenu()
                        navController.navigate("settings") {
                            launchSingleTop = true
                        }
                    },
                    onAccountClick = {
                        musicViewModel.dismissPopupMenu()
                        navController.navigate("account") {
                            launchSingleTop = true
                        }
                    },
                    onSecondaryActionClick = if (uiState.selectedTab == AppTab.LIBRARY) {
                        { musicViewModel.setCreatePlaylistModalOpen(true) }
                    } else null,
                    secondaryActionDescription = "Create Playlist",
                    isScrolled = isCurrentTabScrolled,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            // Bottom Floating Glass Stack: Single Centralized Mini-Player + Floating Pill Navigation Bar
            if (showMiniPlayer) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    auraColors.background.copy(alpha = 0.65f),
                                    auraColors.background.copy(alpha = 0.92f)
                                )
                            )
                        )
                ) {
                    FloatingGlassMiniPlayer(
                        currentSong = uiState.currentSong,
                        isPlaying = uiState.isPlaying,
                        currentPositionSeconds = uiState.currentPositionSeconds,
                        onTogglePlayPause = musicViewModel::togglePlayPause,
                        onSkipNext = musicViewModel::skipNext,
                        onExpandNowPlaying = {
                            navController.navigate("now_playing") {
                                launchSingleTop = true
                            }
                        }
                    )

                    if (isMainTabRoute) {
                        Spacer(modifier = Modifier.height(4.dp))

                        GlassNavigationBar(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = { tab ->
                                musicViewModel.selectTab(tab)
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            accentGlowColor = dynamicAccentColor
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Interactive Track Options Overlay Sheet
        val optionsSong = uiState.activeTrackOptionsSong
        if (optionsSong != null) {
            TrackOptionsOverlaySheet(
                song = optionsSong,
                isLiked = uiState.likedSongIds.contains(optionsSong.id),
                isSavedInLibrary = uiState.savedSongIds.contains(optionsSong.id),
                isDownloaded = uiState.downloadedTracks.any { it.songId == optionsSong.id },
                sleepTimerMinutes = uiState.sleepTimerMinutes,
                onDismiss = musicViewModel::dismissTrackOptions,
                onToggleSaveLibrary = { musicViewModel.toggleSaveSongToLibrary(optionsSong) },
                onAddToPlaylist = { musicViewModel.openAddToPlaylistModal(optionsSong) },
                onDownloadSong = { musicViewModel.downloadSong(optionsSong) },
                onSaveToFolder = { musicViewModel.openSaveToFolderModal(optionsSong) },
                onPlayNext = { musicViewModel.playNextInQueue(optionsSong) },
                onAddToQueue = { musicViewModel.addToQueue(optionsSong) },
                onToggleFavorite = { musicViewModel.toggleFavorite(optionsSong.id) },
                onOpenLyrics = {
                    musicViewModel.playSong(optionsSong)
                    navController.navigate("lyrics") { launchSingleTop = true }
                },
                onShareLyrics = { musicViewModel.shareSongLyrics(optionsSong) },
                onViewCredits = { musicViewModel.openSongCreditsModal(optionsSong) },
                onCreateStation = { musicViewModel.startStationFromSong(optionsSong) },
                onOpenSleepTimer = { musicViewModel.setSleepTimerSheetOpen(true) },
                onSuggestLess = { musicViewModel.suggestLessLikeThis(optionsSong) },
                onReportConcern = { musicViewModel.openReportConcernModal(optionsSong) },
                onShare = { musicViewModel.showToast("Shared '${optionsSong.title}' link") }
            )
        }

        // Song Credits Modal
        SongCreditsModal(
            song = uiState.activeCreditsSong,
            onDismiss = musicViewModel::dismissSongCreditsModal
        )

        // Report a Concern Modal
        ReportConcernModal(
            song = uiState.activeReportConcernSong,
            onSubmitConcern = musicViewModel::submitReportConcern,
            onDismiss = musicViewModel::dismissReportConcernModal
        )

        // Add to Playlist Modal
        AddToPlaylistModal(
            song = uiState.activeAddToPlaylistSong,
            playlists = uiState.allPlaylists,
            onSelectPlaylist = musicViewModel::addSongToPlaylist,
            onDismiss = musicViewModel::dismissAddToPlaylistModal
        )

        // Save to Local Folder Modal
        SaveToFolderModalSheet(
            song = uiState.activeSaveToFolderSong,
            folders = uiState.musicFolders,
            onSelectFolder = { song, folderName -> musicViewModel.moveSongToFolder(song, folderName) },
            onCreateNewFolderClick = { musicViewModel.setCreateFolderModalOpen(true) },
            onDismiss = musicViewModel::dismissSaveToFolderModal
        )

        // Create New Music Folder Modal
        CreateFolderModalSheet(
            visible = uiState.isCreateFolderModalOpen,
            onCreateFolder = musicViewModel::createMusicFolder,
            onDismiss = { musicViewModel.setCreateFolderModalOpen(false) }
        )

        // Create Playlist Modal (Apple Music Library "+" Header Action)
        CreatePlaylistModalSheet(
            visible = uiState.isCreatePlaylistModalOpen,
            onCreatePlaylist = musicViewModel::createCustomPlaylist,
            onDismiss = { musicViewModel.setCreatePlaylistModalOpen(false) }
        )

        // Sleep Timer Modal
        SleepTimerOverlaySheet(
            visible = uiState.isSleepTimerSheetOpen,
            activeMinutes = uiState.sleepTimerMinutes,
            onSelectMinutes = musicViewModel::setSleepTimer,
            onDismiss = { musicViewModel.setSleepTimerSheetOpen(false) }
        )

        // Interactive Audio Output / Spatial Cast Sheet
        if (uiState.isAudioOutputSheetOpen) {
            AudioOutputOverlaySheet(
                availableRoutes = uiState.availableAudioOutputs,
                selectedOutput = uiState.selectedAudioOutput,
                onSelectOutput = musicViewModel::selectAudioOutput,
                onDismiss = { musicViewModel.setAudioOutputSheetOpen(false) },
                hardwareVolumePercent = uiState.hardwareVolumePercent,
                earbudNoiseControlMode = uiState.earbudNoiseControlMode,
                autoPauseOnDisconnect = uiState.autoPauseOnEarbudDisconnect,
                onVolumeChange = musicViewModel::setHardwareVolumePercent,
                onSelectNoiseControlMode = musicViewModel::setEarbudNoiseControlMode,
                onToggleAutoPauseDisconnect = {
                    musicViewModel.setAutoPauseOnEarbudDisconnect(!uiState.autoPauseOnEarbudDisconnect)
                }
            )
        }

        // Floating Glass Toast Banner
        AnimatedVisibility(
            visible = uiState.toastMessage != null,
            enter = fadeIn(tween(180)) + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(tween(180)) + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 64.dp, start = 24.dp, end = 24.dp)
        ) {
            FrostedGlassContainer(
                shape = RoundedCornerShape(50),
                accentGlowColor = ElectricBlue,
                baseAlpha = 0.90f,
                elevation = 16.dp
            ) {
                Text(
                    text = uiState.toastMessage.orEmpty(),
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }

        // Minimalist ONYX MUSIC Splash Screen Overlay (Pure Black + Centered Official Logo)
        AnimatedVisibility(
            visible = uiState.showSplashOverlay,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(450))
        ) {
            OnyxSplashOverlay(
                onSplashFinished = musicViewModel::dismissSplashOverlay
            )
        }
    }
}
