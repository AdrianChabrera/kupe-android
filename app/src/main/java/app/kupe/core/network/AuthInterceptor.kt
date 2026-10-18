package app.kupe.core.network

import app.kupe.core.security.SecureTokenStore
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val store: SecureTokenStore,
    private val apiHost: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.host != apiHost || request.header("Authorization") != null || isPublic(request.url.encodedPath)) {
            return chain.proceed(request)
        }
        val token = store.currentSession()?.accessToken ?: return chain.proceed(request)
        return chain.proceed(request.newBuilder().header("Authorization", "Bearer $token").build())
    }

    private fun isPublic(path: String): Boolean =
        path.startsWith("/api/countries") ||
                path == "/api/auth/login" ||
                path == "/api/auth/refresh-token" ||
                path.startsWith("/api/auth/register/") ||
                (path.startsWith("/api/auth/external/") && !path.endsWith("/link"))
}

class HostAllowlistInterceptor(private val hosts: Set<String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url
        if (!url.isHttps || url.host !in hosts) throw IOException("Blocked request to ${url.host}")
        return chain.proceed(chain.request())
    }
}
