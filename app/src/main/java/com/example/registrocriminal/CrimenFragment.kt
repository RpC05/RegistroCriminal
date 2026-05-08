package com.example.registrocriminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.registrocriminal.databinding.FragmentCrimenBinding
import java.util.Date
import java.util.UUID

class CrimenFragment : Fragment() {

    private lateinit var crimen: Crimen

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

        binding.apply {
            txtTituloCrimen.doOnTextChanged { texto, _, _, _ ->
                crimen = crimen.copy(titulo = texto.toString())
            }

            btnFechaCrimen.apply {
                text = crimen.fecha.toString()
                isEnabled = false
            }

            chkCrimenResuelto.setOnCheckedChangeListener { _, seleccionado ->
                crimen = crimen.copy(resuelto = seleccionado)
            }
        }
    }

    // 4. ¡Lo más importante! Limpiamos la memoria cuando la vista se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}