package com.fynix.android.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fynix.android.data.SettingsRepository
import com.fynix.android.network.models.ServerSettings

/**
 * Mandatory first-launch dialog: shown until a server host is configured.
 * Cannot be dismissed (no host = app can't do anything).
 */
@Composable
fun ServerSetupDialog(
    settingsRepository: SettingsRepository,
    onSaved: () -> Unit
) {
    var host by remember { mutableStateOf(settingsRepository.settings.value.host) }
    var port by remember { mutableStateOf(settingsRepository.settings.value.port.toString()) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                    settingsRepository.updateSettings(
                        ServerSettings(
                            host = host.trim(),
                            port = port.toIntOrNull() ?: 43862,
                            username = username.trim(),
                            password = password
                        )
                    )
                    onSaved()
                }
            ) {
                Text("Connect")
            }
        }
    )
}
