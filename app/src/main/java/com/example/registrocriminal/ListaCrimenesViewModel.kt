package com.example.registrocriminal

import androidx.lifecycle.ViewModel
import java.util.Date
import java.util.UUID

class ListaCrimenesViewModel : ViewModel() {

    val crimenes = mutableListOf<Crimen>()

    init {
        for (i in 0 until 100) {
            val crimen = Crimen(
                id = UUID.randomUUID(),
                titulo = "Crimen # $i",
                fecha = Date(),
                resuelto = i % 2 == 0
            )
            crimenes.add(crimen)
        }
    }
}