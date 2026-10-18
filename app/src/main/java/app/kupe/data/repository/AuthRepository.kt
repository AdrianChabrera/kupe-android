package app.kupe.data.repository

import app.kupe.core.network.ApiException
import app.kupe.core.network.safeCall
import app.kupe.core.security.SecureTokenStore
import app.kupe.data.remote.AuthApi
import app.kupe.data.remote.CompleteRegistrationRequest
import app.kupe.data.remote.ExternalAuthStatus
import app.kupe.data.remote.ExternalLoginRequest
import app.kupe.data.remote.LoginRequest
import app.kupe.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface ExternalOutcome {
    data object LoggedIn : ExternalOutcome
    data class RegistrationRequired(val registrationToken: String, val email: String) : ExternalOutcome
}

class AuthRepository(
    private val api: AuthApi,
    private val store: SecureTokenStore,
) {
    val hasSession: Flow<Boolean> = store.session.map { it != null }

    suspend fun startRegistration(email: String, password: String) =
        safeCall { api.startRegistration(RegisterRequest(email.trim(), password)) }

    suspend fun completeRegistration(
        registrationToken: String,
        username: String,
        countryId: String?,
        languageIds: List<String>,
    ): Result<Unit> = safeCall {
        val tokens = api.completeRegistration(
            CompleteRegistrationRequest(registrationToken, username, countryId, languageIds.ifEmpty { null })
        )
        store.save(tokens)
        Unit
    }

    suspend fun login(email: String, password: String): Result<Unit> = safeCall {
        store.save(api.login(LoginRequest(email.trim(), password)))
        Unit
    }

    suspend fun externalLogin(provider: String, idToken: String): Result<ExternalOutcome> = safeCall {
        val response = api.externalLogin(provider, ExternalLoginRequest(idToken))
        when (response.status) {
            ExternalAuthStatus.Authenticated -> {
                store.save(response.tokens ?: throw ApiException(ApiException.Kind.UNKNOWN))
                ExternalOutcome.LoggedIn
            }
            ExternalAuthStatus.RegistrationRequired -> {
                val registration = response.registration ?: throw ApiException(ApiException.Kind.UNKNOWN)
                ExternalOutcome.RegistrationRequired(registration.registrationToken, registration.email)
            }
        }
    }

    suspend fun logout() = store.clear()
}
