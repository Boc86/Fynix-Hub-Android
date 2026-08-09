package com.fynix.android.ui.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.ServerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val networkRepo: NetworkRepository
) : ViewModel() {
    val player = ExoPlayer.Builder(androidx.appcompat.app.AppCompatActivity().application)
        .build()

    private val _playerState = MutableStateFlow(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState

    fun loadChannel(channelId: String) {
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            try {
                val settings = networkRepo.settings.first()
                networkRepo.getStreamUrl(settings.host, settings.port, channelId)
                    .collect { result ->
                        result.onSuccess { url ->
                            player.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                            player.prepare()
                            player.play()
                            _playerState.value = PlayerState.Playing
                        }.onFailure { e ->
                            _playerState.value = PlayerState.Error
                        }
                    }
            } catch (e: Exception) {
                _playerState.value = PlayerState.Error
            }
        }
    }

    fun release() {
        player.release()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
