package com.fynix.android.network.models

// Gson-based models (no kotlinx.serialization annotations needed)
data class MergedChannel(
    val id: String,
    val name: String,
    val logo: String = "",
    val logoImage: String = "",
    val countryCode: String = "",
    val sources: List<String> = emptyList()
)

data class ChannelsResponse(
    val ok: Boolean,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val channels: List<MergedChannel>
)

data class HealthResponse(
    val ok: Boolean,
    val app: String,
    val version: String,
    val apiVersion: Int
)

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
