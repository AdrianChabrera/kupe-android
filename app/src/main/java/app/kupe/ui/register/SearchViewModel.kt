package app.kupe.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kupe.data.remote.Country
import app.kupe.data.remote.Language
import app.kupe.data.repository.GeoRepository
import app.kupe.di.AppContainer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.Locale

data class SearchState<T>(
    val query: String = "",
    val results: List<T> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
)

open class SearchViewModel<T>(
    private val load: suspend () -> Result<List<T>>,
    private val searchText: (T, Locale) -> List<String>,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState<T>())
    val state: StateFlow<SearchState<T>> = _state.asStateFlow()

    private var locale: Locale = Locale.getDefault()
    private var loaded: List<T>? = null
    private var items: List<Pair<T, List<String>>>? = null
    private var loadJob: Job? = null

    init {
        ensureLoaded()
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value.take(MAX_QUERY_LENGTH)) }
        if (items == null) ensureLoaded()
        applyFilter()
    }

    fun onLocaleChange(newLocale: Locale) {
        if (newLocale == locale) return
        locale = newLocale
        index()
        applyFilter()
    }

    private fun index() {
        items = loaded?.map { item -> item to searchText(item, locale).map(::normalize) }
    }

    private fun ensureLoaded() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _state.update { it.copy(loading = true, error = false) }
            load().fold(
                onSuccess = { list ->
                    loaded = list
                    index()
                    _state.update { it.copy(loading = false) }
                    applyFilter()
                },
                onFailure = { _state.update { it.copy(loading = false, error = true) } },
            )
        }
    }

    private fun applyFilter() {
        val all = items ?: return
        val query = normalize(_state.value.query)
        if (query.isEmpty()) {
            _state.update { it.copy(results = emptyList()) }
            return
        }
        val results = all.asSequence()
            .filter { (_, keys) -> keys.any { it.contains(query) } }
            .sortedBy { (_, keys) -> if (keys.first().startsWith(query)) 0 else 1 } // stable sort
            .map { (item, _) -> item }
            .take(MAX_RESULTS)
            .toList()
        _state.update { it.copy(results = results) }
    }

    private companion object {
        const val MAX_QUERY_LENGTH = 64
        const val MAX_RESULTS = 50
        val DIACRITICS = Regex("\\p{Mn}+")

        fun normalize(text: String): String =
            Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
                .replace(DIACRITICS, "")
                .lowercase()
    }
}

class CountrySearchViewModel(repository: GeoRepository) : SearchViewModel<Country>(
    load = { repository.countries() },
    searchText = { country, locale -> listOf(country.displayName(locale), country.name) },
) {
    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { CountrySearchViewModel(container.geoRepository) }
        }
    }
}

class LanguageSearchViewModel(repository: GeoRepository) : SearchViewModel<Language>(
    load = { repository.languages() },
    searchText = { language, locale ->
        listOfNotNull(language.displayName(locale), language.name?.takeIf { it.isNotBlank() }, language.code)
    },
) {
    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { LanguageSearchViewModel(container.geoRepository) }
        }
    }
}