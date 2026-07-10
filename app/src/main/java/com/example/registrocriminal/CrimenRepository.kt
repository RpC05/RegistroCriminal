package com.example.registrocriminal

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private const val DATABASE_NAME = "crimen-database"

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crimen ADD COLUMN sospechoso TEXT NOT NULL DEFAULT ''")
    }
}

class CrimenRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val database = Room.databaseBuilder(
            context.applicationContext,
            CrimenDatabase::class.java,
            DATABASE_NAME
    ).addMigrations(migration_1_2).build()

    fun getCrimenes(): Flow<List<Crimen>> = database.crimenDAO().getCrimenes()

    suspend fun getCrimen(id: UUID): Crimen = database.crimenDAO().getCrimen(id)

    suspend fun ingresarCrimen(crimen: Crimen) {
        database.crimenDAO().ingresarCrimen(crimen)
    }

    fun actualizarCrimen(crimen: Crimen) {
        coroutineScope.launch {
            database.crimenDAO().actualizarCrimen(crimen)
        }
    }

    fun eliminarCrimen(crimen: Crimen) {
        coroutineScope.launch {
            database.crimenDAO().eliminarCrimen(crimen)
        }
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
