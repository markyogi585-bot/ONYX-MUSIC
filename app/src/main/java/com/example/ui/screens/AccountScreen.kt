package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.UserProfile
import com.example.ui.components.GlassDialog
import com.example.ui.components.ProfileCard
import com.example.ui.components.SubpageTopBar
import com.example.ui.components.SubscriptionCard
import com.example.ui.theme.DestructiveRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

/**
 * High-contrast, fully functional Account Screen with:
 * - PROFILE (Avatar, Username @handle, Display name, Edit Profile, Set Up Profile)
 * - SUBSCRIPTION (Upgrade to Family, Manage Subscription, Payment Information, Redeem Code)
 * - PREFERENCES (Audio Quality, Playback Settings, Notifications, Country/Region)
 * - ACCOUNT MANAGEMENT (Connected Devices, Privacy & Listening Activity, Apps with Access, Sign Out)
 */
@Composable
fun AccountScreen(
    userProfile: UserProfile,
    streamingQualityLabel: String = "Lossless • 24-bit/192 kHz",
    isEditProfileModalOpen: Boolean,
    activeAccountSubModal: String?,
    onCloseAccount: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onCloseEditProfile: () -> Unit,
    onSaveProfileEdits: (String, String) -> Unit,
    onCompleteProfileSetup: () -> Unit,
    onUpgradeFamilyClick: () -> Unit,
    onOpenSubModal: (String?) -> Unit,
    onUpdateCountry: (String) -> Unit,
    onRedeemCode: (String) -> Unit,
    onToggleNewMusicNotif: () -> Unit,
    onToggleFriendNotif: () -> Unit,
    onSelectStreamingQuality: (String) -> Unit = {},
    onToggleSpatialAudio: (Boolean) -> Unit = {},
    onSignOutConfirm: () -> Unit = {},
    firebaseUserEmail: String? = null,
    cloudSyncedItemsCount: Int = 0,
    onGoogleSignInClick: () -> Unit = {},
    isBackgroundPlaybackEnabled: Boolean = true,
    onToggleBackgroundPlayback: (Boolean) -> Unit = {},
    bassBoostPercent: Int = 65,
    onUpdateBassBoost: (Int) -> Unit = {},
    loudnessBoostPercent: Int = 50,
    onUpdateLoudnessBoost: (Int) -> Unit = {},
    spatialSurroundPercent: Int = 70,
    onUpdateSpatialSurround: (Int) -> Unit = {},
    playbackSpeed: Float = 1.0f,
    onSelectPlaybackSpeed: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    var quickDisplayName by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }
    var quickUsernameHandle by remember(userProfile.handle) { mutableStateOf(userProfile.handle) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(auraColors.groupedBackground)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("account_screen_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SubpageTopBar(
                title = stringResource(R.string.account_title),
                onBackClick = onCloseAccount,
                backButtonTestTag = "account_close_button"
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 12.dp,
                    bottom = 64.dp
                )
            ) {
                item(key = "account_profile_section") {
                    ProfileCard(
                        userProfile = userProfile,
                        onEditProfileClick = onOpenEditProfile,
                        onSetUpProfileClick = onCompleteProfileSetup
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item(key = "account_subscription_and_notifications") {
                    SubscriptionCard(
                        userProfile = userProfile,
                        streamingQualityLabel = streamingQualityLabel,
                        onUpgradeFamilyClick = onUpgradeFamilyClick,
                        onManageSubscriptionClick = { onOpenSubModal("subscription") },
                        onPaymentInfoClick = { onOpenSubModal("payment") },
                        onCountryRegionClick = { onOpenSubModal("country") },
                        onRedeemCodeClick = { onOpenSubModal("redeem") },
                        onManageNotificationsClick = { onOpenSubModal("notifications") },
                        onAudioQualityClick = { onOpenSubModal("audio_quality") },
                        onPlaybackSettingsClick = { onOpenSubModal("playback_settings") },
                        onConnectedDevicesClick = { onOpenSubModal("connected_devices") },
                        onPrivacySettingsClick = { onOpenSubModal("privacy") },
                        onAppsWithAccessClick = { onOpenSubModal("apps_access") },
                        onSignOutClick = { onOpenSubModal("sign_out") }
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // USER'S CHOICE: CUSTOM USERNAME SYSTEM & OPTIONAL CLOUD AUTH
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(auraColors.cardSurface)
                            .padding(16.dp)
                            .testTag("custom_username_auth_card")
                    ) {
                        Text(
                            text = "USER'S CHOICE AUTH • CUSTOM USERNAME SYSTEM",
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.7.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Active Handle: ${userProfile.handle} (${userProfile.displayName})",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Choose any custom @username locally (no login required), or optionally link Google Cloud below.",
                            color = auraColors.textSecondary,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = quickDisplayName,
                            onValueChange = { quickDisplayName = it },
                            label = { Text("Your Display Name", fontFamily = InterFontFamily) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = auraColors.textPrimary,
                                unfocusedTextColor = auraColors.textPrimary,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = auraColors.glassBorder,
                                focusedLabelColor = ElectricBlue,
                                unfocusedLabelColor = auraColors.textSecondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_display_name_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = quickUsernameHandle,
                            onValueChange = { quickUsernameHandle = it },
                            label = { Text("Custom @Username Handle", fontFamily = InterFontFamily) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = auraColors.textPrimary,
                                unfocusedTextColor = auraColors.textPrimary,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = auraColors.glassBorder,
                                focusedLabelColor = ElectricBlue,
                                unfocusedLabelColor = auraColors.textSecondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_username_handle_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("@dhurandhar_vip", "@desi_audiophile", "@punjabi_king").forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ElectricBlue.copy(alpha = 0.14f))
                                        .clickable {
                                            quickUsernameHandle = tag
                                            onSaveProfileEdits(quickDisplayName, tag)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        color = ElectricBlue,
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onSaveProfileEdits(quickDisplayName, quickUsernameHandle) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("save_custom_username_button")
                        ) {
                            Text(
                                text = "Save Custom Username (${if (quickUsernameHandle.startsWith("@")) quickUsernameHandle else "@$quickUsernameHandle"})",
                                color = Color.White,
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 24/7 BACKGROUND RUNNING & HI-RES DSP SOUND QUALITY STUDIO CARD
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(auraColors.cardSurface)
                            .padding(16.dp)
                            .testTag("background_sound_engine_card")
                    ) {
                        Text(
                            text = "24/7 BACKGROUND ENGINE & HI-RES SOUND STUDIO",
                            color = ElectricBlue,
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.7.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Background Playback & Screen-Off WakeLock",
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isBackgroundPlaybackEnabled) {
                                        "Active: Music continues when app is minimized or screen is locked"
                                    } else {
                                        "Disabled: Playback stops when app closes"
                                    },
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = isBackgroundPlaybackEnabled,
                                onCheckedChange = onToggleBackgroundPlayback,
                                colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue),
                                modifier = Modifier.testTag("bg_playback_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Subwoofer Bass Boost ($bassBoostPercent%) • Loudness Clarity ($loudnessBoostPercent%) • 3D Spatial ($spatialSurroundPercent%)",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Studio Flat" to Triple(25, 25, 30),
                                "Dhol Bass Boost" to Triple(90, 75, 80),
                                "Dolby 3D Max" to Triple(75, 85, 100)
                            ).forEach { (label, triple) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (bassBoostPercent == triple.first) ElectricBlue
                                            else auraColors.elevatedSurface
                                        )
                                        .clickable {
                                            onUpdateBassBoost(triple.first)
                                            onUpdateLoudnessBoost(triple.second)
                                            onUpdateSpatialSurround(triple.third)
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (bassBoostPercent == triple.first) Color.White else auraColors.textPrimary,
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Playback Speed & Pitch Mode (${playbackSpeed}x)",
                            color = auraColors.textSecondary,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                0.85f to "0.85x Slowed",
                                1.0f to "1.0x Original",
                                1.15f to "1.15x Upbeat",
                                1.25f to "1.25x Nightcore"
                            ).forEach { (spd, label) ->
                                val selected = kotlin.math.abs(playbackSpeed - spd) < 0.03f
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) ElectricBlue else auraColors.elevatedSurface)
                                        .clickable { onSelectPlaybackSpeed(spd) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) Color.White else auraColors.textPrimary,
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Firebase Cloud Sync & Optional Google Sign-In Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(auraColors.cardSurface)
                            .padding(16.dp)
                            .testTag("firebase_cloud_sync_card")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "OPTIONAL FIREBASE CLOUD SYNC",
                                    color = ElectricBlue,
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.7.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (firebaseUserEmail != null) {
                                        "Connected: $firebaseUserEmail"
                                    } else {
                                        "Optional Google Backup (User's Choice)"
                                    },
                                    color = auraColors.textPrimary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (firebaseUserEmail != null) {
                                        "$cloudSyncedItemsCount items backed up in Cloud Firestore"
                                    } else {
                                        "Your @username works offline & online automatically. Connect Google only if you want cloud backup."
                                    },
                                    color = auraColors.textSecondary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (firebaseUserEmail == null) {
                            Button(
                                onClick = onGoogleSignInClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("google_sign_in_button")
                            ) {
                                Text(
                                    text = "Connect Google Account (Optional Cloud Sync)",
                                    color = Color.White,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Button(
                                onClick = onSignOutConfirm,
                                colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed.copy(alpha = 0.18f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("google_sign_out_button")
                            ) {
                                Text(
                                    text = "Disconnect Google Cloud Session",
                                    color = DestructiveRed,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item(key = "account_footer_info") {
                    Spacer(modifier = Modifier.height(28.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_onyx_logo),
                            contentDescription = "ONYX MUSIC",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Signed in as ${userProfile.email}",
                            color = auraColors.textSecondary,
                            fontFamily = InterFontFamily,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Onyx Music Lossless & Spatial Audio • v2.5.0",
                            color = auraColors.textSecondary.copy(alpha = 0.72f),
                            fontFamily = InterFontFamily,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        // Edit Profile Modal
        EditProfileDialogOverlay(
            visible = isEditProfileModalOpen,
            userProfile = userProfile,
            onDismiss = onCloseEditProfile,
            onSave = onSaveProfileEdits
        )

        // Sub-Modals for Subscription, Payment, Country, Redeem, Notifications, Audio Quality, Playback, Devices, Privacy, Apps, Sign Out
        AccountSubModalOverlay(
            modalType = activeAccountSubModal,
            userProfile = userProfile,
            streamingQualityLabel = streamingQualityLabel,
            onDismiss = { onOpenSubModal(null) },
            onUpgradeFamily = onUpgradeFamilyClick,
            onUpdateCountry = onUpdateCountry,
            onRedeemCode = onRedeemCode,
            onToggleNewMusicNotif = onToggleNewMusicNotif,
            onToggleFriendNotif = onToggleFriendNotif,
            onSelectStreamingQuality = onSelectStreamingQuality,
            onToggleSpatialAudio = onToggleSpatialAudio,
            onTogglePrivacySharing = onCompleteProfileSetup,
            onSignOutConfirm = {
                onOpenSubModal(null)
                onSignOutConfirm()
            }
        )
    }
}

@Composable
private fun EditProfileDialogOverlay(
    visible: Boolean,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    val auraColors = LocalAuraColors.current
    var nameInput by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }
    var handleInput by remember(userProfile.handle) { mutableStateOf(userProfile.handle) }

    GlassDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("edit_profile_modal")
    ) {
        Text(
            text = "Edit Profile",
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Customize how your name and handle appear to friends.",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 13.5.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Display Name") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = auraColors.glassBorder,
                focusedTextColor = auraColors.textPrimary,
                unfocusedTextColor = auraColors.textPrimary,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = auraColors.textSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = handleInput,
            onValueChange = { handleInput = it },
            label = { Text("Username Handle") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = auraColors.glassBorder,
                focusedTextColor = auraColors.textPrimary,
                unfocusedTextColor = auraColors.textPrimary,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = auraColors.textSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = auraColors.elevatedSurface)
            ) {
                Text("Cancel", color = auraColors.textPrimary)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = { onSave(nameInput, handleInput) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AccountSubModalOverlay(
    modalType: String?,
    userProfile: UserProfile,
    streamingQualityLabel: String,
    onDismiss: () -> Unit,
    onUpgradeFamily: () -> Unit,
    onUpdateCountry: (String) -> Unit,
    onRedeemCode: (String) -> Unit,
    onToggleNewMusicNotif: () -> Unit,
    onToggleFriendNotif: () -> Unit,
    onSelectStreamingQuality: (String) -> Unit,
    onToggleSpatialAudio: (Boolean) -> Unit,
    onTogglePrivacySharing: () -> Unit,
    onSignOutConfirm: () -> Unit
) {
    val auraColors = LocalAuraColors.current
    var promoCode by remember { mutableStateOf("") }

    GlassDialog(
        visible = modalType != null,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("account_sub_modal_${modalType ?: "none"}")
    ) {
        when (modalType) {
            "subscription" -> {
                Text(
                    text = "Manage Subscription",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Plan: ${userProfile.currentPlan}",
                    color = ElectricBlue,
                    fontFamily = InterFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Includes Lossless Audio (up to 24-bit/192 kHz) and Spatial Audio with Dolby Atmos.",
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onUpgradeFamily,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (userProfile.isFamilyPlan) "Switch to Individual ($10.99/mo)"
                        else "Upgrade to Family Plan ($16.99/mo)",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            "payment" -> {
                Text(
                    text = "Payment Information",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(auraColors.cardSurface)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = userProfile.paymentMethod,
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Primary billing method • Next renewal Nov 28, 2026",
                            color = auraColors.textSecondary,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            "audio_quality" -> {
                Text(
                    text = "Audio Quality",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                val tiers = listOf(
                    "Hi-Res Lossless (24-bit/192 kHz)",
                    "Lossless (24-bit/48 kHz)",
                    "High Efficiency (AAC 256 kbps)"
                )
                tiers.forEach { tier ->
                    val isSelected = streamingQualityLabel.contains(tier.substringBefore(" ("), ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onSelectStreamingQuality(tier)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = tier,
                            color = if (isSelected) ElectricBlue else auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 14.5.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text("Active", color = ElectricBlue, fontSize = 13.sp)
                        }
                    }
                }
            }

            "playback_settings" -> {
                Text(
                    text = "Playback Settings",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spatial Audio with Dolby Atmos",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Automatic head-tracking on supported headphones",
                            color = auraColors.textSecondary,
                            fontSize = 12.5.sp
                        )
                    }
                    Switch(
                        checked = userProfile.dolbyAtmosEnabled,
                        onCheckedChange = { onToggleSpatialAudio(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }
            }

            "connected_devices" -> {
                Text(
                    text = "Connected Devices",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(auraColors.cardSurface)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• This Android Phone (Primary Device)", color = ElectricBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                        Text("• Onyx Web Studio Player • Last active today", color = auraColors.textPrimary, fontSize = 13.5.sp)
                    }
                }
            }

            "privacy" -> {
                Text(
                    text = "Privacy & Listening Activity",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Share Listening Activity with Followers",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Let approved friends see what you are listening to",
                            color = auraColors.textSecondary,
                            fontSize = 12.5.sp
                        )
                    }
                    Switch(
                        checked = userProfile.isProfileSetUp,
                        onCheckedChange = { onTogglePrivacySharing() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }
            }

            "country" -> {
                Text(
                    text = "Select Country / Region",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                val countries = listOf("United States", "India", "United Kingdom", "Japan", "Germany")
                countries.forEach { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onUpdateCountry(country) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = country,
                            color = if (country == userProfile.countryRegion) ElectricBlue else auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.5.sp,
                            fontWeight = if (country == userProfile.countryRegion) FontWeight.Bold else FontWeight.Normal
                        )
                        if (country == userProfile.countryRegion) {
                            Text("Active", color = ElectricBlue, fontSize = 13.sp)
                        }
                    }
                }
            }

            "redeem" -> {
                Text(
                    text = "Redeem Code",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter your 16-digit gift card or promo code.",
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = promoCode,
                    onValueChange = { promoCode = it },
                    placeholder = { Text("ONYX-2026-VIP") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = auraColors.glassBorder,
                        focusedTextColor = auraColors.textPrimary,
                        unfocusedTextColor = auraColors.textPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onRedeemCode(promoCode.ifBlank { "ONYX-2026-VIP" }) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Redeem Now", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            "notifications" -> {
                Text(
                    text = "Manage Notifications",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "New Music & Artist Releases",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Alerts when favorite artists drop new albums",
                            color = auraColors.textSecondary,
                            fontSize = 12.5.sp
                        )
                    }
                    Switch(
                        checked = userProfile.notificationsNewMusic,
                        onCheckedChange = { onToggleNewMusicNotif() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Friend Activity",
                            color = auraColors.textPrimary,
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Updates when friends follow or share playlists",
                            color = auraColors.textSecondary,
                            fontSize = 12.5.sp
                        )
                    }
                    Switch(
                        checked = userProfile.notificationsFriendActivity,
                        onCheckedChange = { onToggleFriendNotif() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }
            }

            "apps_access" -> {
                Text(
                    text = "Apps with Access",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The following connected services have permission to sync playback state:",
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(auraColors.cardSurface)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• Android Auto Media Controller", color = auraColors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                        Text("• Shazam Song Recognition Sync", color = auraColors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                    }
                }
            }

            "sign_out" -> {
                Text(
                    text = "Sign Out of Onyx Music?",
                    color = auraColors.textPrimary,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Signing out of ${userProfile.email} will pause cloud profile sharing until you sign back in.",
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSignOutConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = auraColors.elevatedSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done", color = auraColors.textPrimary)
        }
    }
}
