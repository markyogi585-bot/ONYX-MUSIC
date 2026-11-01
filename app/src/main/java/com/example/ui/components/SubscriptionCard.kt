package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.UserProfile
import com.example.ui.theme.DestructiveRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

/**
 * Recreates the Subscription, Preferences, Notifications, and Account Management sections
 * with solid, high-contrast card surfaces for optimal legibility:
 *
 * SUBSCRIPTION:
 * - Upgrade to Family
 * - Manage Subscription
 * - Payment Information
 * - Redeem Code
 *
 * PREFERENCES:
 * - Audio Quality
 * - Playback Settings
 * - Notifications
 * - Country/Region
 *
 * ACCOUNT MANAGEMENT:
 * - Connected Devices
 * - Privacy & Data
 * - Apps with Access
 * - Sign Out
 */
@Composable
fun SubscriptionCard(
    userProfile: UserProfile,
    streamingQualityLabel: String = "Lossless • 24-bit/192 kHz",
    onUpgradeFamilyClick: () -> Unit,
    onManageSubscriptionClick: () -> Unit,
    onPaymentInfoClick: () -> Unit,
    onCountryRegionClick: () -> Unit,
    onRedeemCodeClick: () -> Unit,
    onManageNotificationsClick: () -> Unit,
    onAudioQualityClick: () -> Unit = {},
    onPlaybackSettingsClick: () -> Unit = {},
    onConnectedDevicesClick: () -> Unit = {},
    onPrivacySettingsClick: () -> Unit = {},
    onAppsWithAccessClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val cardShape = RoundedCornerShape(18.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. SUBSCRIPTION Section Heading
        Text(
            text = "SUBSCRIPTION",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(auraColors.cardSurface)
                .border(0.6.dp, auraColors.glassBorder.copy(alpha = 0.35f), cardShape)
                .testTag("account_subscription_card")
        ) {
            AccountActionRow(
                label = stringResource(R.string.account_upgrade_family),
                valueText = if (userProfile.isFamilyPlan) "Active" else null,
                labelColor = ElectricBlue,
                showChevron = false,
                onClick = onUpgradeFamilyClick,
                testTag = "account_row_upgrade_family"
            )
            InsetCardDivider()

            AccountActionRow(
                label = stringResource(R.string.account_manage_subscription),
                valueText = if (userProfile.isFamilyPlan) "Family" else "Individual",
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onManageSubscriptionClick,
                testTag = "account_row_manage_subscription"
            )
            InsetCardDivider()

            AccountActionRow(
                label = stringResource(R.string.account_payment_info),
                valueText = null,
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onPaymentInfoClick,
                testTag = "account_row_payment_info"
            )
            InsetCardDivider()

            AccountActionRow(
                label = stringResource(R.string.account_redeem_code),
                valueText = null,
                labelColor = ElectricBlue,
                showChevron = false,
                onClick = onRedeemCodeClick,
                testTag = "account_row_redeem_code"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. PREFERENCES Section Heading
        Text(
            text = "PREFERENCES",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(auraColors.cardSurface)
                .border(0.6.dp, auraColors.glassBorder.copy(alpha = 0.35f), cardShape)
                .testTag("account_preferences_card")
        ) {
            AccountActionRow(
                label = "Audio Quality",
                valueText = streamingQualityLabel,
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onAudioQualityClick,
                testTag = "account_row_audio_quality"
            )
            InsetCardDivider()

            AccountActionRow(
                label = "Playback Settings",
                valueText = if (userProfile.dolbyAtmosEnabled) "Dolby Atmos • ${userProfile.crossfadeSeconds}s" else "${userProfile.crossfadeSeconds}s Crossfade",
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onPlaybackSettingsClick,
                testTag = "account_row_playback_settings"
            )
            InsetCardDivider()

            AccountActionRow(
                label = stringResource(R.string.account_manage_notifications),
                valueText = if (userProfile.notificationsNewMusic || userProfile.notificationsFriendActivity) "On" else "Off",
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onManageNotificationsClick,
                testTag = "account_row_manage_notifications"
            )
            InsetCardDivider()

            AccountActionRow(
                label = stringResource(R.string.account_country_region),
                valueText = userProfile.countryRegion,
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onCountryRegionClick,
                testTag = "account_row_country_region"
            )
        }

        Text(
            text = "Configure lossless streaming, spatial audio head-tracking, notifications, and regional catalog availability.",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. ACCOUNT MANAGEMENT Section
        Text(
            text = "ACCOUNT MANAGEMENT",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(auraColors.cardSurface)
                .border(0.6.dp, auraColors.glassBorder.copy(alpha = 0.35f), cardShape)
                .testTag("account_management_card")
        ) {
            AccountActionRow(
                label = "Connected Devices",
                valueText = "This Android Phone",
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onConnectedDevicesClick,
                testTag = "account_row_connected_devices"
            )
            InsetCardDivider()

            AccountActionRow(
                label = "Privacy & Listening Activity",
                valueText = if (userProfile.isProfileSetUp) "Shared" else "Private",
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onPrivacySettingsClick,
                testTag = "account_row_privacy"
            )
            InsetCardDivider()

            AccountActionRow(
                label = "Apps with Access",
                valueText = "2 Connected",
                labelColor = auraColors.textPrimary,
                showChevron = true,
                onClick = onAppsWithAccessClick,
                testTag = "account_row_apps_with_access"
            )
            InsetCardDivider()

            AccountActionRow(
                label = "Sign Out",
                valueText = null,
                labelColor = DestructiveRed,
                showChevron = false,
                onClick = onSignOutClick,
                testTag = "account_row_sign_out"
            )
        }

        Text(
            text = "Connected applications can access your library and listening activity. Signed in with ${userProfile.email}.",
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )
    }
}

@Composable
private fun AccountActionRow(
    label: String,
    valueText: String?,
    labelColor: Color,
    showChevron: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val auraColors = LocalAuraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = labelColor,
            fontFamily = InterFontFamily,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.15).sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!valueText.isNullOrEmpty()) {
                Text(
                    text = valueText,
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = auraColors.textSecondary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun InsetCardDivider() {
    val auraColors = LocalAuraColors.current
    HorizontalDivider(
        modifier = Modifier.padding(start = 18.dp),
        thickness = 0.5.dp,
        color = auraColors.divider
    )
}
