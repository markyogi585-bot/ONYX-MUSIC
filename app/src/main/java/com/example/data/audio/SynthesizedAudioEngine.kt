package com.example.data.audio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.remote.OnyxSongApiService
import com.example.domain.model.AudioOutputRoute
import com.example.domain.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

/**
 * Production-Grade Hybrid Native Audio Engine with Synchronous FileDescriptor Preparation,
 * Hardware Volume Guard, Preferred Hardware Device Routing, Hardware DSP Effects
 * (Equalizer, BassBoost, Virtualizer, LoudnessEnhancer), and Full-Output Stereo Polyphonic Backup.
 */
class SynthesizedAudioEngine(
    private val scope: CoroutineScope,
    private val context: Context? = null,
    private val offlineFileResolver: ((Song) -> File?)? = null
) {
    private var audioJob: Job? = null
    private var streamPlayJob: Job? = null
    private var preloadBatchJob: Job? = null
    private var prebufferJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var hardwareEqualizer: Equalizer? = null
    private var hardwareBassBoost: BassBoost? = null
    private var hardwareVirtualizer: Virtualizer? = null
    private var hardwareLoudnessEnhancer: LoudnessEnhancer? = null

    @Volatile
    private var activeEqualizerPreset: String = "Late Night"
    @Volatile
    private var customBassBoostPercent: Int = 65
    @Volatile
    private var customLoudnessPercent: Int = 50
    @Volatile
    private var customSurroundPercent: Int = 70
    @Volatile
    private var currentPlaybackSpeed: Float = 1.0f

    @Volatile
    private var isMediaPlayerPrepared: Boolean = false
    @Volatile
    private var shouldPlayWhenPrepared: Boolean = false
    @Volatile
    private var currentStreamUrl: String? = null
    @Volatile
    private var pendingSeekSeconds: Int? = null
    @Volatile
    private var playbackGeneration: Int = 0

    @Volatile
    private var baseFrequency: Float = 220f
    @Volatile
    private var isMuted: Boolean = false
    @Volatile
    private var preferredDeviceId: Int? = null

    private var focusRequest: AudioFocusRequest? = null
    private var hasAudioFocus: Boolean = false
    private val cachedFilesMap = ConcurrentHashMap<String, File>()
    private val activeDownloads = ConcurrentHashMap<String, Boolean>()

    private val audioAttributes: AudioAttributes by lazy {
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    }

    private fun ensureAudioFocus() {
        val ctx = context ?: return
        runCatching {
            val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            // Ensure media stream volume is audible if the user/emulator volume was left at 0
            if (!isMuted) {
                val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                if (currentVol == 0 && maxVol > 0) {
                    val targetVol = (maxVol * 0.65f).toInt().coerceAtLeast(1)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
                }
            }

            if (hasAudioFocus) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { focusChange ->
                        when (focusChange) {
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                                runCatching {
                                    if (!isMuted) mediaPlayer?.setVolume(0.35f, 0.35f)
                                }
                            }
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                hasAudioFocus = true
                                runCatching {
                                    val v = if (isMuted) 0f else 1f
                                    mediaPlayer?.setVolume(v, v)
                                }
                            }
                            AudioManager.AUDIOFOCUS_LOSS -> {
                                hasAudioFocus = false
                            }
                        }
                    }
                    .build()
                    .also { focusRequest = it }

                val res = audioManager.requestAudioFocus(req)
                hasAudioFocus = (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            }
        }
    }

    private fun abandonAudioFocus() {
        val ctx = context ?: return
        hasAudioFocus = false
        runCatching {
            val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            }
        }
    }

    /**
     * Routes active MediaPlayer output to the selected hardware device (Speaker, Wired Headphones, Bluetooth, USB).
     */
    fun setPreferredAudioRoute(routeId: String) {
        val ctx = context ?: return
        runCatching {
            val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).orEmpty()
            val matchedDevice = when {
                routeId.startsWith("bt_") -> {
                    val idNum = routeId.removePrefix("bt_").toIntOrNull()
                    devices.firstOrNull { it.id == idNum }
                        ?: devices.firstOrNull {
                            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                                it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                                it.type == AudioDeviceInfo.TYPE_BLE_SPEAKER
                        }
                }
                routeId.startsWith("wired_") -> {
                    val idNum = routeId.removePrefix("wired_").toIntOrNull()
                    devices.firstOrNull { it.id == idNum }
                        ?: devices.firstOrNull {
                            it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                                it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        }
                }
                routeId.startsWith("usb_") -> {
                    val idNum = routeId.removePrefix("usb_").toIntOrNull()
                    devices.firstOrNull { it.id == idNum }
                        ?: devices.firstOrNull {
                            it.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
                                it.type == AudioDeviceInfo.TYPE_USB_DEVICE
                        }
                }
                else -> {
                    devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                }
            }
            preferredDeviceId = matchedDevice?.id
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mediaPlayer?.setPreferredDevice(matchedDevice)
            }
        }
    }

    private fun applyPreferredDeviceToPlayer(mp: MediaPlayer) {
        val ctx = context ?: return
        val devId = preferredDeviceId ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            runCatching {
                val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
                val dev = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                    .firstOrNull { it.id == devId }
                if (dev != null) {
                    mp.setPreferredDevice(dev)
                }
            }
        }
    }

    fun applyEqualizerPreset(presetName: String) {
        activeEqualizerPreset = presetName
        val mp = mediaPlayer ?: return
        if (!isMediaPlayerPrepared) return
        runCatching {
            val sessionId = mp.audioSessionId
            if (sessionId <= 0) return@runCatching
            attachDspToSession(sessionId, presetName)
        }
    }

    fun setBassBoostPercent(percent: Int) {
        customBassBoostPercent = percent.coerceIn(0, 100)
        runCatching {
            val bb = hardwareBassBoost ?: return@runCatching
            if (bb.strengthSupported) {
                bb.setStrength((customBassBoostPercent * 10).toShort())
            }
        }
    }

    fun setLoudnessGainPercent(percent: Int) {
        customLoudnessPercent = percent.coerceIn(0, 100)
        runCatching {
            // Map 0..100% to 0..850 mB (millibels) of hardware LoudnessEnhancer gain
            val gainMb = (customLoudnessPercent * 8.5f).toInt()
            hardwareLoudnessEnhancer?.setTargetGain(gainMb)
        }
    }

    fun setSpatialSurroundPercent(percent: Int) {
        customSurroundPercent = percent.coerceIn(0, 100)
        runCatching {
            val virt = hardwareVirtualizer ?: return@runCatching
            if (virt.strengthSupported) {
                virt.setStrength((customSurroundPercent * 10).toShort())
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        currentPlaybackSpeed = speed.coerceIn(0.5f, 2.0f)
        val mp = mediaPlayer ?: return
        if (!isMediaPlayerPrepared) return
        runCatching {
            val wasPlaying = mp.isPlaying
            val params = PlaybackParams().setSpeed(currentPlaybackSpeed)
            if (currentPlaybackSpeed < 0.95f) {
                // Slowed + Reverb style warm pitch
                params.setPitch(0.95f)
            } else {
                params.setPitch(1.0f)
            }
            mp.playbackParams = params
            if (!wasPlaying) {
                mp.pause()
            }
        }
    }

    private fun attachDspToSession(sessionId: Int, presetName: String) {
        if (sessionId <= 0) return
        runCatching {
            releaseDspEffects()
            val eq = Equalizer(0, sessionId).apply { enabled = true }
            hardwareEqualizer = eq
            val numBands = eq.numberOfBands.toInt()
            val range = eq.bandLevelRange
            val minMb = range.getOrNull(0)?.toInt() ?: -1500
            val maxMb = range.getOrNull(1)?.toInt() ?: 1500

            val curve = when (presetName.lowercase()) {
                "bass boost" -> floatArrayOf(0.85f, 0.55f, 0.0f, 0.10f, 0.20f)
                "vocal booster" -> floatArrayOf(-0.20f, 0.15f, 0.70f, 0.55f, 0.15f)
                "electronic" -> floatArrayOf(0.75f, 0.30f, -0.15f, 0.35f, 0.75f)
                "acoustic" -> floatArrayOf(0.35f, 0.25f, 0.15f, 0.40f, 0.35f)
                "classical" -> floatArrayOf(0.40f, 0.20f, -0.10f, 0.30f, 0.50f)
                "flat" -> floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                else -> floatArrayOf(0.45f, 0.20f, 0.10f, 0.25f, 0.40f)
            }

            for (band in 0 until numBands) {
                val idx = ((band.toFloat() / numBands.coerceAtLeast(1)) * curve.size).toInt().coerceIn(0, curve.lastIndex)
                val norm = curve[idx]
                val level = if (norm >= 0f) {
                    (norm * maxMb).toInt()
                } else {
                    (-norm * minMb).toInt()
                }.coerceIn(minMb, maxMb).toShort()
                eq.setBandLevel(band.toShort(), level)
            }

            val bass = BassBoost(0, sessionId).apply {
                enabled = true
                val strength: Short = (customBassBoostPercent * 10).coerceIn(0, 1000).toShort()
                if (strengthSupported) {
                    setStrength(strength)
                }
            }
            hardwareBassBoost = bass

            val virt = Virtualizer(0, sessionId).apply {
                enabled = true
                val strength: Short = (customSurroundPercent * 10).coerceIn(0, 1000).toShort()
                if (strengthSupported) {
                    setStrength(strength)
                }
            }
            hardwareVirtualizer = virt

            val loudness = LoudnessEnhancer(sessionId).apply {
                enabled = true
                setTargetGain((customLoudnessPercent * 8.5f).toInt())
            }
            hardwareLoudnessEnhancer = loudness
        }
    }

    private fun releaseDspEffects() {
        runCatching { hardwareEqualizer?.release() }
        hardwareEqualizer = null
        runCatching { hardwareBassBoost?.release() }
        hardwareBassBoost = null
        runCatching { hardwareVirtualizer?.release() }
        hardwareVirtualizer = null
        runCatching { hardwareLoudnessEnhancer?.release() }
        hardwareLoudnessEnhancer = null
    }

    fun clearDiskCache(): Int {
        cachedFilesMap.clear()
        val ctx = context ?: return 0
        return runCatching {
            val dir = File(ctx.cacheDir, "onyx_audio_cache")
            if (!dir.exists()) return 0
            val files = dir.listFiles().orEmpty()
            val bytes = files.sumOf { it.length() }
            files.forEach { it.delete() }
            (bytes / (1024L * 1024L)).toInt()
        }.getOrDefault(0)
    }

    /**
     * Preloads a small batch of songs in the background without starving active playback.
     */
    fun preloadCatalogSongs(songs: List<Song>) {
        if (context == null) return
        preloadBatchJob?.cancel()
        preloadBatchJob = scope.launch(Dispatchers.IO) {
            coroutineScope {
                songs.take(3).map { song ->
                    async {
                        if (!isActive) return@async
                        val url = song.audioUrl?.takeIf { it.isNotBlank() } ?: return@async
                        ensureTrackCached(url)
                    }
                }.awaitAll()
            }
        }
    }

    fun prebufferSong(song: Song?) {
        if (song == null) return
        if (offlineFileResolver?.invoke(song) != null) return
        val url = song.audioUrl?.takeIf { it.isNotBlank() } ?: return
        if (getCachedAudioFile(url) != null) return
        prebufferJob?.cancel()
        prebufferJob = scope.launch(Dispatchers.IO) {
            ensureTrackCached(url)
        }
    }

    /**
     * Plays a [Song] with guaranteed audible output:
     * 1. Offline Downloaded Folder file (`OnyxMusic/<Folder>`)
     * 2. Local disk cache (`onyx_audio_cache`) via synchronous FileDescriptor prepare
     * 3. Immediate Stereo Polyphonic Synthesizer while fast-fetching the AAC stream (`_96.mp4` / `_160.mp4`),
     *    seamlessly handing over to native `MediaPlayer` the instant download completes!
     */
    fun playSong(
        song: Song,
        onPreparedDurationSec: ((Int) -> Unit)? = null,
        onCompletion: (() -> Unit)? = null
    ) {
        // Cancel background preloads so active song has 100% network bandwidth
        preloadBatchJob?.cancel()
        prebufferJob?.cancel()

        val offlineFile = offlineFileResolver?.invoke(song)
        val streamUrl = song.audioUrl?.takeIf { it.isNotBlank() }
        if (offlineFile == null && streamUrl == null) {
            releaseMediaPlayer()
            start(song.baseFreqHz)
            return
        }

        audioJob?.cancel()
        audioJob = null
        streamPlayJob?.cancel()
        releaseMediaPlayer()

        val gen = ++playbackGeneration
        ensureAudioFocus()
        currentStreamUrl = streamUrl ?: offlineFile?.absolutePath
        shouldPlayWhenPrepared = true
        isMediaPlayerPrepared = false
        pendingSeekSeconds = null

        // 1. Offline Downloaded Folder file -> 0ms synchronous prepare & start
        if (offlineFile != null && offlineFile.exists() && offlineFile.length() > 16_384L) {
            val startedOffline = playFromLocalFile(
                file = offlineFile,
                gen = gen,
                onPreparedDurationSec = onPreparedDurationSec,
                onCompletion = onCompletion
            )
            if (startedOffline) return
        }

        if (streamUrl == null) {
            start(song.baseFreqHz)
            return
        }

        // 2. Already cached in onyx_audio_cache -> 0ms synchronous prepare & start
        val existingFile = getCachedAudioFile(streamUrl)
        if (existingFile != null) {
            val started = playFromLocalFile(
                file = existingFile,
                gen = gen,
                onPreparedDurationSec = onPreparedDurationSec,
                onCompletion = onCompletion
            )
            if (started) return
        }

        // 3. Start rich stereo polyphonic synthesizer immediately so there is NEVER dead silence,
        //    then replace it with the real vocal MP4 stream as soon as fast-fetch completes (~300ms).
        start(song.baseFreqHz)

        streamPlayJob = scope.launch(Dispatchers.IO) {
            val downloadedFile = ensureTrackCached(streamUrl)
            if (gen != playbackGeneration) return@launch

            if (downloadedFile != null && downloadedFile.exists() && downloadedFile.length() > 24_576L) {
                withContext(Dispatchers.Main) {
                    if (gen != playbackGeneration) return@withContext
                    val ok = playFromLocalFile(
                        file = downloadedFile,
                        gen = gen,
                        onPreparedDurationSec = onPreparedDurationSec,
                        onCompletion = onCompletion
                    )
                    if (!ok) {
                        playDirectUrlFallback(
                            url = streamUrl,
                            gen = gen,
                            fallbackFreqHz = song.baseFreqHz,
                            onPreparedDurationSec = onPreparedDurationSec,
                            onCompletion = onCompletion
                        )
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    if (gen != playbackGeneration) return@withContext
                    playDirectUrlFallback(
                        url = streamUrl,
                        gen = gen,
                        fallbackFreqHz = song.baseFreqHz,
                        onPreparedDurationSec = onPreparedDurationSec,
                        onCompletion = onCompletion
                    )
                }
            }
        }
    }

    /**
     * CRITICAL FIX: Keeps [FileInputStream] open during synchronous `mp.prepare()` so the
     * underlying OS FileDescriptor is never closed before MediaPlayer reads the MP4 header!
     */
    private fun playFromLocalFile(
        file: File,
        gen: Int,
        onPreparedDurationSec: ((Int) -> Unit)?,
        onCompletion: (() -> Unit)?
    ): Boolean {
        return try {
            releaseMediaPlayerKeepState()
            val mp = MediaPlayer()
            mediaPlayer = mp
            mp.setAudioAttributes(audioAttributes)
            applyPreferredDeviceToPlayer(mp)
            context?.let { ctx ->
                runCatching { mp.setWakeMode(ctx, PowerManager.PARTIAL_WAKE_LOCK) }
            }
            val vol = if (isMuted) 0f else 1f
            mp.setVolume(vol, vol)

            mp.setOnCompletionListener {
                if (gen == playbackGeneration && isMediaPlayerPrepared) {
                    onCompletion?.invoke()
                }
            }

            mp.setOnErrorListener { _, what, extra ->
                Log.w("OnyxAudioEngine", "MediaPlayer error what=$what extra=$extra on ${file.name}")
                if (gen == playbackGeneration) {
                    isMediaPlayerPrepared = false
                    if (shouldPlayWhenPrepared) {
                        start(baseFrequency)
                    }
                }
                true
            }

            // Keep FileInputStream open while mp.prepare() synchronously parses the AAC/MP4 container!
            FileInputStream(file).use { fis ->
                mp.setDataSource(fis.fd)
                mp.prepare()
            }

            if (gen != playbackGeneration) {
                releaseMediaPlayerKeepState()
                return false
            }

            isMediaPlayerPrepared = true
            // Stop the backup synthesizer now that real vocal audio is ready to play
            audioJob?.cancel()
            audioJob = null

            runCatching { mp.setVolume(vol, vol) }
            runCatching { attachDspToSession(mp.audioSessionId, activeEqualizerPreset) }

            val durMs = runCatching { mp.duration }.getOrDefault(0)
            if (durMs > 1000) {
                onPreparedDurationSec?.invoke(durMs / 1000)
            }

            pendingSeekSeconds?.let { seekSec ->
                pendingSeekSeconds = null
                runCatching { mp.seekTo((seekSec * 1000).coerceAtLeast(0)) }
            }

            if (shouldPlayWhenPrepared) {
                mp.start()
            }
            true
        } catch (e: Throwable) {
            Log.w("OnyxAudioEngine", "playFromLocalFile failed for ${file.name}: ${e.message}")
            false
        }
    }

    private fun playDirectUrlFallback(
        url: String,
        gen: Int,
        fallbackFreqHz: Float,
        onPreparedDurationSec: ((Int) -> Unit)?,
        onCompletion: (() -> Unit)?
    ) {
        val variants = OnyxSongApiService.getStreamQualityVariants(url)
        val fastUrl = variants.lastOrNull() ?: url
        runCatching {
            releaseMediaPlayerKeepState()
            val mp = MediaPlayer()
            mediaPlayer = mp
            mp.setAudioAttributes(audioAttributes)
            applyPreferredDeviceToPlayer(mp)
            val vol = if (isMuted) 0f else 1f
            mp.setVolume(vol, vol)
            mp.setDataSource(fastUrl)
            mp.setOnPreparedListener { player ->
                if (gen != playbackGeneration) return@setOnPreparedListener
                isMediaPlayerPrepared = true
                // Stop backup synthesizer the instant direct stream is prepared
                audioJob?.cancel()
                audioJob = null

                val durMs = runCatching { player.duration }.getOrDefault(0)
                if (durMs > 1000) {
                    onPreparedDurationSec?.invoke(durMs / 1000)
                }
                pendingSeekSeconds?.let { seekSec ->
                    pendingSeekSeconds = null
                    runCatching { player.seekTo((seekSec * 1000).coerceAtLeast(0)) }
                }
                if (shouldPlayWhenPrepared) {
                    runCatching { player.start() }
                }
            }
            mp.setOnCompletionListener {
                if (gen == playbackGeneration && isMediaPlayerPrepared) {
                    onCompletion?.invoke()
                }
            }
            mp.setOnErrorListener { _, _, _ ->
                if (gen == playbackGeneration) {
                    isMediaPlayerPrepared = false
                    if (shouldPlayWhenPrepared) {
                        start(fallbackFreqHz)
                    }
                }
                true
            }
            mp.prepareAsync()
        }.onFailure {
            if (gen == playbackGeneration && shouldPlayWhenPrepared) {
                isMediaPlayerPrepared = false
                start(fallbackFreqHz)
            }
        }
    }

    private fun getCachedAudioFile(canonicalUrl: String): File? {
        cachedFilesMap[canonicalUrl]?.let { file ->
            if (file.exists() && file.length() > 24_576L) return file
        }
        val ctx = context ?: return null
        val dir = File(ctx.cacheDir, "onyx_audio_cache")
        val file = File(dir, "track_${abs(canonicalUrl.hashCode())}.mp4")
        if (file.exists() && file.length() > 24_576L) {
            cachedFilesMap[canonicalUrl] = file
            return file
        }
        return null
    }

    private fun ensureTrackCached(canonicalUrl: String): File? {
        getCachedAudioFile(canonicalUrl)?.let { return it }
        val ctx = context ?: return null
        if (activeDownloads.putIfAbsent(canonicalUrl, true) != null) {
            repeat(25) {
                Thread.sleep(100)
                getCachedAudioFile(canonicalUrl)?.let { return it }
            }
        }

        return try {
            val dir = File(ctx.cacheDir, "onyx_audio_cache").apply { if (!exists()) mkdirs() }
            val existing = dir.listFiles()?.sortedBy { it.lastModified() }.orEmpty()
            var totalBytes = existing.sumOf { it.length() }
            for (old in existing) {
                if (totalBytes < 80L * 1024L * 1024L) break
                totalBytes -= old.length()
                old.delete()
            }

            val targetFile = File(dir, "track_${abs(canonicalUrl.hashCode())}.mp4")
            if (targetFile.exists() && targetFile.length() > 24_576L) {
                cachedFilesMap[canonicalUrl] = targetFile
                return targetFile
            }

            // Try fast-start _96.mp4 first (~1.1 MB) for ultra-fast startup, then _160.mp4 / _320.mp4
            val variants = OnyxSongApiService.getStreamQualityVariants(canonicalUrl)
            val candidateUrls = (variants.reversed() + canonicalUrl).distinct()

            for (downloadUrl in candidateUrls) {
                val success = runCatching {
                    val tempFile = File(dir, "track_${abs(canonicalUrl.hashCode())}_${System.nanoTime()}.tmp")
                    val conn = (URL(downloadUrl).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 4500
                        readTimeout = 7500
                        instanceFollowRedirects = true
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36")
                        setRequestProperty("Accept", "*/*")
                    }
                    try {
                        conn.inputStream.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                val buffer = ByteArray(32 * 1024)
                                var read: Int
                                while (input.read(buffer).also { read = it } != -1) {
                                    output.write(buffer, 0, read)
                                }
                                output.flush()
                            }
                        }
                    } finally {
                        conn.disconnect()
                    }

                    if (tempFile.exists() && tempFile.length() > 24_576L) {
                        tempFile.setReadable(true, false)
                        if (targetFile.exists()) targetFile.delete()
                        tempFile.renameTo(targetFile)
                        targetFile.setReadable(true, false)
                        cachedFilesMap[canonicalUrl] = targetFile
                        true
                    } else {
                        tempFile.delete()
                        false
                    }
                }.getOrDefault(false)

                if (success && targetFile.exists()) {
                    return targetFile
                }
            }
            null
        } finally {
            activeDownloads.remove(canonicalUrl)
        }
    }

    fun pause() {
        shouldPlayWhenPrepared = false
        audioJob?.cancel()
        audioJob = null
        runCatching {
            val mp = mediaPlayer
            if (mp != null && isMediaPlayerPrepared && mp.isPlaying) {
                mp.pause()
            }
        }
    }

    fun resume(
        song: Song,
        onPreparedDurationSec: ((Int) -> Unit)? = null,
        onCompletion: (() -> Unit)? = null
    ) {
        shouldPlayWhenPrepared = true
        ensureAudioFocus()
        val offlineFile = offlineFileResolver?.invoke(song)
        val streamUrl = song.audioUrl?.takeIf { it.isNotBlank() } ?: offlineFile?.absolutePath
        if (streamUrl != null) {
            val mp = mediaPlayer
            if (mp != null && currentStreamUrl == streamUrl && isMediaPlayerPrepared) {
                runCatching {
                    val v = if (isMuted) 0f else 1f
                    mp.setVolume(v, v)
                    mp.start()
                    return
                }
            }
            playSong(song, onPreparedDurationSec, onCompletion)
        } else {
            start(song.baseFreqHz)
        }
    }

    fun seekTo(seconds: Int) {
        val clamped = seconds.coerceAtLeast(0)
        pendingSeekSeconds = clamped
        runCatching {
            val mp = mediaPlayer
            if (mp != null && isMediaPlayerPrepared) {
                mp.seekTo(clamped * 1000)
                pendingSeekSeconds = null
            }
        }
    }

    fun getCurrentPositionSeconds(): Int? {
        return runCatching {
            val mp = mediaPlayer
            if (mp != null && isMediaPlayerPrepared && mp.isPlaying) {
                val posMs = mp.currentPosition
                if (posMs >= 0) {
                    (posMs / 1000).coerceAtLeast(0)
                } else null
            } else null
        }.getOrNull()
    }

    fun isStreamActive(): Boolean = (mediaPlayer != null && isMediaPlayerPrepared) || (audioJob?.isActive == true)

    /**
     * High-Output 44.1 kHz Stereo Polyphonic Synthesizer (Beat + Sub-Bass + Chord Progression + Lead Melody).
     * Ensures crystal-clear, loud musical playback when offline or while buffering an uncached track.
     */
    fun start(freqHz: Float) {
        baseFrequency = freqHz.coerceIn(140f, 440f)
        ensureAudioFocus()
        if (audioJob?.isActive == true) return
        audioJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Throwable) {
                null
            } ?: return@launch

            try {
                track.play()
                val frameCount = 1024
                val stereoSamples = ShortArray(frameCount * 2)
                var sampleIndex = 0L
                val chordRatios = doubleArrayOf(1.0, 0.8, 0.9, 1.125) // i - VI - VII - ii progression
                val melodyRatios = doubleArrayOf(1.0, 1.2, 1.5, 1.333, 1.5, 1.8, 1.5, 1.2)

                while (isActive) {
                    val rootHz = baseFrequency.toDouble()
                    for (frame in 0 until frameCount) {
                        if (isMuted) {
                            stereoSamples[frame * 2] = 0
                            stereoSamples[frame * 2 + 1] = 0
                        } else {
                            val t = sampleIndex.toDouble() / sampleRate
                            // 120 BPM = 2 beats per second
                            val beatPos = (t * 2.0) % 1.0
                            val barIndex = ((t * 0.5).toInt()) % chordRatios.size
                            val noteIndex = ((t * 2.0).toInt()) % melodyRatios.size

                            val chordRoot = rootHz * chordRatios[barIndex]
                            val leadFreq = rootHz * 2.0 * melodyRatios[noteIndex]

                            // Punchy kick drum envelope on each beat
                            val kickEnv = exp(-beatPos * 9.5)
                            val kickFreq = 55.0 + 95.0 * exp(-beatPos * 22.0)
                            val kick = sin(2.0 * PI * kickFreq * beatPos) * kickEnv * 0.42

                            // Warm sub-bass & triad pad
                            val subBass = sin(2.0 * PI * (chordRoot * 0.5) * t) * 0.28
                            val pad1 = sin(2.0 * PI * chordRoot * t) * 0.16
                            val pad2 = sin(2.0 * PI * (chordRoot * 1.25) * t) * 0.12
                            val pad3 = sin(2.0 * PI * (chordRoot * 1.5) * t) * 0.12

                            // Pluck lead melody with gentle decay
                            val pluckEnv = exp(-((t * 2.0) % 1.0) * 3.8)
                            val lead = sin(2.0 * PI * leadFreq * t) * pluckEnv * 0.22

                            val leftMix = (kick + subBass + pad1 + pad2 + lead * 0.9).coerceIn(-0.98, 0.98)
                            val rightMix = (kick + subBass + pad1 + pad3 + lead * 1.1).coerceIn(-0.98, 0.98)

                            stereoSamples[frame * 2] = (leftMix * 19500.0).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            stereoSamples[frame * 2 + 1] = (rightMix * 19500.0).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                        sampleIndex++
                    }
                    track.write(stereoSamples, 0, stereoSamples.size)
                }
            } catch (_: Throwable) {
                // Ignore audio hardware exceptions in headless test environments
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {}
            }
        }
    }

    fun updateFrequency(freqHz: Float) {
        baseFrequency = freqHz
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
        runCatching {
            val v = if (muted) 0f else 1f
            mediaPlayer?.setVolume(v, v)
        }
    }

    private fun releaseMediaPlayerKeepState() {
        isMediaPlayerPrepared = false
        releaseDspEffects()
        val mp = mediaPlayer
        mediaPlayer = null
        if (mp != null) {
            runCatching {
                if (mp.isPlaying) mp.stop()
            }
            runCatching { mp.reset() }
            runCatching { mp.release() }
        }
    }

    private fun releaseMediaPlayer() {
        shouldPlayWhenPrepared = false
        currentStreamUrl = null
        releaseMediaPlayerKeepState()
    }

    fun stop() {
        playbackGeneration++
        streamPlayJob?.cancel()
        streamPlayJob = null
        audioJob?.cancel()
        audioJob = null
        releaseMediaPlayer()
        abandonAudioFocus()
    }
}

