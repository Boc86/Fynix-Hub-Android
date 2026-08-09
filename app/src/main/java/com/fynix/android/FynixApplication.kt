package com.fynix.android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.SettingsRepository
import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Application class that provides singleton repositories.
 */
class FynixApp : Application() {
    val settingsRepository by lazy { SettingsRepository(this) }
    val networkRepository by lazy { NetworkRepository(settingsRepository.settings) }
}

/**
 * Base ViewModel that has access to the application context for ExoPlayer.
 */
open class BaseViewModel(app: Application) : AndroidViewModel(app)
