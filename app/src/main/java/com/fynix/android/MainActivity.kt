package com.fynix.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.SettingsRepository
import com.fynix.android.ui.theme.FynixHubTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepo = SettingsRepository(this)
        val networkRepo = NetworkRepository(settingsRepo.settings)

        lifecycleScope.launch {
            settingsRepo.settings.first()
            // Initialize with saved settings or defaults
        }

        setContent {
            FynixHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FynixApp(networkRepo = networkRepo)
                }
            }
        }
    }
}
