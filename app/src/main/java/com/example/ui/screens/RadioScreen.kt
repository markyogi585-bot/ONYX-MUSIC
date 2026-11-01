package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.RadioStation
import com.example.ui.components.MusicSectionHeader
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGrey

private data class QuickStationBadge(
    val label: String,
    val subtitle: String,
    val badgeColor: Color
)

/**
 * ONYX MUSIC Radio Screen — Apple Music Reference Layout:
 * 1. Top 3-Column Station Artwork Grid (Radio 1, Hits, Country, Música Uno, Club, Chill)
 * 2. On Air Now — Large horizontal broadcast cards with station artwork, schedule, title, host, and play button
 * 3. Recently Played Stations
 * 4. Featured Broadcasts & Genre Radio
 */
@Composable
fun RadioScreen(
    stations: List<RadioStation>,
    recentlyPlayedStations: List<RadioStation>,
    listState: LazyListState,
    onPlayStation: (RadioStation) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveStations = stations.filter { it.isLive }.ifEmpty { stations.take(2) }
    val recommendedStations = stations

    val gridBadges = listOf(
        QuickStationBadge("Radio 1", "Always On", Color(0xFFE11D48)),
        QuickStationBadge("Hits", "Songs You Know", Color(0xFF168BFF)),
        QuickStationBadge("Country", "Modern Nashville", Color(0xFFD97706)),
        QuickStationBadge("Música Uno", "Global Latin", Color(0xFF10B981)),
        QuickStationBadge("Club", "24/7 Electronic", Color(0xFF7C3AED)),
        QuickStationBadge("Chill", "Ambient Sanctuary", Color(0xFF0284C7))
    )

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("radio_screen_list"),
        contentPadding = PaddingValues(top = 104.dp, bottom = 188.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Apple Music 3-Column Station Artwork Grid
        item(key = "radio_top_station_grid") {
            val gridStations = (stations + stations).take(6)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("radio_station_3col_grid"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                gridStations.chunked(3).forEachIndexed { rowIdx, rowStations ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowStations.forEachIndexed { colIdx, station ->
                            val badge = gridBadges.getOrElse(rowIdx * 3 + colIdx) { gridBadges.first() }
                            StationGridSquareTile(
                                station = station,
                                titleOverlay = badge.label,
                                subtitleOverlay = badge.subtitle,
                                accentColor = badge.badgeColor,
                                onClick = { onPlayStation(station) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 2. On Air Now Section
        item(key = "radio_live_header") {
            Spacer(modifier = Modifier.height(4.dp))
            MusicSectionHeader(
                title = "On Air Now",
                subtitle = "Live global broadcasts hosted by world-class selectors"
            )
        }

        items(liveStations, key = { "live_${it.id}" }) { station ->
            OnAirBroadcastCard(
                station = station,
                onPlayClick = { onPlayStation(station) },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // 3. Recently Played Stations
        if (recentlyPlayedStations.isNotEmpty()) {
            item(key = "radio_recent_section") {
                Spacer(modifier = Modifier.height(4.dp))
                MusicSectionHeader(
                    title = "Recently Played Stations",
                    subtitle = "Jump back into your favorite frequencies"
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.testTag("row_recent_stations")
                ) {
                    items(recentlyPlayedStations, key = { "rec_st_${it.id}" }) { station ->
                        CompactRadioStationTile(
                            station = station,
                            onPlayClick = { onPlayStation(station) }
                        )
                    }
                }
            }
        }

        // 4. Featured Broadcasts & Genre Radio
        item(key = "radio_recommended_header") {
            Spacer(modifier = Modifier.height(4.dp))
            MusicSectionHeader(
                title = "Featured Shows & Stations",
                subtitle = "Curated 24/7 Electronic, Bollywood & International streams"
            )
        }

        items(recommendedStations, key = { "rec_${it.id}" }) { station ->
            RadioStationCard(
                station = station,
                onPlayClick = { onPlayStation(station) },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun StationGridSquareTile(
    station: RadioStation,
    titleOverlay: String,
    subtitleOverlay: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(CharcoalCard)
            .border(0.75.dp, Color.White.copy(alpha = 0.12f), shape)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = station.artworkModel,
            placeholder = painterResource(station.artworkRes),
            error = painterResource(station.artworkRes),
            contentDescription = titleOverlay,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            PureBlack.copy(alpha = 0.30f),
                            PureBlack.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(10.dp)
        ) {
            Text(
                text = "ONYX MUSIC",
                color = TextPrimaryWhite.copy(alpha = 0.82f),
                fontFamily = InterFontFamily,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = titleOverlay,
                    color = TextPrimaryWhite,
                    fontFamily = InterFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitleOverlay,
                    color = TextPrimaryWhite.copy(alpha = 0.75f),
                    fontFamily = InterFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun OnAirBroadcastCard(
    station: RadioStation,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val cardShape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(auraColors.cardSurface)
            .border(0.8.dp, Color.White.copy(alpha = 0.10f), cardShape)
            .clickable(onClick = onPlayClick)
            .padding(14.dp)
            .testTag("radio_card_${station.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PureBlack)
                .border(0.6.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
        ) {
            AsyncImage(
                model = station.artworkModel,
                placeholder = painterResource(station.artworkRes),
                error = painterResource(station.artworkRes),
                contentDescription = station.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Sensors,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "LIVE • ${station.scheduleText.uppercase()}",
                    color = auraColors.textSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = station.name,
                color = auraColors.textPrimary,
                fontFamily = InterFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = station.hostName,
                color = ElectricBlue,
                fontFamily = InterFontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = station.tagline,
                color = auraColors.textSecondary,
                fontFamily = InterFontFamily,
                fontSize = 12.5.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF252527))
                .border(0.8.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                .clickable(onClick = onPlayClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Listen to ${station.name}",
                tint = TextPrimaryWhite,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CompactRadioStationTile(
    station: RadioStation,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .width(162.dp)
            .clickable(onClick = onPlayClick)
            .testTag("compact_station_${station.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(auraColors.cardSurface)
                .border(0.7.dp, GlassBorderSubtle, shape)
        ) {
            AsyncImage(
                model = station.artworkModel,
                placeholder = painterResource(station.artworkRes),
                error = painterResource(station.artworkRes),
                contentDescription = station.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue)
                    .clickable(onClick = onPlayClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play ${station.name}",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = station.name,
            color = auraColors.textPrimary,
            fontFamily = InterFontFamily,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = station.hostName,
            color = auraColors.textSecondary,
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RadioStationCard(
    station: RadioStation,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(
                elevation = 18.dp,
                shape = cardShape,
                ambientColor = station.primaryColor.copy(alpha = 0.35f),
                spotColor = PureBlack
            )
            .clip(cardShape)
            .background(CharcoalCard)
            .border(0.8.dp, GlassBorderSubtle, cardShape)
            .clickable(onClick = onPlayClick)
    ) {
        AsyncImage(
            model = station.artworkModel,
            placeholder = painterResource(station.artworkRes),
            error = painterResource(station.artworkRes),
            contentDescription = station.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PureBlack.copy(alpha = 0.36f),
                            PureBlack.copy(alpha = 0.65f),
                            PureBlack.copy(alpha = 0.92f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (station.isLive) ElectricBlue.copy(alpha = 0.24f)
                        else PureBlack.copy(alpha = 0.55f)
                    )
                    .border(
                        0.7.dp,
                        if (station.isLive) ElectricBlue else GlassBorderSubtle,
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Sensors,
                    contentDescription = null,
                    tint = if (station.isLive) ElectricBlue else TextSecondaryGrey,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = station.scheduleText,
                    color = TextPrimaryWhite,
                    fontFamily = InterFontFamily,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = station.name,
                        color = TextPrimaryWhite,
                        fontFamily = InterFontFamily,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = station.hostName,
                        color = ElectricBlue,
                        fontFamily = InterFontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = station.tagline,
                        color = TextSecondaryGrey,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .clickable(onClick = onPlayClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Listen to ${station.name}",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
