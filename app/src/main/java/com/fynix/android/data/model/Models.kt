package com.fynix.android.data.model

import kotlinx.serialization.Serializable

/**
 * Response from GET /api/health
 */
@Serializable
data class HealthResponse(
    val ok: Boolean,
    val app: String,
    val version: String,
    val apiVersion: String
)

/**
 * Response from GET /api/verify
 */
@Serializable
data class VerifyResponse(
    val ok: Boolean,
    val user: String?
)

/**
 * A single channel with its sources
 */
@Serializable
data class Channel(
    val id: String,
    val name: String,
    val logo: String? = null,
    val logoImage: String? = null,
    val countryCode: String? = null,
    val sources: List<ChannelSource> = emptyList()
)

/**
 * A source for a channel
 */
@Serializable
data class ChannelSource(
    val url: String,
    val quality: String? = null,
    val language: String? = null
)

/**
 * Response from GET /api/channels and GET /api/channels/search
 */
@Serializable
data class ChannelsResponse(
    val ok: Boolean,
    val total: Int? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val channels: List<Channel> = emptyList()
)

/**
 * Server connection settings
 */
@Serializable
data class ServerSettings(
    val host: String = "192.168.1.100",
    val port: Int = 8080,
    val username: String = "",
    val password: String = ""
) {
    fun baseUrl(): String = "http://$host:$port"
}
