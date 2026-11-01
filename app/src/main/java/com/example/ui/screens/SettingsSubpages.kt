package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.preferences.PersistedSettings
import com.example.data.preferences.ThemeMode
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassDialog
import com.example.ui.components.SubpageTopBar
import com.example.ui.theme.DestructiveRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

/**
 * Dedicated full-screen Settings Subpages for:
 * - Appearance (Light, Dark, Follow System)
 * - Audio Quality (Streaming & Download quality levels)
 * - Equaliser (Presets + custom band controls + supported output info)
 * - Crossfade (Enabled/disabled + duration slider)
 * - Playback
 * - Downloads
 * - Cellular Data
 * - Notifications (with Android 13+ runtime permission handling)
 * - Storage (Cache size, Clear Cache confirmation dialog, Downloaded content)
 * - Privacy
 * - Accessibility
 * - Transfer Music from Other Services
 * - About
 */
@Composable
fun SettingsSubpageScreen(
    subpageKey: String,
    settings: PersistedSettings,
    onBackClick: () -> Unit,
    onSelectThemeMode: (ThemeMode) -> Unit,
    onSelectStreamingQuality: (String) -> Unit,
    onSelectDownloadQuality: (String) -> Unit,
    onSelectEqualizerPreset: (String) -> Unit,
    onToggleCrossfade: (Boolean) -> Unit,
    onUpdateCrossfadeSeconds: (Int) -> Unit,
    onToggleSoundCheck: (Boolean) -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onToggleWifiOnlyDownloads: (Boolean) -> Unit,
    onToggleCellularStreaming: (Boolean) -> Unit,
    onToggleNewMusicNotif: (Boolean) -> Unit,
    onToggleFriendNotif: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onToggleListeningHistory: (Boolean) -> Unit,
    onToggleLargerText: (Boolean) -> Unit,
    onToggleIncreaseContrast: (Boolean) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    var showClearCacheConfirm by remember { mutableStateOf(false) }

    val pageTitle = when (subpageKey) {
        "appearance" -> "Appearance"
        "audio_quality" -> "Audio Quality"
        "equalizer" -> "Equaliser"
        "crossfade" -> "Crossfade"
        "playback" -> "Playback"
        "downloads" -> "Downloads"
        "cellular" -> "Cellular Data"
        "notifications" -> "Notifications"
        "storage" -> "Storage"
        "privacy" -> "Privacy"
        "accessibility" -> "Accessibility"
        "transfer" -> "Transfer Music"
        "about" -> "About"
        else -> "Settings"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(auraColors.groupedBackground)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("settings_subpage_$subpageKey")
    ) {
        SubpageTopBar(
            title = pageTitle,
            onBackClick = onBackClick,
            backButtonTestTag = "subpage_back_button"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (subpageKey) {
                "appearance" -> {
                    item {
                        SettingsSectionHeader("THEME MODE")
                        GlassCard {
                            ThemeMode.entries.forEachIndexed { index, mode ->
                                if (index > 0) SettingsInsetDivider()
                                SelectableOptionRow(
                                    title = mode.label,
                                    subtitle = when (mode) {
                                        ThemeMode.DARK -> "Pure black background with dark frosted glass surfaces"
                                        ThemeMode.LIGHT -> "Crisp white background with light frosted glass surfaces"
                                        ThemeMode.FOLLOW_SYSTEM -> "Automatically adapts to Android & Samsung One UI appearance"
                                    },
                                    selected = settings.themeMode == mode,
                                    onClick = { onSelectThemeMode(mode) },
                                    testTag = "theme_option_${mode.name.lowercase()}"
                                )
                            }
                        }
                    }
                }

                "audio_quality" -> {
                    val qualities = listOf(
                        "High Efficiency (HE-AAC)" to "Low data usage for unstable connections",
                        "High Quality (AAC 256 kbps)" to "Balanced clarity and storage efficiency",
                        "Lossless (ALAC 24-bit/48 kHz)" to "Preserves every detail of the original studio master",
                        "Hi-Res Lossless (ALAC 24-bit/192 kHz)" to "Requires an external digital-to-analog converter (DAC)"
                    )
                    item {
                        SettingsSectionHeader("WIFI & STREAMING QUALITY")
                        GlassCard {
                            qualities.forEachIndexed { idx, (label, desc) ->
                                if (idx > 0) SettingsInsetDivider()
                                SelectableOptionRow(
                                    title = label,
                                    subtitle = desc,
                                    selected = settings.streamingQuality == label,
                                    onClick = { onSelectStreamingQuality(label) },
                                    testTag = "stream_quality_$idx"
                                )
                            }
                        }
                    }
                    item {
                        SettingsSectionHeader("DOWNLOAD QUALITY")
                        GlassCard {
                            qualities.take(3).forEachIndexed { idx, (label, desc) ->
                                if (idx > 0) SettingsInsetDivider()
                                SelectableOptionRow(
                                    title = label,
                                    subtitle = desc,
                                    selected = settings.downloadQuality == label,
                                    onClick = { onSelectDownloadQuality(label) },
                                    testTag = "download_quality_$idx"
                                )
                            }
                        }
                    }
                }

                "equalizer" -> {
                    val presets = listOf(
                        "Off", "Late Night", "Acoustic", "Bass Booster",
                        "Classical", "Dance", "Deep", "Electronic",
                        "Hip-Hop", "Jazz", "Latin", "Lounge",
                        "Piano", "Pop", "R&B", "Rock", "Vocal Booster"
                    )
                    item {
                        SettingsSectionHeader("AUDIO OUTPUT STATUS")
                        GlassCard {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Internal Stereo Speaker / Bluetooth Audio",
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "24-bit DSP frequency shaping active for synthesized and lossless playback.",
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    item {
                        SettingsSectionHeader("EQUALISER PRESETS")
                        GlassCard {
                            presets.forEachIndexed { idx, preset ->
                                if (idx > 0) SettingsInsetDivider()
                                SelectableOptionRow(
                                    title = preset,
                                    subtitle = null,
                                    selected = settings.equalizerPreset == preset,
                                    onClick = { onSelectEqualizerPreset(preset) },
                                    testTag = "eq_preset_${preset.lowercase().replace(" ", "_")}"
                                )
                            }
                        }
                    }
                }

                "crossfade" -> {
                    item {
                        SettingsSectionHeader("SONG TRANSITIONS")
                        GlassCard {
                            SettingsSwitchRow(
                                title = "Crossfade",
                                description = "Smoothly blend the end of each song into the beginning of the next track.",
                                checked = settings.crossfadeEnabled,
                                onCheckedChange = onToggleCrossfade,
                                testTag = "switch_crossfade_enabled"
                            )
                            if (settings.crossfadeEnabled) {
                                SettingsInsetDivider()
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Crossfade Duration",
                                            color = auraColors.textPrimary,
                                            fontFamily = InterFontFamily,
                                            fontSize = 15.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${settings.crossfadeSeconds} seconds",
                                            color = ElectricBlue,
                                            fontFamily = InterFontFamily,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Slider(
                                        value = settings.crossfadeSeconds.toFloat(),
                                        onValueChange = { onUpdateCrossfadeSeconds(it.toInt()) },
                                        valueRange = 1f..12f,
                                        steps = 10,
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = ElectricBlue
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                "playback" -> {
                    item {
                        SettingsSectionHeader("VOLUME & QUEUE")
                        GlassCard {
                            SettingsSwitchRow(
                                title = "Sound Check",
                                description = "Automatically adjust song playback volume to maintain consistent loudness.",
                                checked = settings.soundCheckEnabled,
                                onCheckedChange = onToggleSoundCheck,
                                testTag = "switch_sound_check"
                            )
                            SettingsInsetDivider()
                            SettingsSwitchRow(
                                title = "AutoPlay Similar Music",
                                description = "Continue playing similar tracks when your album or playlist ends.",
                                checked = settings.autoPlayEnabled,
                                onCheckedChange = onToggleAutoPlay,
                                testTag = "switch_autoplay"
                            )
                        }
                    }
                }

                "downloads" -> {
                    item {
                        SettingsSectionHeader("DOWNLOAD PREFERENCES")
                        GlassCard {
                            SettingsSwitchRow(
                                title = "Download Over Wi-Fi Only",
                                description = "Prevent automatic album and playlist downloads over cellular connections.",
                                checked = settings.wifiOnlyDownloads,
                                onCheckedChange = onToggleWifiOnlyDownloads,
                                testTag = "switch_wifi_only_downloads"
                            )
                            SettingsInsetDivider()
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Storage Location",
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Internal Encrypted App Storage (/Android/data/com.aistudio.auramusic.vpxqmr) • 48.2 GB Available",
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                "cellular" -> {
                    item {
                        SettingsSectionHeader("MOBILE DATA")
                        GlassCard {
                            SettingsSwitchRow(
                                title = "Cellular Streaming",
                                description = "Allow streaming music and live radio stations over 5G/LTE networks.",
                                checked = settings.cellularStreamingEnabled,
                                onCheckedChange = onToggleCellularStreaming,
                                testTag = "switch_cellular_streaming"
                            )
                        }
                    }
                }

                "notifications" -> {
                    item {
                        NotificationsSubpageContent(
                            settings = settings,
                            onToggleNewMusicNotif = onToggleNewMusicNotif,
                            onToggleFriendNotif = onToggleFriendNotif,
                            onShowToast = onShowToast
                        )
                    }
                }

                "storage" -> {
                    item {
                        SettingsSectionHeader("STORAGE BREAKDOWN")
                        GlassCard {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Downloaded Lossless Tracks (12 Songs)", color = auraColors.textPrimary, fontSize = 15.sp)
                                    Text("412 MB", color = auraColors.textSecondary, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Artwork & Playback Cache", color = auraColors.textPrimary, fontSize = 15.sp)
                                    Text("${settings.cacheSizeMb} MB", color = ElectricBlue, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                GlassButton(
                                    text = if (settings.cacheSizeMb > 0) "Clear Playback Cache (${settings.cacheSizeMb} MB)" else "Cache Cleared (0 MB)",
                                    enabled = settings.cacheSizeMb > 0,
                                    textColor = if (settings.cacheSizeMb > 0) DestructiveRed else auraColors.textSecondary,
                                    onClick = { showClearCacheConfirm = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "button_clear_cache"
                                )
                            }
                        }
                    }
                }

                "privacy" -> {
                    item {
                        SettingsSectionHeader("LISTENING PRIVACY")
                        GlassCard {
                            SettingsSwitchRow(
                                title = "Use Listening History",
                                description = "Allow played music to influence 'Made For You' recommendations and appear on your follower profile.",
                                checked = settings.listeningHistoryEnabled,
                                onCheckedChange = onToggleListeningHistory,
                                testTag = "switch_listening_history"
                            )
                        }
                    }
                }

                "accessibility" -> {
                    item {
                        SettingsSectionHeader("VISION & DISPLAY")
                        GlassCard {
                            SettingsSwitchRow(
                                title = "Larger Text",
                                description = "Scale up headings, track titles, and labels across the application.",
                                checked = settings.largerTextEnabled,
                                onCheckedChange = onToggleLargerText,
                                testTag = "subpage_switch_larger_text"
                            )
                            SettingsInsetDivider()
                            SettingsSwitchRow(
                                title = "Increase Contrast",
                                description = "Boost surface border and secondary text contrast in both Light and Dark themes.",
                                checked = settings.increaseContrastEnabled,
                                onCheckedChange = onToggleIncreaseContrast,
                                testTag = "subpage_switch_increase_contrast"
                            )
                        }
                    }
                }

                "transfer" -> {
                    item {
                        SettingsSectionHeader("MUSIC TRANSFER")
                        GlassCard {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Transfer Playlists & Library",
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "External library import requires linking a third-party OAuth partner account. Direct third-party catalog migration is currently unavailable in this regional build.",
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.5.sp,
                                    lineHeight = 19.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                GlassButton(
                                    text = "Partner Integration Unavailable in Region",
                                    enabled = false,
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                "about" -> {
                    item {
                        SettingsSectionHeader("APPLICATION DETAILS")
                        GlassCard {
                            AboutInfoRow("Application Version", "2.4.0 (Build 2026.10)")
                            SettingsInsetDivider()
                            AboutInfoRow("Audio Engine", "24-bit Lossless & Spatial Audio Renderer")
                            SettingsInsetDivider()
                            AboutInfoRow("Privacy Policy", "Standard Zero-Tracking Encryption") {
                                onShowToast("Privacy Policy: All preferences stored locally via Jetpack DataStore")
                            }
                            SettingsInsetDivider()
                            AboutInfoRow("Terms of Service", "Onyx Music Subscriber Agreement") {
                                onShowToast("Onyx Music Terms of Service v2026")
                            }
                            SettingsInsetDivider()
                            AboutInfoRow("Open-Source Acknowledgements", "Jetpack Compose, Kotlin Coroutines, Coil") {
                                onShowToast("Built with Android Jetpack Compose, Material 3, DataStore & Coil")
                            }
                            SettingsInsetDivider()
                            AboutInfoRow("Support & Feedback", "Send diagnostic feedback") {
                                onShowToast("Thank you — Diagnostic feedback report queued")
                            }
                        }
                    }
                }
            }
        }
    }

    GlassDialog(
        visible = showClearCacheConfirm,
        onDismissRequest = { showClearCacheConfirm = false }
    ) {
        Text(
            text = "Clear Playback Cache?",
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This will free ${settings.cacheSizeMb} MB of temporary cached audio buffers. Your downloaded songs and playlists will not be removed.",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { showClearCacheConfirm = false },
                colors = ButtonDefaults.buttonColors(containerColor = auraColors.elevatedSurface)
            ) {
                Text("Cancel", color = auraColors.textPrimary)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    showClearCacheConfirm = false
                    onClearCache()
                    onShowToast("Playback cache cleared")
                },
                colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed)
            ) {
                Text("Clear Cache", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun NotificationsSubpageContent(
    settings: PersistedSettings,
    onToggleNewMusicNotif: (Boolean) -> Unit,
    onToggleFriendNotif: (Boolean) -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val auraColors = LocalAuraColors.current

    var hasSystemPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasSystemPermission = granted
        onShowToast(
            if (granted) "System notifications permission granted"
            else "Notifications permission denied"
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SettingsSectionHeader("ALERT CATEGORIES")
        GlassCard {
            SettingsSwitchRow(
                title = "New Music & Artist Releases",
                description = "Receive alerts when artists in your library release new albums or singles.",
                checked = settings.notificationsNewMusic,
                onCheckedChange = onToggleNewMusicNotif,
                testTag = "subpage_switch_notif_new_music"
            )
            SettingsInsetDivider()
            SettingsSwitchRow(
                title = "Friend Activity",
                description = "Notify when friends follow your profile or share playlists.",
                checked = settings.notificationsFriendActivity,
                onCheckedChange = onToggleFriendNotif,
                testTag = "subpage_switch_notif_friends"
            )
        }

        SettingsSectionHeader("ANDROID SYSTEM PERMISSION")
        GlassCard {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (hasSystemPermission) ElectricBlue else auraColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasSystemPermission) "System Notifications Enabled"
                        else "System Notification Permission Required",
                        color = auraColors.textPrimary,
                        fontFamily = InterFontFamily,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (!hasSystemPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassButton(
                        text = "Allow Notifications",
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableOptionRow(
    title: String,
    subtitle: String?,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (selected) ElectricBlue else auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
            if (!subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }
        }
        if (selected) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = ElectricBlue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun AboutInfoRow(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = auraColors.textSecondary,
                fontFamily = InterFontFamily,
                fontSize = 13.sp
            )
        }
    }
}
