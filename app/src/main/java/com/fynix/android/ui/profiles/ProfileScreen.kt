package com.fynix.android.ui.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fynix.android.data.ServerSettings
import com.fynix.android.data.SettingsRepository
import com.fynix.android.network.createApi
import com.fynix.android.network.models.ProfileInfo
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

data class AppSettings(
    val host: String = "",
    val port: Int = 43862,
    val username: String = "",
    val password: String = ""
)

@Composable
fun ProfileScreen(
    settingsRepository: SettingsRepository,
    onConnected: (AppSettings) -> Unit
) {
    val scope = rememberCoroutineScope()
    var serverSettings by remember { mutableStateOf(AppSettings()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var profiles by remember { mutableStateOf<List<ProfileInfo>>(emptyList()) }
    var selectedProfile by remember { mutableStateOf<ProfileInfo?>(null) }

    fun connect() {
        loading = true
        error = null
        val finalSettings = serverSettings
        val finalRepository = settingsRepository
        val finalOnConnected = onConnected
        scope.launch {
            try {
                val api = createApi(finalSettings.host, finalSettings.port, finalSettings.username, finalSettings.password)
                val health = api.getHealth()
                if (health.ok && health.data != null) {
                    val profilesResp = api.getProfiles()
                    if (profilesResp.ok && profilesResp.data != null) {
                        profiles = profilesResp.data.profiles
                        selectedProfile = profilesResp.data.profiles.firstOrNull { it.isActive }
                        finalRepository.saveSettings(
                            ServerSettings(
                                host = finalSettings.host,
                                port = finalSettings.port,
                                username = finalSettings.username,
                                password = finalSettings.password,
                                activeProfileId = selectedProfile?.id ?: ""
                            )
                        )
                        loading = false
                        finalOnConnected(finalSettings)
                    } else {
                        error = profilesResp.error ?: "Failed to get profiles"
                        loading = false
                    }
                } else {
                    error = health.error ?: "Connection failed"
                    loading = false
                }
            } catch (e: Exception) {
                error = e.message ?: "Connection failed"
                loading = false
            }
        }
    }

    fun selectProfile(profile: ProfileInfo) {
        selectedProfile = profile
        val finalSettings = serverSettings
        val finalRepository = settingsRepository
        val finalOnConnected = onConnected
        scope.launch {
            try {
                val api = createApi(finalSettings.host, finalSettings.port, finalSettings.username, finalSettings.password)
                val select = api.selectProfile(profile.id)
                if (select.ok) {
                    finalRepository.saveSettings(
                        ServerSettings(
                            host = finalSettings.host,
                            port = finalSettings.port,
                            username = finalSettings.username,
                            password = finalSettings.password,
                            activeProfileId = profile.id
                        )
                    )
                    finalOnConnected(finalSettings)
                } else {
                    error = select.error
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to select profile"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "FYNIX",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B00)
            )
        )
        Text(
            text = "Media Hub",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Connect to Server",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = serverSettings.host,
            onValueChange = { serverSettings = serverSettings.copy(host = it) },
            label = { Text("Host / IP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serverSettings.port.toString(),
            onValueChange = { serverSettings = serverSettings.copy(port = it.toIntOrNull() ?: 43862) },
            label = { Text("Port") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serverSettings.username,
            onValueChange = { serverSettings = serverSettings.copy(username = it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serverSettings.password,
            onValueChange = { serverSettings = serverSettings.copy(password = it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { connect() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && serverSettings.host.isNotEmpty()
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Connect")
        }

        if (profiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Select Profile",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        selected = selectedProfile?.id == profile.id,
                        onClick = { selectProfile(profile) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: ProfileInfo,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2D2D2D) else Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(profile.avatarColor))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.firstOrNull()?.uppercaseChar().toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}