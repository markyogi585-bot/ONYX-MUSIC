package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.domain.model.LiveChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object LiveTvService {
    private const val TAG = "LiveTvService"
    private const val M3U_URL = "https://main.freelivtv.workers.dev/provider.m3u"
    private const val CACHE_FILE_NAME = "cached_livetv_channels.json"

    private var inMemoryChannels: List<LiveChannel>? = null

    suspend fun fetchLiveChannels(context: Context? = null, forceRefresh: Boolean = false): List<LiveChannel> =
        withContext(Dispatchers.IO) {
            if (!forceRefresh && inMemoryChannels != null && inMemoryChannels!!.isNotEmpty()) {
                return@withContext inMemoryChannels!!
            }

            // Try reading from local file cache first if not forcing refresh
            val cacheFile = context?.let { File(it.cacheDir, CACHE_FILE_NAME) }
            if (!forceRefresh && cacheFile != null && cacheFile.exists()) {
                runCatching {
                    val cached = readChannelsFromCacheFile(cacheFile)
                    if (cached.isNotEmpty()) {
                        inMemoryChannels = cached
                        return@withContext cached
                    }
                }
            }

            // Fetch from live worker M3U endpoint
            try {
                val url = URL(M3U_URL)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 12000
                    readTimeout = 15000
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                }

                if (conn.responseCode in 200..299) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8))
                    val m3uContent = reader.readText()
                    reader.close()
                    conn.disconnect()

                    val parsed = parseM3uPlaylist(m3uContent)
                    if (parsed.isNotEmpty()) {
                        inMemoryChannels = parsed
                        if (cacheFile != null) {
                            saveChannelsToCacheFile(cacheFile, parsed)
                        }
                        return@withContext parsed
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch live channels: ${e.message}", e)
            }

            // If network fetch fails, fallback to cached or built-in channels
            if (cacheFile != null && cacheFile.exists()) {
                runCatching {
                    val cached = readChannelsFromCacheFile(cacheFile)
                    if (cached.isNotEmpty()) {
                        inMemoryChannels = cached
                        return@withContext cached
                    }
                }
            }

            val fallback = getFallbackChannels()
            inMemoryChannels = fallback
            fallback
        }

    fun parseM3uPlaylist(content: String): List<LiveChannel> {
        val channels = mutableListOf<LiveChannel>()
        val blocks = content.split("#EXTINF:")

        val idRegex = Regex("""tvg-id="([^"]+)"""")
        val logoRegex = Regex("""tvg-logo="([^"]+)"""")
        val catRegex = Regex("""group-title="([^"]+)"""")

        for (block in blocks) {
            val trimmed = block.trim()
            if (trimmed.isEmpty()) continue

            val lines = trimmed.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue

            val header = lines[0]
            val name = if (header.contains(",")) header.substringAfterLast(",").trim() else ""

            var userAgent: String? = null
            var cookie: String? = null
            var licenseKey: String? = null
            var streamUrl = ""

            for (i in 1 until lines.size) {
                val l = lines[i]
                when {
                    l.startsWith("#EXTVLCOPT:http-user-agent=") -> {
                        userAgent = l.removePrefix("#EXTVLCOPT:http-user-agent=").trim()
                    }
                    l.startsWith("#EXTHTTP:") -> {
                        runCatching {
                            val rawJson = l.removePrefix("#EXTHTTP:").trim()
                            val json = JSONObject(rawJson)
                            cookie = json.optString("cookie", null)
                        }
                    }
                    l.startsWith("#KODIPROP:inputstream.adaptive.license_key=") -> {
                        licenseKey = l.removePrefix("#KODIPROP:inputstream.adaptive.license_key=").trim()
                    }
                    l.startsWith("http://") || l.startsWith("https://") -> {
                        streamUrl = l
                    }
                }
            }

            if (name.isNotEmpty() && streamUrl.isNotEmpty()) {
                val idMatch = idRegex.find(header)?.groupValues?.getOrNull(1) ?: "ch_${channels.size}"
                val logoMatch = logoRegex.find(header)?.groupValues?.getOrNull(1) ?: ""
                val catMatch = catRegex.find(header)?.groupValues?.getOrNull(1) ?: "Entertainment"

                val isHd = name.contains("HD", ignoreCase = true)

                channels.add(
                    LiveChannel(
                        id = idMatch,
                        name = name,
                        category = normalizeCategory(catMatch, name),
                        logoUrl = logoMatch,
                        streamUrl = streamUrl,
                        userAgent = userAgent,
                        cookie = cookie,
                        licenseKey = licenseKey,
                        isHd = isHd
                    )
                )
            }
        }
        return channels
    }

    private fun normalizeCategory(groupTitle: String, name: String): String {
        val lower = groupTitle.lowercase()
        val nameLower = name.lowercase()
        return when {
            lower.contains("music") || nameLower.contains("9xm") || nameLower.contains("mtv") || nameLower.contains("zoom") || nameLower.contains("music") -> "Music"
            lower.contains("movie") || nameLower.contains("cinema") || nameLower.contains("max") || nameLower.contains("gold") -> "Movies"
            lower.contains("sport") || nameLower.contains("cricket") || nameLower.contains("sports") -> "Sports"
            lower.contains("news") || nameLower.contains("aaj tak") || nameLower.contains("ndtv") || nameLower.contains("abp") -> "News"
            lower.contains("entertainment") -> "Entertainment"
            lower.contains("punjabi") -> "Punjabi"
            lower.contains("kids") -> "Kids"
            lower.contains("devotional") -> "Devotional"
            else -> groupTitle.ifBlank { "Entertainment" }
        }
    }

    private fun saveChannelsToCacheFile(file: File, channels: List<LiveChannel>) {
        runCatching {
            val root = JSONObject()
            val arr = org.json.JSONArray()
            for (ch in channels.take(500)) {
                val obj = JSONObject().apply {
                    put("id", ch.id)
                    put("name", ch.name)
                    put("category", ch.category)
                    put("logoUrl", ch.logoUrl)
                    put("streamUrl", ch.streamUrl)
                    put("userAgent", ch.userAgent)
                    put("cookie", ch.cookie)
                    put("licenseKey", ch.licenseKey)
                    put("isHd", ch.isHd)
                }
                arr.put(obj)
            }
            root.put("channels", arr)
            file.writeText(root.toString())
        }
    }

    private fun readChannelsFromCacheFile(file: File): List<LiveChannel> {
        val content = file.readText()
        val root = JSONObject(content)
        val arr = root.optJSONArray("channels") ?: return emptyList()
        val list = mutableListOf<LiveChannel>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                LiveChannel(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    logoUrl = obj.optString("logoUrl", ""),
                    streamUrl = obj.getString("streamUrl"),
                    userAgent = obj.optString("userAgent", null),
                    cookie = obj.optString("cookie", null),
                    licenseKey = obj.optString("licenseKey", null),
                    isHd = obj.optBoolean("isHd", false)
                )
            )
        }
        return list
    }

    fun getFallbackChannels(): List<LiveChannel> = listOf(
        LiveChannel(
            id = "9xm",
            name = "9XM",
            category = "Music",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/9XM.png",
            streamUrl = "https://d2q8p4pe5spbak.cloudfront.net/bpk-tv/9XM/default/index.m3u8",
            isHd = true
        ),
        LiveChannel(
            id = "9xjalwa",
            name = "9X Jalwa",
            category = "Music",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/9X_Jalwa.png",
            streamUrl = "https://d2q8p4pe5spbak.cloudfront.net/bpk-tv/9XJalwa/default/index.m3u8",
            isHd = true
        ),
        LiveChannel(
            id = "mtv_beats",
            name = "MTV Beats HD",
            category = "Music",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/MTV_Beats_HD.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/MTV_Beats_HD_MOB/WDVLive/index.mpd",
            isHd = true
        ),
        LiveChannel(
            id = "b4u_music",
            name = "B4U Music",
            category = "Music",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/B4U_Music.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/B4U_Music_MOB/WDVLive/index.mpd",
            isHd = true
        ),
        LiveChannel(
            id = "music_india",
            name = "Music India",
            category = "Music",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/Music_India.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/Music_India_MOB/WDVLive/index.mpd",
            isHd = false
        ),
        LiveChannel(
            id = "star_plus_hd",
            name = "Star Plus HD",
            category = "Entertainment",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/Star_Plus_HD.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/Star_Plus_HD_MOB/WDVLive/index.mpd?",
            isHd = true
        ),
        LiveChannel(
            id = "zee_tv_hd",
            name = "Zee TV HD",
            category = "Entertainment",
            logoUrl = "https://jiotv.catchup.cdn.jio.com/dare_images/images/ZeeTVHD.png",
            streamUrl = "https://jiotvbpkstb.cdn.jio.com/bpk-tv/ZeeTVHD_BTS/WDVLive/index.mpd",
            isHd = true
        ),
        LiveChannel(
            id = "sony_max",
            name = "Sony MAX HD",
            category = "Movies",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/SET_MAX_HD.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/SET_MAX_HD_MOB/WDVLive/index.mpd",
            isHd = true
        ),
        LiveChannel(
            id = "star_gold",
            name = "Star Gold HD",
            category = "Movies",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/Star_Gold_HD.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/Star_Gold_HD_MOB/WDVLive/index.mpd",
            isHd = true
        ),
        LiveChannel(
            id = "aaj_tak_hd",
            name = "Aaj Tak HD",
            category = "News",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/Aaj_Tak_HD.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/Aaj_Tak_HD_MOB/WDVLive/index.mpd",
            isHd = true
        ),
        LiveChannel(
            id = "star_sports_1_hd",
            name = "Star Sports 1 HD",
            category = "Sports",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/Star_Sports_1_HD.png",
            streamUrl = "https://jiotvmblive.cdn.jio.com/bpk-tv/Star_Sports_1_HD_MOB/WDVLive/index.mpd",
            isHd = true
        )
    )
}
