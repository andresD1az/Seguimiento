package com.example.seguimiento.data.mapper

import com.example.seguimiento.data.local.entity.PokemonEntity
import com.example.seguimiento.data.remote.model.PokemonDetailResponse
import com.example.seguimiento.data.remote.model.PokemonSpeciesResponse
import com.example.seguimiento.domain.model.Pokemon
import com.example.seguimiento.domain.model.PokemonSummary

// Remote → Entity
fun PokemonDetailResponse.toEntity(
    species: PokemonSpeciesResponse?,
    pageIndex: Int
): PokemonEntity {
    val imageUrl = sprites.other?.officialArtwork?.frontDefault
        ?: sprites.frontDefault
        ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

    val typesStr = types.sortedBy { it.slot }.joinToString(",") { it.type.name }
    val abilitiesStr = abilities.joinToString(",") { it.ability.name }
    val statsStr = stats.joinToString(",") { "${it.stat.name}:${it.baseStat}" }

    val description = species?.flavorTextEntries
        ?.firstOrNull { it.language.name == "es" }
        ?.flavorText
        ?.replace("\n", " ")
        ?.replace("\u000c", " ")
        ?: species?.flavorTextEntries
            ?.firstOrNull { it.language.name == "en" }
            ?.flavorText
            ?.replace("\n", " ")
            ?.replace("\u000c", " ")
        ?: ""

    val genus = species?.genera
        ?.firstOrNull { it.language.name == "es" }
        ?.genus
        ?: species?.genera
            ?.firstOrNull { it.language.name == "en" }
            ?.genus
        ?: ""

    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = typesStr,
        height = height,
        weight = weight,
        baseExperience = baseExperience ?: 0,
        abilities = abilitiesStr,
        stats = statsStr,
        captureRate = species?.captureRate ?: 0,
        isLegendary = species?.isLegendary ?: false,
        isMythical = species?.isMythical ?: false,
        description = description,
        genus = genus,
        pageIndex = pageIndex
    )
}

// Entity → Domain
fun PokemonEntity.toDomain(): Pokemon {
    val typesList = if (types.isBlank()) emptyList() else types.split(",")
    val abilitiesList = if (abilities.isBlank()) emptyList() else abilities.split(",")
    val statsMap = if (stats.isBlank()) emptyMap() else {
        stats.split(",").associate { entry ->
            val parts = entry.split(":")
            parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
        }
    }
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = typesList,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        abilities = abilitiesList,
        stats = statsMap,
        captureRate = captureRate,
        isLegendary = isLegendary,
        isMythical = isMythical,
        description = description,
        genus = genus
    )
}

fun PokemonEntity.toSummary(): PokemonSummary {
    val typesList = if (types.isBlank()) emptyList() else types.split(",")
    return PokemonSummary(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = typesList
    )
}
