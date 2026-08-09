package com.fynix.android.data.serializer

import com.fynix.android.data.model.ServerSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * JsonSerializer for ServerSettings.
 *
 * Uses kotlinx.serialization with JSON to persist settings to DataStore.
 * This is a simple wrapper around the default JSON serializer.
 */
object SettingsSerializer {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Serialize ServerSettings to a JSON string.
     */
    fun serialize(settings: ServerSettings): String {
        return json.encodeToString(ServerSettings.serializer(), settings)
    }

    /**
     * Deserialize a JSON string to ServerSettings.
     */
    fun deserialize(jsonString: String): ServerSettings {
        return json.decodeFromString(ServerSettings.serializer(), jsonString)
    }

    /**
     * Convert ServerSettings to a Preferences map for DataStore.
     */
    fun toPreferences(settings: ServerSettings): Map<String, String> {
        return mapOf(
            "host" to settings.host,
            "port" to settings.port.toString(),
            "username" to settings.username,
            "password" to settings.password
        )
    }

    /**
     * Convert a Preferences map back to ServerSettings.
     */
    fun fromPreferences(map: Map<String, String>): ServerSettings {
        return ServerSettings(
            host = map["host"] ?: "192.168.1.100",
            port = map["port"]?.toIntOrNull() ?: 8080,
            username = map["username"] ?: "",
            password = map["password"] ?: ""
        )
    }
}