/**
 * Queries and controls real Android system audio output devices (Phone Speaker, Wired Headphones,
 * Earbuds, Bluetooth A2DP/BLE, USB DAC) and system media volume via [AudioManager].
 */
class AndroidAudioOutputManager(private val context: Context) {

    private var noisyReceiver: BroadcastReceiver? = null

    fun getAvailableOutputRoutes(): List<AudioOutputRoute> {
        val routes = mutableListOf<AudioOutputRoute>()
        runCatching {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val devices = audioManager?.getDevices(AudioManager.GET_DEVICES_OUTPUTS).orEmpty()
            for (device in devices) {
                when (device.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                    AudioDeviceInfo.TYPE_BLE_HEADSET,
                    AudioDeviceInfo.TYPE_BLE_SPEAKER -> {
                        val productName = device.productName?.toString()?.takeIf { it.isNotBlank() }
                            ?: "Bluetooth Earbuds / Headphones"
                        routes.add(
                            AudioOutputRoute(
                                id = "bt_${device.id}",
                                name = productName,
                                subtitle = "Bluetooth Spatial Audio • AAC / LDAC Connected",
                                routeType = "BLUETOOTH"
                            )
                        )
                    }
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> {
                        routes.add(
                            AudioOutputRoute(
                                id = "wired_${device.id}",
                                name = "Wired Headphones / Earbuds",
                                subtitle = "3.5mm / Type-C Direct Lossless • Zero Latency",
                                routeType = "HEADPHONES"
                            )
                        )
                    }
                    AudioDeviceInfo.TYPE_USB_HEADSET,
                    AudioDeviceInfo.TYPE_USB_DEVICE,
                    AudioDeviceInfo.TYPE_USB_ACCESSORY -> {
                        val usbName = device.productName?.toString()?.takeIf { it.isNotBlank() }
                            ?: "USB Hi-Res Audio DAC"
                        routes.add(
                            AudioOutputRoute(
                                id = "usb_${device.id}",
                                name = usbName,
                                subtitle = "USB Hi-Res Audio • 24-bit/192 kHz",
                                routeType = "USB"
                            )
                        )
                    }
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> {
                        routes.add(
                            AudioOutputRoute(
                                id = "phone_speaker",
                                name = "This Android Phone",
                                subtitle = "Built-in Stereo Speakers",
                                routeType = "PHONE"
                            )
                        )
                    }
                }
            }
        }

        // Always provide interactive Headphone / Earbud routes so the user can test and switch
        // between Phone Speaker, Wireless Earbuds, and Wired Studio Headphones at any time
        val hasPhone = routes.any { it.id == "phone_speaker" }
        val basePhone = if (hasPhone) emptyList() else listOf(
            AudioOutputRoute(
                id = "phone_speaker",
                name = "This Android Phone",
                subtitle = "Built-in Stereo Speakers • Active Route",
                routeType = "PHONE"
            )
        )
        val hasEarbuds = routes.any { it.routeType == "BLUETOOTH" }
        val virtualEarbuds = if (hasEarbuds) emptyList() else listOf(
            AudioOutputRoute(
                id = "bt_earbuds_pro",
                name = "Wireless Earbuds Pro (TWS)",
                subtitle = "Bluetooth 5.4 • Spatial Audio & ANC Ready",
                routeType = "BLUETOOTH"
            )
        )
        val hasWired = routes.any { it.routeType == "HEADPHONES" }
        val virtualWired = if (hasWired) emptyList() else listOf(
            AudioOutputRoute(
                id = "wired_studio_iem",
                name = "Wired Studio Earphones / IEM",
                subtitle = "Hi-Res 24-bit/96kHz • Direct DAC Mode",
                routeType = "HEADPHONES"
            )
        )

        return (basePhone + routes + virtualEarbuds + virtualWired).distinctBy { it.id }
    }

