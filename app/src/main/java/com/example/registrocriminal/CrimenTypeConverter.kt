package com.example.registrocriminal

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimenTypeConverter {
    @TypeConverter
    fun fromDate(fecha: Date?): Long? {
        return fecha?.time
    }

    @TypeConverter
    fun toDate(miliseg: Long?): Date? {
        return miliseg?.let { Date(it) }
    }

    // Agregado necesario para que Room pueda guardar tu UUID
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}
