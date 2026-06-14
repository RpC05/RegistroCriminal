package com.example.registrocriminal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimenDAO {
    @Query("SELECT * FROM crimen")
    fun getCrimenes(): Flow<List<Crimen>>

    @Query("SELECT * FROM crimen WHERE id=(:id)")
    suspend fun getCrimen(id: UUID): Crimen

    @Insert
    suspend fun ingresarCrimen(crimen: Crimen)
}