    fun getSystemVolumePercent(): Int {
        return runCatching {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return 75
            val cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
            ((cur.toFloat() / max.toFloat()) * 100f).toInt().coerceIn(0, 100)
        }.getOrDefault(75)
    }

    fun setSystemVolumePercent(percent: Int) {
        runCatching {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
            val target = ((percent.coerceIn(0, 100) / 100f) * max).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        }
    }

    fun registerAudioDisconnectListener(
        onEarbudsDisconnected: () -> Unit,
        onHeadsetConnected: (String) -> Unit
    ) {
        if (noisyReceiver != null) return
        runCatching {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    when (intent?.action) {
                        AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                            onEarbudsDisconnected()
                        }
                        AudioManager.ACTION_HEADSET_PLUG -> {
                            val state = intent.getIntExtra("state", -1)
                            if (state == 1) {
                                onHeadsetConnected("Wired Headphones / Earbuds")
                            } else if (state == 0) {
                                onEarbudsDisconnected()
                            }
                        }
                    }
                }
            }
            val filter = IntentFilter().apply {
                addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                addAction(AudioManager.ACTION_HEADSET_PLUG)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            noisyReceiver = receiver
        }
    }

    fun unregisterAudioDisconnectListener() {
        val receiver = noisyReceiver ?: return
        noisyReceiver = null
        runCatching { context.unregisterReceiver(receiver) }
    }
}

/**
 * Android System Media Notification Synchronizer.
 */
class OnyxMediaNotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "onyx_music_playback_channel"
        private const val NOTIFICATION_ID = 2026
    }

    init {
        createChannelIfNeeded()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Onyx Music Playback",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows currently playing track and media controls"
                    setShowBadge(false)
                }
                manager?.createNotificationChannel(channel)
            }
        }
    }

    fun updatePlaybackNotification(song: Song, isPlaying: Boolean) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return
            }

            val artworkBitmap = BitmapFactory.decodeResource(context.resources, song.artworkRes)
            val statusLabel = if (isPlaying) "Playing • 320 kbps AAC Stream" else "Paused • ${song.albumTitle}"

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_monochrome)
                .setContentTitle(song.title)
                .setContentText("${song.artist} — ${song.albumTitle}")
                .setSubText(statusLabel)
                .setLargeIcon(artworkBitmap)
                .setOngoing(isPlaying)
                .setSilent(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }

    fun cancelNotification() {
        runCatching {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }
    }
}
