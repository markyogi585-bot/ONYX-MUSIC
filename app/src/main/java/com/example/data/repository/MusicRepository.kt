package com.example.data.repository

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.data.remote.OnyxSongApiService
import com.example.domain.model.Album
import com.example.domain.model.ArtistProfile
import com.example.domain.model.Playlist
import com.example.domain.model.RadioStation
import com.example.domain.model.Song
import com.example.domain.model.UserProfile

/**
 * Real Music Catalog Repository populated from
 * https://osint-apis-hub.onrender.com/api/song?key=Titan&song=chandani
 * and top Indian & Global streaming hits with live 320kbps AAC audio URLs and 600x600 cover artwork.
 */
class MusicRepository {

    val albums: List<Album> = listOf(
        Album(
            id = "alb_neon_horizons",
            title = "Chandni",
            artist = "Sachet Tandon, Parampara Tandon, B Praak",
            categoryTag = "BOLLYWOOD • SPATIAL AUDIO",
            releaseYear = 2024,
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF168BFF),
            secondaryColor = Color(0xFF7209B7),
            badgeText = "TOP PICK FOR YOU",
            description = "Sachet-Parampara, Jaani, and B Praak deliver an unforgettable romantic anthem in 320 kbps AAC.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_velvet_echoes",
            title = "Brahmastra (Original Motion Picture Soundtrack)",
            artist = "Pritam, Arijit Singh, Amitabh Bhattacharya",
            categoryTag = "BOLLYWOOD • POP",
            releaseYear = 2023,
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFFF2A5F),
            secondaryColor = Color(0xFF3A0CA3),
            badgeText = "CHART TOPPER",
            description = "Soaring orchestral arrangements and Arijit Singh's iconic vocals in Kesariya.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music112/v4/9f/13/ca/9f13ca3b-e533-03e0-f19a-f0aaa774581d/196589311191.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_midnight_tokyo",
            title = "Hass Hass",
            artist = "Diljit Dosanjh, Sia, Greg Kurstin",
            categoryTag = "PUNJABI • GLOBAL POP",
            releaseYear = 2024,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFF041C24),
            badgeText = "DOLBY ATMOS",
            description = "Global Punjabi pop phenomenon Diljit Dosanjh teams up with Sia and producer Greg Kurstin.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/af/11/f9/af11f978-7b08-8025-eb57-25682c8d9acb/5054197842245.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_solar_flare",
            title = "Rowdy Rathore",
            artist = "Sajid Wajid, Shreya Ghoshal",
            categoryTag = "BOLLYWOOD • HAPPY DIWALI",
            releaseYear = 2024,
            artworkRes = R.drawable.img_album_solar_flare,
            primaryColor = Color(0xFFFF9E00),
            secondaryColor = Color(0xFF2A1205),
            badgeText = "EVERGREEN HIT",
            description = "Classic melodic Bollywood celebration featuring Shreya Ghoshal's Chandaniya (Lori Lori).",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music113/v4/d7/6c/ac/d76cac8f-fe0d-d358-0b93-a3645c150825/886443470781.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_astral_drift",
            title = "G.O.A.T.",
            artist = "Diljit Dosanjh",
            categoryTag = "PUNJABI • CHARTS",
            releaseYear = 2024,
            artworkRes = R.drawable.img_album_astral_drift,
            primaryColor = Color(0xFF4CC9F0),
            secondaryColor = Color(0xFF0B1340),
            badgeText = "MASTERED FOR AUDIO",
            description = "Hard-hitting Punjabi anthems including G.O.A.T. and Born to Shine by Diljit Dosanjh.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/d2/89/ac/d289ac98-749e-3822-6b6e-b06aa4815715/859740651597_cover.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_emerald_pulse",
            title = "Aashiqui 2",
            artist = "Mithoon, Arijit Singh",
            categoryTag = "ROMANTIC • TAMIL & HINDI",
            releaseYear = 2024,
            artworkRes = R.drawable.img_album_emerald_pulse,
            primaryColor = Color(0xFF10B981),
            secondaryColor = Color(0xFF06291D),
            description = "The timeless romantic soundtrack led by Arijit Singh and Mithoon's Tum Hi Ho.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/bb/23/ee/bb23eeed-0c35-4f1d-2b11-485622777ae4/8902894353007_cover.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_obsidian_hearts",
            title = "Dhurandhar",
            artist = "Arijit Singh, Shashwat Sachdev, Irshad Kamil",
            categoryTag = "SOULFUL • TELUGU & HINDI",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            primaryColor = Color(0xFF3B82F6),
            secondaryColor = Color(0xFF1E2430),
            badgeText = "EDITORS' CHOICE",
            description = "Deep emotional acoustics and soaring vocals on Gehra Hua.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/74/b6/95/74b69546-42ce-b146-de43-2e66807234ec/199806576946.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_violet_mirage",
            title = "amnesia: side A",
            artist = "sufr",
            categoryTag = "INDIE POP • BOLLYWOOD",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_violet_mirage,
            primaryColor = Color(0xFFF72585),
            secondaryColor = Color(0xFF2E0938),
            description = "Late-night indie vibes and atmospheric production on chaandni.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/fb/72/42/fb7242e4-589a-0b80-4269-3744e9746324/8721253325109.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_golden_hour",
            title = "LA POSTAL",
            artist = "Daddy Yankee, Diljit Dosanjh",
            categoryTag = "LATIN • PUNJABI",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_golden_hour,
            primaryColor = Color(0xFFF97316),
            secondaryColor = Color(0xFF3D1308),
            description = "Cross-cultural reggaeton and Punjabi fusion on POKITO.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/24/2a/a4/242aa4a9-808f-419f-289c-e7e6a1517c08/4593106.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_glacial_static",
            title = "2 States",
            artist = "Shankar-Ehsaan-Loy, K. Mohan, Yashita Sharma",
            categoryTag = "BOLLYWOOD • SPATIAL AUDIO",
            releaseYear = 2024,
            artworkRes = R.drawable.img_album_glacial_static,
            primaryColor = Color(0xFF38BDF8),
            secondaryColor = Color(0xFF0C2D48),
            description = "Acoustic warmth and heartfelt melodies on Chaandaniya.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_nocturne",
            title = "Master Of Melody",
            artist = "Mithoon, Vishal Mishra, Sayeed Quadri",
            categoryTag = "MELODY • RADIO",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            description = "Mithoon and Vishal Mishra collaborate on Chandaniya.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/bb/23/ee/bb23eeed-0c35-4f1d-2b11-485622777ae4/8902894353007_cover.jpg/600x600bb.jpg"
        ),
        Album(
            id = "alb_cyber_bloom",
            title = "Chandni Sessions",
            artist = "Vishal Mishra",
            categoryTag = "ACOUSTIC • SING",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            badgeText = "EARLY ACCESS",
            description = "Stripped-back soulful rendition of Chandni by Vishal Mishra.",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        )
    )

    val songs: List<Song> = listOf(
        Song(
            id = "sng_1",
            title = "Chandni",
            artist = "Sachet Tandon, Parampara Tandon, Jaani, B Praak",
            albumTitle = "Chandni",
            durationSeconds = 248,
            artworkRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFF168BFF),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 220.0f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Chandni",
                "Sachet Tandon, Parampara Tandon, Jaani, B Praak",
                "Chandni",
                248
            ),
            composer = "Sachet-Parampara, Jaani",
            producer = "B Praak • T-Series",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/596/83de4dfa3ea262f00d6e91c916a1b84b_320.mp4"
        ),
        Song(
            id = "sng_2",
            title = "Kesariya",
            artist = "Pritam, Arijit Singh, Amitabh Bhattacharya",
            albumTitle = "Brahmastra (Original Motion Picture Soundtrack)",
            durationSeconds = 268,
            artworkRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFFFF2A5F),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 246.94f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Kesariya",
                "Pritam, Arijit Singh, Amitabh Bhattacharya",
                "Brahmastra",
                268
            ),
            composer = "Pritam, Amitabh Bhattacharya",
            producer = "Sony Music Entertainment",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music112/v4/9f/13/ca/9f13ca3b-e533-03e0-f19a-f0aaa774581d/196589311191.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/871/c2febd353f3a076a406fa37510f31f9f_320.mp4"
        ),
        Song(
            id = "sng_3",
            title = "Hass Hass",
            artist = "Diljit Dosanjh, Sia, Greg Kurstin",
            albumTitle = "Hass Hass",
            durationSeconds = 153,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF00F5D4),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 261.63f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Hass Hass",
                "Diljit Dosanjh, Sia",
                "Hass Hass",
                153
            ),
            composer = "Diljit Dosanjh, Sia, Greg Kurstin",
            producer = "Greg Kurstin",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/af/11/f9/af11f978-7b08-8025-eb57-25682c8d9acb/5054197842245.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/245/fd196de0f557e19e2e8d42150d34cf5b_320.mp4"
        ),
        Song(
            id = "sng_4",
            title = "Chandaniya (Lori Lori)",
            artist = "Sajid Wajid, Shreya Ghoshal",
            albumTitle = "Rowdy Rathore",
            durationSeconds = 231,
            artworkRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFFFF9E00),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 293.66f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Chandaniya (Lori Lori)",
                "Sajid Wajid, Shreya Ghoshal",
                "Rowdy Rathore",
                231
            ),
            composer = "Sajid Wajid, Sameer Anjaan",
            producer = "Sony Music India",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music113/v4/d7/6c/ac/d76cac8f-fe0d-d358-0b93-a3645c150825/886443470781.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/781/338adf4bc0ac0fc99653ced48f68d24d_320.mp4"
        ),
        Song(
            id = "sng_5",
            title = "G.O.A.T.",
            artist = "Diljit Dosanjh",
            albumTitle = "G.O.A.T.",
            durationSeconds = 224,
            artworkRes = R.drawable.img_album_astral_drift,
            accentColor = Color(0xFF4CC9F0),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 329.63f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "G.O.A.T.",
                "Diljit Dosanjh",
                "G.O.A.T.",
                224
            ),
            composer = "Karan Aujla, Diljit Dosanjh",
            producer = "G-Funk",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/d2/89/ac/d289ac98-749e-3822-6b6e-b06aa4815715/859740651597_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/597/ce842951d6cde3c4355046ca5e250809_320.mp4"
        ),
        Song(
            id = "sng_6",
            title = "Tum Hi Ho",
            artist = "Mithoon, Arijit Singh",
            albumTitle = "Aashiqui 2",
            durationSeconds = 262,
            artworkRes = R.drawable.img_album_emerald_pulse,
            accentColor = Color(0xFF10B981),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 196.00f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Tum Hi Ho",
                "Mithoon, Arijit Singh",
                "Aashiqui 2",
                262
            ),
            composer = "Mithoon",
            producer = "T-Series",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/bb/23/ee/bb23eeed-0c35-4f1d-2b11-485622777ae4/8902894353007_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/430/5c5ea5cc00e3bff45616013226f376fe_320.mp4"
        ),
        Song(
            id = "sng_7",
            title = "Gehra Hua",
            artist = "Arijit Singh, Shashwat Sachdev, Irshad Kamil",
            albumTitle = "Dhurandhar",
            durationSeconds = 362,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            accentColor = Color(0xFF3B82F6),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 233.08f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Gehra Hua",
                "Arijit Singh, Shashwat Sachdev",
                "Dhurandhar",
                362
            ),
            composer = "Shashwat Sachdev, Irshad Kamil",
            producer = "Shashwat Sachdev",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/74/b6/95/74b69546-42ce-b146-de43-2e66807234ec/199806576946.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/475/04cc3b6ccd915df473947d932f094545_320.mp4"
        ),
        Song(
            id = "sng_8",
            title = "chaandni",
            artist = "sufr",
            albumTitle = "amnesia: side A",
            durationSeconds = 195,
            artworkRes = R.drawable.img_album_violet_mirage,
            accentColor = Color(0xFFF72585),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 277.18f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "chaandni",
                "sufr",
                "amnesia: side A",
                195
            ),
            composer = "sufr",
            producer = "sufr • Indie Sessions",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/fb/72/42/fb7242e4-589a-0b80-4269-3744e9746324/8721253325109.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/996/f3d1fb935a11d4cf56bb5a4a2ad9f7e4_320.mp4"
        ),
        Song(
            id = "sng_9",
            title = "POKITO",
            artist = "Daddy Yankee, Diljit Dosanjh",
            albumTitle = "LA POSTAL",
            durationSeconds = 181,
            artworkRes = R.drawable.img_album_golden_hour,
            accentColor = Color(0xFFF97316),
            isExplicit = false,
            trackNumber = 3,
            baseFreqHz = 311.13f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "POKITO",
                "Daddy Yankee, Diljit Dosanjh",
                "LA POSTAL",
                181
            ),
            composer = "Daddy Yankee, Diljit Dosanjh",
            producer = "El Cartel Records",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/24/2a/a4/242aa4a9-808f-419f-289c-e7e6a1517c08/4593106.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/153/a82d072afafd2a8ca9eb49a1a7869006_320.mp4"
        ),
        Song(
            id = "sng_10",
            title = "Chaandaniya",
            artist = "Shankar-Ehsaan-Loy, K. Mohan, Yashita Sharma",
            albumTitle = "2 States",
            durationSeconds = 248,
            artworkRes = R.drawable.img_album_glacial_static,
            accentColor = Color(0xFF38BDF8),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 349.23f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Chaandaniya",
                "Shankar-Ehsaan-Loy, K. Mohan",
                "2 States",
                248
            ),
            composer = "Shankar-Ehsaan-Loy, Amitabh Bhattacharya",
            producer = "T-Series",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/930/ffa217a381849fbf83c9ea552625ca3f_320.mp4"
        ),
        Song(
            id = "sng_11",
            title = "Born to Shine",
            artist = "Diljit Dosanjh",
            albumTitle = "G.O.A.T.",
            durationSeconds = 214,
            artworkRes = R.drawable.img_album_nocturne,
            accentColor = Color(0xFFEAB308),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 174.61f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Born to Shine",
                "Diljit Dosanjh",
                "G.O.A.T.",
                214
            ),
            composer = "Amrit Maan, Diljit Dosanjh",
            producer = "Desi Crew",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/d2/89/ac/d289ac98-749e-3822-6b6e-b06aa4815715/859740651597_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/945/b509ce839059a03d7fa023630236b568_320.mp4"
        ),
        Song(
            id = "sng_12",
            title = "Chandni (Acoustic Version)",
            artist = "Vishal Mishra",
            albumTitle = "Chandni Sessions",
            durationSeconds = 157,
            artworkRes = R.drawable.img_album_cyber_bloom,
            accentColor = Color(0xFFEC4899),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 392.00f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Chandni",
                "Vishal Mishra",
                "Chandni Sessions",
                157
            ),
            composer = "Vishal Mishra",
            producer = "Vishal Mishra Studio",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/657/051654ae648d84c952c05849b4a2da70_320.mp4"
        )
    )

    val playlists: List<Playlist> = listOf(
        Playlist(
            id = "pl_midnight_drive",
            title = "Chandni & Midnight Hits",
            curator = "Onyx Music India",
            updatedLabel = "UPDATED TODAY",
            description = "Chandni, Kesariya, Chaandaniya, and late-night romantic Bollywood anthems.",
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF168BFF),
            secondaryColor = Color(0xFF1B0B3B),
            songIds = listOf("sng_1", "sng_2", "sng_4", "sng_10", "sng_12"),
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        ),
        Playlist(
            id = "pl_velvet_lounge",
            title = "Arijit Singh Essentials",
            curator = "Onyx Music Romantic",
            updatedLabel = "UPDATED YESTERDAY",
            description = "Soulful romantic classics featuring Kesariya, Tum Hi Ho, and Gehra Hua.",
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFFF2A5F),
            secondaryColor = Color(0xFF2B0510),
            songIds = listOf("sng_2", "sng_6", "sng_7", "sng_1"),
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music112/v4/9f/13/ca/9f13ca3b-e533-03e0-f19a-f0aaa774581d/196589311191.jpg/600x600bb.jpg"
        ),
        Playlist(
            id = "pl_deep_focus",
            title = "Punjabi Global Anthems",
            curator = "Onyx Music Punjabi",
            updatedLabel = "UPDATED FRIDAY",
            description = "Diljit Dosanjh, Sia, and Daddy Yankee hits streaming in 320 kbps lossless.",
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            songIds = listOf("sng_3", "sng_5", "sng_9", "sng_11"),
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/af/11/f9/af11f978-7b08-8025-eb57-25682c8d9acb/5054197842245.jpg/600x600bb.jpg"
        ),
        Playlist(
            id = "pl_new_wave",
            title = "Bollywood Top 50",
            curator = "Onyx Music Charts",
            updatedLabel = "UPDATED JUST NOW",
            description = "The most streamed Hindi and Punjabi songs across India right now.",
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            songIds = listOf("sng_1", "sng_2", "sng_3", "sng_4", "sng_6"),
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/bb/23/ee/bb23eeed-0c35-4f1d-2b11-485622777ae4/8902894353007_cover.jpg/600x600bb.jpg"
        ),
        Playlist(
            id = "pl_golden_acoustic",
            title = "Indie & Acoustic Vibes",
            curator = "Onyx Music Indie",
            updatedLabel = "UPDATED THIS WEEK",
            description = "Warm acoustic sessions, indie pop, and unplugged vocals.",
            artworkRes = R.drawable.img_album_golden_hour,
            primaryColor = Color(0xFFF97316),
            secondaryColor = Color(0xFF3D1308),
            songIds = listOf("sng_8", "sng_12", "sng_10", "sng_1"),
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/fb/72/42/fb7242e4-589a-0b80-4269-3744e9746324/8721253325109.jpg/600x600bb.jpg"
        )
    )

    val artists: List<ArtistProfile> = listOf(
        ArtistProfile(
            id = "art_kaelen",
            name = "Arijit Singh",
            genre = "Bollywood • Romantic",
            monthlyListeners = "42.5M Listeners",
            avatarRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFF168BFF),
            topAlbumId = "alb_velvet_echoes",
            avatarUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music112/v4/9f/13/ca/9f13ca3b-e533-03e0-f19a-f0aaa774581d/196589311191.jpg/600x600bb.jpg"
        ),
        ArtistProfile(
            id = "art_soraia",
            name = "Sachet & Parampara",
            genre = "Bollywood • Melody",
            monthlyListeners = "24.8M Listeners",
            avatarRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFFFF2A5F),
            topAlbumId = "alb_neon_horizons",
            avatarUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        ),
        ArtistProfile(
            id = "art_kenji",
            name = "Diljit Dosanjh",
            genre = "Punjabi • Global Pop",
            monthlyListeners = "31.2M Listeners",
            avatarRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF00F5D4),
            topAlbumId = "alb_midnight_tokyo",
            avatarUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/af/11/f9/af11f978-7b08-8025-eb57-25682c8d9acb/5054197842245.jpg/600x600bb.jpg"
        ),
        ArtistProfile(
            id = "art_aurelia",
            name = "Shreya Ghoshal",
            genre = "Bollywood • Classical",
            monthlyListeners = "28.4M Listeners",
            avatarRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFFFF9E00),
            topAlbumId = "alb_solar_flare",
            avatarUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music113/v4/d7/6c/ac/d76cac8f-fe0d-d358-0b93-a3645c150825/886443470781.jpg/600x600bb.jpg"
        ),
        ArtistProfile(
            id = "art_zara",
            name = "Vishal Mishra",
            genre = "Indie • Soundtrack",
            monthlyListeners = "19.6M Listeners",
            avatarRes = R.drawable.img_album_cyber_bloom,
            accentColor = Color(0xFFEC4899),
            topAlbumId = "alb_cyber_bloom",
            avatarUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/bb/23/ee/bb23eeed-0c35-4f1d-2b11-485622777ae4/8902894353007_cover.jpg/600x600bb.jpg"
        ),
        ArtistProfile(
            id = "art_julian",
            name = "B Praak",
            genre = "Punjabi • Soul",
            monthlyListeners = "21.9M Listeners",
            avatarRes = R.drawable.img_album_nocturne,
            accentColor = Color(0xFFEAB308),
            topAlbumId = "alb_neon_horizons",
            avatarUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        )
    )

    val radioStations: List<RadioStation> = listOf(
        RadioStation(
            id = "rad_aura_1",
            name = "Onyx Music 1",
            tagline = "Non-stop Bollywood & Global hits. Chandni, Kesariya & more 24/7.",
            hostName = "Live from Mumbai & London",
            isLive = true,
            scheduleText = "LIVE NOW • 22:00 – 00:00",
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF168BFF),
            secondaryColor = Color(0xFF06163A),
            featuredSongId = "sng_1",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/da/cf/a1/dacfa1c8-45c9-6ad8-5abe-2d92e7320d67/8903431954596_cover.jpg/600x600bb.jpg"
        ),
        RadioStation(
            id = "rad_tokyo_nights",
            name = "Punjabi Hits Radio",
            tagline = "Diljit Dosanjh, B Praak, and global Punjabi chartbusters.",
            hostName = "Hosted by DJ Kabir",
            isLive = true,
            scheduleText = "LIVE STREAM • PUNJABI & POP",
            artworkRes = R.drawable.img_album_midnight_tokyo,
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFF041C24),
            featuredSongId = "sng_3",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/af/11/f9/af11f978-7b08-8025-eb57-25682c8d9acb/5054197842245.jpg/600x600bb.jpg"
        ),
        RadioStation(
            id = "rad_club_europa",
            name = "Romantic Lounge Radio",
            tagline = "Arijit Singh, Shreya Ghoshal, and Vishal Mishra romantic favorites.",
            hostName = "Curated by Onyx India",
            isLive = false,
            scheduleText = "EPISODE 48 • 60 MIN MIX",
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            featuredSongId = "sng_2",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music112/v4/9f/13/ca/9f13ca3b-e533-03e0-f19a-f0aaa774581d/196589311191.jpg/600x600bb.jpg"
        ),
        RadioStation(
            id = "rad_neo_classical",
            name = "Midnight Unplugged Radio",
            tagline = "Late-night indie, acoustic Hindi melodies, and soulful ballads.",
            hostName = "Hosted by sufr & Friends",
            isLive = false,
            scheduleText = "24/7 CONTINUOUS STATION",
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            featuredSongId = "sng_8",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/fb/72/42/fb7242e4-589a-0b80-4269-3744e9746324/8721253325109.jpg/600x600bb.jpg"
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
        countryRegion = "India",
        notificationsNewMusic = true,
        notificationsFriendActivity = true,
        losslessAudioEnabled = true,
        dolbyAtmosEnabled = true,
        crossfadeSeconds = 6
    )
}
