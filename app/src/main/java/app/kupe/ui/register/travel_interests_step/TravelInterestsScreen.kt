package app.kupe.ui.register.travel_interests_step

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import app.kupe.ui.components.HintText
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading
import app.kupe.ui.register.MAX_TRAVEL_INTERESTS
import app.kupe.ui.register.RegistrationViewModel
import app.kupe.ui.register.TravelInterestViewModel
import app.kupe.ui.register.travel_interests_step.components.TravelInterestTile
import app.kupe.ui.theme.Coral

private const val GRID_COLUMNS = 3

@Composable
fun TravelInterestsScreen(
    viewModel: RegistrationViewModel,
    interestsViewModel: TravelInterestViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val interestsState by interestsViewModel.state.collectAsStateWithLifecycle()
    BackHandler(onBack = onBack)

    val limitReached = state.travelInterestCodes.size >= MAX_TRAVEL_INTERESTS

    RegistrationScaffold(
        step = 4,
        footer = {
            if (limitReached) HintText(stringResource(R.string.interests_limit, MAX_TRAVEL_INTERESTS))
            NavigationFooter(
                onBack = onBack,
                onNext = onNext,
            )
        },
    ) {
        StepHeading(stringResource(R.string.interests_title), stringResource(R.string.interests_subtitle))
        Spacer(Modifier.height(24.dp))
        when {
            interestsState.loading -> CircularProgressIndicator(color = Coral, modifier = Modifier.padding(16.dp))
            interestsState.error -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.search_error), style = MaterialTheme.typography.bodyMedium)
                KupeButton(
                    stringResource(R.string.action_retry),
                    interestsViewModel::load,
                    Modifier.fillMaxWidth(),
                    KupeButtonStyle.Neutral,
                )
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(GRID_COLUMNS),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(interestsState.interests, key = { it.code }) { interest ->
                    val selected = interest.code in state.travelInterestCodes
                    TravelInterestTile(
                        interest = interest,
                        selected = selected,
                        enabled = selected || !limitReached,
                        onToggle = { viewModel.toggleTravelInterest(interest.code) },
                    )
                }
            }
        }
    }
}
