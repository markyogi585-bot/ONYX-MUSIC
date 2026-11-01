package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyOnyxMusicNavigationBackStackNowPlayingLyricsQueueSettingsAndAccount() {
        // 1. Dismiss Splash Screen if visible
        val splashNodes = composeTestRule.onAllNodesWithTag("onyx_splash_screen").fetchSemanticsNodes()
        if (splashNodes.isNotEmpty()) {
            composeTestRule.onNodeWithTag("onyx_splash_screen").performClick()
        }

        // 2. Verify Home Screen Top Picks for You & Floating Glass Navigation Bar
        composeTestRule.onNodeWithTag("floating_glass_navigation_bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Top Picks for You").assertIsDisplayed()

        // Scroll to Recently Played and Made For You sections
        composeTestRule.onNodeWithTag("home_screen_list")
            .performScrollToNode(hasTestTag("row_recently_played"))
        composeTestRule.onNodeWithText("Recently Played").assertIsDisplayed()

        composeTestRule.onNodeWithTag("home_screen_list")
            .performScrollToNode(hasTestTag("row_made_for_you"))
        composeTestRule.onNodeWithText("Made For You").assertIsDisplayed()

        // 3. Test True Back Stack: HOME -> PLAYLIST -> NOW PLAYING -> LYRICS -> BACK -> QUEUE -> BACK -> PLAYLIST -> HOME
        composeTestRule.onNodeWithTag("playlist_card_pl_midnight_drive").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("detail_overlay_sheet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail_play_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail_shuffle_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail_add_library_button").assertIsDisplayed()

        // Expand Mini-Player from Playlist Detail to open Now Playing on top of Playlist Detail
        composeTestRule.onNodeWithTag("floating_mini_player").performClick()
        composeTestRule.onNodeWithTag("now_playing_full_sheet").assertIsDisplayed()

        // Open Song Context Menu from Now Playing and verify rich actions (e.g., View Credits)
        composeTestRule.onNodeWithTag("now_playing_more_button").performClick()
        composeTestRule.onNodeWithTag("track_options_sheet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("track_option_credits").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("song_credits_modal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").performClick()

        // Open Audio Output sheet and verify real Android system route is displayed
        composeTestRule.onNodeWithTag("now_playing_audio_output_button").performClick()
        composeTestRule.onNodeWithTag("audio_output_sheet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active Android system audio routes").assertIsDisplayed()
        composeTestRule.onNodeWithTag("audio_route_phone_speaker").performClick()

        // Open Lyrics from Now Playing, then press Back to return to Now Playing
        composeTestRule.onNodeWithTag("now_playing_lyrics_button").performClick()
        composeTestRule.onNodeWithTag("lyrics_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lyrics_back_button").performClick()
        composeTestRule.onNodeWithTag("now_playing_full_sheet").assertIsDisplayed()

        // Open Queue from Now Playing, then press Back to return to Now Playing
        composeTestRule.onNodeWithTag("now_playing_queue_button").performClick()
        composeTestRule.onNodeWithTag("queue_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("queue_back_button").performClick()
        composeTestRule.onNodeWithTag("now_playing_full_sheet").assertIsDisplayed()

        // Press Back on Now Playing -> MUST return to Playlist Detail (not skip to Home)
        composeTestRule.onNodeWithTag("now_playing_collapse_button").performClick()
        composeTestRule.onNodeWithTag("detail_overlay_sheet").assertIsDisplayed()

        // Press Back on Playlist Detail -> MUST return to Home
        composeTestRule.onNodeWithTag("detail_back_button").performClick()
        composeTestRule.onNodeWithTag("home_screen_list").assertIsDisplayed()

        // 4. Switch to New, Radio, Library & Search Tabs
        composeTestRule.onNodeWithTag("nav_tab_new").performClick()
        composeTestRule.onNodeWithText("Early Access").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_tab_radio").performClick()
        composeTestRule.onNodeWithText("Live Broadcasts").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_tab_library").performClick()
        composeTestRule.onNodeWithTag("library_screen_list")
            .performScrollToNode(hasTestTag("library_sort_row"))
        composeTestRule.onNodeWithTag("library_sort_title_az").performClick()

        composeTestRule.onNodeWithTag("nav_tab_search").performClick()
        composeTestRule.onNodeWithText("Happy Diwali").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bollywood").assertIsDisplayed()

        // 5. Open Floating Three-Dot Glass Popup Menu -> Navigate to Settings
        composeTestRule.onNodeWithTag("floating_three_dot_menu_button").performClick()
        composeTestRule.onNodeWithTag("popup_menu_settings").assertIsDisplayed()
        composeTestRule.onNodeWithTag("popup_menu_settings").performClick()

        // Verify Settings Screen sections & Circular Back Button
        composeTestRule.onNodeWithTag("settings_screen_root").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_back_button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add & Delete Playlist Songs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add & Delete Favourite Songs").assertIsDisplayed()

        // Scroll to Appearance row, open Appearance subpage, switch to Light mode, and navigate back
        composeTestRule.onNodeWithTag("settings_lazy_column")
            .performScrollToNode(hasTestTag("row_appearance"))
        composeTestRule.onNodeWithTag("row_appearance").performClick()
        composeTestRule.onNodeWithTag("theme_option_light").assertIsDisplayed()
        composeTestRule.onNodeWithTag("theme_option_light").performClick()
        composeTestRule.onNodeWithTag("subpage_back_button").performClick()

        // Return from Settings to main tab using Circular Back Button
        composeTestRule.onNodeWithTag("settings_back_button").performClick()
        composeTestRule.onNodeWithTag("floating_glass_navigation_bar").assertIsDisplayed()

        // 6. Open Three-Dot Menu -> Navigate to Account Screen
        composeTestRule.onNodeWithTag("floating_three_dot_menu_button").performClick()
        composeTestRule.onNodeWithTag("popup_menu_account").performClick()
        composeTestRule.onNodeWithTag("account_screen_container").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set Up Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upgrade to Family").assertIsDisplayed()
        composeTestRule.onNodeWithTag("account_close_button").performClick()
    }
}
