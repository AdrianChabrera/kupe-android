package app.kupe.core.network

import app.kupe.core.security.Session
import app.kupe.core.security.SecureTokenStore
import app.kupe.data.remote.RefreshTokenRequest
import app.kupe.data.remote.TokenResponse
import kotlinx.serialization.SerializationException
import okhttp3.Authenticator
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class TokenAuthenticator(
    private val store: SecureTokenStore,
    private val bareClient: OkHttpClient,
    private val baseUrl: HttpUrl,
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request
        val sentToken = request.header("Authorization")?.removePrefix("Bearer ")?.trim() ?: return null
        if (request.url.host != baseUrl.host || attempts(response) >= 2) return null

        synchronized(lock) {
            val session = store.currentSession() ?: return null
            if (session.accessToken != sentToken) return request.withToken(session.accessToken)
            val renewed = refresh(session) ?: return null
            return request.withToken(renewed.accessToken)
        }
    }

    private fun refresh(session: Session): Session? {
        val userId = session.userId ?: run { store.clearBlocking(); return null }
        val payload = ApiJson.encodeToString(RefreshTokenRequest(userId, session.refreshToken))
        val request = Request.Builder()
            .url(baseUrl.resolve("api/auth/refresh-token") ?: return null)
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()
        return try {
            bareClient.newCall(request).execute().use { resp ->
                when {
                    resp.isSuccessful -> {
                        val raw = resp.body?.string() ?: return@use null
                        store.saveBlocking(ApiJson.decodeFromString<TokenResponse>(raw))
                    }
                    resp.code == 400 || resp.code == 401 -> { store.clearBlocking(); null }
                    else -> null
                }
            }
        } catch (e: IOException) {
            null
        } catch (e: SerializationException) {
            null
        }
    }

    private fun attempts(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) { count++; prior = prior.priorResponse }
        return count
    }

    private fun Request.withToken(token: String): Request =
        newBuilder().header("Authorization", "Bearer $token").build()
}
