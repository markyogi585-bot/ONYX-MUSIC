package com.example.data.repository

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.domain.model.Album
import com.example.domain.model.ArtistProfile
import com.example.domain.model.LyricLine
import com.example.domain.model.Playlist
import com.example.domain.model.RadioStation
import com.example.domain.model.Song
import com.example.domain.model.UserProfile

class MusicRepository {

    val albums: List<Album> = listOf(
        Album(
            id = "alb_neon_horizons",
            title = "Neon Horizons",
            artist = "Kaelen Vance",
            categoryTag = "ELECTRONIC • SPATIAL AUDIO",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF0A84FF),
            secondaryColor = Color(0xFF7209B7),
            badgeText = "TOP PICK FOR YOU",
            description = "Crystalline analog synthesizers collide with widescreen sub-bass architecture in Kaelen Vance's fourth studio odyssey."
        ),
        Album(
            id = "alb_velvet_echoes",
            title = "Velvet Echoes",
            artist = "Soraia Lune",
            categoryTag = "ALTERNATIVE R&B • POP",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFFF2A5F),
            secondaryColor = Color(0xFF3A0CA3),
            badgeText = "EXCLUSIVE PRE-RELEASE",
            description = "Intimate nocturnal vocals layered over liquid chrome percussion and lush tape-saturated strings."
        ),
        Album(
            id = "alb_midnight_tokyo",
            title = "Midnight in Shibuya",
            artist = "Kenji & The Ghosts",
            categoryTag = "NU-JAZZ • AMBIENT",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFF041C24),
            badgeText = "DOLBY ATMOS",
            description = "Rain-slicked electric piano improvisations recorded live in a subterranean Tokyo studio."
        ),
        Album(
            id = "alb_solar_flare",
            title = "Solaris Eclipse",
            artist = "Aurelia Sol",
            categoryTag = "INDIE ELECTRONIC • HAPPY DIWALI",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_solar_flare,
            primaryColor = Color(0xFFFF9E00),
            secondaryColor = Color(0xFF2A1205),
            badgeText = "NEW ALBUM",
            description = "Warm desert horizons and hypnotic modular arpeggios celebrating the golden hour."
        ),
        Album(
            id = "alb_astral_drift",
            title = "Astral Drift",
            artist = "Nyx Protocol",
            categoryTag = "SYNTHWAVE • CHARTS",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_astral_drift,
            primaryColor = Color(0xFF4CC9F0),
            secondaryColor = Color(0xFF0B1340),
            badgeText = "MASTERED FOR AUDIO",
            description = "Deep-space telemetry translated into soaring melodic synth hooks and gated reverb drums."
        ),
        Album(
            id = "alb_emerald_pulse",
            title = "Bio-Luminescence",
            artist = "Moss & Circuit",
            categoryTag = "AMBIENT TECHNO • TAMIL",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_emerald_pulse,
            primaryColor = Color(0xFF10B981),
            secondaryColor = Color(0xFF06291D),
            description = "Organic field recordings woven seamlessly into precision micro-house rhythms."
        ),
        Album(
            id = "alb_obsidian_hearts",
            title = "Obsidian Hearts",
            artist = "The Monochrome Ensemble",
            categoryTag = "MODERN POST-ROCK • TELUGU",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            primaryColor = Color(0xFF3B82F6),
            secondaryColor = Color(0xFF1E2430),
            badgeText = "EDITORS' CHOICE",
            description = "Architectural guitar crescendos and minimalist chamber arrangements."
        ),
        Album(
            id = "alb_violet_mirage",
            title = "Ultraviolet Mirage",
            artist = "Celeste Nova",
            categoryTag = "DREAM POP • BOLLYWOOD",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_violet_mirage,
            primaryColor = Color(0xFFF72585),
            secondaryColor = Color(0xFF2E0938),
            description = "Weightless vocal harmonies bathed in shimmering chorus pedals and vintage tape delay."
        ),
        Album(
            id = "alb_golden_hour",
            title = "Canyon Tapes Vol. 4",
            artist = "Wilder & West",
            categoryTag = "INDIE FOLK • PUNJABI",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_golden_hour,
            primaryColor = Color(0xFFF97316),
            secondaryColor = Color(0xFF3D1308),
            description = "Sun-drenched acoustic grooves captured on 2-inch analog magnetic tape."
        ),
        Album(
            id = "alb_glacial_static",
            title = "Glacial Static",
            artist = "Fjord",
            categoryTag = "NORDIC ELECTRONICA • SPATIAL AUDIO",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_glacial_static,
            primaryColor = Color(0xFF38BDF8),
            secondaryColor = Color(0xFF0C2D48),
            description = "Sub-zero crystalline textures paired with warm analog basslines from Reykjavik."
        ),
        Album(
            id = "alb_nocturne",
            title = "Nocturne IX",
            artist = "Julian Richter",
            categoryTag = "NEO-CLASSICAL • RADIO",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            description = "Felt-piano meditations and solo cello suites for late-night focus."
        ),
        Album(
            id = "alb_cyber_bloom",
            title = "Hyperbloom",
            artist = "Zara K.",
            categoryTag = "FUTURE CLUB • SING",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            badgeText = "EARLY ACCESS",
            description = "High-energy club percussion meets holographic vocal synthesis."
        )
    )

    private val defaultVocalLyrics: List<LyricLine> = com.example.domain.model.LrcLyricsParser.parse(
        """
        [00:00.00] City lights fracture through the obsidian glass
        [00:18.00] Every frequency suspended in the midnight air
        [00:36.00] We trace the neon horizon where the echoes meet
        [00:54.00] Weightless in the current of a distant synth
        [01:16.00] Hold the signal steady through the static rain
        [01:38.00] Chromatic shadows dancing on the silver tide
        [02:04.00] Drifting past the edge of what we used to know
        [02:32.00] Into the electric blue of another dawn
        [03:04.00] Still resonating after the silence falls
        """.trimIndent()
    )

    private val velvetVocalLyrics: List<LyricLine> = com.example.domain.model.LrcLyricsParser.parse(
        """
        [00:00.00] Crimson velvet shadows on the studio floor
        [00:20.00] Whispered melodies wrapped in analog warmth
        [00:42.00] Two AM reflections on a rain-slicked avenue
        [01:05.00] Let the bassline carry what we left unsaid
        [01:30.00] Slow motion heartbeat in a crowded room
        [01:58.00] Fading into gold before the morning light
        [02:30.00] Every echo brings your cadence back to me
        """.trimIndent()
    )

    val songs: List<Song> = listOf(
        Song(
            id = "sng_1",
            title = "Prism Refraction",
            artist = "Kaelen Vance",
            albumTitle = "Neon Horizons",
            durationSeconds = 224,
            artworkRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFF0A84FF),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 220.0f,
            synchronizedLyrics = defaultVocalLyrics,
            composer = "Kaelen Vance, Elena Rostova",
            producer = "Kaelen Vance • Onyx Spatial Studio"
        ),
        Song(
            id = "sng_2",
            title = "Crimson Silk (After Hours)",
            artist = "Soraia Lune",
            albumTitle = "Velvet Echoes",
            durationSeconds = 198,
            artworkRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFFFF2A5F),
            isExplicit = true,
            trackNumber = 2,
            baseFreqHz = 246.94f,
            synchronizedLyrics = velvetVocalLyrics,
            composer = "Soraia Lune, Marcus Aurelius",
            producer = "Velvet Room Productions"
        ),
        Song(
            id = "sng_3",
            title = "Rain on Takeshita Street",
            artist = "Kenji & The Ghosts",
            albumTitle = "Midnight in Shibuya",
            durationSeconds = 252,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF00F5D4),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 261.63f,
            synchronizedLyrics = emptyList(),
            composer = "Kenji Takahashi",
            producer = "Shibuya Analog Collective"
        ),
        Song(
            id = "sng_4",
            title = "Corona Halo",
            artist = "Aurelia Sol",
            albumTitle = "Solaris Eclipse",
            durationSeconds = 211,
            artworkRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFFFF9E00),
            isExplicit = false,
            trackNumber = 3,
            baseFreqHz = 293.66f,
            synchronizedLyrics = defaultVocalLyrics,
            composer = "Aurelia Sol",
            producer = "Solaris Sound System"
        ),
        Song(
            id = "sng_5",
            title = "Starlight Telemetry",
            artist = "Nyx Protocol",
            albumTitle = "Astral Drift",
            durationSeconds = 245,
            artworkRes = R.drawable.img_album_astral_drift,
            accentColor = Color(0xFF4CC9F0),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 329.63f,
            synchronizedLyrics = defaultVocalLyrics,
            composer = "Nyx Protocol",
            producer = "Deep Orbit Mastering"
        ),
        Song(
            id = "sng_6",
            title = "Chlorophyll & Copper",
            artist = "Moss & Circuit",
            albumTitle = "Bio-Luminescence",
            durationSeconds = 278,
            artworkRes = R.drawable.img_album_emerald_pulse,
            accentColor = Color(0xFF10B981),
            isExplicit = false,
            trackNumber = 4,
            baseFreqHz = 196.00f,
            synchronizedLyrics = emptyList(),
            composer = "Moss & Circuit",
            producer = "Botanical Modular Lab"
        ),
        Song(
            id = "sng_7",
            title = "Glass Cathedral",
            artist = "The Monochrome Ensemble",
            albumTitle = "Obsidian Hearts",
            durationSeconds = 312,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            accentColor = Color(0xFF3B82F6),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 233.08f,
            synchronizedLyrics = defaultVocalLyrics
        ),
        Song(
            id = "sng_8",
            title = "Lavender Vapor",
            artist = "Celeste Nova",
            albumTitle = "Ultraviolet Mirage",
            durationSeconds = 186,
            artworkRes = R.drawable.img_album_violet_mirage,
            accentColor = Color(0xFFF72585),
            isExplicit = true,
            trackNumber = 1,
            baseFreqHz = 277.18f,
            synchronizedLyrics = velvetVocalLyrics
        ),
        Song(
            id = "sng_9",
            title = "Mojave Highway 62",
            artist = "Wilder & West",
            albumTitle = "Canyon Tapes Vol. 4",
            durationSeconds = 205,
            artworkRes = R.drawable.img_album_golden_hour,
            accentColor = Color(0xFFF97316),
            isExplicit = false,
            trackNumber = 5,
            baseFreqHz = 311.13f,
            synchronizedLyrics = defaultVocalLyrics
        ),
        Song(
            id = "sng_10",
            title = "Aurora Subzero",
            artist = "Fjord",
            albumTitle = "Glacial Static",
            durationSeconds = 239,
            artworkRes = R.drawable.img_album_glacial_static,
            accentColor = Color(0xFF38BDF8),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 349.23f,
            synchronizedLyrics = defaultVocalLyrics
        ),
        Song(
            id = "sng_11",
            title = "Etude in Obsidian",
            artist = "Julian Richter",
            albumTitle = "Nocturne IX",
            durationSeconds = 194,
            artworkRes = R.drawable.img_album_nocturne,
            accentColor = Color(0xFFEAB308),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 174.61f,
            synchronizedLyrics = emptyList()
        ),
        Song(
            id = "sng_12",
            title = "Kinetic Petals",
            artist = "Zara K.",
            albumTitle = "Hyperbloom",
            durationSeconds = 175,
            artworkRes = R.drawable.img_album_cyber_bloom,
            accentColor = Color(0xFFEC4899),
            isExplicit = true,
            trackNumber = 3,
            baseFreqHz = 392.00f,
            synchronizedLyrics = velvetVocalLyrics
        )
    )

    val playlists: List<Playlist> = listOf(
        Playlist(
            id = "pl_midnight_drive",
            title = "Midnight Glass Drive",
            curator = "Onyx Music Electronic",
            updatedLabel = "UPDATED TODAY",
            description = "Sleek nocturnal electronic, melodic house, and atmospheric synthwave for late-night drives.",
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF0A84FF),
            secondaryColor = Color(0xFF1B0B3B),
            songIds = listOf("sng_1", "sng_5", "sng_10", "sng_12", "sng_3")
        ),
        Playlist(
            id = "pl_velvet_lounge",
            title = "Velvet Silk & Soul",
            curator = "Onyx Music R&B",
            updatedLabel = "UPDATED YESTERDAY",
            description = "Future soul, alt-R&B, and slow-burning vocal production.",
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFFF2A5F),
            secondaryColor = Color(0xFF2B0510),
            songIds = listOf("sng_2", "sng_8", "sng_9", "sng_1")
        ),
        Playlist(
            id = "pl_deep_focus",
            title = "Pure Spatial Focus",
            curator = "Onyx Music Ambient",
            updatedLabel = "UPDATED FRIDAY",
            description = "Immersive Dolby Atmos instrumentals and neo-classical piano for deep flow states.",
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            songIds = listOf("sng_3", "sng_6", "sng_7", "sng_11")
        ),
        Playlist(
            id = "pl_new_wave",
            title = "Future Club 2026",
            curator = "Onyx Music Dance",
            updatedLabel = "UPDATED JUST NOW",
            description = "Forward-thinking club rhythms, UK garage textures, and euphoric synth leads.",
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            songIds = listOf("sng_12", "sng_1", "sng_4", "sng_5")
        ),
        Playlist(
            id = "pl_golden_acoustic",
            title = "Golden Hour Horizon",
            curator = "Onyx Music Indie",
            updatedLabel = "UPDATED THIS WEEK",
            description = "Warm analog indie, organic grooves, and sunset psychedelia.",
            artworkRes = R.drawable.img_album_golden_hour,
            primaryColor = Color(0xFFF97316),
            secondaryColor = Color(0xFF3D1308),
            songIds = listOf("sng_4", "sng_9", "sng_7", "sng_2")
        )
    )

    val artists: List<ArtistProfile> = listOf(
        ArtistProfile(
            id = "art_kaelen",
            name = "Kaelen Vance",
            genre = "Electronic • Spatial",
            monthlyListeners = "4.8M Listeners",
            avatarRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFF0A84FF),
            topAlbumId = "alb_neon_horizons"
        ),
        ArtistProfile(
            id = "art_soraia",
            name = "Soraia Lune",
            genre = "Alternative R&B",
            monthlyListeners = "6.2M Listeners",
            avatarRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFFFF2A5F),
            topAlbumId = "alb_velvet_echoes"
        ),
        ArtistProfile(
            id = "art_kenji",
            name = "Kenji & The Ghosts",
            genre = "Nu-Jazz • Tokyo",
            monthlyListeners = "2.9M Listeners",
            avatarRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF00F5D4),
            topAlbumId = "alb_midnight_tokyo"
        ),
        ArtistProfile(
            id = "art_aurelia",
            name = "Aurelia Sol",
            genre = "Indie Electronic",
            monthlyListeners = "3.4M Listeners",
            avatarRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFFFF9E00),
            topAlbumId = "alb_solar_flare"
        ),
        ArtistProfile(
            id = "art_zara",
            name = "Zara K.",
            genre = "Future Club",
            monthlyListeners = "5.1M Listeners",
            avatarRes = R.drawable.img_album_cyber_bloom,
            accentColor = Color(0xFFEC4899),
            topAlbumId = "alb_cyber_bloom"
        ),
        ArtistProfile(
            id = "art_julian",
            name = "Julian Richter",
            genre = "Neo-Classical",
            monthlyListeners = "1.9M Listeners",
            avatarRes = R.drawable.img_album_nocturne,
            accentColor = Color(0xFFEAB308),
            topAlbumId = "alb_nocturne"
        )
    )

    val radioStations: List<RadioStation> = listOf(
        RadioStation(
            id = "rad_aura_1",
            name = "Onyx 1 Live",
            tagline = "The global flagship station. New music first, 24/7.",
            hostName = "Hosted by Elena Rostova in London",
            isLive = true,
            scheduleText = "LIVE NOW • 22:00 – 00:00",
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF0A84FF),
            secondaryColor = Color(0xFF06163A),
            featuredSongId = "sng_1"
        ),
        RadioStation(
            id = "rad_tokyo_nights",
            name = "Shibuya After Hours",
            tagline = "Underground beats, nu-jazz, and electronic sets from Tokyo.",
            hostName = "Hosted by Kenji Takahashi",
            isLive = true,
            scheduleText = "LIVE STREAM • CHILL & ELECTRONIC",
            artworkRes = R.drawable.img_album_midnight_tokyo,
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFF041C24),
            featuredSongId = "sng_3"
        ),
        RadioStation(
            id = "rad_club_europa",
            name = "Club Kinetic Radio",
            tagline = "Non-stop warehouse mixes, melodic techno, and future club.",
            hostName = "Resident Mix by Zara K.",
            isLive = false,
            scheduleText = "EPISODE 48 • 60 MIN MIX",
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            featuredSongId = "sng_12"
        ),
        RadioStation(
            id = "rad_neo_classical",
            name = "Obsidian Piano Radio",
            tagline = "Minimalist classical, ambient strings, and meditative soundscapes.",
            hostName = "Curated by Julian Richter",
            isLive = false,
            scheduleText = "24/7 CONTINUOUS STATION",
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            featuredSongId = "sng_11"
        )
    )

    val defaultUserProfile = UserProfile(
        displayName = "Alex Rivera",
        handle = "@alexrivera",
        email = "alex.rivera@icloud.com",
        avatarRes = R.drawable.img_avatar_profile,
        isProfileSetUp = false,
        currentPlan = "Individual Plan • $10.99/mo",
        isFamilyPlan = false,
        paymentMethod = "Google Pay •••• 4829",
        countryRegion = "United States",
        notificationsNewMusic = true,
        notificationsFriendActivity = true,
        losslessAudioEnabled = true,
        dolbyAtmosEnabled = true,
        crossfadeSeconds = 6
    )
}
