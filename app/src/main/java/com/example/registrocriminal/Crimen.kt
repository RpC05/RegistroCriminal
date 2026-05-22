package com.example.registrocriminal

import java.util.Date
import java.util.UUID

data class Crimen(
    val id: UUID = UUID.randomUUID(),
    var titulo: String = "",
    var fecha: Date = Date(),
    var resuelto: Boolean = false,
    var crimenMayor: Boolean = false
)