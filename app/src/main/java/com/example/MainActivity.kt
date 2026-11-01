package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.firebase.configureAppCheck
import com.example.ui.navigation.AuraMusicApp
import com.example.ui.theme.AuraMusicTheme
import com.example.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            val musicViewModel: MusicViewModel = viewModel()
            val uiState by musicViewModel.uiState.collectAsStateWithLifecycle()
            val prefs = uiState.persistedSettings

            AuraMusicTheme(
                themeMode = prefs.themeMode,
                largerText = prefs.largerTextEnabled,
                increaseContrast = prefs.increaseContrastEnabled
            ) {
                AuraMusicApp(musicViewModel = musicViewModel)
            }
        }
    }
}
