package com.fynix.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.SettingsRepository
import com.fynix.android.ui.channels.ChannelListScreen
import com.fynix.android.ui.input.InputManager
import com.fynix.android.ui.input.InputMode
import com.fynix.android.ui.input.LocalInputManager
import com.fynix.android.ui.player.PlayerScreen
import com.fynix.android.ui.search.SearchScreen
import com.fynix.android.ui.settings.SettingsScreen

/**
 * Top-level navigation destinations (Player is a transient overlay).
 */
sealed interface NavTab {
    val title: String
    val icon: ImageVector

    data object Channels : NavTab {
        override val title = "Channels"
        override val icon = Icons.Default.PlayArrow
    }
    data object Search : NavTab {
        override val title = "Search"
        override val icon = Icons.Default.Search
    }
    data object Settings : NavTab {
        override val title = "Settings"
        override val icon = Icons.Default.Settings
    }
}

@Composable
fun FynixApp(
    networkRepo: NetworkRepository,
    settingsRepo: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val inputManager = remember(ctx) { InputManager(ctx) }
    val isDpad = inputManager.mode.value == InputMode.DPAD

    var selectedTab by remember { mutableStateOf<NavTab>(NavTab.Channels) }
    var playingChannelId by remember { mutableStateOf<String?>(null) }

    CompositionLocalProvider(LocalInputManager provides inputManager) {
        Box(modifier = modifier.fillMaxSize()) {
            if (isDpad) {
                // TV sidebar + content side-by-side
                Row(modifier = Modifier.fillMaxSize()) {
                    TVSidebar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        modifier = Modifier.width(72.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        ContentPane(
                            selectedTab = selectedTab,
                            onChannelSelected = { playingChannelId = it },
                            networkRepo = networkRepo,
                            settingsRepo = settingsRepo,
                            onOpenSettings = { selectedTab = NavTab.Settings }
                        )
                    }
                }
            } else {
                // Mobile: bottom nav bar + content (content fills, bar overlays bottom)
                ContentPane(
                    selectedTab = selectedTab,
                    onChannelSelected = { playingChannelId = it },
                    networkRepo = networkRepo,
                    settingsRepo = settingsRepo,
                    onOpenSettings = { selectedTab = NavTab.Settings },
                    modifier = Modifier.navigationBarsPadding()
                )
                BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.navigationBarsPadding()
                )
            }

            // Player overlay — full-screen on top of everything
            val channelId = playingChannelId
            if (channelId != null) {
                PlayerScreen(
                    channelId = channelId,
                    onBack = { playingChannelId = null },
                    networkRepo = networkRepo,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun ContentPane(
    selectedTab: NavTab,
    onChannelSelected: (String) -> Unit,
    networkRepo: NetworkRepository,
    settingsRepo: SettingsRepository,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (selectedTab) {
            is NavTab.Channels -> ChannelListScreen(
                networkRepo = networkRepo,
                settingsRepository = settingsRepo,
                onChannelSelected = onChannelSelected,
                onOpenSettings = onOpenSettings
            )
            is NavTab.Search -> SearchScreen(
                networkRepo = networkRepo,
                onChannelSelected = onChannelSelected
            )
            is NavTab.Settings -> SettingsScreen(
                onBack = onOpenSettings,
                settingsRepository = settingsRepo
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(NavTab.Channels, NavTab.Search, NavTab.Settings)
    NavigationBar(modifier = modifier) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) }
            )
        }
    }
}

@Composable
private fun TVSidebar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(NavTab.Channels, NavTab.Search, NavTab.Settings)
    Column(modifier = modifier) {
        NavigationRail {
            tabs.forEach { tab ->
                NavigationRailItem(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    icon = { Icon(tab.icon, contentDescription = tab.title) },
                    label = { Text(tab.title, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }
    }
}
