package com.example.registrocriminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CrimenViewModel : ViewModel() {

    private val repositorio = CrimenRepository.get()

    private val _crimen: MutableStateFlow<Crimen?> = MutableStateFlow(null)
    val crimen: StateFlow<Crimen?> = _crimen.asStateFlow()

    // 1. MÉTODO EXTRA: Usamos el ID para ir a buscar el crimen a la base de datos
    fun cargarCrimen(id: UUID) {
        viewModelScope.launch {
            _crimen.value = repositorio.getCrimen(id)
        }
    }

    // 2. Diapositiva (Parte Inferior): Lógica para actualizar la información
    fun actualizarCrimen(onUpdate: (Crimen) -> Crimen) {
        _crimen.update { anterior ->
            anterior?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _crimen.value?.let { repositorio.actualizarCrimen(it) }
    }
}