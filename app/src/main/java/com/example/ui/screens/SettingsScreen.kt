package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.preferences.PersistedSettings
import com.example.ui.components.GlassCard
import com.example.ui.components.SubpageTopBar
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

/**
 * Pixel-accurate recreation of the uploaded Apple Music Settings screenshot.
 * Features:
 * - Circular back button at top-left & centered "Settings" title
 * - LIBRARY section:
 *   1. Add & Delete Playlist Songs (Title, Description, Toggle switch)
 *   2. Add & Delete Favourite Songs (Title, Description, Toggle switch)
 *   3. Transfer Music from Other Services (Title, Description, Navigation action)
 * - AUDIO section:
 *   1. Spatial Audio (Toggle switch)
 *   2. Audio Quality (Navigation action)
 *   3. Crossfade (Current selected value, Navigation action)
 *   4. Equaliser (Description, Navigation action)
 * - Additional Settings sections:
 *   Appearance, Playback, Downloads, Cellular Data, Notifications, Storage,
 *   Privacy, Accessibility (Larger Text, Increase Contrast), Diagnostics, About.
 */
@Composable
fun SettingsScreen(
    settings: PersistedSettings,
    onBackClick: () -> Unit,
    onNavigateSubpage: (String) -> Unit,
    onNavigateAccount: () -> Unit = {},
    onToggleAddDeletePlaylist: (Boolean) -> Unit,
    onToggleAddDeleteFavourite: (Boolean) -> Unit,
    onToggleSpatialAudio: (Boolean) -> Unit,
    onToggleLargerText: (Boolean) -> Unit,
    onToggleIncreaseContrast: (Boolean) -> Unit,
    onToggleDiagnostics: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(auraColors.groupedBackground)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("settings_screen_root")
    ) {
        SubpageTopBar(
            title = "Settings",
            onBackClick = onBackClick,
            backButtonTestTag = "settings_back_button"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settings_lazy_column"),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 10.dp,
                bottom = 64.dp
            )
        ) {
            // 1.LIBRARY SECTION
            item(key = "sec_library") {
                SettingsSectionHeader("LIBRARY")
                GlassCard {
                    SettingsSwitchRow(
                        title = "Add & Delete Playlist Songs",
                        description = "Add songs to your library when you add them to a playlist, and delete them from playlists when deleted from your library.",
                        checked = settings.addDeletePlaylistSongs,
                        onCheckedChange = onToggleAddDeletePlaylist,
                        testTag = "switch_add_delete_playlist"
                    )
                    SettingsInsetDivider()
                    SettingsSwitchRow(
                        title = "Add & Delete Favourite Songs",
                        description = "Add songs to your library when you favourite them, and remove from favourites if deleted from library.",
                        checked = settings.addDeleteFavouriteSongs,
                        onCheckedChange = onToggleAddDeleteFavourite,
                        testTag = "switch_add_delete_favourite"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Transfer Music from Other Services",
                        description = "Import playlists and saved tracks from external music services.",
                        valueText = null,
                        onClick = { onNavigateSubpage("transfer") },
                        testTag = "row_transfer_music"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. AUDIO SECTION
            item(key = "sec_audio") {
                SettingsSectionHeader("AUDIO")
                GlassCard {
                    SettingsSwitchRow(
                        title = "Spatial Audio",
                        description = "Listen to supported songs in immersive 3D Dolby Atmos audio.",
                        checked = settings.spatialAudioEnabled,
                        onCheckedChange = onToggleSpatialAudio,
                        testTag = "switch_spatial_audio"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Audio Quality",
                        description = settings.streamingQuality,
                        valueText = null,
                        onClick = { onNavigateSubpage("audio_quality") },
                        testTag = "row_audio_quality"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Crossfade",
                        description = null,
                        valueText = if (settings.crossfadeEnabled) "${settings.crossfadeSeconds}s" else "Off",
                        onClick = { onNavigateSubpage("crossfade") },
                        testTag = "row_crossfade"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Equaliser",
                        description = "Preset: ${settings.equalizerPreset}",
                        valueText = null,
                        onClick = { onNavigateSubpage("equalizer") },
                        testTag = "row_equalizer"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. APPEARANCE & PLAYBACK SECTION
            item(key = "sec_appearance_playback") {
                SettingsSectionHeader("APPEARANCE & PLAYBACK")
                GlassCard {
                    SettingsNavigationRow(
                        title = "Appearance",
                        description = "Light, Dark, or Follow System",
                        valueText = settings.themeMode.label,
                        onClick = { onNavigateSubpage("appearance") },
                        testTag = "row_appearance"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Playback",
                        description = "Sound Check, AutoPlay, and gapless transitions",
                        valueText = null,
                        onClick = { onNavigateSubpage("playback") },
                        testTag = "row_playback"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. DOWNLOADS, CELLULAR & STORAGE SECTION
            item(key = "sec_downloads_storage") {
                SettingsSectionHeader("DOWNLOADS & DATA")
                GlassCard {
                    SettingsNavigationRow(
                        title = "Downloads",
                        description = if (settings.wifiOnlyDownloads) "Wi-Fi Only • Lossless" else "Wi-Fi & Cellular",
                        valueText = null,
                        onClick = { onNavigateSubpage("downloads") },
                        testTag = "row_downloads"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Cellular Data",
                        description = "Manage streaming and download data over mobile networks",
                        valueText = if (settings.cellularStreamingEnabled) "On" else "Off",
                        onClick = { onNavigateSubpage("cellular") },
                        testTag = "row_cellular_data"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Storage",
                        description = "Audio cache and downloaded tracks",
                        valueText = "${settings.cacheSizeMb} MB",
                        onClick = { onNavigateSubpage("storage") },
                        testTag = "row_storage"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 5. NOTIFICATIONS & PRIVACY SECTION
            item(key = "sec_notifications_privacy") {
                SettingsSectionHeader("NOTIFICATIONS & PRIVACY")
                GlassCard {
                    SettingsNavigationRow(
                        title = "Notifications",
                        description = "New releases, friend activity, and system permissions",
                        valueText = if (settings.notificationsNewMusic) "On" else "Off",
                        onClick = { onNavigateSubpage("notifications") },
                        testTag = "row_notifications"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "Privacy",
                        description = "Listening history and personal data controls",
                        valueText = null,
                        onClick = { onNavigateSubpage("privacy") },
                        testTag = "row_privacy"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 6. ACCESSIBILITY SECTION
            item(key = "sec_accessibility") {
                SettingsSectionHeader("ACCESSIBILITY")
                GlassCard {
                    SettingsNavigationRow(
                        title = "Accessibility",
                        description = "Display, motion, and typography preferences",
                        valueText = null,
                        onClick = { onNavigateSubpage("accessibility") },
                        testTag = "row_accessibility"
                    )
                    SettingsInsetDivider()
                    SettingsSwitchRow(
                        title = "Larger Text",
                        description = "Increase typography scale across all screens and cards.",
                        checked = settings.largerTextEnabled,
                        onCheckedChange = onToggleLargerText,
                        testTag = "switch_larger_text"
                    )
                    SettingsInsetDivider()
                    SettingsSwitchRow(
                        title = "Increase Contrast",
                        description = "Enhance text, border, and divider legibility.",
                        checked = settings.increaseContrastEnabled,
                        onCheckedChange = onToggleIncreaseContrast,
                        testTag = "switch_increase_contrast"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 7. ACCOUNT, DIAGNOSTICS & ABOUT SECTION
            item(key = "sec_diagnostics_about") {
                SettingsSectionHeader("ACCOUNT & SYSTEM")
                GlassCard {
                    SettingsNavigationRow(
                        title = "Onyx Music Account",
                        description = "Manage profile, subscription, payment method, and notifications",
                        valueText = null,
                        onClick = onNavigateAccount,
                        testTag = "row_account_settings"
                    )
                    SettingsInsetDivider()
                    SettingsSwitchRow(
                        title = "Diagnostics",
                        description = "Help improve Onyx Music by automatically sending anonymous crash and audio performance logs.",
                        checked = settings.diagnosticsEnabled,
                        onCheckedChange = onToggleDiagnostics,
                        testTag = "switch_diagnostics"
                    )
                    SettingsInsetDivider()
                    SettingsNavigationRow(
                        title = "About",
                        description = "Version, privacy policy, terms, and open-source licences",
                        valueText = "v2.5.0",
                        onClick = { onNavigateSubpage("about") },
                        testTag = "row_about"
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_onyx_logo),
                        contentDescription = "ONYX MUSIC",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ONYX MUSIC • v2.5.0",
                        color = auraColors.textSecondary.copy(alpha = 0.75f),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    val auraColors = LocalAuraColors.current
    Text(
        text = title,
        color = auraColors.textSecondary,
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    description: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.15).sp
            )
            if (!description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 17.5.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ElectricBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = auraColors.elevatedSurface
            )
        )
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    description: String?,
    valueText: String?,
    onClick: () -> Unit,
    testTag: String
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.15).sp
            )
            if (!description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 17.5.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!valueText.isNullOrEmpty()) {
                Text(
                    text = valueText,
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 14.5.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = auraColors.textSecondary,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

@Composable
fun SettingsInsetDivider() {
    val auraColors = LocalAuraColors.current
    HorizontalDivider(
        modifier = Modifier.padding(start = 18.dp),
        thickness = 0.5.dp,
        color = auraColors.divider
    )
}
