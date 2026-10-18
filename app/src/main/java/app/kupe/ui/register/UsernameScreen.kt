@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.ui.components.AppLogoPlaceholder
import app.kupe.ui.components.HintText
import app.kupe.ui.components.KupeTextField
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold

@Composable
fun UsernameScreen(viewModel: RegistrationViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(onBack = onBack)

    val trimmed = state.username.trim()
    val errorRes = when {
        state.takenUsername != null && state.takenUsername == trimmed -> R.string.error_username_taken
        state.showUsernameErrors -> Validators.username(trimmed)
        else -> null
    }
    val next = { if (viewModel.validateUsername()) onNext() }

    RegistrationScaffold(
        step = 2,
        footer = {
            NavigationFooter(onBack = onBack, onNext = next, showChangeLaterHint = false)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppLogoPlaceholder()
            Text(stringResource(R.string.username_prompt), style = MaterialTheme.typography.bodyMedium)
            KupeTextField(
                label = stringResource(R.string.label_username),
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                fieldModifier = Modifier.semantics { contentType = ContentType.NewUsername },
                error = errorRes?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { next() }),
            )
            HintText(stringResource(R.string.username_hint))
        }
    }
}
