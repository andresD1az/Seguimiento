package com.example.seguimiento.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.seguimiento.ui.components.ConnectivityBanner
import com.example.seguimiento.ui.components.PokemonCard
import com.example.seguimiento.ui.viewmodel.PokemonListViewModel

private val POKEMON_TYPES = listOf(
    "", "fire", "water", "grass", "electric", "psychic", "ice",
    "dragon", "dark", "fairy", "fighting", "poison", "ground",
    "flying", "bug", "rock", "ghost", "steel", "normal"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onPokemonClick: (Int) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val searchName by viewModel.searchName.collectAsStateWithLifecycle()
    val searchType by viewModel.searchType.collectAsStateWithLifecycle()
    val isFiltering by viewModel.isFiltering.collectAsStateWithLifecycle()
    val filteredPokemon by viewModel.filteredPokemon.collectAsStateWithLifecycle()
    val isLoadingType by viewModel.isLoadingType.collectAsStateWithLifecycle()
    val pagedPokemon = viewModel.pagedPokemon.collectAsLazyPagingItems()

    var typeDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pokédex",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFCC0000),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ConnectivityBanner(isOnline = isOnline)

            // Indicador de carga al buscar por tipo desde la API
            if (isLoadingType) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFCC0000)
                )
            }

            // Search filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchName,
                    onValueChange = viewModel::onSearchNameChange,
                    label = { Text("Buscar por nombre") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchName.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchNameChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (searchType.isBlank()) "Todos los tipos" else searchType.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        POKEMON_TYPES.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(if (type.isBlank()) "Todos los tipos" else type.replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    viewModel.onSearchTypeChange(type)
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (isFiltering) {
                // Filtered results
                if (filteredPokemon.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron Pokémon",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredPokemon, key = { it.id }) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) }
                            )
                        }
                    }
                }
            } else {
                // Paged results
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pagedPokemon.itemCount) { index ->
                        val pokemon = pagedPokemon[index]
                        if (pokemon != null) {
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) }
                            )
                        }
                    }

                    // Loading indicator at bottom
                    when (val appendState = pagedPokemon.loadState.append) {
                        is LoadState.Loading -> {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFFCC0000),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item(span = { GridItemSpan(2) }) {
                                Text(
                                    text = "Error al cargar más Pokémon",
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        else -> {}
                    }

                    // Initial loading
                    when (val refreshState = pagedPokemon.loadState.refresh) {
                        is LoadState.Loading -> {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            color = Color(0xFFCC0000),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Cargando Pokémon...",
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item(span = { GridItemSpan(2) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "⚠️ Sin conexión",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Mostrando datos almacenados localmente",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
