package com.example.data.remote

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.domain.model.Album
import com.example.domain.model.LrcLyricsParser
import com.example.domain.model.LyricLine
import com.example.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

/**
 * Live Music API Client for ONYX MUSIC.
 * Queries https://osint-apis-hub.onrender.com/api/song?key=Titan&song=<query>
 * to fetch real playable 320kbps AAC audio streams, track metadata, and albums,
 * and resolves high-resolution 600x600 cover artwork.
 */
object OnyxSongApiService {

    private const val BASE_API_URL = "https://osint-apis-hub.onrender.com/api/song?key=Titan&song="
    private const val ITUNES_ARTWORK_API = "https://itunes.apple.com/search?entity=song&limit=1&term="

    private val artworkCache = ConcurrentHashMap<String, String>()
    private val querySongsCache = ConcurrentHashMap<String, List<Song>>()

    private val fallbackDrawables = listOf(
        R.drawable.img_album_neon_horizons,
        R.drawable.img_album_velvet_echoes,
        R.drawable.img_album_midnight_tokyo,
        R.drawable.img_album_solar_flare,
        R.drawable.img_album_astral_drift,
        R.drawable.img_album_emerald_pulse,
        R.drawable.img_album_obsidian_hearts,
        R.drawable.img_album_violet_mirage,
        R.drawable.img_album_golden_hour,
        R.drawable.img_album_glacial_static,
        R.drawable.img_album_nocturne,
        R.drawable.img_album_cyber_bloom
    )

    private val accentPalette = listOf(
        Color(0xFF168BFF),
        Color(0xFFFF2A5F),
        Color(0xFF00F5D4),
        Color(0xFFFF9E00),
        Color(0xFF4CC9F0),
        Color(0xFF10B981),
        Color(0xFF3B82F6),
        Color(0xFFF72585),
        Color(0xFFF97316),
        Color(0xFF38BDF8),
        Color(0xFFEAB308),
        Color(0xFFEC4899)
    )

    /**
     * Searches songs live from https://osint-apis-hub.onrender.com/api/song?key=Titan&song=<query>
     */
    suspend fun searchSongs(query: String, resolveArtwork: Boolean = true): List<Song> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim().ifEmpty { "chandani" }
        val cacheKey = cleanQuery.lowercase()
        querySongsCache[cacheKey]?.let { cached ->
            if (cached.isNotEmpty()) return@withContext cached
        }

        val encoded = URLEncoder.encode(cleanQuery, "UTF-8")
        val endpoint = "$BASE_API_URL$encoded"
        val rawJson = httpGet(endpoint, timeoutMs = 15000) ?: return@withContext emptyList()

        val parsedSongs = parseSongsFromJson(rawJson, cleanQuery)
        if (parsedSongs.isEmpty()) return@withContext emptyList()

        val enriched = if (resolveArtwork) {
            coroutineScope {
                parsedSongs.mapIndexed { idx, song ->
                    async {
                        if (!song.artworkUrl.isNullOrBlank() || idx >= 8) {
                            song
                        } else {
                            val artUrl = fetchHighResArtworkUrl(song.title, song.artist)
                            if (artUrl.isNotBlank()) song.copy(artworkUrl = artUrl) else song
                        }
                    }
                }.awaitAll()
            }
        } else {
            parsedSongs
        }

