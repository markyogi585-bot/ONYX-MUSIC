package com.example.data.video

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.domain.model.LiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LivePlayerState(
    val currentChannel: LiveChannel? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null,
    val isMuted: Boolean = false,
    val isFullscreen: Boolean = false
)

@OptIn(UnstableApi::class)
class LiveStreamPlayerEngine(private val context: Context) {
    private val TAG = "LiveStreamPlayer"

    private var exoPlayer: ExoPlayer? = null

    private val _playerState = MutableStateFlow(LivePlayerState())
    val playerState: StateFlow<LivePlayerState> = _playerState.asStateFlow()

    val player: ExoPlayer
        get() {
            if (exoPlayer == null) {
                initPlayer()
            }
            return exoPlayer!!
        }

    private fun initPlayer() {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 2000,
                /* maxBufferMs = */ 6000,
                /* bufferForPlaybackMs = */ 500,
                /* bufferForPlaybackAfterRebufferMs = */ 1000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val player = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = true,
                            errorMessage = null
                        )
                    }
                    Player.STATE_READY -> {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = false,
                            isPlaying = player.isPlaying,
                            errorMessage = null
                        )
                    }
                    Player.STATE_ENDED -> {
                        _playerState.value = _playerState.value.copy(
                            isPlaying = false,
                            isBuffering = false
                        )
                    }
                    Player.STATE_IDLE -> {
                        _playerState.value = _playerState.value.copy(
                            isPlaying = false,
                            isBuffering = false
                        )
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "LiveStream error: ${error.message}", error)
                _playerState.value = _playerState.value.copy(
                    isBuffering = false,
                    isPlaying = false,
                    errorMessage = "Stream connecting... retrying in buffer-free mode"
                )
            }
        })

        exoPlayer = player
    }

    fun playChannel(channel: LiveChannel) {
        val player = this.player

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(channel.userAgent ?: "vortex")
            .setAllowCrossProtocolRedirects(true)
            .setKeepPostFor302Redirects(true)
            .setConnectTimeoutMs(10000)
            .setReadTimeoutMs(10000)

        if (!channel.cookie.isNullOrBlank()) {
            dataSourceFactory.setDefaultRequestProperties(mapOf("Cookie" to channel.cookie))
        }

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)

        val mediaItemBuilder = MediaItem.Builder()
            .setUri(Uri.parse(channel.streamUrl))

        if (!channel.licenseKey.isNullOrBlank() && channel.licenseKey.startsWith("http")) {
            mediaItemBuilder.setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID)
                    .setLicenseUri(channel.licenseKey)
                    .build()
            )
        }

        val mediaSource = mediaSourceFactory.createMediaSource(mediaItemBuilder.build())

        _playerState.value = _playerState.value.copy(
            currentChannel = channel,
            isBuffering = true,
            isPlaying = true,
            errorMessage = null
        )

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun setMuted(muted: Boolean) {
        exoPlayer?.let { player ->
            player.volume = if (muted) 0f else 1f
            _playerState.value = _playerState.value.copy(isMuted = muted)
        }
    }

    fun toggleMute() {
        exoPlayer?.let { player ->
            val nextMuted = !_playerState.value.isMuted
            player.volume = if (nextMuted) 0f else 1f
            _playerState.value = _playerState.value.copy(isMuted = nextMuted)
        }
    }

    fun toggleFullscreen() {
        val next = !_playerState.value.isFullscreen
        _playerState.value = _playerState.value.copy(isFullscreen = next)
    }

    fun setFullscreen(fullscreen: Boolean) {
        _playerState.value = _playerState.value.copy(isFullscreen = fullscreen)
    }

    fun stop() {
        exoPlayer?.stop()
        _playerState.value = _playerState.value.copy(
            currentChannel = null,
            isPlaying = false,
            isBuffering = false,
            isFullscreen = false
        )
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
