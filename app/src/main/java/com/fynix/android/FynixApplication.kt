package com.fynix.android

import android.app.Application
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.SettingsRepository
import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Application class that provides singleton repositories.
 */
class FynixApp : Application() {
    val settingsRepository by lazy { SettingsRepository() }
    val networkRepository by lazy { NetworkRepository(settingsRepository.settings) }
}
