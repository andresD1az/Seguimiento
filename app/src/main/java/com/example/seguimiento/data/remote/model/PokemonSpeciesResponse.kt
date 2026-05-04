package com.example.seguimiento.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @SerializedName("genera") val genera: List<GenusEntry>,
    @SerializedName("capture_rate") val captureRate: Int,
    @SerializedName("base_happiness") val baseHappiness: Int?,
    @SerializedName("is_legendary") val isLegendary: Boolean,
    @SerializedName("is_mythical") val isMythical: Boolean
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    @SerializedName("language") val language: NamedApiResource
)

data class GenusEntry(
    @SerializedName("genus") val genus: String,
    @SerializedName("language") val language: NamedApiResource
)
