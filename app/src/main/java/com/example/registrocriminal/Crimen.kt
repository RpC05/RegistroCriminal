package com.example.registrocriminal

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "crimen")
data class Crimen(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var titulo: String = "",
    var fecha: Date = Date(),
    var resuelto: Boolean = false,
    var crimenMayor: Boolean = false
)