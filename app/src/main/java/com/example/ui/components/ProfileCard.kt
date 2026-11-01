package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.model.UserProfile
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors

@Composable
fun ProfileCard(
    userProfile: UserProfile,
    onEditProfileClick: () -> Unit,
    onSetUpProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val cardShape = RoundedCornerShape(18.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. User Avatar + Display Name + Blue Edit Action Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(auraColors.cardSurface)
                .border(0.6.dp, auraColors.glassBorder.copy(alpha = 0.35f), cardShape)
                .clickable(onClick = onEditProfileClick)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .testTag("account_profile_header_card"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .border(1.dp, ElectricBlue.copy(alpha = 0.5f), CircleShape)
                ) {
                    AsyncImage(
                        model = userProfile.avatarRes,
                        contentDescription = userProfile.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = userProfile.displayName,
                        color = auraColors.textPrimary,
                        fontFamily = InterFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (userProfile.isProfileSetUp) "${userProfile.handle} • Public Profile" else "View Profile",
                        color = auraColors.textSecondary,
                        fontFamily = InterFontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onEditProfileClick)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag("account_edit_profile_button")
            ) {
                Text(
                    text = stringResource(R.string.account_edit),
                    color = ElectricBlue,
                    fontFamily = InterFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. "Set Up Profile" Large Rounded Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(auraColors.cardSurface)
                .border(0.6.dp, auraColors.glassBorder.copy(alpha = 0.35f), cardShape)
                .clickable(onClick = onSetUpProfileClick)
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .testTag("account_setup_profile_card"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (userProfile.isProfileSetUp) "Profile Sharing Active"
                        else stringResource(R.string.account_setup_profile_title),
                        color = if (userProfile.isProfileSetUp) ElectricBlue else auraColors.textPrimary,
                        fontFamily = InterFontFamily,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    )
                    if (userProfile.isProfileSetUp) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.account_setup_profile_subtitle),
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = auraColors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
