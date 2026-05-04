package com.example.seguimiento.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<PokemonResult>
)

data class PokemonResult(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
) {
    // Extract ID from URL: https://pokeapi.co/api/v2/pokemon/1/
    val id: Int get() = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
}
