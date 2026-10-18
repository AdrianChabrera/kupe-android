@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package app.kupe.ui.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.data.google.GoogleIdTokenProvider
import app.kupe.ui.components.AppLogoPlaceholder
import app.kupe.ui.components.FormError
import app.kupe.ui.components.GoogleSignInButton
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.components.KupeTextField
import app.kupe.ui.register.Validators
import app.kupe.ui.theme.Coral
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    googleProvider: GoogleIdTokenProvider,
    onBack: () -> Unit,
    onLoggedIn: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler(onBack = onBack)
    LaunchedEffect(state.loggedIn) { if (state.loggedIn) onLoggedIn() }

    val emailError = if (state.showErrors) Validators.email(state.email)?.let { stringResource(it) } else null
    val passwordError = if (state.showErrors && state.password.isEmpty()) stringResource(R.string.error_password_required) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineLarge)
            AppLogoPlaceholder()
            Text(stringResource(R.string.login_prompt), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            KupeTextField(
                label = stringResource(R.string.label_email),
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                fieldModifier = Modifier.semantics { contentType = ContentType.EmailAddress },
                enabled = !state.isLoading,
                error = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next, autoCorrectEnabled = false),
            )
            KupeTextField(
                label = stringResource(R.string.label_password),
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                fieldModifier = Modifier.semantics { contentType = ContentType.Password },
                enabled = !state.isLoading,
                error = passwordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done, autoCorrectEnabled = false),
                keyboardActions = KeyboardActions(onDone = { viewModel.login() }),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(if (passwordVisible) R.string.hide_password else R.string.show_password),
                        )
                    }
                },
            )
            FormError(state.errorRes)
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HorizontalDivider(Modifier.weight(1f), color = Coral)
                Text(stringResource(R.string.or_divider), color = Coral, style = MaterialTheme.typography.labelMedium)
                HorizontalDivider(Modifier.weight(1f), color = Coral)
            }
            GoogleSignInButton(
                label = stringResource(R.string.log_in_with),
                enabled = !state.isLoading,
                onClick = { scope.launch { viewModel.onGoogleResult(googleProvider.requestIdToken(context)) } },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KupeButton(stringResource(R.string.action_go_back), onBack, Modifier.weight(1f), KupeButtonStyle.Neutral, enabled = !state.isLoading)
            KupeButton(stringResource(R.string.action_login), viewModel::login, Modifier.weight(1f), loading = state.isLoading)
        }
    }
}