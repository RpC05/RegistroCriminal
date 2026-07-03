package com.example.registrocriminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import java.util.Date
import java.util.UUID
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registrocriminal.databinding.FragmentListaCrimenesBinding
import kotlinx.coroutines.launch
import androidx.core.os.bundleOf

class ListaCrimenesFragment : Fragment() {

    private var _binding: FragmentListaCrimenesBinding? = null
    private val binding get() = _binding!!

    private val listaCrimenesViewModel: ListaCrimenesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaCrimenesBinding.inflate(inflater, container, false)
        binding.crimenRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarCrimen.setOnClickListener {
            mostraNuevoCrimen()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listaCrimenesViewModel.crimenes.collect { crimenes ->

                    if (crimenes.isEmpty()) {
                        binding.crimenRecyclerView.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                    } else {
                        binding.crimenRecyclerView.visibility = View.VISIBLE
                        binding.emptyView.visibility = View.GONE
                    }

                    // Nombramos la variable que recibimos como 'crimenId'
                    binding.crimenRecyclerView.adapter = CrimenAdapter(crimenes) { crimenId ->

                        // En vez de usar Directions (SafeArgs), empaquetamos el ID nativamente
                        val paquete = bundleOf("crimenId" to crimenId)

                        // Navegamos usando nuestra acción y enviándole el paquete
                        findNavController().navigate(R.id.mostrar_crimen, paquete)

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragmento_lista_crimenes, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nuevo_crimen -> {
                mostraNuevoCrimen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostraNuevoCrimen() {
        viewLifecycleOwner.lifecycleScope.launch {
            val nuevoCrimen = Crimen(
                id = UUID.randomUUID(),
                titulo = "",
                fecha = Date(),
                resuelto = false
            )
            listaCrimenesViewModel.ingresarCrimen(nuevoCrimen)
            val paquete = androidx.core.os.bundleOf("crimenId" to nuevoCrimen.id)
            findNavController().navigate(R.id.mostrar_crimen, paquete)
        }
    }
}