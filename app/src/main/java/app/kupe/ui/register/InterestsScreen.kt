package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.ui.components.HintText
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading
import app.kupe.ui.theme.Coral
import app.kupe.ui.theme.FieldSurface

private const val GRID_COLUMNS = 3
private val TileHeight = 116.dp
private val TileLabelHeight = 34.dp

@Composable
fun InterestsScreen(
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
                    InterestTile(
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

@Composable
private fun InterestTile(
    interest: TravelInterestUi,
    selected: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(TileHeight)
            .alpha(if (enabled) 1f else 0.45f)
            .clip(shape)
            .background(if (selected) Coral else FieldSurface)
            .then(if (selected) Modifier else Modifier.border(1.dp, Coral, shape))
            .toggleable(value = selected, enabled = enabled, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            interest.iconRes?.let { icon ->
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(TileLabelHeight), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(interest.nameRes),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
