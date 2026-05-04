package com.example.seguimiento.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonTypeResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("pokemon") val pokemon: List<TypePokemonEntry>
)

data class TypePokemonEntry(
    @SerializedName("pokemon") val pokemon: NamedApiResource,
    @SerializedName("slot") val slot: Int
)
