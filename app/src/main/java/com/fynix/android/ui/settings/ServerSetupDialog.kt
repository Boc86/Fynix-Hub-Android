package com.fynix.android.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fynix.android.data.SettingsRepository
import com.fynix.android.data.ServerSettings
import kotlinx.coroutines.launch

/**
 * Mandatory first-launch dialog: shown until a server host is configured.
 * Cannot be dismissed (no host = app can't do anything).
 * Receives [initialSettings] from the caller so it doesn't need to observe the Flow.
 */
@Composable
fun ServerSetupDialog(
    initialSettings: ServerSettings,
    settingsRepository: SettingsRepository,
    onSaved: () -> Unit
) {
    var host by remember { mutableStateOf(initialSettings.host) }
    var port by remember { mutableStateOf(initialSettings.port.toString()) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { /* mandatory until host set */ },
        title = { Text("Connect to Fynix Hub") },
        text = {
            Column {
                Text("Enter your desktop app's server details.")
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        settingsRepository.saveSettings(
                            ServerSettings(
                                host = host.trim(),
                                port = port.toIntOrNull() ?: 43862,
                                username = username.trim(),
                                password = password
                            )
                        )
                        onSaved()
                    }
                }
            ) {
                Text("Connect")
            }
        }
    )
}
