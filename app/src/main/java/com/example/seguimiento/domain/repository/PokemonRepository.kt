package com.example.seguimiento.domain.repository

import androidx.paging.PagingData
import com.example.seguimiento.domain.model.Pokemon
import com.example.seguimiento.domain.model.PokemonSummary
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonPaged(): Flow<PagingData<PokemonSummary>>
    fun searchPokemon(name: String, type: String): Flow<List<PokemonSummary>>
    suspend fun getPokemonDetail(id: Int): Pokemon?
    // Llama al endpoint /type/{typeName} para obtener pokémon del tipo,
    // los persiste en Room y los devuelve como Flow desde la BD
    suspend fun fetchAndCachePokemonByType(typeName: String)
    fun getPokemonByType(type: String): Flow<List<PokemonSummary>>
}
