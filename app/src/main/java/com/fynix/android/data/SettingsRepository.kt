package com.fynix.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.dataStore
import androidx.datastore.preferences.edit
import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val HOST_KEY = stringPreferencesKey("host")
private val PORT_KEY = intPreferencesKey("port")
private val USERNAME_KEY = stringPreferencesKey("username")
private val PASSWORD_KEY = stringPreferencesKey("password")

class SettingsRepository(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore("server_settings")

    val settings: Flow<ServerSettings> = dataStore.data
        .map { prefs ->
            ServerSettings(
                host = prefs[HOST_KEY] ?: "",
                port = prefs[PORT_KEY] ?: 43862,
                username = prefs[USERNAME_KEY] ?: "",
                password = prefs[PASSWORD_KEY] ?: ""
            )
        }

    suspend fun saveSettings(settings: ServerSettings) {
        dataStore.edit { prefs ->
            prefs[HOST_KEY] = settings.host
            prefs[PORT_KEY] = settings.port
            prefs[USERNAME_KEY] = settings.username
            prefs[PASSWORD_KEY] = settings.password
        }
    }
}
