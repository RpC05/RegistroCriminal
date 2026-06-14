package com.example.registrocriminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged // ¡Muy importante que esta línea esté aquí!
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.registrocriminal.databinding.FragmentCrimenBinding
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import android.widget.Toast

class CrimenFragment : Fragment() {

    private val crimenViewModel: CrimenViewModel by viewModels()
    private var _binding: FragmentCrimenBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "No se puede acceder al binding..."
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimenId = arguments?.getSerializable("crimenId") as? UUID
        crimenId?.let {
            crimenViewModel.cargarCrimen(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- DESAFÍO: Evitar que el usuario regrese si el título está en blanco ---
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.txtTituloCrimen.text.toString().isNotBlank()) {
                    // Si no está en blanco, navegamos hacia atrás manualmente
                    findNavController().popBackStack()
                } else {
                    // Si está en blanco, avisamos al usuario y no hacemos nada más (evitamos salir)
                    Toast.makeText(requireContext(), "El título no puede estar en blanco", Toast.LENGTH_SHORT).show()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // --- 1. NUEVO CÓDIGO (Diapositiva): Escuchamos las acciones del usuario ---
        binding.apply {

            // Si el usuario escribe o borra algo en el título...
            txtTituloCrimen.doOnTextChanged { texto, _, _, _ ->
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(titulo = texto.toString())
                }
            }

            // Desactivamos el botón de fecha (seguramente la guía te hará usarlo luego)
            btnFechaCrimen.apply {
                isEnabled = false
            }

            // Si el usuario marca o desmarca la casilla de "Resuelto"...
            chkCrimenResuelto.setOnCheckedChangeListener { _, seleccionado ->
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(resuelto = seleccionado)
                }
            }
        }

        // --- 2. LO QUE YA TENÍAMOS: Reflejamos los cambios visualmente ---
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimenViewModel.crimen.collect { crimen ->
                    crimen?.let { actualizarUI(it) }
                }
            }
        }
    }

    private fun actualizarUI(crimen: Crimen) {
        binding.apply {
            // El 'if' evita un bucle infinito al momento de actualizar el texto
            if (txtTituloCrimen.text.toString() != crimen.titulo) {
                txtTituloCrimen.setText(crimen.titulo)
            }
            btnFechaCrimen.text = crimen.fecha.toString()
            chkCrimenResuelto.isChecked = crimen.resuelto
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}