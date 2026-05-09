package com.example.seguimiento.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.seguimiento.domain.model.PokemonSummary
import com.example.seguimiento.domain.repository.PokemonRepository
import com.example.seguimiento.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    // Network state
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Search filters
    private val _searchName = MutableStateFlow("")
    val searchName: StateFlow<String> = _searchName.asStateFlow()

    private val _searchType = MutableStateFlow("")
    val searchType: StateFlow<String> = _searchType.asStateFlow()

    // Loading state for type fetch
    private val _isLoadingType = MutableStateFlow(false)
    val isLoadingType: StateFlow<Boolean> = _isLoadingType.asStateFlow()

    // Whether filter mode is active
    val isFiltering: StateFlow<Boolean> = combine(_searchName, _searchType) { name, type ->
        name.isNotBlank() || type.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Paged data (used when not filtering)
    val pagedPokemon: Flow<PagingData<PokemonSummary>> = repository
        .getPokemonPaged()
        .cachedIn(viewModelScope)

    // Filtered results (used when filtering) — reads from local DB
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredPokemon: StateFlow<List<PokemonSummary>> = combine(
        _searchName.debounce(300),
        _searchType.debounce(300)
    ) { name, type -> Pair(name, type) }
        .flatMapLatest { (name, type) ->
            repository.searchPokemon(name, type)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchNameChange(query: String) {
        _searchName.value = query
    }

    /**
     * Cuando el usuario selecciona un tipo:
     * 1. Actualiza el filtro local inmediatamente (la UI reacciona al Flow de la BD)
     * 2. Si hay conexión, llama al Endpoint 4 (/type/{typeName}) para traer
     *    pokémon de ese tipo desde la API y persistirlos en Room.
     *    Luego el Flow de la BD se actualiza automáticamente.
     */
    fun onSearchTypeChange(type: String) {
        _searchType.value = type
        if (type.isNotBlank() && networkMonitor.isCurrentlyOnline()) {
            viewModelScope.launch {
                _isLoadingType.value = true
                repository.fetchAndCachePokemonByType(type)
                _isLoadingType.value = false
            }
        }
    }

    fun clearFilters() {
        _searchName.value = ""
        _searchType.value = ""
    }
}
