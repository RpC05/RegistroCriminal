package com.example.registrocriminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.ContactsContract
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

private const val FORMATO_FECHA = "EEE, MMM, dd"

class CrimenFragment : Fragment() {

    private val crimenViewModel: CrimenViewModel by viewModels()
    private var _binding: FragmentCrimenBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "No se puede acceder al binding..."
        }

    private val selectorSospechos = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { obtenerContactoSeleccionado(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

            btnSeleccionarSospechoso.setOnClickListener {
                selectorSospechos.launch(null)
            }
            
            val intentSeleccionarSospechoso = selectorSospechos.contract.createIntent(
                requireContext(), null
            )
            btnSeleccionarSospechoso.isEnabled = puedeResolveIntent(intentSeleccionarSospechoso)
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

            btnEnviarReporte.setOnClickListener {
                val intentReporte = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getReporteCrimen(crimen))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.reporte_asunto))
                }
                val intentSelector = Intent.createChooser(intentReporte, getString(R.string.enviar_reporte))
                startActivity(intentSelector)
            }

            btnSeleccionarSospechoso.text = crimen.sospechoso.ifEmpty {
                getString(R.string.boton_sospechoso)
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
        inflater.inflate(R.menu.fragment_crimen, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.eliminar_crimen -> {
                crimenViewModel.crimen.value?.let {
                    crimenViewModel.eliminarCrimen(it)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getReporteCrimen(crimen: Crimen): String {
        val stringCrimenResuelto = if (crimen.resuelto) {
            getString(R.string.reporte_resuelto)
        } else {
            getString(R.string.reporte_no_resuelto)
        }
        val stringFecha = android.text.format.DateFormat.format(FORMATO_FECHA, crimen.fecha).toString()
        val textoSospechoso = if (crimen.sospechoso.isBlank()) {
            getString(R.string.reporte_sin_sospechoso)
        } else {
            getString(R.string.reporte_con_sospechoso, crimen.sospechoso)
        }
        return getString(
            R.string.reporte_Crimen,
            crimen.titulo, stringFecha, stringCrimenResuelto, textoSospechoso
        )
    }

    private fun obtenerContactoSeleccionado(UriContacto: Uri) {
        val campoConsulta = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursorConsulta = requireActivity().contentResolver.query(
            UriContacto, campoConsulta, null, null, null
        )
        cursorConsulta?.use { cursor ->
            if (cursor.moveToFirst()) {
                val culpable = cursor.getString(0)
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(sospechoso = culpable)
                }
            }
        }
    }

    private fun puedeResolveIntent(intent: Intent): Boolean {
        // intent.addCategory(Intent.CATEGORY_HOME) // Descomentar para probar la protección si no hay app
        val packageManager: PackageManager = requireActivity().packageManager
        val resolveActivity: ResolveInfo? = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveActivity != null
    }
}