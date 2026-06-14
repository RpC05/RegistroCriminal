package com.example.registrocriminal

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import java.util.UUID

private const val DATABASE_NAME = "crimen-database"

class CrimenRepository private constructor(context: Context) {

    private val database = Room.databaseBuilder(
            context.applicationContext,
            CrimenDatabase::class.java,
            DATABASE_NAME
    ).build()

    fun getCrimenes(): Flow<List<Crimen>> = database.crimenDAO().getCrimenes()

    suspend fun getCrimen(id: UUID): Crimen = database.crimenDAO().getCrimen(id)

    suspend fun ingresarCrimen(crimen: Crimen) {
        database.crimenDAO().ingresarCrimen(crimen)
    }

    companion object {
        private var INSTANCIA: CrimenRepository? = null

        fun inicializar(context: Context) {
            if (INSTANCIA == null) {
                INSTANCIA = CrimenRepository(context)
            }
        }

        fun get(): CrimenRepository {
            return INSTANCIA ?: throw IllegalStateException("Debe inicializar el repositorio")
        }
    }
}