        if (enriched.isNotEmpty()) {
            querySongsCache[cacheKey] = enriched
        }
        enriched
    }

    /**
     * Builds corresponding Album models from a list of live API songs.
     */
    fun buildAlbumsFromSongs(songs: List<Song>, categoryTag: String = "BOLLYWOOD • SPATIAL AUDIO"): List<Album> {
        return songs
            .distinctBy { it.albumTitle.lowercase().trim() }
            .mapIndexed { idx, song ->
                val hashIdx = abs((song.albumTitle + song.artist).hashCode()) % fallbackDrawables.size
                Album(
                    id = "alb_api_${abs((song.albumTitle + song.id).hashCode())}",
                    title = song.albumTitle,
                    artist = song.artist,
                    categoryTag = categoryTag,
                    releaseYear = 2026,
                    artworkRes = song.artworkRes,
                    primaryColor = song.accentColor,
                    secondaryColor = accentPalette[(hashIdx + 3) % accentPalette.size],
                    badgeText = if (idx == 0) "FEATURED STREAM" else "LOSSLESS • DOLBY ATMOS",
                    description = "${song.title} by ${song.artist} — streaming in 320 kbps AAC from ${song.albumTitle}.",
                    artworkUrl = song.artworkUrl
                )
            }
    }

    private fun parseSongsFromJson(rawJson: String, queryTag: String): List<Song> {
        return runCatching {
            val trimmed = rawJson.trim()
            val resultsArray: JSONArray? = if (trimmed.startsWith("[")) {
                JSONArray(trimmed)
            } else {
                val root = JSONObject(trimmed)
                root.optJSONArray("results")
                    ?: root.optJSONArray("data")
                    ?: root.optJSONArray("songs")
                    ?: root.optJSONObject("data")?.optJSONArray("results")
            }

            if (resultsArray == null || resultsArray.length() == 0) {
                return@runCatching emptyList()
            }

            val list = mutableListOf<Song>()
            for (i in 0 until resultsArray.length()) {
                val obj = resultsArray.optJSONObject(i) ?: continue
                val rawTitle = obj.optString("title")
                    .ifBlank { obj.optString("name") }
                    .ifBlank { obj.optString("song") }
                val title = decodeHtmlEntities(rawTitle).trim()
                if (title.isEmpty()) continue

                val rawArtist = obj.optString("artists")
                    .ifBlank { obj.optString("artist") }
                    .ifBlank { obj.optString("primaryArtists") }
                    .ifBlank { obj.optString("singers") }
                    .ifBlank { "Unknown Artist" }
                val artist = decodeHtmlEntities(rawArtist).trim()

                val rawAlbum = obj.optString("album")
                    .ifBlank { title }
                val albumTitle = decodeHtmlEntities(rawAlbum).trim()

                val audioUrl = extractAudioStreamUrl(obj)
                if (audioUrl.isBlank()) continue

                val durationStr = obj.optString("duration", "3:45")
                val durationSeconds = parseDurationToSeconds(durationStr)

                val directArtworkUrl = extractImageUrl(obj)
                val stableHash = abs((title + artist + audioUrl).hashCode())
                val drawableRes = fallbackDrawables[stableHash % fallbackDrawables.size]
                val accentColor = accentPalette[stableHash % accentPalette.size]
                val baseFreq = 196f + (stableHash % 180).toFloat()

                val songId = "api_song_${abs(audioUrl.hashCode())}"
                val syncedLyrics = generateSyncedLyricsForSong(title, artist, albumTitle, durationSeconds)

                list.add(
                    Song(
                        id = songId,
                        title = title,
                        artist = artist,
                        albumTitle = albumTitle,
                        durationSeconds = durationSeconds,
                        artworkRes = drawableRes,
                        accentColor = accentColor,
                        isExplicit = false,
                        isSpatialAudio = true,
                        trackNumber = i + 1,
                        baseFreqHz = baseFreq,
                        synchronizedLyrics = syncedLyrics,
                        composer = artist,
                        producer = "$albumTitle • Onyx 320kbps Master",
                        audioQualityLabel = "320 kbps AAC • Live Stream",
                        artworkUrl = directArtworkUrl.takeIf { it.isNotBlank() },
                        audioUrl = audioUrl
                    )
                )
            }
            list.distinctBy { it.audioUrl ?: it.id }
        }.getOrElse { emptyList() }
    }

    private fun extractAudioStreamUrl(obj: JSONObject): String {
        val directKeys = listOf("download_url", "downloadUrl", "media_url", "stream_url", "audio", "url")
        for (key in directKeys) {
            val value = obj.opt(key) ?: continue
            when (value) {
                is String -> if (value.startsWith("http")) return value
                is JSONArray -> {
                    for (j in value.length() - 1 downTo 0) {
                        val item = value.opt(j)
                        if (item is String && item.startsWith("http")) return item
                        if (item is JSONObject) {
                            val link = item.optString("url").ifBlank { item.optString("link") }
                            if (link.startsWith("http")) return link
                        }
                    }
                }
            }
        }
        return ""
    }

    private fun extractImageUrl(obj: JSONObject): String {
        val imgKeys = listOf("image", "artwork", "thumbnail", "cover")
        for (key in imgKeys) {
            val value = obj.opt(key) ?: continue
            when (value) {
                is String -> if (value.startsWith("http")) return value
                is JSONArray -> {
                    for (j in value.length() - 1 downTo 0) {
                        val item = value.opt(j)
                        if (item is String && item.startsWith("http")) return item
                        if (item is JSONObject) {
                            val link = item.optString("url").ifBlank { item.optString("link") }
                            if (link.startsWith("http")) return link
                        }
                    }
                }
            }
        }
        return ""
    }

    private fun fetchHighResArtworkUrl(title: String, artist: String): String {
        val primaryArtist = artist.split(",").firstOrNull()?.trim().orEmpty()
        val cleanTitle = title.substringBefore("(").trim().ifEmpty { title }
        val lookupKey = "$cleanTitle|$primaryArtist".lowercase()
        artworkCache[lookupKey]?.let { return it }

        return runCatching {
            val encoded = URLEncoder.encode("$cleanTitle $primaryArtist", "UTF-8")
            val jsonStr = httpGet("$ITUNES_ARTWORK_API$encoded", timeoutMs = 6000) ?: return@runCatching ""
            val root = JSONObject(jsonStr)
            val results = root.optJSONArray("results") ?: return@runCatching ""
            if (results.length() == 0) return@runCatching ""
            val first = results.optJSONObject(0) ?: return@runCatching ""
            val art100 = first.optString("artworkUrl100")
            val art600 = art100.replace("100x100bb", "600x600bb")
            if (art600.startsWith("http")) {
                artworkCache[lookupKey] = art600
                art600
            } else ""
        }.getOrElse { "" }
    }

    private fun httpGet(urlString: String, timeoutMs: Int = 12000): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android; OnyxMusic/2.0)")
                setRequestProperty("Accept", "application/json")
            }
            if (connection.responseCode in 200..299) {
                BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use { reader ->
                    reader.readText()
                }
            } else null
        } catch (_: Throwable) {
            null
        } finally {
            runCatching { connection?.disconnect() }
        }
    }

    fun parseDurationToSeconds(durationStr: String): Int {
        val clean = durationStr.trim()
        clean.toIntOrNull()?.let { if (it > 0) return it }
        val parts = clean.split(":").mapNotNull { it.trim().toIntOrNull() }
        return when (parts.size) {
            2 -> (parts[0] * 60 + parts[1]).coerceAtLeast(30)
            3 -> (parts[0] * 3600 + parts[1] * 60 + parts[2]).coerceAtLeast(30)
            else -> 220
        }
    }

    fun decodeHtmlEntities(input: String): String {
        return input
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }

    fun generateSyncedLyricsForSong(
        title: String,
        artist: String,
        albumTitle: String,
        durationSeconds: Int
    ): List<LyricLine> {
        val primaryArtist = artist.split(",").firstOrNull()?.trim() ?: artist
        return LrcLyricsParser.parse(
            """
            [00:00.00] ♪ $title — $primaryArtist ♪
            [00:14.00] From the album "$albumTitle"
            [00:28.00] Streaming in 320 kbps Dolby Atmos Lossless
            [00:46.00] Every melody of $title echoes through the night
            [01:08.00] Vocal harmonies by $artist
            [01:34.00] Feel the rhythm and spatial depth in Onyx Music
            [02:02.00] ♪ Instrumental & Vocal Bridge — $title ♪
            [02:32.00] Pure high-resolution soundstage by $primaryArtist
            """.trimIndent()
        ).filter { it.timestampSec < durationSeconds }
    }
}
