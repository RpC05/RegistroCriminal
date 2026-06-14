package com.example.registrocriminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaCrimenesViewModel : ViewModel() {

    private val crimenRepository = CrimenRepository.get()

    private val _crimenes: MutableStateFlow<List<Crimen>> = MutableStateFlow(emptyList())
    val crimenes: StateFlow<List<Crimen>> get() = _crimenes.asStateFlow()

    init {
        viewModelScope.launch {
            crimenRepository.getCrimenes().collect { lista ->
                if (lista.isEmpty()) {
                    // Generar datos ficticios si la base de datos está vacía
                    viewModelScope.launch {
                        for (i in 0 until 100) {
                            val crimen = Crimen(
                                titulo = "Evento # $i",
                                fecha = java.util.Date(),
                                resuelto = i % 2 == 0,
                                crimenMayor = i % 3 == 0
                            )
                            crimenRepository.ingresarCrimen(crimen)
                        }
                    }
                }
                _crimenes.value = lista
            }
        }
    }

    suspend fun ingresarCrimen(crimen: Crimen) {
        crimenRepository.ingresarCrimen(crimen)
    }
}