package com.fynix.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fynix.android.data.NetworkRepository
import com.fynix.android.ui.channels.ChannelListScreen
import com.fynix.android.ui.player.PlayerScreen
import com.fynix.android.ui.settings.SettingsScreen

@Composable
fun FynixApp(
    networkRepo: NetworkRepository,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Channels) }
    var selectedChannelId by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        is Screen.Channels -> ChannelListScreen(
            networkRepo = networkRepo,
            onChannelSelected = { channelId ->
                selectedChannelId = channelId
                currentScreen = Screen.Player(channelId)
            }
        )
        is Screen.Player -> PlayerScreen(
            channelId = (currentScreen as Screen.Player).channelId,
            onBack = { currentScreen = Screen.Channels },
            networkRepo = networkRepo
        )
        is Screen.Settings -> SettingsScreen(
            onBack = { currentScreen = Screen.Channels }
        )
    }
}

sealed class Screen {
    data object Channels : Screen()
    data class Player(val channelId: String) : Screen()
    data object Settings : Screen()
}
