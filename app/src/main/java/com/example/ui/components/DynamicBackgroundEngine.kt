package com.example.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalAuraColors
import com.example.ui.theme.PureBlack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

/**
 * Extracted dominant colour palette from an album/song/playlist artwork drawable.
 */
data class ArtworkColorPalette(
    val dominant: Color,
    val secondary: Color,
    val deepShadow: Color
)

/**
 * Centralized Screen Background Architecture types.
 * Ensures each major screen has its own distinct background identity.
 */
enum class ScreenBackgroundType {
    HOME,
    NEW,
    RADIO,
    LIBRARY,
    SEARCH,
    PLAYLIST_DETAIL,
    ALBUM_DETAIL,
    NOW_PLAYING,
    LYRICS,
    QUEUE,
    SETTINGS_OR_ACCOUNT
}

/**
 * Singleton Artwork Colour Extraction Engine with in-memory LRU caching.
 * Samples actual bitmap pixels from local drawable resources to derive dominant vibrant,
 * secondary harmonic, and deep shadow background colours.
 */
object ArtworkColorExtractor {
    private val cache = LruCache<Int, ArtworkColorPalette>(64)

    fun getCached(@DrawableRes resId: Int): ArtworkColorPalette? =
        if (resId != 0) cache.get(resId) else null

    suspend fun extractPalette(
        context: Context,
        @DrawableRes resId: Int,
        fallbackColor: Color = ElectricBlue
    ): ArtworkColorPalette {
        if (resId == 0) {
            return createPaletteFromSeed(fallbackColor)
        }
        cache.get(resId)?.let { return it }

        return withContext(Dispatchers.Default) {
            runCatching {
                val opts = BitmapFactory.Options().apply {
                    inSampleSize = 6
                    inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
                }
                val bitmap = BitmapFactory.decodeResource(context.resources, resId, opts)
                    ?: return@runCatching createPaletteFromSeed(fallbackColor)

                var rDom = 0L
                var gDom = 0L
                var bDom = 0L
                var domWeight = 0L

                var rSec = 0L
                var gSec = 0L
                var bSec = 0L
                var secWeight = 0L

                val width = bitmap.width
                val height = bitmap.height
                val step = max(1, min(width, height) / 20)

                for (y in 0 until height step step) {
                    for (x in 0 until width step step) {
                        val pixel = bitmap.getPixel(x, y)
                        val r = (pixel shr 16) and 0xFF
                        val g = (pixel shr 8) and 0xFF
                        val b = pixel and 0xFF

                        val maxC = max(r, max(g, b))
                        val minC = min(r, min(g, b))
                        val saturation = if (maxC == 0) 0 else ((maxC - minC) * 100) / maxC
                        val brightness = (r + g + b) / 3

                        // Weight colorful, non-black, non-blown-out pixels higher
                        if (brightness in 18..244) {
                            val weight = (saturation * saturation / 15 + 15).toLong()
                            if (y < height / 2) {
                                rDom += r * weight
                                gDom += g * weight
                                bDom += b * weight
                                domWeight += weight
                            } else {
                                rSec += r * weight
                                gSec += g * weight
                                bSec += b * weight
                                secWeight += weight
                            }
                        }
                    }
                }
                bitmap.recycle()

                val rawDom = if (domWeight > 0) {
                    boostColorVibrancy(
                        (rDom / domWeight).toInt(),
                        (gDom / domWeight).toInt(),
                        (bDom / domWeight).toInt()
                    )
                } else {
                    fallbackColor
                }

                // Blend extracted pixel color with the track's curated seed accent so the hue is always rich and faithful
                val domColor = Color(
                    red = (rawDom.red * 0.65f + fallbackColor.red * 0.35f).coerceIn(0f, 1f),
                    green = (rawDom.green * 0.65f + fallbackColor.green * 0.35f).coerceIn(0f, 1f),
                    blue = (rawDom.blue * 0.65f + fallbackColor.blue * 0.35f).coerceIn(0f, 1f),
                    alpha = 1f
                )

                val secColor = if (secWeight > 0) {
                    val rawSec = boostColorVibrancy(
                        (rSec / secWeight).toInt(),
                        (gSec / secWeight).toInt(),
                        (bSec / secWeight).toInt()
                    )
                    Color(
                        red = (rawSec.red * 0.72f).coerceIn(0.04f, 0.9f),
                        green = (rawSec.green * 0.70f).coerceIn(0.04f, 0.9f),
                        blue = (rawSec.blue * 0.78f).coerceIn(0.04f, 0.95f),
                        alpha = 1f
                    )
                } else {
                    Color(
                        red = (domColor.red * 0.58f).coerceIn(0.04f, 1f),
                        green = (domColor.green * 0.52f).coerceIn(0.04f, 1f),
                        blue = (domColor.blue * 0.70f).coerceIn(0.04f, 1f),
                        alpha = 1f
                    )
                }

                val deepShadow = Color(
                    red = (domColor.red * 0.18f).coerceIn(0.02f, 0.22f),
                    green = (domColor.green * 0.16f).coerceIn(0.02f, 0.22f),
                    blue = (domColor.blue * 0.22f).coerceIn(0.02f, 0.26f),
                    alpha = 1f
                )

                ArtworkColorPalette(
                    dominant = domColor,
                    secondary = secColor,
                    deepShadow = deepShadow
                ).also { palette ->
                    cache.put(resId, palette)
                }
            }.getOrElse {
                createPaletteFromSeed(fallbackColor)
            }
        }
    }

