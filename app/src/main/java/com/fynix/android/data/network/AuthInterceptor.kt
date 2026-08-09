package com.fynix.android.data.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Basic Authentication interceptor for Retrofit.
 *
 * Adds an Authorization header when credentials are present in the settings.
 */
class AuthInterceptor(
    private val username: String,
    private val password: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val shouldAuth = request.url.encodedPath.startsWith("/api/") &&
            request.url.encodedPath != "/api/health"

        return if (shouldAuth && username.isNotEmpty()) {
            val credential = Credentials.basic(username, password)
            chain.proceed(request.newBuilder()
                .header("Authorization", credential)
                .build())
        } else {
            chain.proceed(request)
        }
    }
}
