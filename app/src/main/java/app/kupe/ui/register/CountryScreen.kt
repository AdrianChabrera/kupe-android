package app.kupe.ui.register

import androidx.activity.compose.BackHandler
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
import app.kupe.core.locale.currentLocale
import app.kupe.data.remote.Flags
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading

@Composable
fun CountryScreen(
    viewModel: RegistrationViewModel,
    searchViewModel: CountrySearchViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val search by searchViewModel.state.collectAsStateWithLifecycle()
    val locale = currentLocale()
    LaunchedEffect(locale) { searchViewModel.onLocaleChange(locale) }
    BackHandler(onBack = onBack)

    RegistrationScaffold(
        step = 5,
        footer = {
            NavigationFooter(
                onBack = onBack,
                onNext = onNext,
                onSkip = { viewModel.clearCountry(); onNext() },
                nextEnabled = state.country != null,
            )
        },
    ) {
        StepHeading(stringResource(R.string.country_title), stringResource(R.string.country_subtitle))
        Spacer(Modifier.height(24.dp))
        state.country?.let { country ->
            SelectedItemRow(
                label = country.displayName(locale),
                flagUrl = Flags.forCountry(country.iso2),
                removeIcon = RemoveIcon,
                onRemove = viewModel::clearCountry,
            )
            Spacer(Modifier.height(12.dp))
        }
        SearchPicker(
            state = search,
            results = search.results,
            onQueryChange = searchViewModel::onQueryChange,
            onSelect = { country ->
                viewModel.selectCountry(country)
                searchViewModel.onQueryChange("")
            },
            itemKey = { it.id },
            itemLabel = { it.displayName(locale) },
            itemFlag = { Flags.forCountry(it.iso2) },
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
    }
}