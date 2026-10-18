package app.kupe.core.security

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.kupe.core.network.ApiJson
import app.kupe.data.remote.TokenResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val accessToken: String,
    val refreshToken: String,
    val userId: String?,
) {
    override fun toString() = "Session(userId=$userId)"
}

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "kupe_session")

class SecureTokenStore(context: Context) {

    private val dataStore = context.applicationContext.sessionDataStore
    private val cipher = KeystoreCipher()
    private val key = stringPreferencesKey("session")

    val session: Flow<Session?> = dataStore.data
        .map { prefs -> prefs[key]?.let(::decode) }
        .distinctUntilChanged()

    suspend fun save(tokens: TokenResponse): Session {
        val session = Session(tokens.accessToken, tokens.refreshToken, JwtUtils.userId(tokens.accessToken))
        dataStore.edit { it[key] = cipher.encrypt(ApiJson.encodeToString(session)) }
        return session
    }

    suspend fun clear() {
        dataStore.edit { it.remove(key) }
    }

    fun currentSession(): Session? = runBlocking { session.first() }
    fun saveBlocking(tokens: TokenResponse): Session = runBlocking { save(tokens) }
    fun clearBlocking() = runBlocking { clear() }

    private fun decode(blob: String): Session? =
        cipher.decrypt(blob)?.let { runCatching { ApiJson.decodeFromString<Session>(it) }.getOrNull() }
}
