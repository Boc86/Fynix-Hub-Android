package com.fynix.android.data.network

import com.fynix.android.data.model.ChannelsResponse
import com.fynix.android.data.model.HealthResponse
import com.fynix.android.data.model.ServerSettings
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Retrofit API interface for the Fynix Media Hub backend.
 *
 * All endpoints except /api/health require Basic auth via the Authorization header.
 */
interface NetworkApi {

    /** GET /api/health */
    @GET("api/health")
    suspend fun getHealth(
        @Header("Authorization") auth: String? = null
    ): HealthResponse

    /** GET /api/verify */
    @GET("api/verify")
    suspend fun getVerify(
        @Header("Authorization") auth: String? = null
    ): com.fynix.android.data.model.VerifyResponse

    /**
     * GET /api/channels?limit=100&offset=0
     */
    @GET("api/channels")
    suspend fun getChannels(
        @Header("Authorization") auth: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ChannelsResponse

    /**
     * GET /api/channels/search?q=BBC&limit=50
     */
    @GET("api/channels/search")
    suspend fun searchChannels(
        @Header("Authorization") auth: String? = null,
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): ChannelsResponse

    /**
     * Returns the stream URL for a channel.
     * The actual playlist is at GET /api/stream/{channelId}/p/
     */
    @GET("api/stream/{channelId}/p/")
    suspend fun getStreamPlaylist(
        @Header("Authorization") auth: String? = null,
        @Url url: String
    ): String

    /**
     * POST /api/stream/stop → { channelId }
     */
    @retrofit2.http.POST("api/stream/stop")
    suspend fun stopPlayback(
        @Header("Authorization") auth: String? = null,
        @retrofit2.http.Body request: StopRequest
    ): StopResponse

    /**
     * Utility: build the stream URL string for a given channel.
     * The app constructs this and passes it to getStreamPlaylist via @Url.
     */
    companion object {
        fun buildStreamUrl(settings: ServerSettings, channelId: String): String {
            return "${settings.baseUrl()}/api/stream/$channelId/p/"
        }
    }
}

/** Request body for POST /api/stream/stop */
data class StopRequest(
    val channelId: String
)

/** Response from POST /api/stream/stop */
data class StopResponse(
    val ok: Boolean
)
