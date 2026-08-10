package com.fynix.android.data

import com.fynix.android.network.NetworkApi
import com.fynix.android.network.createApi
import com.fynix.android.network.models.ConnectionState
import com.fynix.android.network.models.MergedChannel
import com.fynix.android.network.models.ServerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class NetworkRepository(
    val settings: Flow<ServerSettings>
) {
    private fun api(settings: ServerSettings): NetworkApi =
        createApi(settings.host, settings.port, settings.username, settings.password)

    val connectionState: Flow<ConnectionState> = settings
        .map { settings ->
            try {
                val health = api(settings).getHealth()
                if (health.ok) ConnectionState.Connected(health.version)
                else ConnectionState.Error("Invalid response")
            } catch (e: Exception) {
                ConnectionState.Error(e.message ?: "Connection failed")
            }
        }
        .catch { emit(ConnectionState.Error(it.message ?: "Unknown error")) }

    fun getChannels(
        host: String,
        port: Int,
        username: String = "",
        password: String = "",
        limit: Int = 100,
        offset: Int = 0
    ): Flow<Result<List<MergedChannel>>> = flow {
        val api = createApi(host, port, username, password)
        val response = api.getChannels(limit, offset)
        emit(Result.success(response.channels))
    }.catch { emit(Result.failure(it)) }

    fun searchChannels(
        host: String,
        port: Int,
        username: String = "",
        password: String = "",
        query: String,
        limit: Int = 50
    ): Flow<Result<List<MergedChannel>>> = flow {
        val api = createApi(host, port, username, password)
        val response = api.searchChannels(query, limit)
        emit(Result.success(response.channels))
    }.catch { emit(Result.failure(it)) }

    fun getStreamUrl(
        host: String,
        port: Int,
        channelId: String
    ): Flow<Result<String>> = flow {
        emit(Result.success("http://${host}:${port}/api/stream/${channelId}/p/"))
    }.catch { emit(Result.failure(it)) }

    suspend fun stopPlayback(host: String, port: Int, channelId: String): Result<Unit> {
        return try {
            val api = createApi(host, port)
            // POST to stop endpoint (not implemented in API yet, return success)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
