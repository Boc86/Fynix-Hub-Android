package com.fynix.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MergedChannel(
    val id: String,
    val name: String,
    val logo: String = "",
    @SerialName("logoImage")
    val logoImage: String = "",
    @SerialName("countryCode")
    val countryCode: String = "",
    val sources: List<String> = emptyList()
)

@Serializable
data class ChannelsResponse(
    val ok: Boolean,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val channels: List<MergedChannel>
)

@Serializable
data class HealthResponse(
    val ok: Boolean,
    val app: String,
    val version: String,
    val apiVersion: Int
)

@Serializable
data class ServerSettings(
    val host: String = "",
    val port: Int = 43862,
    val username: String = "",
    val password: String = ""
)

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(val version: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
