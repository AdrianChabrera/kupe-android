package app.kupe.data.remote

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class RegisterRequest(val email: String, val password: String) {
    override fun toString() = "RegisterRequest(email=$email)"
}

@Serializable
data class LoginRequest(val email: String, val password: String) {
    override fun toString() = "LoginRequest(email=$email)"
}

@Serializable
data class CompleteRegistrationRequest(
    val registrationToken: String,
    val username: String,
    val countryId: String? = null,
    val languageIds: List<String>? = null,
) {
    override fun toString() = "CompleteRegistrationRequest(username=$username)"
}

@Serializable
data class ExternalLoginRequest(val token: String) {
    override fun toString() = "ExternalLoginRequest"
}

@Serializable
data class RefreshTokenRequest(val userId: String, val refreshToken: String) {
    override fun toString() = "RefreshTokenRequest(userId=$userId)"
}

@Serializable
data class TokenResponse(val accessToken: String, val refreshToken: String) {
    override fun toString() = "TokenResponse"
}

@Serializable
data class RegistrationTokenResponse(
    val registrationToken: String,
    val expiresAt: String,
    val email: String,
    val provider: String? = null,
) {
    override fun toString() = "RegistrationTokenResponse(email=$email)"
}

@Serializable
enum class ExternalAuthStatus { Authenticated, RegistrationRequired }

@Serializable
data class ExternalAuthResponse(
    val status: ExternalAuthStatus,
    val tokens: TokenResponse? = null,
    val registration: RegistrationTokenResponse? = null,
)

@Serializable
data class Country(val id: String, val iso2: String, val name: String)

@Serializable
data class Language(val id: String, val code: String, val name: String? = null) {
    val displayName: String
        get() = name?.takeIf { it.isNotBlank() }
            ?: Locale.forLanguageTag(code).getDisplayLanguage(Locale.ENGLISH)
                .takeIf { it.isNotBlank() && !it.equals(code, ignoreCase = true) }
            ?: code
}

object ErrorCodes {
    const val COUNTRY_NOT_FOUND = "COUNTRY_NOT_FOUND"
    const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"
    const val EXTERNAL_EMAIL_CONFLICT = "EXTERNAL_EMAIL_CONFLICT"
    const val EXTERNAL_EMAIL_NOT_VERIFIED = "EXTERNAL_EMAIL_NOT_VERIFIED"
    const val EXTERNAL_IDENTITY_ALREADY_LINKED = "EXTERNAL_IDENTITY_ALREADY_LINKED"
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val INVALID_EXTERNAL_TOKEN = "INVALID_EXTERNAL_TOKEN"
    const val INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN"
    const val INVALID_REGISTRATION_TOKEN = "INVALID_REGISTRATION_TOKEN"
    const val LANGUAGE_NOT_FOUND = "LANGUAGE_NOT_FOUND"
    const val UNSUPPORTED_EXTERNAL_PROVIDER = "UNSUPPORTED_EXTERNAL_PROVIDER"
    const val USERNAME_ALREADY_EXISTS = "USERNAME_ALREADY_EXISTS"

    val ALL = setOf(
        COUNTRY_NOT_FOUND, EMAIL_ALREADY_EXISTS, EXTERNAL_EMAIL_CONFLICT, EXTERNAL_EMAIL_NOT_VERIFIED,
        EXTERNAL_IDENTITY_ALREADY_LINKED, INVALID_CREDENTIALS, INVALID_EXTERNAL_TOKEN, INVALID_REFRESH_TOKEN,
        INVALID_REGISTRATION_TOKEN, LANGUAGE_NOT_FOUND, UNSUPPORTED_EXTERNAL_PROVIDER, USERNAME_ALREADY_EXISTS,
    )
}
