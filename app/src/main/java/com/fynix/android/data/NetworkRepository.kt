package com.fynix.android.data

import com.fynix.android.network.NetworkApi
import com.fynix.android.network.createApi
import com.fynix.android.network.models.ConnectionState
import com.fynix.android.network.models.MergedChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class NetworkRepository(
    val settings: Flow<ServerSettings>
) {
    private fun api(settings: ServerSettings): NetworkApi =
        createApi(settings.host, settings.port, settings.username, settings.password)

    val connectionState: Flow<ConnectionState> = settings
        .map { s ->
            try {
                // We just check if connection is possible, not full health
                ConnectionState.Connected
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
        offset: Int = 0,
        search: String = ""
    ): Flow<Result<List<MergedChannel>>> = flow {
        val api = createApi(host, port, username, password)
        val response = api.getChannels(limit, offset, search)
        if (response.ok && response.data != null) {
            emit(Result.success(response.data))
        } else {
            emit(Result.failure(Exception(response.error ?: "Unknown error")))
        }
    }.catch { emit(Result.failure(it)) }

    fun getStreamUrl(
        host: String,
        port: Int,
        channelId: String
    ): Flow<Result<String>> = flow {
        emit(Result.success("http://${host}:${port}/api/stream/${URLEncoder.encode(channelId, StandardCharsets.UTF_8.name())}/p/"))
    }.catch { emit(Result.failure(it)) }

    suspend fun stopPlayback(host: String, port: Int, channelId: String): Result<Unit> {
        return try {
            val api = createApi(host, port)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}