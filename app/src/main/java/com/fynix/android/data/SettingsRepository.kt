package com.fynix.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class ServerSettings(
    val host: String = "",
    val port: Int = 43862,
    val username: String = "",
    val password: String = "",
    val activeProfileId: String = "",
    val playerName: String = ""
)

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

class SettingsRepository(private val context: Context) {
    private object Keys {
        val HOST = stringPreferencesKey("host")
        val PORT = intPreferencesKey("port")
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val ACTIVE_PROFILE_ID = stringPreferencesKey("activeProfileId")
        val PLAYER_NAME = stringPreferencesKey("playerName")
    }

    val settings: Flow<ServerSettings> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs ->
            ServerSettings(
                host = prefs[Keys.HOST] ?: "",
                port = prefs[Keys.PORT] ?: 43862,
                username = prefs[Keys.USERNAME] ?: "",
                password = prefs[Keys.PASSWORD] ?: "",
                activeProfileId = prefs[Keys.ACTIVE_PROFILE_ID] ?: "",
                playerName = prefs[Keys.PLAYER_NAME] ?: ""
            )
        }

    suspend fun saveSettings(settings: ServerSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HOST] = settings.host
            prefs[Keys.PORT] = settings.port
            prefs[Keys.USERNAME] = settings.username
            prefs[Keys.PASSWORD] = settings.password
            prefs[Keys.ACTIVE_PROFILE_ID] = settings.activeProfileId
            prefs[Keys.PLAYER_NAME] = settings.playerName
        }
    }
}