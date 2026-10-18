package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.data.remote.Flags
import app.kupe.ui.components.HintText
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading

@Composable
fun LanguagesScreen(
    viewModel: RegistrationViewModel,
    searchViewModel: LanguageSearchViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val search by searchViewModel.state.collectAsStateWithLifecycle()
    BackHandler(onBack = onBack)

    val limitReached = state.languages.size >= MAX_LANGUAGES
    val available = search.results.filter { r -> state.languages.none { it.id == r.id } }

    RegistrationScaffold(
        step = 6,
        footer = {
            HintText(stringResource(R.string.languages_info))
            NavigationFooter(
                onBack = onBack,
                onNext = onNext,
                onSkip = { viewModel.clearLanguages(); onNext() },
                nextEnabled = state.languages.isNotEmpty(),
            )
        },
    ) {
        StepHeading(stringResource(R.string.languages_title), stringResource(R.string.languages_subtitle))
        Spacer(Modifier.height(16.dp))
        if (state.languages.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 180.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                state.languages.forEach { language ->
                    SelectedItemRow(
                        label = language.displayName,
                        flagUrl = Flags.forLanguage(language.code),
                        removeIcon = DeleteIcon,
                        onRemove = { viewModel.removeLanguage(language) },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        if (limitReached) {
            HintText(stringResource(R.string.languages_limit, MAX_LANGUAGES))
        } else {
            SearchPicker(
                state = search,
                results = available,
                onQueryChange = searchViewModel::onQueryChange,
                onSelect = { language ->
                    viewModel.addLanguage(language)
                    searchViewModel.onQueryChange("")
                },
                itemKey = { it.id },
                itemLabel = { it.displayName },
                itemFlag = { Flags.forLanguage(it.code) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }
    }
}
