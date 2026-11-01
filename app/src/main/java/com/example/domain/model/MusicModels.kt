package com.example.domain.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class AppTab(val route: String, val label: String) {
    HOME("home", "Home"),
    NEW("new", "New"),
    RADIO("radio", "Radio"),
    LIBRARY("library", "Library"),
    SEARCH("search", "Search")
}

enum class RepeatMode(val label: String) {
    OFF("Repeat Off"),
    ALL("Repeat All"),
    ONE("Repeat One")
}

enum class LibrarySortOrder(val label: String) {
    RECENTLY_ADDED("Recently Added"),
    TITLE_AZ("Title A–Z"),
    ARTIST("Artist")
}

data class LyricLine(
    val timestampSec: Int,
    val text: String,
    val translatedText: String? = null
)

/**
 * Timestamped LRC Lyrics Parser & Formatter.
 * Parses standard LRC lines such as "[00:18.00] Every frequency suspended in the midnight air"
 * into time-synced [LyricLine] instances synchronized with the playback engine.
 */
object LrcLyricsParser {
    private val lineRegex = Regex("""\[(\d{1,2}):(\d{2})(?:\.(\d{1,3}))?]\s*(.+)""")

    fun parse(lrcContent: String): List<LyricLine> {
        if (lrcContent.isBlank()) return emptyList()
        return lrcContent
            .lineSequence()
            .mapNotNull { raw ->
                val match = lineRegex.matchEntire(raw.trim()) ?: return@mapNotNull null
                val minutes = match.groupValues[1].toIntOrNull() ?: 0
                val seconds = match.groupValues[2].toIntOrNull() ?: 0
                val text = match.groupValues[4].trim()
                if (text.isEmpty()) null else LyricLine(minutes * 60 + seconds, text)
            }
            .sortedBy { it.timestampSec }
            .toList()
    }
}

data class AudioOutputRoute(
    val id: String,
    val name: String,
    val subtitle: String,
    val routeType: String
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val categoryTag: String,
    val releaseYear: Int,
    @param:DrawableRes val artworkRes: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val isLossless: Boolean = true,
    val isSpatialAudio: Boolean = true,
    val badgeText: String? = null,
    val description: String = "",
    val artworkUrl: String? = null
) {
    val artworkModel: Any
        get() = artworkUrl?.takeIf { it.isNotBlank() } ?: artworkRes
}

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val albumTitle: String,
    val durationSeconds: Int,
    @param:DrawableRes val artworkRes: Int,
    val accentColor: Color,
    val isExplicit: Boolean = false,
    val isSpatialAudio: Boolean = true,
    val trackNumber: Int = 1,
    val baseFreqHz: Float = 220f,
    val synchronizedLyrics: List<LyricLine> = emptyList(),
    val composer: String = "Studio Ensemble",
    val producer: String = "Onyx Spatial Mastering Lab",
    val audioQualityLabel: String = "320 kbps AAC • Dolby Atmos Lossless",
    val artworkUrl: String? = null,
    val audioUrl: String? = null
) {
    val artworkModel: Any
        get() = artworkUrl?.takeIf { it.isNotBlank() } ?: artworkRes

    val formattedDuration: String
        get() {
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return "%d:%02d".format(mins, secs)
        }

    val hasSynchronizedLyrics: Boolean
        get() = synchronizedLyrics.isNotEmpty()
}

data class Playlist(
    val id: String,
    val title: String,
    val curator: String,
    val updatedLabel: String,
    val description: String,
    @param:DrawableRes val artworkRes: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val songIds: List<String>,
    val artworkUrl: String? = null
) {
    val artworkModel: Any
        get() = artworkUrl?.takeIf { it.isNotBlank() } ?: artworkRes
}

data class RadioStation(
    val id: String,
    val name: String,
    val tagline: String,
    val hostName: String,
    val isLive: Boolean,
    val scheduleText: String,
    @param:DrawableRes val artworkRes: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val featuredSongId: String,
    val artworkUrl: String? = null
) {
    val artworkModel: Any
        get() = artworkUrl?.takeIf { it.isNotBlank() } ?: artworkRes
}

data class ArtistProfile(
    val id: String,
    val name: String,
    val genre: String,
    val monthlyListeners: String,
    @param:DrawableRes val avatarRes: Int,
    val accentColor: Color,
    val topAlbumId: String,
    val avatarUrl: String? = null
) {
    val avatarModel: Any
        get() = avatarUrl?.takeIf { it.isNotBlank() } ?: avatarRes
}

data class UserProfile(
    val displayName: String,
    val handle: String,
    val email: String,
    @param:DrawableRes val avatarRes: Int,
    val isProfileSetUp: Boolean,
    val currentPlan: String,
    val isFamilyPlan: Boolean,
    val paymentMethod: String,
    val countryRegion: String,
    val notificationsNewMusic: Boolean,
    val notificationsFriendActivity: Boolean,
    val losslessAudioEnabled: Boolean,
    val dolbyAtmosEnabled: Boolean,
    val crossfadeSeconds: Int
)
