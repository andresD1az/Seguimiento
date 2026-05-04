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
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    // Network state
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Search filters
    private val _searchName = MutableStateFlow("")
    val searchName: StateFlow<String> = _searchName.asStateFlow()

    private val _searchType = MutableStateFlow("")
    val searchType: StateFlow<String> = _searchType.asStateFlow()

    // Whether filter mode is active
    val isFiltering: StateFlow<Boolean> = combine(_searchName, _searchType) { name, type ->
        name.isNotBlank() || type.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Paged data (used when not filtering)
    val pagedPokemon: Flow<PagingData<PokemonSummary>> = repository
        .getPokemonPaged()
        .cachedIn(viewModelScope)

    // Filtered results (used when filtering)
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

    fun onSearchTypeChange(type: String) {
        _searchType.value = type
    }

    fun clearFilters() {
        _searchName.value = ""
        _searchType.value = ""
    }
}
