package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.core.locale.currentLocale
import app.kupe.data.remote.Flags
import app.kupe.ui.components.HintText
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading
import app.kupe.ui.theme.Coral
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun LanguagesScreen(
    viewModel: RegistrationViewModel,
    searchViewModel: LanguageSearchViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val search by searchViewModel.state.collectAsStateWithLifecycle()
    val locale = currentLocale()
    LaunchedEffect(locale) { searchViewModel.onLocaleChange(locale) }
    BackHandler(onBack = onBack)

    val limitReached = state.languages.size >= MAX_LANGUAGES
    val available = search.results.filter { r -> state.languages.none { it.id == r.id } }

    RegistrationScaffold(
        step = 6,
        footer = {
            NavigationFooter(
                onBack = onBack,
                onNext = onNext,
            )
        },
    ) {
        StepHeading(stringResource(R.string.languages_title), stringResource(R.string.languages_subtitle))
        Spacer(Modifier.height(16.dp))
        if (state.languages.isNotEmpty()) {
            if (state.languages.size > 1) {
                HintText(stringResource(R.string.languages_order_hint))
                Spacer(Modifier.height(8.dp))
            }
            val listState = rememberLazyListState()
            val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
                viewModel.moveLanguage(from.index, to.index)
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().heightIn(max = 180.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(state.languages, key = { it.id }) { language ->
                    ReorderableItem(reorderableState, key = language.id) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "languageDragElevation")
                        val label = language.displayName(locale)
                        SelectedItemRow(
                            label = label,
                            flagUrl = Flags.forLanguage(language.code),
                            removeIcon = DeleteIcon,
                            onRemove = { viewModel.removeLanguage(language) },
                            modifier = Modifier.shadow(elevation, RoundedCornerShape(10.dp)),
                            dragHandle = {
                                IconButton(onClick = {}, modifier = Modifier.draggableHandle()) {
                                    Icon(
                                        Icons.Default.DragHandle,
                                        contentDescription = stringResource(R.string.reorder_item, label),
                                        tint = Coral,
                                    )
                                }
                            },
                        )
                    }
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
                itemLabel = { it.displayName(locale) },
                itemFlag = { Flags.forLanguage(it.code) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }
    }
}