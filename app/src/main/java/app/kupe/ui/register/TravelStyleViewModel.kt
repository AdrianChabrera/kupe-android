package app.kupe.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kupe.data.repository.UsersRepository
import app.kupe.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TravelStylesState(
    val styles: List<TravelStyleUi> = emptyList(),
    val loading: Boolean = true,
    val error: Boolean = false,
)

class TravelStyleViewModel(private val repository: UsersRepository) : ViewModel() {
    private val _state = MutableStateFlow(TravelStylesState())
    val state: StateFlow<TravelStylesState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true, error = false) }
        viewModelScope.launch {
            repository.travelStyles().fold(
                onSuccess = { list ->
                    val styles = list.mapNotNull { TravelStyleUi.fromCode(it) }.sortedBy { it.ordinal }
                    _state.update { TravelStylesState(styles = styles, loading = false) }
                },
                onFailure = { _state.update { it.copy(loading = false, error = true) } },
            )
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { TravelStyleViewModel(container.usersRepository) }
        }
    }
}