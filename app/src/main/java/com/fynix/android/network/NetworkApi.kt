package com.fynix.android.network

import com.fynix.android.network.models.ChannelsResponse
import com.fynix.android.network.models.HealthResponse
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {
    @GET("api/health")
    suspend fun getHealth(): HealthResponse

    @GET("api/channels")
    suspend fun getChannels(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ChannelsResponse

    @GET("api/channels/search")
    suspend fun searchChannels(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): ChannelsResponse
}

class AuthInterceptor(
    private val username: String,
    private val password: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = if (username.isNotEmpty()) {
            chain.request().newBuilder()
                .header("Authorization", Credentials.basic(username, password))
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

fun createApi(
    host: String,
    port: Int,
    username: String = "",
    password: String = ""
): NetworkApi {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(username, password))
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Access-Control-Allow-Origin", "*")
                .build()
            chain.proceed(request)
        }
        .build()

    val baseUrl = "http://${host}:${port}/"
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NetworkApi::class.java)
}
