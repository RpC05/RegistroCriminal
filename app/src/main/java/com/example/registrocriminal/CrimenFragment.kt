package com.example.registrocriminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.registrocriminal.databinding.FragmentCrimenBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimenFragment : Fragment() {

    private lateinit var crimen: Crimen
    private val listaCrimenesViewModel: ListaCrimenesViewModel by viewModels()

    // 1. Declaramos _binding como nullable (?) y lo iniciamos en null
    private var _binding: FragmentCrimenBinding? = null

    // 2. Creamos un "get()" seguro. checkNotNull lanza error si intentamos usar la vista cuando ya se destruyó
    private val binding
        get() = checkNotNull(_binding) {
            "No se puede acceder al binding porque es nulo. ¿Está la vista visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crimen = Crimen(id = UUID.randomUUID(), titulo = "", fecha = Date(), resuelto = false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 3. Asignamos la vista inflada a la variable _binding
        _binding = FragmentCrimenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Opcional (Diapo 26): Ingresar un dato duro de prueba si la base está vacía
        viewLifecycleOwner.lifecycleScope.launch {
            // Puedes comentar esto luego de correr la app la primera vez para no llenar tu DB
            /*
            listaCrimenesViewModel.ingresarCrimen(Crimen(
                id = UUID.randomUUID(),
                titulo = "Impresora dañada",
                fecha = Date(),
                resuelto = false,
                crimenMayor = true
            ))
            */
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listaCrimenesViewModel.crimenes.collect { crimenes ->
                    // Aquí actualizas tu adaptador con la nueva lista que viene de la BD
                    // binding.crimenRecyclerView.adapter = ListaCrimenAdapter(crimenes)
                }
            }
        }
    }

    // 4. ¡Lo más importante! Limpiamos la memoria cuando la vista se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}