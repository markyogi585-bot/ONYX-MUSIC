package com.example.data.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

/**
 * Foreground Media Playback Service for ONYX MUSIC.
 * Keeps audio playing smoothly in the background when the user switches apps or locks the screen,
 * holds CPU WakeLock + High-Performance WifiLock, and exposes rich notification controls.
 */
class OnyxPlaybackService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var wifiLock: WifiManager.WifiLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireLocks()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE_PLAY -> onTogglePlayCallback?.invoke()
            ACTION_NEXT -> onNextCallback?.invoke()
            ACTION_PREV -> onPrevCallback?.invoke()
            ACTION_STOP -> {
                releaseLocks()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }

        val title = intent?.getStringExtra(EXTRA_TITLE) ?: currentTitle
        val artist = intent?.getStringExtra(EXTRA_ARTIST) ?: currentArtist
        val quality = intent?.getStringExtra(EXTRA_QUALITY) ?: currentQuality
        val isPlaying = intent?.getBooleanExtra(EXTRA_IS_PLAYING, currentIsPlaying) ?: currentIsPlaying

        currentTitle = title
        currentArtist = artist
        currentQuality = quality
        currentIsPlaying = isPlaying

        if (isPlaying) {
            acquireLocks()
        }

        val notification = buildNotification(title, artist, quality, isPlaying)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (_: Exception) {
            // In unit test or restricted background start context, post via NotificationManager
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.notify(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    private fun acquireLocks() {
        try {
            if (wakeLock == null) {
                val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
                wakeLock = pm?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "OnyxMusic::BackgroundPlaybackWakeLock"
                )?.apply {
                    setReferenceCounted(false)
                }
            }
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire(4 * 60 * 60 * 1000L) // Up to 4 hours continuous playback
            }

            if (wifiLock == null) {
                val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                @Suppress("DEPRECATION")
                val lockMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    WifiManager.WIFI_MODE_FULL_LOW_LATENCY
                } else {
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF
                }
                wifiLock = wm?.createWifiLock(lockMode, "OnyxMusic::BackgroundWifiLock")?.apply {
                    setReferenceCounted(false)
                }
            }
            if (wifiLock?.isHeld == false) {
                wifiLock?.acquire()
            }
        } catch (_: Exception) {
        }
    }

    private fun releaseLocks() {
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
            if (wifiLock?.isHeld == true) wifiLock?.release()
        } catch (_: Exception) {
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Onyx Music Background Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "24/7 Lossless & Dolby Atmos background playback controls"
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(
        title: String,
        artist: String,
        quality: String,
        isPlaying: Boolean
    ): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            100,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevPending = serviceActionPendingIntent(ACTION_PREV, 101)
        val togglePending = serviceActionPendingIntent(ACTION_TOGGLE_PLAY, 102)
        val nextPending = serviceActionPendingIntent(ACTION_NEXT, 103)

        val playPauseLabel = if (isPlaying) "Pause" else "Play"
        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_onyx_music)
            .setContentTitle(title)
            .setContentText("$artist • $quality")
            .setSubText("Onyx Hi-Res Background Engine")
            .setContentIntent(contentPendingIntent)
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPending)
            .addAction(playPauseIcon, playPauseLabel, togglePending)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPending)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun serviceActionPendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, OnyxPlaybackService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        releaseLocks()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "onyx_playback_channel"
        const val NOTIFICATION_ID = 4096

        const val ACTION_UPDATE = "com.example.data.audio.ACTION_UPDATE"
        const val ACTION_TOGGLE_PLAY = "com.example.data.audio.ACTION_TOGGLE_PLAY"
        const val ACTION_NEXT = "com.example.data.audio.ACTION_NEXT"
        const val ACTION_PREV = "com.example.data.audio.ACTION_PREV"
        const val ACTION_STOP = "com.example.data.audio.ACTION_STOP"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_QUALITY = "extra_quality"
        const val EXTRA_IS_PLAYING = "extra_is_playing"

        @Volatile
        private var currentTitle: String = "Dhurandhar - Title Track"
        @Volatile
        private var currentArtist: String = "Hanumankind, Jasmine Sandlas"
        @Volatile
        private var currentQuality: String = "320kbps AAC • Dolby Atmos"
        @Volatile
        private var currentIsPlaying: Boolean = false

        var onTogglePlayCallback: (() -> Unit)? = null
        var onNextCallback: (() -> Unit)? = null
        var onPrevCallback: (() -> Unit)? = null

        fun syncPlaybackState(
            context: Context,
            title: String,
            artist: String,
            qualityBadge: String,
            isPlaying: Boolean,
            enabled: Boolean = true
        ) {
            try {
                val appContext = context.applicationContext
                if (!enabled) {
                    val stopIntent = Intent(appContext, OnyxPlaybackService::class.java).apply {
                        action = ACTION_STOP
                    }
                    appContext.startService(stopIntent)
                    return
                }
                val intent = Intent(appContext, OnyxPlaybackService::class.java).apply {
                    action = ACTION_UPDATE
                    putExtra(EXTRA_TITLE, title)
                    putExtra(EXTRA_ARTIST, artist)
                    putExtra(EXTRA_QUALITY, qualityBadge)
                    putExtra(EXTRA_IS_PLAYING, isPlaying)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isPlaying) {
                    appContext.startForegroundService(intent)
                } else {
                    appContext.startService(intent)
                }
            } catch (_: Exception) {
                // Ignore background service start restrictions during JVM tests
            }
        }
    }
}
