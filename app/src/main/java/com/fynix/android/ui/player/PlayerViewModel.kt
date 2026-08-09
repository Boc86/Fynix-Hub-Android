package com.fynix.android.ui.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.ServerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val networkRepo: NetworkRepository
) : ViewModel() {
    // ExoPlayer will be initialized in initWithContext
    private var _player: ExoPlayer? = null
    val player: ExoPlayer? get() = _player

    private val _playerState = MutableStateFlow(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState

    fun initWithContext(appContext: android.content.Context) {
        _player = ExoPlayer.Builder(appContext).build()
    }

    fun loadChannel(channelId: String) {
        val player = _player ?: return
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            try {
                val settings = networkRepo.settings.first()
                val url = "http://${settings.host}:${settings.port}/api/stream/${channelId}/p/"
                player.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                player.prepare()
                player.play()
                _playerState.value = PlayerState.Playing
            } catch (e: Exception) {
                _playerState.value = PlayerState.Error
            }
        }
    }

    fun release() {
        _player?.release()
        _player = null
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}

enum class PlayerState {
    Idle, Loading, Playing, Error
}
