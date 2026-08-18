package com.fynix.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.fynix.android.data.ServerSettings
import com.fynix.android.data.SettingsRepository
import com.fynix.android.ui.profiles.ProfileScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val settingsRepository by lazy {
        (application as FynixApplication).settingsRepository
    }
    private val networkRepository by lazy {
        (application as FynixApplication).networkRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Repositories are singletons on the Application (see FynixApplication.kt).
                    val networkRepo = remember { networkRepository }
                    val settingsRepo = remember { settingsRepository }

                    var ready by remember { mutableStateOf(false) }

                    // Auto-connect on startup: read saved settings once.
                    // If a host was previously configured, jump straight to
                    // the app — no profile/connect screen needed.
                    val savedSettings by settingsRepo.settings.collectAsState(
                        initial = ServerSettings()
                    )
                    LaunchedEffect(Unit) {
                        val saved = settingsRepo.settings.first()
                        if (saved.host.isNotBlank()) ready = true
                    }

                    // React to settings saved from the profile/connect flow.
                    LaunchedEffect(savedSettings.host) {
                        if (savedSettings.host.isNotBlank() && !ready) {
                            ready = true
                        }
                    }

                    if (ready) {
                        FynixApp(
                            networkRepo = networkRepo,
                            settingsRepo = settingsRepo
                        )
                    } else {
                        ProfileScreen(
                            settingsRepository = settingsRepo,
                            onConnected = { settings ->
                                lifecycleScope.launch {
                                    settingsRepo.saveSettings(settings)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
