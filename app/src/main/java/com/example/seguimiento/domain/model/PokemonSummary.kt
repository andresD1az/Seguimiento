package com.example.seguimiento.domain.model

data class PokemonSummary(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>
) {
    val formattedName: String get() = name.replaceFirstChar { it.uppercase() }
}
