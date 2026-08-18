package com.fynix.android.ui.channels

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.SettingsRepository
import com.fynix.android.network.models.MergedChannel
import com.fynix.android.ui.components.FocusableCard
import com.fynix.android.ui.settings.ServerSetupDialog

@Composable
fun ChannelListScreen(
    networkRepo: NetworkRepository,
    settingsRepository: SettingsRepository,
    onChannelSelected: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelListViewModel = viewModel(factory = ChannelListViewModelFactory(networkRepo))
) {
    val channels by viewModel.channels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val settings by settingsRepository.settings.collectAsState(initial = com.fynix.android.data.ServerSettings())

    LaunchedEffect(settings.host) {
        if (settings.host.isNotBlank()) viewModel.loadChannels()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(channels) { channel ->
                FocusableCard(
                    focused = false,
                    onClick = { onChannelSelected(channel.id) },
                    logoUrl = channel.logo.ifEmpty { channel.logoImage },
                    name = channel.name,
                    logoHeight = 160
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        error?.let { message ->
            Text(
                text = "Error: $message",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Button(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Settings")
        }
    }

    if (settings.host.isBlank()) {
        ServerSetupDialog(
            initialSettings = settings,
            settingsRepository = settingsRepository,
            onSaved = { /* host set -> LaunchedEffect reloads channels */ }
        )
    }
}
