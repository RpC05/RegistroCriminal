package com.example.registrocriminal

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.registrocriminal.databinding.ListItemCrimenBinding
import com.example.registrocriminal.databinding.ListItemCrimenMayorBinding

import android.view.View

class CrimenHolder(
    private val binding: ListItemCrimenBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun enlazar(crimen: Crimen) {
        val formatter = java.text.SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", java.util.Locale.getDefault())
        binding.tituloCrimen.text = crimen.titulo
        binding.fechaCrimen.text = formatter.format(crimen.fecha)
        binding.imvResuelto.visibility = if (crimen.resuelto) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.root.setOnClickListener {
            Toast.makeText(binding.root.context, "${crimen.titulo}", Toast.LENGTH_LONG).show()
        }
    }
}

class CrimenMayorHolder(
    private val binding: ListItemCrimenMayorBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun enlazar(crimen: Crimen) {
        val formatter = java.text.SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", java.util.Locale.getDefault())
        binding.tituloCrimen.text = crimen.titulo
        binding.fechaCrimen.text = formatter.format(crimen.fecha)
        binding.root.setOnClickListener {
            Toast.makeText(binding.root.context, "${crimen.titulo}", Toast.LENGTH_LONG).show()
        }
        binding.botonPolicia.setOnClickListener {
            Toast.makeText(binding.root.context, "Llamando a la policía para ${crimen.titulo}", Toast.LENGTH_SHORT).show()
        }
    }
}

class CrimenAdapter(
    private val crimenes: List<Crimen>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (crimenes[position].crimenMayor) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val binding = ListItemCrimenMayorBinding.inflate(inflater, parent, false)
            CrimenMayorHolder(binding)
        } else {
            val binding = ListItemCrimenBinding.inflate(inflater, parent, false)
            CrimenHolder(binding)
        }
    }

    override fun getItemCount() = crimenes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val crimen = crimenes[position]
        when (holder) {
            is CrimenHolder -> holder.enlazar(crimen)
            is CrimenMayorHolder -> holder.enlazar(crimen)
        }
    }
}