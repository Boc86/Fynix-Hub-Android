package com.fynix.android.data

import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory settings repository.
 *
 * In a full implementation, this would be backed by DataStore.
 * For now, provides a simple reactive wrapper around ServerSettings.
 */
class SettingsRepository {
    private val _settings = MutableStateFlow(ServerSettings())
    val settings: StateFlow<ServerSettings> = _settings

    fun updateSettings(newSettings: ServerSettings) {
        _settings.value = newSettings
    }

    suspend fun saveSettings(newSettings: ServerSettings) {
        _settings.value = newSettings
    }
}
