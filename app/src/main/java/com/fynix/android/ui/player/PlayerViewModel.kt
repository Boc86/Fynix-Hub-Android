package com.fynix.android.ui.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fynix.android.data.NetworkRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.Credentials

class PlayerViewModel(
    application: Application,
    private val networkRepo: NetworkRepository
) : AndroidViewModel(application) {

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player

    private val _playerState = MutableStateFlow(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState

    fun loadChannel(channelId: String) {
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            try {
                val settings = networkRepo.settings.first()
                val url = "http://${settings.host}:${settings.port}/api/stream/${channelId}/p/"
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                    .setDefaultRequestProperties(
                        if (settings.username.isNotEmpty()) {
                            mapOf(
                                "Authorization" to Credentials.basic(
                                    settings.username,
                                    settings.password
                                )
                            )
                        } else {
                            emptyMap()
                        }
                    )
                val player = ExoPlayer.Builder(getApplication())
                    .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
                    .build()
                player.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                player.prepare()
                player.play()
                _player.value = player
                _playerState.value = PlayerState.Playing
            } catch (e: Exception) {
                _playerState.value = PlayerState.Error
            }
        }
    }

    fun release() {
        _player.value?.release()
        _player.value = null
        _playerState.value = PlayerState.Idle
    }

    override fun onCleared() {
        release()
        super.onCleared()
    }

    companion object {
        fun factory(networkRepo: NetworkRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application,
                    networkRepo
                )
            }
        }
    }
}

enum class PlayerState {
    Idle, Loading, Playing, Error
}
