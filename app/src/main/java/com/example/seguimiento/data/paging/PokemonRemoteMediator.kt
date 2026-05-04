package com.example.seguimiento.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.seguimiento.data.local.dao.PokemonDao
import com.example.seguimiento.data.local.entity.PokemonEntity
import com.example.seguimiento.data.mapper.toEntity
import com.example.seguimiento.data.remote.api.PokeApiService

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokeApiService,
    private val dao: PokemonDao
) : RemoteMediator<Int, PokemonEntity>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastPageIndex = dao.getLastPageIndex() ?: 0
                    (lastPageIndex + 1) * PAGE_SIZE
                }
            }

            val pageIndex = offset / PAGE_SIZE

            val response = api.getPokemonList(limit = PAGE_SIZE, offset = offset)

            val entities = response.results.mapNotNull { result ->
                try {
                    val detail = api.getPokemonDetail(result.name)
                    val species = try {
                        api.getPokemonSpecies(result.name)
                    } catch (e: Exception) {
                        null
                    }
                    detail.toEntity(species, pageIndex)
                } catch (e: Exception) {
                    null
                }
            }

            if (loadType == LoadType.REFRESH) {
                dao.clearAll()
            }

            dao.insertAll(entities)

            MediatorResult.Success(endOfPaginationReached = response.next == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
