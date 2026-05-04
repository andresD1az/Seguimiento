package com.example.seguimiento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,           // CSV: "fire,flying"
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val abilities: String,       // CSV: "blaze,solar-power"
    val stats: String,           // JSON-like: "hp:45,attack:60"
    val captureRate: Int,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val description: String,
    val genus: String,
    val pageIndex: Int           // Para mantener orden de paginación
)
