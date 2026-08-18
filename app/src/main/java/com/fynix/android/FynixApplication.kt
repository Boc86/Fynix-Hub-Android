package com.fynix.android

import android.app.Application
import com.fynix.android.data.NetworkRepository
import com.fynix.android.data.SettingsRepository

/**
 * Application class that provides singleton repositories.
 * SettingsRepository now uses DataStore for persistence across restarts.
 */
class FynixApplication : Application() {
    val settingsRepository by lazy { SettingsRepository(this) }
    val networkRepository by lazy { NetworkRepository(settingsRepository.settings) }
}
