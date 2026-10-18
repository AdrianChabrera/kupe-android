package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.ui.components.FormError
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.PlaceholderBox
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading

@Composable
fun TravelStyleScreen(onBack: () -> Unit, onSkip: () -> Unit, onNext: () -> Unit) {
    BackHandler(onBack = onBack)
    RegistrationScaffold(
        step = 3,
        footer = { NavigationFooter(onBack = onBack, onNext = onNext, onSkip = onSkip) },
    ) {
        StepHeading(stringResource(R.string.travel_style_title), stringResource(R.string.travel_style_subtitle))
        Spacer(Modifier.height(24.dp))
        PlaceholderBox(Modifier.fillMaxWidth().weight(1f))
    }
}

@Composable
fun InterestsScreen(onBack: () -> Unit, onSkip: () -> Unit, onNext: () -> Unit) {
    BackHandler(onBack = onBack)
    RegistrationScaffold(
        step = 4,
        footer = { NavigationFooter(onBack = onBack, onNext = onNext, onSkip = onSkip) },
    ) {
        StepHeading(stringResource(R.string.interests_title), stringResource(R.string.interests_subtitle))
        Spacer(Modifier.height(24.dp))
        PlaceholderBox(Modifier.fillMaxWidth().weight(1f))
    }
}

@Composable
fun ProfilePhotoScreen(
    viewModel: RegistrationViewModel,
    onBack: () -> Unit,
    onRestart: () -> Unit,
    onUsernameRejected: () -> Unit,
    onCompleted: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = !state.isLoading, onBack = onBack)
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RegistrationEvent.RestartRequired -> onRestart()
                RegistrationEvent.UsernameRejected -> onUsernameRejected()
                RegistrationEvent.Authenticated -> onCompleted()
                RegistrationEvent.CredentialsAccepted -> Unit
            }
        }
    }

    RegistrationScaffold(
        step = 7,
        footer = {
            FormError(state.errorRes)
            NavigationFooter(
                onBack = onBack,
                onNext = viewModel::finish,
                onSkip = viewModel::finish,
                skipLabel = stringResource(R.string.action_upload_later),
                nextLabel = stringResource(R.string.action_finish),
                loading = state.isLoading,
            )
        },
    ) {
        StepHeading(stringResource(R.string.photo_title), stringResource(R.string.photo_subtitle))
        Spacer(Modifier.height(24.dp))
        Column(Modifier.weight(1f).fillMaxWidth()) {
            PlaceholderBox(Modifier.fillMaxWidth().weight(1f))
        }
    }
}
