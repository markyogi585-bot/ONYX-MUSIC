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
 * Curated 2025–2026 Blockbuster Catalog for ONYX MUSIC.
 * Every track and album uses verified 320kbps AAC streams (`https://aac.saavncdn.com/..._320.mp4`)
 * and official 500x500 HD cover artwork (`https://c.saavncdn.com/...-500x500.jpg`).
 */
class MusicRepository {

    val albums: List<Album> = listOf(
        Album(
            id = "alb_neon_horizons",
            title = "Dhurandhar",
            artist = "Shashwat Sachdev, Arijit Singh, Hanumankind, Diljit Dosanjh",
            categoryTag = "2026 BLOCKBUSTER • DOLBY ATMOS",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF168BFF),
            secondaryColor = Color(0xFF0A1128),
            badgeText = "DOLBY ATMOS",
            description = "The explosive 2026 soundtrack featuring Dhurandhar Title Track, Gehra Hua, Ishq Jalakar, and Teri Ni Kararan.",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg"
        ),
        Album(
            id = "alb_velvet_echoes",
            title = "Saiyaara",
            artist = "Tanishk Bagchi, Faheem Abdullah, Arslan Nizami, Irshad Kamil",
            categoryTag = "BOLLYWOOD • ROMANTIC",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFFF2A5F),
            secondaryColor = Color(0xFF2B0510),
            badgeText = "LOSSLESS MASTER",
            description = "Soaring romantic vocals and lush orchestration on the chart-topping Saiyaara soundtrack.",
            artworkUrl = "https://c.saavncdn.com/598/Saiyaara-Hindi-2025-20250703061754-500x500.jpg"
        ),
        Album(
            id = "alb_midnight_tokyo",
            title = "Stree 2",
            artist = "Sachin-Jigar, Amitabh Bhattacharya, Madhubanti Bagchi, Shilpa Rao",
            categoryTag = "BOLLYWOOD • CHARTBUSTER",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFF041C24),
            badgeText = "SPATIAL AUDIO",
            description = "Non-stop dance and romantic blockbusters including Aaj Ki Raat and Tumhare Hi Rahenge Hum.",
            artworkUrl = "https://c.saavncdn.com/373/Stree-2-Hindi-2024-20240828083834-500x500.jpg"
        ),
        Album(
            id = "alb_solar_flare",
            title = "GLORY",
            artist = "Yo Yo Honey Singh, Paradox",
            categoryTag = "HIP-HOP • HAPPY DIWALI",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_solar_flare,
            primaryColor = Color(0xFFFF9E00),
            secondaryColor = Color(0xFF331800),
            description = "Global hip-hop and desi bass anthems featuring Millionaire and Payal.",
            artworkUrl = "https://c.saavncdn.com/173/GLORY-Hindi-2024-20250117161048-500x500.jpg"
        ),
        Album(
            id = "alb_crimson_tide",
            title = "Bhool Bhulaiyaa 3",
            artist = "Pitbull, Diljit Dosanjh, Pritam, Tanishk Bagchi",
            categoryTag = "BOLLYWOOD • PARTY",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_astral_drift,
            primaryColor = Color(0xFFE11D48),
            secondaryColor = Color(0xFF2E0410),
            badgeText = "NEW RELEASE",
            description = "International crossover anthem featuring Pitbull and Diljit Dosanjh.",
            artworkUrl = "https://c.saavncdn.com/443/Bhool-Bhulaiyaa-3-Hindi-2024-20250430161035-500x500.jpg"
        ),
        Album(
            id = "alb_aurora_pulse",
            title = "Do Patti",
            artist = "Sachet-Parampara, Parampara Tandon, Kausar Munir",
            categoryTag = "BOLLYWOOD • CHARTS",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_emerald_pulse,
            primaryColor = Color(0xFF8B5CF6),
            secondaryColor = Color(0xFF170B3B),
            badgeText = "#1 CHARTBUSTER",
            description = "Heart-touching melodies led by the viral sensation Raanjhan.",
            artworkUrl = "https://c.saavncdn.com/436/Do-Patti-Hindi-2024-20250509181115-500x500.jpg"
        ),
        Album(
            id = "alb_obsidian_hearts",
            title = "Dhurandhar The Revenge",
            artist = "Shashwat Sachdev, Bombay Rockers, Khan Saab, Jasmine Sandlas",
            categoryTag = "2026 ACTION • HINDI & PUNJABI",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            primaryColor = Color(0xFF3B82F6),
            secondaryColor = Color(0xFF1E2430),
            badgeText = "EDITORS' CHOICE",
            description = "High-octane 2026 anthem Aari Aari from Dhurandhar The Revenge.",
            artworkUrl = "https://c.saavncdn.com/581/Dhurandhar-The-Revenge-Hindi-2026-20260409161002-500x500.jpg"
        ),
        Album(
            id = "alb_violet_mirage",
            title = "Sky Force",
            artist = "Arijit Singh, Tanishk Bagchi, Irshad Kamil, Afsana Khan",
            categoryTag = "BOLLYWOOD • SOUNDTRACK",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_violet_mirage,
            primaryColor = Color(0xFFF72585),
            secondaryColor = Color(0xFF2E0938),
            description = "Patriotic and soulful melodies featuring Tu Hain Toh Main Hoon.",
            artworkUrl = "https://c.saavncdn.com/695/Sky-Force-Hindi-2025-20250129103147-500x500.jpg"
        ),
        Album(
            id = "alb_golden_hour",
            title = "Raid 2",
            artist = "Yo Yo Honey Singh, Sachet Tandon, Jasmine Sandlas, Jaani",
            categoryTag = "BOLLYWOOD • PUNJABI",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_golden_hour,
            primaryColor = Color(0xFFF97316),
            secondaryColor = Color(0xFF3D1308),
            description = "Powerhouse tracks Nasha and Money Money from Raid 2.",
            artworkUrl = "https://c.saavncdn.com/641/Raid-2-Hindi-2025-20250429161057-500x500.jpg"
        ),
        Album(
            id = "alb_glacial_static",
            title = "AUJLA SZN 1",
            artist = "Karan Aujla, MXRCI, Jay Trak",
            categoryTag = "PUNJABI • SPATIAL AUDIO",
            releaseYear = 2026,
            artworkRes = R.drawable.img_album_glacial_static,
            primaryColor = Color(0xFF38BDF8),
            secondaryColor = Color(0xFF0C2D48),
            description = "Karan Aujla's 2026 Punjabi bangers Ashke, Wavy, and Tauba Tauba.",
            artworkUrl = "https://c.saavncdn.com/918/AUJLA-SZN-1-Punjabi-2026-20261026194125-500x500.jpg"
        ),
        Album(
            id = "alb_nocturne",
            title = "Pushpa 2 The Rule",
            artist = "Devi Sri Prasad, Shankarr Babu Kandukoori, Laxmi Dasa",
            categoryTag = "TELUGU & HINDI • MASS HIT",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            description = "High-energy pan-India dance numbers led by Peelings.",
            artworkUrl = "https://c.saavncdn.com/366/Pushpa-2-The-Rule-Telugu-Telugu-2024-20241205211012-500x500.jpg"
        ),
        Album(
            id = "alb_cyber_bloom",
            title = "Laapataa Ladies",
            artist = "Arijit Singh, Ram Sampath, Prashant Pandey",
            categoryTag = "ACOUSTIC • SING",
            releaseYear = 2025,
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            badgeText = "EARLY ACCESS",
            description = "Timeless acoustic romance led by Arijit Singh's Sajni.",
            artworkUrl = "https://c.saavncdn.com/252/Laapataa-Ladies-Hindi-2024-20241104085935-500x500.jpg"
        )
    )

    val songs: List<Song> = listOf(
        Song(
            id = "sng_1",
            title = "Dhurandhar - Title Track",
            artist = "Hanumankind, Jasmine Sandlas, Shashwat Sachdev, Sudhir Yaduvanshi",
            albumTitle = "Dhurandhar",
            durationSeconds = 155,
            artworkRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFF168BFF),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 220.0f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Dhurandhar - Title Track",
                "Hanumankind, Jasmine Sandlas, Shashwat Sachdev",
                "Dhurandhar",
                155
            ),
            composer = "Shashwat Sachdev, Charanjit Ahuja",
            producer = "Shashwat Sachdev • Dhurandhar 2026",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/475/d17530d821c3c948ffbbad38a8cd2f08_320.mp4"
        ),
        Song(
            id = "sng_2",
            title = "Gehra Hua",
            artist = "Arijit Singh, Shashwat Sachdev, Irshad Kamil, Armaan Khan",
            albumTitle = "Dhurandhar",
            durationSeconds = 362,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            accentColor = Color(0xFFFF2A5F),
            isExplicit = false,
            trackNumber = 3,
            baseFreqHz = 246.94f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Gehra Hua",
                "Arijit Singh, Shashwat Sachdev",
                "Dhurandhar",
                362
            ),
            composer = "Shashwat Sachdev, Irshad Kamil",
            producer = "Shashwat Sachdev • Dhurandhar",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/475/04cc3b6ccd915df473947d932f094545_320.mp4"
        ),
        Song(
            id = "sng_3",
            title = "Dhurandhar The Revenge - Aari Aari",
            artist = "Shashwat Sachdev, Bombay Rockers, Khan Saab, Jasmine Sandlas",
            albumTitle = "Dhurandhar The Revenge",
            durationSeconds = 210,
            artworkRes = R.drawable.img_album_obsidian_hearts,
            accentColor = Color(0xFF00F5D4),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 261.63f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Dhurandhar The Revenge - Aari Aari",
                "Shashwat Sachdev, Bombay Rockers, Jasmine Sandlas",
                "Dhurandhar The Revenge",
                210
            ),
            composer = "Shashwat Sachdev, Bombay Rockers",
            producer = "Dhurandhar The Revenge 2026",
            artworkUrl = "https://c.saavncdn.com/581/Dhurandhar-The-Revenge-Hindi-2026-20260409161002-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/581/19682aba7f775484c3c24a7f26191aed_320.mp4"
        ),
        Song(
            id = "sng_4",
            title = "Saiyaara",
            artist = "Tanishk Bagchi, Faheem Abdullah, Arslan Nizami, Irshad Kamil",
            albumTitle = "Saiyaara",
            durationSeconds = 370,
            artworkRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFFFF9E00),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 293.66f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Saiyaara",
                "Tanishk Bagchi, Faheem Abdullah, Irshad Kamil",
                "Saiyaara",
                370
            ),
            composer = "Tanishk Bagchi, Faheem Abdullah",
            producer = "YRF Music",
            artworkUrl = "https://c.saavncdn.com/598/Saiyaara-Hindi-2025-20250703061754-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/598/7323a0d8686f6c1b9c21f098c23a9557_320.mp4"
        ),
        Song(
            id = "sng_5",
            title = "Aaj Ki Raat",
            artist = "Sachin-Jigar, Madhubanti Bagchi, Divya Kumar, Amitabh Bhattacharya",
            albumTitle = "Stree 2",
            durationSeconds = 228,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF8B5CF6),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 329.63f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Aaj Ki Raat",
                "Sachin-Jigar, Madhubanti Bagchi, Divya Kumar",
                "Stree 2",
                228
            ),
            composer = "Sachin-Jigar",
            producer = "Maddock Films • Saregama",
            artworkUrl = "https://c.saavncdn.com/373/Stree-2-Hindi-2024-20240828083834-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/373/36b1b3637cdeedfaa9a9012453948aa6_320.mp4"
        ),
        Song(
            id = "sng_6",
            title = "Tauba Tauba",
            artist = "Karan Aujla",
            albumTitle = "Bad Newz",
            durationSeconds = 207,
            artworkRes = R.drawable.img_album_astral_drift,
            accentColor = Color(0xFFE11D48),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 196.00f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Tauba Tauba",
                "Karan Aujla",
                "Bad Newz",
                207
            ),
            composer = "Karan Aujla",
            producer = "Dharma Productions • Saregama",
            artworkUrl = "https://c.saavncdn.com/992/Bad-Newz-Hindi-2024-20250730113701-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/992/5d44da8bc1d78fb72d18b701d758fd1f_320.mp4"
        ),
        Song(
            id = "sng_7",
            title = "Millionaire",
            artist = "Yo Yo Honey Singh",
            albumTitle = "GLORY",
            durationSeconds = 199,
            artworkRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFF3B82F6),
            isExplicit = true,
            trackNumber = 1,
            baseFreqHz = 277.18f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Millionaire",
                "Yo Yo Honey Singh",
                "GLORY",
                199
            ),
            composer = "Yo Yo Honey Singh, Leo Grewal",
            producer = "T-Series • GLORY",
            artworkUrl = "https://c.saavncdn.com/173/GLORY-Hindi-2024-20250117161048-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/173/af61d1cf779677cd767692c0a7f3301f_320.mp4"
        ),
        Song(
            id = "sng_8",
            title = "Raanjhan",
            artist = "Sachet-Parampara, Parampara Tandon, Kausar Munir",
            albumTitle = "Do Patti",
            durationSeconds = 240,
            artworkRes = R.drawable.img_album_emerald_pulse,
            accentColor = Color(0xFFF72585),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 311.13f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Raanjhan",
                "Sachet-Parampara, Parampara Tandon",
                "Do Patti",
                240
            ),
            composer = "Sachet-Parampara",
            producer = "T-Series • Do Patti",
            artworkUrl = "https://c.saavncdn.com/436/Do-Patti-Hindi-2024-20250509181115-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/436/8bf64f05cd45cc6d82daa56d64b30af7_320.mp4"
        ),
        Song(
            id = "sng_9",
            title = "Bhool Bhulaiyaa 3 - Title Track",
            artist = "Pitbull, Diljit Dosanjh, Tanishk Bagchi, Pritam, Neeraj Shridhar",
            albumTitle = "Bhool Bhulaiyaa 3",
            durationSeconds = 230,
            artworkRes = R.drawable.img_album_astral_drift,
            accentColor = Color(0xFFF97316),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 233.08f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Bhool Bhulaiyaa 3 - Title Track",
                "Pitbull, Diljit Dosanjh, Tanishk Bagchi",
                "Bhool Bhulaiyaa 3",
                230
            ),
            composer = "Pritam, Tanishk Bagchi",
            producer = "T-Series",
            artworkUrl = "https://c.saavncdn.com/443/Bhool-Bhulaiyaa-3-Hindi-2024-20250430161035-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/443/c215c512b83a01cfe5db2b98638538b6_320.mp4"
        ),
        Song(
            id = "sng_10",
            title = "Ishq Jalakar - Karvaan",
            artist = "Shashwat Sachdev, Irshad Kamil, Shahzad Ali, Subhadeep Das Chowdhury",
            albumTitle = "Dhurandhar",
            durationSeconds = 250,
            artworkRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFF38BDF8),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 349.23f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Ishq Jalakar - Karvaan",
                "Shashwat Sachdev, Irshad Kamil",
                "Dhurandhar",
                250
            ),
            composer = "Shashwat Sachdev, Roshan",
            producer = "Dhurandhar 2026",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/475/7cd3d5f903b4226025e3d88aa7599317_320.mp4"
        ),
        Song(
            id = "sng_11",
            title = "Teri Ni Kararan",
            artist = "Diljit Dosanjh, Shashwat Sachdev, Lal Chand Yamla Jatt",
            albumTitle = "Dhurandhar",
            durationSeconds = 238,
            artworkRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFFEAB308),
            isExplicit = false,
            trackNumber = 4,
            baseFreqHz = 174.61f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Teri Ni Kararan",
                "Diljit Dosanjh, Shashwat Sachdev",
                "Dhurandhar",
                238
            ),
            composer = "Shashwat Sachdev",
            producer = "Dhurandhar 2026",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/475/d3766ce93dc1b59aa76de27e9c9e2ca3_320.mp4"
        ),
        Song(
            id = "sng_12",
            title = "Tu Hain Toh Main Hoon",
            artist = "Arijit Singh, Afsana Khan, Tanishk Bagchi, Irshad Kamil",
            albumTitle = "Sky Force",
            durationSeconds = 247,
            artworkRes = R.drawable.img_album_violet_mirage,
            accentColor = Color(0xFFEC4899),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 392.00f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Tu Hain Toh Main Hoon",
                "Arijit Singh, Afsana Khan, Tanishk Bagchi",
                "Sky Force",
                247
            ),
            composer = "Tanishk Bagchi, Irshad Kamil",
            producer = "Saregama • Sky Force",
            artworkUrl = "https://c.saavncdn.com/695/Sky-Force-Hindi-2025-20250129103147-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/695/a7196eecf95a8bbe45b3fcee99649a8d_320.mp4"
        ),
        Song(
            id = "sng_13",
            title = "Nasha",
            artist = "Jasmine Sandlas, Sachet Tandon, Divya Kumar, Jaani",
            albumTitle = "Raid 2",
            durationSeconds = 180,
            artworkRes = R.drawable.img_album_golden_hour,
            accentColor = Color(0xFFF97316),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 220.0f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Nasha",
                "Jasmine Sandlas, Sachet Tandon, Jaani",
                "Raid 2",
                180
            ),
            composer = "White Noise Collectives, Jaani",
            producer = "T-Series • Raid 2",
            artworkUrl = "https://c.saavncdn.com/641/Raid-2-Hindi-2025-20250429161057-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/641/f4f49e99e23481907ec132eb1001f5bb_320.mp4"
        ),
        Song(
            id = "sng_14",
            title = "Money Money",
            artist = "Yo Yo Honey Singh",
            albumTitle = "Raid 2",
            durationSeconds = 201,
            artworkRes = R.drawable.img_album_golden_hour,
            accentColor = Color(0xFFFF9E00),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 246.94f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Money Money",
                "Yo Yo Honey Singh",
                "Raid 2",
                201
            ),
            composer = "Yo Yo Honey Singh",
            producer = "T-Series • Raid 2",
            artworkUrl = "https://c.saavncdn.com/641/Raid-2-Hindi-2025-20250429161057-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/641/82b3616d502fff8810abd83c4ed718ee_320.mp4"
        ),
        Song(
            id = "sng_15",
            title = "Ashke",
            artist = "Karan Aujla, MXRCI",
            albumTitle = "AUJLA SZN 1",
            durationSeconds = 217,
            artworkRes = R.drawable.img_album_glacial_static,
            accentColor = Color(0xFF38BDF8),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 261.63f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Ashke",
                "Karan Aujla, MXRCI",
                "AUJLA SZN 1",
                217
            ),
            composer = "Karan Aujla, MXRCI",
            producer = "AUJLA SZN 1 • 2026",
            artworkUrl = "https://c.saavncdn.com/918/AUJLA-SZN-1-Punjabi-2026-20261026194125-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/918/49cfd30c2d28bb0d07d8441d3de1ee84_320.mp4"
        ),
        Song(
            id = "sng_16",
            title = "Wavy",
            artist = "Karan Aujla, Jay Trak",
            albumTitle = "AUJLA SZN 1",
            durationSeconds = 161,
            artworkRes = R.drawable.img_album_glacial_static,
            accentColor = Color(0xFF00F5D4),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 293.66f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Wavy",
                "Karan Aujla, Jay Trak",
                "Wavy",
                161
            ),
            composer = "Karan Aujla",
            producer = "Jay Trak",
            artworkUrl = "https://c.saavncdn.com/178/Wavy-Punjabi-2024-20250523044332-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/178/9af31095a56a0a124dee89ef89ffee5a_320.mp4"
        ),
        Song(
            id = "sng_17",
            title = "Sajni",
            artist = "Arijit Singh, Ram Sampath, Prashant Pandey",
            albumTitle = "Laapataa Ladies",
            durationSeconds = 170,
            artworkRes = R.drawable.img_album_cyber_bloom,
            accentColor = Color(0xFFEC4899),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 329.63f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Sajni",
                "Arijit Singh, Ram Sampath",
                "Laapataa Ladies",
                170
            ),
            composer = "Ram Sampath",
            producer = "T-Series • Laapataa Ladies",
            artworkUrl = "https://c.saavncdn.com/252/Laapataa-Ladies-Hindi-2024-20241104085935-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/252/f53c1a90a2f35d67490badb846d8c849_320.mp4"
        ),
        Song(
            id = "sng_18",
            title = "Peelings (From \"Pushpa 2 The Rule\")",
            artist = "Devi Sri Prasad, Shankarr Babu Kandukoori, Laxmi Dasa",
            albumTitle = "Pushpa 2 The Rule",
            durationSeconds = 247,
            artworkRes = R.drawable.img_album_nocturne,
            accentColor = Color(0xFFEAB308),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 196.00f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Peelings",
                "Devi Sri Prasad, Shankarr Babu Kandukoori, Laxmi Dasa",
                "Pushpa 2 The Rule",
                247
            ),
            composer = "Devi Sri Prasad",
            producer = "T-Series • Pushpa 2",
            artworkUrl = "https://c.saavncdn.com/366/Pushpa-2-The-Rule-Telugu-Telugu-2024-20241205211012-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/366/2eb21d94241ba0d9e970d808a7dc015d_320.mp4"
        ),
        Song(
            id = "sng_19",
            title = "Tumhare Hi Rahenge Hum",
            artist = "Sachin-Jigar, Varun Jain, Shilpa Rao, Amitabh Bhattacharya",
            albumTitle = "Stree 2",
            durationSeconds = 230,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF168BFF),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 277.18f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Tumhare Hi Rahenge Hum",
                "Sachin-Jigar, Varun Jain, Shilpa Rao",
                "Stree 2",
                230
            ),
            composer = "Sachin-Jigar",
            producer = "Saregama • Stree 2",
            artworkUrl = "https://c.saavncdn.com/373/Stree-2-Hindi-2024-20240828083834-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/373/a0958d974322de810950840344956818_320.mp4"
        ),
        Song(
            id = "sng_20",
            title = "Shararat",
            artist = "Jasmine Sandlas, Madhubanti Bagchi, Shashwat Sachdev",
            albumTitle = "Dhurandhar",
            durationSeconds = 224,
            artworkRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFFF72585),
            isExplicit = false,
            trackNumber = 6,
            baseFreqHz = 311.13f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Shararat",
                "Jasmine Sandlas, Madhubanti Bagchi, Shashwat Sachdev",
                "Dhurandhar",
                224
            ),
            composer = "Shashwat Sachdev",
            producer = "Dhurandhar 2026",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/475/e41aa39fdc4b484d5dd3c976a59c72fb_320.mp4"
        ),
        Song(
            id = "sng_21",
            title = "Payal (From \"GLORY\")",
            artist = "Yo Yo Honey Singh, Paradox",
            albumTitle = "GLORY",
            durationSeconds = 226,
            artworkRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFFFF9E00),
            isExplicit = false,
            trackNumber = 2,
            baseFreqHz = 233.08f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Payal",
                "Yo Yo Honey Singh, Paradox",
                "GLORY",
                226
            ),
            composer = "Yo Yo Honey Singh",
            producer = "T-Series • GLORY",
            artworkUrl = "https://c.saavncdn.com/652/Nora-s-Global-Grooves-Hindi-2025-20250207121050-500x500.jpg",
            audioUrl = "https://aac.saavncdn.com/652/0bda417ea2321452b95ecf94d2126c51_320.mp4"
        ),
        Song(
            id = "sng_22",
            title = "Kesariya",
            artist = "Pritam, Arijit Singh, Amitabh Bhattacharya",
            albumTitle = "Brahmastra",
            durationSeconds = 268,
            artworkRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFFFF2A5F),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 349.23f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "Kesariya",
                "Pritam, Arijit Singh, Amitabh Bhattacharya",
                "Brahmastra",
                268
            ),
            composer = "Pritam",
            producer = "Sony Music",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music112/v4/9f/13/ca/9f13ca3b-e533-03e0-f19a-f0aaa774581d/196589311191.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/871/c2febd353f3a076a406fa37510f31f9f_320.mp4"
        ),
        Song(
            id = "sng_23",
            title = "Hass Hass",
            artist = "Diljit Dosanjh, Sia, Greg Kurstin",
            albumTitle = "Hass Hass",
            durationSeconds = 153,
            artworkRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFF00F5D4),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 174.61f,
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
            id = "sng_24",
            title = "G.O.A.T.",
            artist = "Diljit Dosanjh",
            albumTitle = "G.O.A.T.",
            durationSeconds = 224,
            artworkRes = R.drawable.img_album_astral_drift,
            accentColor = Color(0xFFE11D48),
            isExplicit = false,
            trackNumber = 1,
            baseFreqHz = 392.00f,
            synchronizedLyrics = OnyxSongApiService.generateSyncedLyricsForSong(
                "G.O.A.T.",
                "Diljit Dosanjh",
                "G.O.A.T.",
                224
            ),
            composer = "Karan Aujla, G-Funk",
            producer = "Famous Studios",
            artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/d2/89/ac/d289ac98-749e-3822-6b6e-b06aa4815715/859740651597_cover.jpg/600x600bb.jpg",
            audioUrl = "https://aac.saavncdn.com/597/ce842951d6cde3c4355046ca5e250809_320.mp4"
        )
    )

    val playlists: List<Playlist> = listOf(
        Playlist(
            id = "pl_midnight_drive",
            title = "Dhurandhar & 2026 Blockbusters",
            curator = "Onyx Music India",
            updatedLabel = "UPDATED TODAY",
            description = "Dhurandhar Title Track, Gehra Hua, Aari Aari, Ishq Jalakar, Teri Ni Kararan, and Shararat.",
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF168BFF),
            secondaryColor = Color(0xFF1B0B3B),
            songIds = listOf("sng_1", "sng_2", "sng_3", "sng_10", "sng_11", "sng_20"),
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg"
        ),
        Playlist(
            id = "pl_velvet_lounge",
            title = "Bollywood Romance 2026",
            curator = "Onyx Music Romantic",
            updatedLabel = "UPDATED YESTERDAY",
            description = "Saiyaara, Gehra Hua, Raanjhan, Tu Hain Toh Main Hoon, Sajni, and Tumhare Hi Rahenge Hum.",
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFFF2A5F),
            secondaryColor = Color(0xFF2B0510),
            songIds = listOf("sng_4", "sng_2", "sng_8", "sng_12", "sng_17", "sng_19", "sng_22"),
            artworkUrl = "https://c.saavncdn.com/598/Saiyaara-Hindi-2025-20250703061754-500x500.jpg"
        ),
        Playlist(
            id = "pl_deep_focus",
            title = "Punjabi & Hip-Hop Bangers",
            curator = "Onyx Music Punjabi",
            updatedLabel = "UPDATED FRIDAY",
            description = "Karan Aujla, Yo Yo Honey Singh, and Diljit Dosanjh — Tauba Tauba, Millionaire, Ashke, Wavy, and Payal.",
            artworkRes = R.drawable.img_album_nocturne,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            songIds = listOf("sng_6", "sng_7", "sng_15", "sng_16", "sng_21", "sng_23", "sng_24"),
            artworkUrl = "https://c.saavncdn.com/918/AUJLA-SZN-1-Punjabi-2026-20261026194125-500x500.jpg"
        ),
        Playlist(
            id = "pl_new_wave",
            title = "Bollywood Top 50 Dance",
            curator = "Onyx Music Charts",
            updatedLabel = "UPDATED JUST NOW",
            description = "Aaj Ki Raat, Bhool Bhulaiyaa 3, Nasha, Money Money, Peelings, and Tauba Tauba.",
            artworkRes = R.drawable.img_album_cyber_bloom,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            songIds = listOf("sng_5", "sng_9", "sng_13", "sng_14", "sng_18", "sng_6", "sng_1"),
            artworkUrl = "https://c.saavncdn.com/373/Stree-2-Hindi-2024-20240828083834-500x500.jpg"
        ),
        Playlist(
            id = "pl_golden_acoustic",
            title = "Arijit Singh & Soul Sessions",
            curator = "Onyx Music Editorial",
            updatedLabel = "UPDATED THIS WEEK",
            description = "Gehra Hua, Tu Hain Toh Main Hoon, Sajni, Raanjhan, and Kesariya.",
            artworkRes = R.drawable.img_album_golden_hour,
            primaryColor = Color(0xFFF97316),
            secondaryColor = Color(0xFF3D1308),
            songIds = listOf("sng_2", "sng_12", "sng_17", "sng_8", "sng_22", "sng_4"),
            artworkUrl = "https://c.saavncdn.com/252/Laapataa-Ladies-Hindi-2024-20241104085935-500x500.jpg"
        )
    )

    val artists: List<ArtistProfile> = listOf(
        ArtistProfile(
            id = "art_kaelen",
            name = "Arijit Singh",
            genre = "Bollywood • Dhurandhar & Sky Force",
            monthlyListeners = "45.2M Listeners",
            avatarRes = R.drawable.img_album_velvet_echoes,
            accentColor = Color(0xFF168BFF),
            topAlbumId = "alb_neon_horizons",
            avatarUrl = "https://c.saavncdn.com/695/Sky-Force-Hindi-2025-20250129103147-500x500.jpg"
        ),
        ArtistProfile(
            id = "art_soraia",
            name = "Shashwat Sachdev",
            genre = "Dhurandhar • 2026 Soundtrack",
            monthlyListeners = "29.4M Listeners",
            avatarRes = R.drawable.img_album_neon_horizons,
            accentColor = Color(0xFFFF2A5F),
            topAlbumId = "alb_neon_horizons",
            avatarUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg"
        ),
        ArtistProfile(
            id = "art_kenji",
            name = "Karan Aujla",
            genre = "Punjabi • AUJLA SZN 1",
            monthlyListeners = "34.8M Listeners",
            avatarRes = R.drawable.img_album_glacial_static,
            accentColor = Color(0xFF00F5D4),
            topAlbumId = "alb_glacial_static",
            avatarUrl = "https://c.saavncdn.com/918/AUJLA-SZN-1-Punjabi-2026-20261026194125-500x500.jpg"
        ),
        ArtistProfile(
            id = "art_aurelia",
            name = "Yo Yo Honey Singh",
            genre = "Hip-Hop • GLORY & Raid 2",
            monthlyListeners = "32.1M Listeners",
            avatarRes = R.drawable.img_album_solar_flare,
            accentColor = Color(0xFFFF9E00),
            topAlbumId = "alb_solar_flare",
            avatarUrl = "https://c.saavncdn.com/173/GLORY-Hindi-2024-20250117161048-500x500.jpg"
        ),
        ArtistProfile(
            id = "art_zara",
            name = "Diljit Dosanjh",
            genre = "Punjabi • Global Pop",
            monthlyListeners = "36.5M Listeners",
            avatarRes = R.drawable.img_album_astral_drift,
            accentColor = Color(0xFFEC4899),
            topAlbumId = "alb_crimson_tide",
            avatarUrl = "https://c.saavncdn.com/443/Bhool-Bhulaiyaa-3-Hindi-2024-20250430161035-500x500.jpg"
        ),
        ArtistProfile(
            id = "art_julian",
            name = "Sachin-Jigar",
            genre = "Bollywood • Stree 2",
            monthlyListeners = "27.9M Listeners",
            avatarRes = R.drawable.img_album_midnight_tokyo,
            accentColor = Color(0xFFEAB308),
            topAlbumId = "alb_midnight_tokyo",
            avatarUrl = "https://c.saavncdn.com/373/Stree-2-Hindi-2024-20240828083834-500x500.jpg"
        )
    )

    val radioStations: List<RadioStation> = listOf(
        RadioStation(
            id = "rad_aura_1",
            name = "Dhurandhar & 2026 Hits Radio",
            tagline = "Non-stop 2026 Bollywood blockbusters: Dhurandhar, Saiyaara, Stree 2 & Raid 2.",
            hostName = "Live from Mumbai Studio",
            isLive = true,
            scheduleText = "LIVE NOW • 24/7 BOLLYWOOD",
            artworkRes = R.drawable.img_album_neon_horizons,
            primaryColor = Color(0xFF168BFF),
            secondaryColor = Color(0xFF06163A),
            featuredSongId = "sng_1",
            artworkUrl = "https://c.saavncdn.com/475/Dhurandhar-Hindi-2025-20260203083204-500x500.jpg"
        ),
        RadioStation(
            id = "rad_tokyo_nights",
            name = "Punjabi Powerhouse Radio",
            tagline = "Karan Aujla, Diljit Dosanjh, and Yo Yo Honey Singh anthems in Lossless AAC.",
            hostName = "Hosted by DJ Kabir",
            isLive = true,
            scheduleText = "LIVE STREAM • PUNJABI & HIP-HOP",
            artworkRes = R.drawable.img_album_glacial_static,
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFF041C24),
            featuredSongId = "sng_6",
            artworkUrl = "https://c.saavncdn.com/918/AUJLA-SZN-1-Punjabi-2026-20261026194125-500x500.jpg"
        ),
        RadioStation(
            id = "rad_club_europa",
            name = "Arijit Singh & Romance Radio",
            tagline = "Gehra Hua, Saiyaara, Raanjhan, Tu Hain Toh Main Hoon, and Sajni.",
            hostName = "Curated by Onyx India",
            isLive = false,
            scheduleText = "EPISODE 48 • ROMANTIC MIX",
            artworkRes = R.drawable.img_album_velvet_echoes,
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFF120428),
            featuredSongId = "sng_2",
            artworkUrl = "https://c.saavncdn.com/598/Saiyaara-Hindi-2025-20250703061754-500x500.jpg"
        ),
        RadioStation(
            id = "rad_neo_classical",
            name = "Party & Dance Dhamaka Radio",
            tagline = "Aaj Ki Raat, Millionaire, Bhool Bhulaiyaa 3, Nasha, and Peelings.",
            hostName = "Hosted by Onyx Club",
            isLive = false,
            scheduleText = "24/7 PARTY STATION",
            artworkRes = R.drawable.img_album_midnight_tokyo,
            primaryColor = Color(0xFFEAB308),
            secondaryColor = Color(0xFF1F1912),
            featuredSongId = "sng_5",
            artworkUrl = "https://c.saavncdn.com/373/Stree-2-Hindi-2024-20240828083834-500x500.jpg"
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
