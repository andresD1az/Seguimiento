package com.example.seguimiento.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.seguimiento.domain.model.Pokemon
import com.example.seguimiento.ui.components.StatBar
import com.example.seguimiento.ui.components.TypeChip
import com.example.seguimiento.ui.components.typeColor
import com.example.seguimiento.ui.viewmodel.DetailUiState
import com.example.seguimiento.ui.viewmodel.PokemonDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    onBack: () -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val s = uiState) {
                        is DetailUiState.Success -> s.pokemon.formattedName
                        else -> "Detalle"
                    }
                    Text(text = title, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (uiState is DetailUiState.Error) {
                        IconButton(onClick = viewModel::loadPokemon) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reintentar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFCC0000),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFCC0000)
                    )
                }
                is DetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⚠️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.message,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                is DetailUiState.Success -> {
                    PokemonDetailContent(pokemon = state.pokemon)
                }
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(pokemon: Pokemon) {
    val primaryColor = if (pokemon.types.isNotEmpty()) typeColor(pokemon.types[0]) else Color(0xFF888888)
    val secondaryColor = if (pokemon.types.size > 1) typeColor(pokemon.types[1]) else primaryColor.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.8f), secondaryColor.copy(alpha = 0.3f))
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#${pokemon.id.toString().padStart(3, '0')}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.formattedName,
                    modifier = Modifier.size(180.dp)
                )
                Text(
                    text = pokemon.formattedName,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                if (pokemon.genus.isNotBlank()) {
                    Text(
                        text = pokemon.genus,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pokemon.types.forEach { type -> TypeChip(type = type) }
                }
                if (pokemon.isLegendary) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⭐ Legendario",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                if (pokemon.isMythical) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✨ Mítico",
                        color = Color(0xFFE040FB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Description
            if (pokemon.description.isNotBlank()) {
                SectionTitle("Descripción")
                Text(
                    text = pokemon.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Info grid
            SectionTitle("Información")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(label = "Altura", value = "${"%.1f".format(pokemon.heightInMeters)} m")
                InfoItem(label = "Peso", value = "${"%.1f".format(pokemon.weightInKg)} kg")
                InfoItem(label = "Exp. Base", value = pokemon.baseExperience.toString())
                InfoItem(label = "Captura", value = pokemon.captureRate.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Abilities
            SectionTitle("Habilidades")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pokemon.abilities.forEach { ability ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = ability.replace("-", " ").replaceFirstChar { it.uppercase() },
                            fontSize = 13.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Stats
            SectionTitle("Estadísticas base")
            val statColors = mapOf(
                "hp" to Color(0xFFFF5959),
                "attack" to Color(0xFFF5AC78),
                "defense" to Color(0xFFFAE078),
                "special-attack" to Color(0xFF9DB7F5),
                "special-defense" to Color(0xFFA7DB8D),
                "speed" to Color(0xFFFA92B2)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pokemon.stats.forEach { (statName, value) ->
                    StatBar(
                        statName = statName,
                        value = value,
                        color = statColors[statName] ?: primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFCC0000),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}
