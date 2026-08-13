package com.fynix.android.network

import com.fynix.android.network.models.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.*

data class ServerResponse<T>(val ok: Boolean, val data: T? = null, val error: String? = null)

class NetworkApi(private val baseUrl: String, private val auth: String? = null) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .also { if (auth != null) it.header("Authorization", "Basic $auth") }
                .build()
            chain.proceed(request)
        }
        .build()

    suspend fun getHealth(): ServerResponse<HealthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/health")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext ServerResponse(false, null, "Empty response")
            val json = JSONObject(body)
            if (!json.optBoolean("ok")) return@withContext ServerResponse(false, null, json.optString("error"))
            val data = HealthResponse.fromJson(json)
            ServerResponse(true, data)
        } catch (e: Exception) {
            ServerResponse(false, null, e.message ?: "Health check failed")
        }
    }

    suspend fun getProfiles(): ServerResponse<ProfilesResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/profiles")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext ServerResponse(false, null, "Empty response")
            val json = JSONObject(body)
            if (!json.optBoolean("ok")) return@withContext ServerResponse(false, null, json.optString("error"))
            val data = ProfilesResponse.fromJson(json)
            ServerResponse(true, data)
        } catch (e: Exception) {
            ServerResponse(false, null, e.message ?: "Failed to fetch profiles")
        }
    }

    suspend fun selectProfile(profileId: String): ServerResponse<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().put("profileId", profileId)
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$baseUrl/api/profiles/select")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext ServerResponse(false, null, "HTTP ${response.code}")
            ServerResponse(true, Unit)
        } catch (e: Exception) {
            ServerResponse(false, null, e.message ?: "Failed to select profile")
        }
    }

    suspend fun getChannels(limit: Int = 100, offset: Int = 0, search: String = ""): ServerResponse<List<MergedChannel>> = withContext(Dispatchers.IO) {
        try {
            val url = if (search.isNotBlank()) {
                "$baseUrl/api/channels/search?q=${java.net.URLEncoder.encode(search, "UTF-8")}&limit=$limit"
            } else {
                "$baseUrl/api/channels?limit=$limit&offset=$offset"
            }
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext ServerResponse(false, emptyList(), "Empty response")
            val json = JSONObject(body)
            if (!json.optBoolean("ok")) return@withContext ServerResponse(false, emptyList(), json.optString("error"))
            val rawChannels = json.optJSONArray("channels")
            val channels = mutableListOf<MergedChannel>()
            if (rawChannels != null) {
                for (i in 0 until rawChannels.length()) {
                    val ch = rawChannels.getJSONObject(i)
                    channels.add(MergedChannel.fromJson(ch))
                }
            }
            ServerResponse(true, channels)
        } catch (e: Exception) {
            ServerResponse(false, emptyList(), e.message ?: "Failed to fetch channels")
        }
    }

    suspend fun getStreamUrl(channelId: String): ServerResponse<StreamResponse> = withContext(Dispatchers.IO) {
        try {
            val encoded = java.net.URLEncoder.encode(channelId, "UTF-8")
            val request = Request.Builder()
                .url("$baseUrl/api/stream/$encoded/p/")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext ServerResponse(false, null, "Empty response")
            val json = JSONObject(body)
            if (!json.optBoolean("ok")) return@withContext ServerResponse(false, null, json.optString("error"))
            val data = StreamResponse.fromJson(json)
            ServerResponse(true, data)
        } catch (e: Exception) {
            ServerResponse(false, null, e.message ?: "Failed to get stream")
        }
    }
}

fun createApi(host: String, port: Int, username: String = "", password: String = ""): NetworkApi {
    val baseUrl = "http://$host:$port"
    val auth = if (username.isNotEmpty()) {
        val credentials = "$username:$password"
        android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.NO_WRAP)
    } else null
    return NetworkApi(baseUrl, auth)
}