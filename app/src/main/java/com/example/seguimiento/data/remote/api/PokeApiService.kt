package com.example.seguimiento.data.remote.api

import com.example.seguimiento.data.remote.model.PokemonDetailResponse
import com.example.seguimiento.data.remote.model.PokemonListResponse
import com.example.seguimiento.data.remote.model.PokemonSpeciesResponse
import com.example.seguimiento.data.remote.model.PokemonTypeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    // Endpoint 1: Lista paginada de pokémon
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    // Endpoint 2: Detalle de un pokémon por nombre o ID
    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetail(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetailResponse

    // Endpoint 3: Especie de un pokémon (descripción, categoría, etc.)
    @GET("pokemon-species/{nameOrId}")
    suspend fun getPokemonSpecies(
        @Path("nameOrId") nameOrId: String
    ): PokemonSpeciesResponse

    // Endpoint 4: Pokémon filtrados por tipo
    @GET("type/{typeName}")
    suspend fun getPokemonByType(
        @Path("typeName") typeName: String
    ): PokemonTypeResponse
}
