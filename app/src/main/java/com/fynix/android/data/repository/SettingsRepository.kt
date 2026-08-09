package com.fynix.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.fynix.android.data.model.ServerSettings

/**
 * DataStore key for the settings file.
 */
private val PreferencesHostKey = stringPreferencesKey("host")
private val PreferencesPortKey = intPreferencesKey("port")
private val PreferencesUsernameKey = stringPreferencesKey("username")
private val PreferencesPasswordKey = stringPreferencesKey("password")
private val PreferencesLastConnectedKey = booleanPreferencesKey("last_connected")

/**
 * Settings repository backed by DataStore.
 *
 * Provides both a current snapshot and a reactive Flow of settings.
 */
class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * Reactive stream of current settings.
     */
    val settingsFlow: Flow<ServerSettings> = dataStore.data.map { prefs ->
        ServerSettings(
            host = prefs[PreferencesHostKey] ?: "192.168.1.100",
            port = prefs[PreferencesPortKey] ?: 8080,
            username = prefs[PreferencesUsernameKey] ?: "",
            password = prefs[PreferencesPasswordKey] ?: "",
        )
    }

    /**
     * Save updated settings.
     */
    suspend fun saveSettings(settings: ServerSettings) {
        dataStore.edit { prefs ->
            prefs[PreferencesHostKey] = settings.host
            prefs[PreferencesPortKey] = settings.port
            prefs[PreferencesUsernameKey] = settings.username
            prefs[PreferencesPasswordKey] = settings.password
        }
    }

    /**
     * Clear all stored settings.
     */
    suspend fun clearSettings() {
        dataStore.edit { prefs ->
            prefs.remove(PreferencesHostKey)
            prefs.remove(PreferencesPortKey)
            prefs.remove(PreferencesUsernameKey)
            prefs.remove(PreferencesPasswordKey)
        }
    }
}

/**
 * Application-wide DataStore instance.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fynix_settings")
