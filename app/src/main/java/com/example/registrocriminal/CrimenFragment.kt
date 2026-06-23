package com.example.registrocriminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged // ¡Muy importante que esta línea esté aquí!
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.registrocriminal.databinding.FragmentCrimenBinding
import kotlinx.coroutines.launch
import androidx.core.os.bundleOf
import java.util.UUID
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import java.util.Date

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

        setFragmentResultListener(DatePickerFragment.CLAVE_FECHA_SOLICITADA) { _, bundle ->
            val result = bundle.getSerializable(DatePickerFragment.CLAVE_FECHA_SOLICITADA) as? Date
            if (result != null) {
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(fecha = result)
                }
            }
        }

        setFragmentResultListener(TimePickerFragment.CLAVE_TIEMPO_SOLICITADO) { _, bundle ->
            val result = bundle.getSerializable(TimePickerFragment.CLAVE_TIEMPO_SOLICITADO) as? Date
            if (result != null) {
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(fecha = result)
                }
            }
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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.txtTituloCrimen.text.toString().isNotBlank()) {
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "El título no puede estar en blanco", Toast.LENGTH_SHORT).show()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.apply {

            txtTituloCrimen.doOnTextChanged { texto, _, _, _ ->
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(titulo = texto.toString())
                }
            }

            chkCrimenResuelto.setOnCheckedChangeListener { _, seleccionado ->
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(resuelto = seleccionado)
                }
            }
        }

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
            if (txtTituloCrimen.text.toString() != crimen.titulo) {
                txtTituloCrimen.setText(crimen.titulo)
            }
            btnFechaCrimen.text = android.text.format.DateFormat.format("EEEE, MMM dd, yyyy", crimen.fecha)
            btnFechaCrimen.setOnClickListener {
                val paquete = bundleOf("fechaCrimen" to crimen.fecha)
                findNavController().navigate(R.id.selectorFecha, paquete)
            }

            btnTiempoCrimen.text = android.text.format.DateFormat.format("HH:mm", crimen.fecha)
            btnTiempoCrimen.setOnClickListener {
                val paquete = bundleOf("fechaCrimen" to crimen.fecha)
                findNavController().navigate(R.id.selectorTiempo, paquete)
            }
            chkCrimenResuelto.isChecked = crimen.resuelto
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}