package com.fynix.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.fynix.android.ui.theme.FynixHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FynixHubTheme {
                val app = application as FynixApp
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FynixApp(
                        networkRepo = app.networkRepository,
                        settingsRepo = app.settingsRepository,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
