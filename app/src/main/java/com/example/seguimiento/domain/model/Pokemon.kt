package com.example.seguimiento.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Int,        // en decímetros
    val weight: Int,        // en hectogramos
    val baseExperience: Int,
    val abilities: List<String>,
    val stats: Map<String, Int>,
    val captureRate: Int,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val description: String,
    val genus: String
) {
    val heightInMeters: Float get() = height / 10f
    val weightInKg: Float get() = weight / 10f
    val formattedName: String get() = name.replaceFirstChar { it.uppercase() }
}