    private fun boostColorVibrancy(r: Int, g: Int, b: Int): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(r, g, b, hsv)
        hsv[1] = (hsv[1] * 1.30f).coerceIn(0.42f, 0.92f)
        hsv[2] = (hsv[2] * 1.12f).coerceIn(0.42f, 0.86f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    fun createPaletteFromSeed(seed: Color): ArtworkColorPalette {
        val argb = seed.toArgb()
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        val boosted = boostColorVibrancy(r, g, b)
        val secondary = Color(
            red = (boosted.red * 0.60f).coerceIn(0.04f, 1f),
            green = (boosted.green * 0.52f).coerceIn(0.04f, 1f),
            blue = (boosted.blue * 0.74f).coerceIn(0.04f, 1f),
            alpha = 1f
        )
        val deep = Color(
            red = (boosted.red * 0.18f).coerceIn(0.02f, 0.22f),
            green = (boosted.green * 0.16f).coerceIn(0.02f, 0.22f),
            blue = (boosted.blue * 0.22f).coerceIn(0.02f, 0.26f),
            alpha = 1f
        )
        return ArtworkColorPalette(dominant = boosted, secondary = secondary, deepShadow = deep)
    }
}

/**
 * Centralized Screen-Specific & Artwork-Derived Background Composable.
 * - For NOW_PLAYING, LYRICS, ALBUM_DETAIL, and PLAYLIST_DETAIL:
 *   1. Renders a heavily blurred artwork layer that smoothly crossfades when the track changes
 *   2. Applies an animated multi-stop gradient derived from the artwork's extracted dominant colors
 *   3. Adds a balanced dark readability overlay so text and controls remain crisp
 * - For main browse screens (HOME, NEW, RADIO, LIBRARY, SEARCH):
 *   Maintains clean pure-black and subtle midnight surfaces without distracting fixed brown/orange tints.
 */
@Composable
fun OnyxDynamicBackground(
    screenType: ScreenBackgroundType,
    @DrawableRes artworkRes: Int = 0,
    seedColor: Color = ElectricBlue,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val auraColors = LocalAuraColors.current

    var extractedPalette by remember(artworkRes, seedColor) {
        mutableStateOf(
            ArtworkColorExtractor.getCached(artworkRes)
                ?: ArtworkColorExtractor.createPaletteFromSeed(seedColor)
        )
    }

    LaunchedEffect(artworkRes, seedColor) {
        if (artworkRes != 0) {
            extractedPalette = ArtworkColorExtractor.extractPalette(context, artworkRes, seedColor)
        } else {
            extractedPalette = ArtworkColorExtractor.createPaletteFromSeed(seedColor)
        }
    }

    val isArtworkImmersiveScreen = screenType in listOf(
        ScreenBackgroundType.NOW_PLAYING,
        ScreenBackgroundType.LYRICS,
        ScreenBackgroundType.PLAYLIST_DETAIL,
        ScreenBackgroundType.ALBUM_DETAIL,
        ScreenBackgroundType.QUEUE
    )

    val (targetTop, targetMid, targetBottom, targetOrb) = remember(
        screenType,
        extractedPalette,
        auraColors.isDark
    ) {
        if (!auraColors.isDark && !isArtworkImmersiveScreen) {
            listOf(
                auraColors.background,
                auraColors.background,
                auraColors.background,
                Color.Transparent
            )
        } else {
            when (screenType) {
                ScreenBackgroundType.HOME,
                ScreenBackgroundType.NEW,
                ScreenBackgroundType.RADIO,
                ScreenBackgroundType.LIBRARY,
                ScreenBackgroundType.SEARCH -> listOf(
                    PureBlack,
                    PureBlack,
                    PureBlack,
                    Color.Transparent
                )

                ScreenBackgroundType.PLAYLIST_DETAIL,
                ScreenBackgroundType.ALBUM_DETAIL -> listOf(
                    extractedPalette.dominant.copy(alpha = 0.76f),
                    extractedPalette.secondary.copy(alpha = 0.48f),
                    Color(0xFF050508),
                    extractedPalette.dominant.copy(alpha = 0.48f)
                )

                ScreenBackgroundType.NOW_PLAYING -> listOf(
                    extractedPalette.dominant.copy(alpha = 0.86f),
                    extractedPalette.secondary.copy(alpha = 0.64f),
                    extractedPalette.deepShadow,
                    extractedPalette.dominant.copy(alpha = 0.62f)
                )

                ScreenBackgroundType.LYRICS -> listOf(
                    extractedPalette.dominant.copy(alpha = 0.82f),
                    extractedPalette.secondary.copy(alpha = 0.58f),
                    extractedPalette.deepShadow,
                    extractedPalette.dominant.copy(alpha = 0.54f)
                )

                ScreenBackgroundType.QUEUE -> listOf(
                    extractedPalette.dominant.copy(alpha = 0.54f),
                    extractedPalette.deepShadow,
                    PureBlack,
                    extractedPalette.secondary.copy(alpha = 0.36f)
                )

                ScreenBackgroundType.SETTINGS_OR_ACCOUNT -> listOf(
                    auraColors.groupedBackground,
                    auraColors.groupedBackground,
                    auraColors.groupedBackground,
                    Color.Transparent
                )
            }
        }
    }

    val animTop by animateColorAsState(targetTop, tween(550), label = "bg_top")
    val animMid by animateColorAsState(targetMid, tween(550), label = "bg_mid")
    val animBottom by animateColorAsState(targetBottom, tween(550), label = "bg_bottom")
    val animOrb by animateColorAsState(targetOrb, tween(550), label = "bg_orb")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (auraColors.isDark || isArtworkImmersiveScreen) PureBlack else auraColors.background)
    ) {
        // 1. Heavily blurred album artwork layer for full-screen player, lyrics, album & playlist screens
        if (isArtworkImmersiveScreen && artworkRes != 0) {
            val artworkLayerAlpha = when (screenType) {
                ScreenBackgroundType.NOW_PLAYING,
                ScreenBackgroundType.LYRICS -> 0.58f
                ScreenBackgroundType.ALBUM_DETAIL,
                ScreenBackgroundType.PLAYLIST_DETAIL -> 0.42f
                else -> 0.32f
            }
            Crossfade(
                targetState = artworkRes,
                animationSpec = tween(550),
                label = "blurred_artwork_backdrop"
            ) { resId ->
                if (resId != 0) {
                    AsyncImage(
                        model = resId,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = 1.42f
                                scaleY = 1.42f
                                alpha = artworkLayerAlpha
                            }
                            .realBackdropBlur(88f)
                    )
                }
            }
        }

        // 2. Smoothly animated extracted-color gradient mesh (only active on artwork-dependent screens)
        if (isArtworkImmersiveScreen) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(animTop, animMid, animBottom)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animOrb, Color.Transparent),
                        center = Offset(size.width * 0.78f, size.height * 0.18f),
                        radius = size.width * 0.88f
                    ),
                    center = Offset(size.width * 0.78f, size.height * 0.18f),
                    radius = size.width * 0.88f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animOrb.copy(alpha = animOrb.alpha * 0.65f), Color.Transparent),
                        center = Offset(size.width * 0.22f, size.height * 0.74f),
                        radius = size.width * 0.78f
                    ),
                    center = Offset(size.width * 0.22f, size.height * 0.74f),
                    radius = size.width * 0.78f
                )
            }

            // 3. Dark readability overlay for full-screen artwork pages
            val scrimColors = when (screenType) {
                ScreenBackgroundType.NOW_PLAYING,
                ScreenBackgroundType.LYRICS -> listOf(
                    PureBlack.copy(alpha = 0.26f),
                    PureBlack.copy(alpha = 0.44f),
                    PureBlack.copy(alpha = 0.74f)
                )
                else -> listOf(
                    PureBlack.copy(alpha = 0.22f),
                    PureBlack.copy(alpha = 0.52f),
                    PureBlack.copy(alpha = 0.86f)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = scrimColors))
            )
        }

        content()
    }
}
