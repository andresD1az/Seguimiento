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
        // Try local first
        val local = dao.getPokemonById(id)
        if (local != null) return local.toDomain()

        // Fallback to remote
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

    override fun getPokemonByType(type: String): Flow<List<PokemonSummary>> {
        return dao.filterByType(type).map { list -> list.map { it.toSummary() } }
    }
}
