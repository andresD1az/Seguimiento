package com.example.seguimiento.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seguimiento.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun getAllPokemonPaged(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchByName(query: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE types LIKE '%' || :type || '%' ORDER BY id ASC")
    fun filterByType(type: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :name || '%' AND types LIKE '%' || :type || '%' ORDER BY id ASC")
    fun searchByNameAndType(name: String, type: String): Flow<List<PokemonEntity>>

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun count(): Int

    @Query("DELETE FROM pokemon")
    suspend fun clearAll()

    @Query("SELECT MAX(pageIndex) FROM pokemon")
    suspend fun getLastPageIndex(): Int?
}
