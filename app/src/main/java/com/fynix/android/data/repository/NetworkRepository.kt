package com.fynix.android.data.repository

import com.fynix.android.data.model.Channel
import com.fynix.android.data.model.ChannelsResponse
import com.fynix.android.data.model.HealthResponse
import com.fynix.android.data.model.ServerSettings
import com.fynix.android.data.network.NetworkApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository that provides reactive data streams from the Fynix Media Hub API.
 *
 * All methods return Flows so the UI can observe changes reactively.
 */
class NetworkRepository(
    private val api: NetworkApi,
    private val settings: ServerSettings
) {

    /**
     * Health check flow. Replays the last value to new collectors.
     */
    fun getHealth(): Flow<HealthResponse> = flow {
        emit(api.getHealth(authHeader()))
    }

    /**
     * Fetch all channels.
     */
    fun getChannels(limit: Int = 100, offset: Int = 0): Flow<ChannelsResponse> = flow {
        emit(api.getChannels(authHeader(), limit, offset))
    }

    /**
     * Search channels by query.
     */
    fun searchChannels(query: String, limit: Int = 50): Flow<ChannelsResponse> = flow {
        emit(api.searchChannels(authHeader(), query, limit))
    }

    /**
     * Get the stream playlist URL text for a channel.
     */
    fun getStreamUrl(channelId: String): Flow<String> = flow {
        val url = NetworkApi.buildStreamUrl(settings, channelId)
        emit(api.getStreamPlaylist(authHeader(), url))
    }

    /**
     * Stop playback for a channel.
     */
    fun stopPlayback(channelId: String): Flow<Unit> = flow {
        api.stopPlayback(authHeader(), com.fynix.android.data.network.StopRequest(channelId))
        emit(Unit)
    }

    private fun authHeader(): String? {
        return if (settings.username.isNotEmpty()) {
            com.squareup.okhttp3.Credentials.basic(settings.username, settings.password)
        } else null
    }
}
