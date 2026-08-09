package com.fynix.android.ui.player

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.fynix.android.data.NetworkRepository

@Composable
fun PlayerScreen(
    channelId: String,
    onBack: () -> Unit,
    networkRepo: NetworkRepository,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    val playerState by viewModel.playerState

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.player
                }
            },
            update = { view ->
                view.player = viewModel.player
            }
        )

        // TODO: Add loading/error overlays
        if (playerState == PlayerState.Loading) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    }

    DisposableEffect(channelId) {
        viewModel.loadChannel(channelId, networkRepo)
        onDispose {
            viewModel.release()
        }
    }
}

enum class PlayerState {
    Idle, Loading, Playing, Error
}
