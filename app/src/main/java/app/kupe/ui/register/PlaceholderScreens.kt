package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.ui.components.FormError
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading
import app.kupe.ui.theme.Coral

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
                nextLabel = stringResource(R.string.action_finish),
                loading = state.isLoading,
                showChangeLaterHint = false,
            )
        },
    ) {
        StepHeading(stringResource(R.string.photo_title), stringResource(R.string.photo_subtitle))
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            ) {
            Image(
                painter = painterResource(R.drawable.work_in_progress),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.8f),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.work_in_progress),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Coral,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.photo_future_update),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        }
    }
}
