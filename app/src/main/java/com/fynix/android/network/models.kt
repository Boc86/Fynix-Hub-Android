package com.fynix.android.network.models

import org.json.JSONObject

data class HealthResponse(
    val ok: Boolean,
    val app: String,
    val version: String,
    val apiVersion: Int
) {
    companion object {
        fun fromJson(json: JSONObject): HealthResponse {
            return HealthResponse(
                ok = json.optBoolean("ok"),
                app = json.optString("app"),
                version = json.optString("version"),
                apiVersion = json.optInt("apiVersion", 1)
            )
        }
    }
}

data class ProfileInfo(
    val id: String,
    val name: String,
    val avatarColor: String,
    val isActive: Boolean = false
)

data class ProfilesResponse(
    val profiles: List<ProfileInfo>,
    val activeProfileId: String
) {
    companion object {
        fun fromJson(json: JSONObject): ProfilesResponse {
            val rawProfiles = json.optJSONArray("profiles")
            val profiles = mutableListOf<ProfileInfo>()
            if (rawProfiles != null) {
                for (i in 0 until rawProfiles.length()) {
                    val p = rawProfiles.getJSONObject(i)
                    profiles.add(ProfileInfo(
                        id = p.optString("id"),
                        name = p.optString("name"),
                        avatarColor = p.optString("avatarColor", "#E50914"),
                        isActive = p.optBoolean("isActive")
                    ))
                }
            }
            return ProfilesResponse(profiles, json.optString("activeProfileId", ""))
        }
    }
}

data class MergedChannel(
    val id: String,
    val name: String,
    val logo: String = "",
    val logoImage: String = "",
    val countryCode: String = "",
    val sources: List<String> = emptyList()
) {
    companion object {
        fun fromJson(json: JSONObject): MergedChannel {
            val rawSources = json.optJSONArray("sources")
            val sources = mutableListOf<String>()
            if (rawSources != null) {
                for (i in 0 until rawSources.length()) {
                    sources.add(rawSources.getString(i))
                }
            }
            return MergedChannel(
                id = json.optString("id"),
                name = json.optString("name"),
                logo = json.optString("logo", ""),
                logoImage = json.optString("logoImage", ""),
                countryCode = json.optString("countryCode", ""),
                sources = sources
            )
        }
    }
}

data class StreamResponse(
    val url: String,
    val title: String
) {
    companion object {
        fun fromJson(json: JSONObject): StreamResponse {
            return StreamResponse(
                url = json.optString("url", ""),
                title = json.optString("title", "")
            )
        }
    }
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}