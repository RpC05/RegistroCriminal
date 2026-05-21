package com.example.registrocriminal

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.registrocriminal.databinding.ListItemCrimenBinding

class CrimenHolder(
    private val binding: ListItemCrimenBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun enlazar(crimen: Crimen) {
        binding.apply {
            tituloCrimen.text = crimen.titulo
            fechaCrimen.text = crimen.fecha.toString()

            root.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "${crimen.titulo}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class CrimenAdapter(
    private val crimenes: List<Crimen>
) : RecyclerView.Adapter<CrimenHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimenHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimenBinding.inflate(inflater, parent, false)
        return CrimenHolder(binding)
    }

    override fun getItemCount() = crimenes.size

    override fun onBindViewHolder(holder: CrimenHolder, position: Int) {
        val crimen = crimenes[position]
        holder.enlazar(crimen)
    }
}