package app.kupe.ui.register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kupe.R
import app.kupe.core.network.ApiException
import app.kupe.data.google.GoogleSignInResult
import app.kupe.data.remote.Country
import app.kupe.data.remote.ErrorCodes
import app.kupe.data.remote.Language
import app.kupe.data.repository.AuthRepository
import app.kupe.data.repository.ExternalOutcome
import app.kupe.di.AppContainer
import app.kupe.ui.common.toMessageRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val MAX_LANGUAGES = 20
private const val GOOGLE_PROVIDER = "google"

data class RegistrationState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val country: Country? = null,
    val languages: List<Language> = emptyList(),
    val isExternal: Boolean = false,
    val isLoading: Boolean = false,
    val showCredentialErrors: Boolean = false,
    val showUsernameErrors: Boolean = false,
    val takenUsername: String? = null,
    @StringRes val errorRes: Int? = null,
) {
    override fun toString() = "RegistrationState(step data hidden)"
}

sealed interface RegistrationEvent {
    data object CredentialsAccepted : RegistrationEvent
    data object Authenticated : RegistrationEvent
    data object RestartRequired : RegistrationEvent
    data object UsernameRejected : RegistrationEvent
}

class RegistrationViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    private val _events = Channel<RegistrationEvent>(Channel.BUFFERED)
    val events: Flow<RegistrationEvent> = _events.receiveAsFlow()

    private var registrationToken: String? = null

    fun onEmailChange(value: String) = _state.update { it.copy(email = value.take(254), errorRes = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value.take(128), errorRes = null) }
    fun onUsernameChange(value: String) = _state.update { it.copy(username = value.take(32), errorRes = null) }

    fun selectCountry(country: Country) = _state.update { it.copy(country = country) }
    fun clearCountry() = _state.update { it.copy(country = null) }

    fun addLanguage(language: Language) = _state.update {
        if (it.languages.size >= MAX_LANGUAGES || it.languages.any { l -> l.id == language.id }) it
        else it.copy(languages = it.languages + language)
    }
    fun removeLanguage(language: Language) = _state.update { it.copy(languages = it.languages.filterNot { l -> l.id == language.id }) }
    fun clearLanguages() = _state.update { it.copy(languages = emptyList()) }

    fun submitCredentials() {
        val current = _state.value
        if (current.isLoading) return
        _state.update { it.copy(showCredentialErrors = true) }
        if (Validators.email(current.email) != null || Validators.password(current.password) != null) return

        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            auth.startRegistration(current.email, current.password).fold(
                onSuccess = { response ->
                    registrationToken = response.registrationToken
                    _state.update { it.copy(isLoading = false, email = response.email, password = "", showCredentialErrors = false) }
                    _events.send(RegistrationEvent.CredentialsAccepted)
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, errorRes = e.toMessageRes()) } },
            )
        }
    }

    fun onGoogleResult(result: GoogleSignInResult) {
        when (result) {
            is GoogleSignInResult.Success -> signInWithGoogle(result.idToken)
            GoogleSignInResult.Cancelled -> Unit
            GoogleSignInResult.NotConfigured -> _state.update { it.copy(errorRes = R.string.error_google_not_configured) }
            GoogleSignInResult.Failure -> _state.update { it.copy(errorRes = R.string.error_google_failed) }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            auth.externalLogin(GOOGLE_PROVIDER, idToken).fold(
                onSuccess = { outcome ->
                    when (outcome) {
                        ExternalOutcome.LoggedIn -> {
                            _state.update { RegistrationState() }
                            _events.send(RegistrationEvent.Authenticated)
                        }
                        is ExternalOutcome.RegistrationRequired -> {
                            registrationToken = outcome.registrationToken
                            _state.update { it.copy(isLoading = false, isExternal = true, email = outcome.email) }
                            _events.send(RegistrationEvent.CredentialsAccepted)
                        }
                    }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, errorRes = e.toMessageRes()) } },
            )
        }
    }

    fun validateUsername(): Boolean {
        _state.update { it.copy(showUsernameErrors = true) }
        return Validators.username(_state.value.username.trim()) == null
    }

    fun discardPendingRegistration() {
        registrationToken = null
        _state.update {
            it.copy(password = "", isExternal = false, email = if (it.isExternal) "" else it.email, errorRes = null)
        }
    }

    fun finish() {
        val current = _state.value
        if (current.isLoading) return
        val token = registrationToken
        if (token == null) {
            _state.update { it.copy(errorRes = R.string.error_registration_expired) }
            viewModelScope.launch { _events.send(RegistrationEvent.RestartRequired) }
            return
        }
        val username = current.username.trim()
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            auth.completeRegistration(token, username, current.country?.id, current.languages.map { it.id }).fold(
                onSuccess = {
                    registrationToken = null
                    _state.value = RegistrationState()
                    _events.send(RegistrationEvent.Authenticated)
                },
                onFailure = { e -> handleCompleteFailure(e, username) },
            )
        }
    }

    private suspend fun handleCompleteFailure(error: Throwable, username: String) {
        val code = (error as? ApiException)?.errorCode
        when (code) {
            ErrorCodes.USERNAME_ALREADY_EXISTS -> {
                _state.update { it.copy(isLoading = false, takenUsername = username, showUsernameErrors = true) }
                _events.send(RegistrationEvent.UsernameRejected)
            }
            ErrorCodes.INVALID_REGISTRATION_TOKEN,
            ErrorCodes.EMAIL_ALREADY_EXISTS,
            ErrorCodes.EXTERNAL_EMAIL_CONFLICT,
            ErrorCodes.EXTERNAL_IDENTITY_ALREADY_LINKED -> {
                discardPendingRegistration()
                _state.update { it.copy(isLoading = false, errorRes = error.toMessageRes()) }
                _events.send(RegistrationEvent.RestartRequired)
            }
            else -> _state.update { it.copy(isLoading = false, errorRes = error.toMessageRes()) }
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { RegistrationViewModel(container.authRepository) }
        }
    }
}
