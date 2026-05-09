package com.example.seguimiento.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.seguimiento.data.local.dao.PokemonDao
import com.example.seguimiento.data.mapper.toDomain
import com.example.seguimiento.data.mapper.toEntity
import com.example.seguimiento.data.mapper.toSummary
import com.example.seguimiento.data.paging.PokemonRemoteMediator
import com.example.seguimiento.data.remote.api.PokeApiService
import com.example.seguimiento.domain.model.Pokemon
import com.example.seguimiento.domain.model.PokemonSummary
import com.example.seguimiento.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPokemonPaged(): Flow<PagingData<PokemonSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = PokemonRemoteMediator.PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = PokemonRemoteMediator(api, dao),
            pagingSourceFactory = { dao.getAllPokemonPaged() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toSummary() }
        }
    }

    override fun searchPokemon(name: String, type: String): Flow<List<PokemonSummary>> {
        return when {
            name.isNotBlank() && type.isNotBlank() ->
                dao.searchByNameAndType(name, type).map { list -> list.map { it.toSummary() } }
            name.isNotBlank() ->
                dao.searchByName(name).map { list -> list.map { it.toSummary() } }
            type.isNotBlank() ->
                dao.filterByType(type).map { list -> list.map { it.toSummary() } }
            else ->
                dao.searchByName("").map { list -> list.map { it.toSummary() } }
        }
    }

    override suspend fun getPokemonDetail(id: Int): Pokemon? {
        // Primero intenta desde la BD local
        val local = dao.getPokemonById(id)
        if (local != null) return local.toDomain()

        // Fallback a la API remota
        return try {
            val detail = api.getPokemonDetail(id.toString())
            val species = try { api.getPokemonSpecies(id.toString()) } catch (e: Exception) { null }
            val entity = detail.toEntity(species, -1)
            dao.insert(entity)
            entity.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Endpoint 4: GET /type/{typeName}
     * Obtiene la lista de pokémon del tipo desde la API, luego para cada uno
     * que no esté en la BD local llama a /pokemon/{id} y /pokemon-species/{id}
     * para obtener el detalle completo y persistirlo en Room.
     */
    override suspend fun fetchAndCachePokemonByType(typeName: String) {
        try {
            val typeResponse = api.getPokemonByType(typeName)
            // Tomar los primeros 40 para no saturar la API
            val toFetch = typeResponse.pokemon.take(40)
            val entities = toFetch.mapNotNull { entry ->
                val name = entry.pokemon.name
                // Extraer ID de la URL para verificar si ya existe en BD
                val idFromUrl = entry.pokemon.url.trimEnd('/').substringAfterLast('/').toIntOrNull()
                // Si ya está en BD, no volver a descargar
                if (idFromUrl != null && dao.getPokemonById(idFromUrl) != null) return@mapNotNull null
                try {
                    val detail = api.getPokemonDetail(name)
                    val species = try { api.getPokemonSpecies(name) } catch (e: Exception) { null }
                    detail.toEntity(species, -1)
                } catch (e: Exception) {
                    null
                }
            }
            if (entities.isNotEmpty()) {
                dao.insertAll(entities)
            }
        } catch (e: Exception) {
            // Si falla la red, los datos locales ya están disponibles
        }
    }

    override fun getPokemonByType(type: String): Flow<List<PokemonSummary>> {
        return dao.filterByType(type).map { list -> list.map { it.toSummary() } }
    }
}
