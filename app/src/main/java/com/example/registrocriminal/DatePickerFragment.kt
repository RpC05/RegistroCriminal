package com.example.registrocriminal

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class DatePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val resultDate: Date = GregorianCalendar(year, month, day).time
            setFragmentResult(
                CLAVE_FECHA_SOLICITADA,
                bundleOf(CLAVE_FECHA_SOLICITADA to resultDate)
            )
        }

        val calendario = Calendar.getInstance()
        val fecha = arguments?.getSerializable("fechaCrimen") as? Date ?: Date()
        calendario.time = fecha
        val anioInicial = calendario.get(Calendar.YEAR)
        val mesInicial = calendario.get(Calendar.MONTH)
        val diaInicial = calendario.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            listener,
            anioInicial,
            mesInicial,
            diaInicial
        )
    }

    companion object {
        const val CLAVE_FECHA_SOLICITADA = "fecha_solicitada"
    }
}
