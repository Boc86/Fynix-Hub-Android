package com.fynix.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Key for DataStore persistence. */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private object SettingsKeys {
    val HOST = stringPreferencesKey("host")
    val PORT = intPreferencesKey("port")
    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")
}

/**
 * Settings repository backed by DataStore SharedPreferences.
 * Persisted across restarts — survives process kills.
 */
class SettingsRepository(private val context: Context) {
    private val dataStore = context.dataStore

    val settings: Flow<ServerSettings> = dataStore.data
        .map { prefs ->
            ServerSettings(
                host = prefs[SettingsKeys.HOST] ?: "",
                port = prefs[SettingsKeys.PORT] ?: 43862,
                username = prefs[SettingsKeys.USERNAME] ?: "",
                password = prefs[SettingsKeys.PASSWORD] ?: ""
            )
        }

    suspend fun saveSettings(newSettings: ServerSettings) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.HOST] = newSettings.host
            prefs[SettingsKeys.PORT] = newSettings.port
            prefs[SettingsKeys.USERNAME] = newSettings.username
            prefs[SettingsKeys.PASSWORD] = newSettings.password
        }
    }
}
