package app.kupe.ui.register.travel_style_step

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading
import app.kupe.ui.register.RegistrationViewModel
import app.kupe.ui.register.travel_style_step.components.TravelStyleCarousel
import app.kupe.ui.theme.Coral

@Composable
fun TravelStyleScreen(
    viewModel: RegistrationViewModel,
    stylesViewModel: TravelStyleViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stylesState by stylesViewModel.state.collectAsStateWithLifecycle()
    BackHandler(onBack = onBack)

    RegistrationScaffold(
        step = 3,
        footer = {
            NavigationFooter(
                onBack = onBack,
                onNext = onNext,
            )
        },
    ) {
        StepHeading(stringResource(R.string.travel_style_title), stringResource(R.string.travel_style_subtitle))
        Spacer(Modifier.height(24.dp))
        when {
            stylesState.loading -> CircularProgressIndicator(color = Coral, modifier = Modifier.padding(16.dp))
            stylesState.error -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.search_error), style = MaterialTheme.typography.bodyMedium)
                KupeButton(
                    stringResource(R.string.action_retry),
                    stylesViewModel::load,
                    Modifier.fillMaxWidth(),
                    KupeButtonStyle.Neutral,
                )
            }
            stylesState.styles.isNotEmpty() -> TravelStyleCarousel(
                styles = stylesState.styles,
                selectedCode = state.travelStyleCode,
                onStyleShown = { viewModel.selectTravelStyle(it.code) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}