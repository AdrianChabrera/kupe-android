package app.kupe.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kupe.R
import app.kupe.data.google.GoogleSignInResult
import app.kupe.data.repository.AuthRepository
import app.kupe.data.repository.ExternalOutcome
import app.kupe.di.AppContainer
import app.kupe.ui.common.toMessageRes
import app.kupe.ui.register.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val GOOGLE_PROVIDER = "google"

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val showErrors: Boolean = false,
    val loggedIn: Boolean = false,
    @StringRes val errorRes: Int? = null,
) {
    override fun toString() = "LoginState(hidden)"
}

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value.take(254), errorRes = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value.take(128), errorRes = null) }

    fun login() {
        val current = _state.value
        if (current.isLoading) return
        _state.update { it.copy(showErrors = true) }
        if (Validators.email(current.email) != null || current.password.isEmpty()) return

        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            auth.login(current.email, current.password).fold(
                onSuccess = { _state.update { LoginState(loggedIn = true) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, password = "", errorRes = e.toMessageRes()) } },
            )
        }
    }

    fun onGoogleResult(result: GoogleSignInResult) {
        when (result) {
            is GoogleSignInResult.Success -> loginWithGoogle(result.idToken)
            GoogleSignInResult.Cancelled -> Unit
            GoogleSignInResult.NotConfigured -> _state.update { it.copy(errorRes = R.string.error_google_not_configured) }
            GoogleSignInResult.Failure -> _state.update { it.copy(errorRes = R.string.error_google_failed) }
        }
    }

    private fun loginWithGoogle(idToken: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            auth.externalLogin(GOOGLE_PROVIDER, idToken).fold(
                onSuccess = { outcome ->
                    when (outcome) {
                        ExternalOutcome.LoggedIn -> _state.update { LoginState(loggedIn = true) }
                        // Not registered yet: the registration token is discarded, the user must register first.
                        is ExternalOutcome.RegistrationRequired ->
                            _state.update { it.copy(isLoading = false, errorRes = R.string.error_google_no_account) }
                    }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, errorRes = e.toMessageRes()) } },
            )
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(container.authRepository) }
        }
    }
}