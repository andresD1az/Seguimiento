package com.example.seguimiento.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("base_experience") val baseExperience: Int?,
    @SerializedName("sprites") val sprites: SpritesResponse,
    @SerializedName("types") val types: List<TypeSlotResponse>,
    @SerializedName("stats") val stats: List<StatSlotResponse>,
    @SerializedName("abilities") val abilities: List<AbilitySlotResponse>
)

data class SpritesResponse(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("other") val other: OtherSpritesResponse?
)

data class OtherSpritesResponse(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtworkResponse?
)

data class OfficialArtworkResponse(
    @SerializedName("front_default") val frontDefault: String?
)

data class TypeSlotResponse(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: NamedApiResource
)

data class StatSlotResponse(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("stat") val stat: NamedApiResource
)

data class AbilitySlotResponse(
    @SerializedName("ability") val ability: NamedApiResource,
    @SerializedName("is_hidden") val isHidden: Boolean
)

data class NamedApiResource(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)
