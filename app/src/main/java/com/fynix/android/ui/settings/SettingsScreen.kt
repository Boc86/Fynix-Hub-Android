package com.fynix.android.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fynix.android.data.SettingsRepository
import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    // Collect initial settings once
    val settings by settingsRepository.settings.collectAsState(initial = ServerSettings())

    var host by remember { mutableStateOf(settings.host) }
    var port by remember { mutableStateOf(settings.port.toString()) }
    var username by remember { mutableStateOf(settings.username) }
    var password by remember { mutableStateOf(settings.password) }
    val scope = rememberCoroutineScope()

    // Re-sync fields when settings change externally
    LaunchedEffect(settings.host) {
        if (settings.host != host) {
            host = settings.host
            port = settings.port.toString()
            username = settings.username
            password = settings.password
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Server Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        ServerSettingsForm(
            host = host,
            onHostChange = { host = it },
            port = port,
            onPortChange = { port = it },
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newSettings = ServerSettings(
                    host = host.trim(),
                    port = port.toIntOrNull() ?: 43862,
                    username = username.trim(),
                    password = password
                )
                scope.launch {
                    settingsRepository.saveSettings(newSettings)
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
