package com.example.registrocriminal

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val fecha = arguments?.getSerializable("fechaCrimen") as? Date ?: Date()
            val calendar = Calendar.getInstance()
            calendar.time = fecha

            val resultDate: Date = GregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute
            ).time

            setFragmentResult(
                CLAVE_TIEMPO_SOLICITADO,
                bundleOf(CLAVE_TIEMPO_SOLICITADO to resultDate)
            )
        }

        val calendario = Calendar.getInstance()
        val fecha = arguments?.getSerializable("fechaCrimen") as? Date ?: Date()
        calendario.time = fecha
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            listener,
            hora,
            minuto,
            DateFormat.is24HourFormat(requireContext())
        )
    }

    companion object {
        const val CLAVE_TIEMPO_SOLICITADO = "tiempo_solicitado"
    }
}
