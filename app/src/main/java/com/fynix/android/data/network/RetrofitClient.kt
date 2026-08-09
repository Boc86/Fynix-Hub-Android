package com.fynix.android.data.network

import com.fynix.android.data.model.ServerSettings
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json

/**
 * Retrofit client factory for the Fynix Media Hub API.
 */
object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.100:8080/"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private var retrofit: Retrofit? = null
    private var authInterceptor: AuthInterceptor? = null

    /**
     * Get or create the Retrofit instance.
     *
     * @param settings Optional server settings for auth. If null, no auth header is added.
     */
    fun getApi(settings: ServerSettings? = null): NetworkApi {
        val currentSettings = settings ?: ServerSettings()

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(currentSettings.username, currentSettings.password))
            .build()

        return retrofit?.let {
            it.create(NetworkApi::class.java)
        } ?: run {
            val newRetrofit = Retrofit.Builder()
                .baseUrl(currentSettings.baseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit = newRetrofit
            newRetrofit.create(NetworkApi::class.java)
        }
    }

    /**
     * Reset the client (for testing or when settings change).
     */
    fun reset() {
        retrofit = null
        authInterceptor = null
    }
}
