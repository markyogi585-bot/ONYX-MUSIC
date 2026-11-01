package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "aura_music_user_preferences"
)

enum class ThemeMode(val label: String) {
    FOLLOW_SYSTEM("Follow System"),
    LIGHT("Light"),
    DARK("Dark")
}

data class PersistedSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val addDeletePlaylistSongs: Boolean = true,
    val addDeleteFavouriteSongs: Boolean = true,
    val spatialAudioEnabled: Boolean = true,
    val streamingQuality: String = "Lossless (ALAC 24-bit/48 kHz)",
    val downloadQuality: String = "Lossless (ALAC 24-bit/48 kHz)",
    val crossfadeEnabled: Boolean = true,
    val crossfadeSeconds: Int = 6,
    val equalizerPreset: String = "Late Night",
    val soundCheckEnabled: Boolean = true,
    val autoPlayEnabled: Boolean = true,
    val wifiOnlyDownloads: Boolean = true,
    val cellularStreamingEnabled: Boolean = true,
    val largerTextEnabled: Boolean = false,
    val increaseContrastEnabled: Boolean = false,
    val diagnosticsEnabled: Boolean = false,
    val listeningHistoryEnabled: Boolean = true,
    val notificationsNewMusic: Boolean = true,
    val notificationsFriendActivity: Boolean = true,
    val cacheSizeMb: Int = 342
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>?) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ADD_DELETE_PLAYLIST = booleanPreferencesKey("add_delete_playlist_songs")
        val ADD_DELETE_FAVOURITE = booleanPreferencesKey("add_delete_favourite_songs")
        val SPATIAL_AUDIO = booleanPreferencesKey("spatial_audio_enabled")
        val STREAMING_QUALITY = stringPreferencesKey("streaming_quality")
        val DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")
        val CROSSFADE_ENABLED = booleanPreferencesKey("crossfade_enabled")
        val CROSSFADE_SECONDS = intPreferencesKey("crossfade_seconds")
        val EQUALIZER_PRESET = stringPreferencesKey("equalizer_preset")
        val SOUND_CHECK = booleanPreferencesKey("sound_check_enabled")
        val AUTO_PLAY = booleanPreferencesKey("auto_play_enabled")
        val WIFI_ONLY_DOWNLOADS = booleanPreferencesKey("wifi_only_downloads")
        val CELLULAR_STREAMING = booleanPreferencesKey("cellular_streaming_enabled")
        val LARGER_TEXT = booleanPreferencesKey("larger_text_enabled")
        val INCREASE_CONTRAST = booleanPreferencesKey("increase_contrast_enabled")
        val DIAGNOSTICS = booleanPreferencesKey("diagnostics_enabled")
        val LISTENING_HISTORY = booleanPreferencesKey("listening_history_enabled")
        val NOTIF_NEW_MUSIC = booleanPreferencesKey("notifications_new_music")
        val NOTIF_FRIEND_ACTIVITY = booleanPreferencesKey("notifications_friend_activity")
        val CACHE_SIZE_MB = intPreferencesKey("cache_size_mb")
    }

    val settingsFlow: Flow<PersistedSettings> = dataStore?.data
        ?.catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        ?.map { prefs ->
            val themeName = prefs[Keys.THEME_MODE] ?: ThemeMode.DARK.name
            val theme = runCatching { ThemeMode.valueOf(themeName) }.getOrDefault(ThemeMode.DARK)
            PersistedSettings(
                themeMode = theme,
                addDeletePlaylistSongs = prefs[Keys.ADD_DELETE_PLAYLIST] ?: true,
                addDeleteFavouriteSongs = prefs[Keys.ADD_DELETE_FAVOURITE] ?: true,
                spatialAudioEnabled = prefs[Keys.SPATIAL_AUDIO] ?: true,
                streamingQuality = prefs[Keys.STREAMING_QUALITY] ?: "Lossless (ALAC 24-bit/48 kHz)",
                downloadQuality = prefs[Keys.DOWNLOAD_QUALITY] ?: "Lossless (ALAC 24-bit/48 kHz)",
                crossfadeEnabled = prefs[Keys.CROSSFADE_ENABLED] ?: true,
                crossfadeSeconds = prefs[Keys.CROSSFADE_SECONDS] ?: 6,
                equalizerPreset = prefs[Keys.EQUALIZER_PRESET] ?: "Late Night",
                soundCheckEnabled = prefs[Keys.SOUND_CHECK] ?: true,
                autoPlayEnabled = prefs[Keys.AUTO_PLAY] ?: true,
                wifiOnlyDownloads = prefs[Keys.WIFI_ONLY_DOWNLOADS] ?: true,
                cellularStreamingEnabled = prefs[Keys.CELLULAR_STREAMING] ?: true,
                largerTextEnabled = prefs[Keys.LARGER_TEXT] ?: false,
                increaseContrastEnabled = prefs[Keys.INCREASE_CONTRAST] ?: false,
                diagnosticsEnabled = prefs[Keys.DIAGNOSTICS] ?: false,
                listeningHistoryEnabled = prefs[Keys.LISTENING_HISTORY] ?: true,
                notificationsNewMusic = prefs[Keys.NOTIF_NEW_MUSIC] ?: true,
                notificationsFriendActivity = prefs[Keys.NOTIF_FRIEND_ACTIVITY] ?: true,
                cacheSizeMb = prefs[Keys.CACHE_SIZE_MB] ?: 342
            )
        } ?: kotlinx.coroutines.flow.flowOf(PersistedSettings())

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore?.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setAddDeletePlaylistSongs(enabled: Boolean) {
        dataStore?.edit { it[Keys.ADD_DELETE_PLAYLIST] = enabled }
    }

    suspend fun setAddDeleteFavouriteSongs(enabled: Boolean) {
        dataStore?.edit { it[Keys.ADD_DELETE_FAVOURITE] = enabled }
    }

    suspend fun setSpatialAudio(enabled: Boolean) {
        dataStore?.edit { it[Keys.SPATIAL_AUDIO] = enabled }
    }

    suspend fun setStreamingQuality(quality: String) {
        dataStore?.edit { it[Keys.STREAMING_QUALITY] = quality }
    }

    suspend fun setDownloadQuality(quality: String) {
        dataStore?.edit { it[Keys.DOWNLOAD_QUALITY] = quality }
    }

    suspend fun setCrossfadeEnabled(enabled: Boolean) {
        dataStore?.edit { it[Keys.CROSSFADE_ENABLED] = enabled }
    }

    suspend fun setCrossfadeSeconds(seconds: Int) {
        dataStore?.edit { it[Keys.CROSSFADE_SECONDS] = seconds }
    }

    suspend fun setEqualizerPreset(preset: String) {
        dataStore?.edit { it[Keys.EQUALIZER_PRESET] = preset }
    }

    suspend fun setSoundCheck(enabled: Boolean) {
        dataStore?.edit { it[Keys.SOUND_CHECK] = enabled }
    }

    suspend fun setAutoPlay(enabled: Boolean) {
        dataStore?.edit { it[Keys.AUTO_PLAY] = enabled }
    }

    suspend fun setWifiOnlyDownloads(enabled: Boolean) {
        dataStore?.edit { it[Keys.WIFI_ONLY_DOWNLOADS] = enabled }
    }

    suspend fun setCellularStreaming(enabled: Boolean) {
        dataStore?.edit { it[Keys.CELLULAR_STREAMING] = enabled }
    }

    suspend fun setLargerText(enabled: Boolean) {
        dataStore?.edit { it[Keys.LARGER_TEXT] = enabled }
    }

    suspend fun setIncreaseContrast(enabled: Boolean) {
        dataStore?.edit { it[Keys.INCREASE_CONTRAST] = enabled }
    }

    suspend fun setDiagnostics(enabled: Boolean) {
        dataStore?.edit { it[Keys.DIAGNOSTICS] = enabled }
    }

    suspend fun setListeningHistory(enabled: Boolean) {
        dataStore?.edit { it[Keys.LISTENING_HISTORY] = enabled }
    }

    suspend fun setNotificationsNewMusic(enabled: Boolean) {
        dataStore?.edit { it[Keys.NOTIF_NEW_MUSIC] = enabled }
    }

    suspend fun setNotificationsFriendActivity(enabled: Boolean) {
        dataStore?.edit { it[Keys.NOTIF_FRIEND_ACTIVITY] = enabled }
    }

    suspend fun clearCache() {
        dataStore?.edit { it[Keys.CACHE_SIZE_MB] = 0 }
    }
}
