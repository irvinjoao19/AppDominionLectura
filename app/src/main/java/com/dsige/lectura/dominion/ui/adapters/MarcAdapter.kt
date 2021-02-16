package com.dsige.lectura.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Marca
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class MarcAdapter(private val listener: OnItemClickListener.MarcaListener) :
        RecyclerView.Adapter<MarcAdapter.ViewHolder>() {

    private var marca = emptyList<Marca>()

    fun addItems(list: List<Marca>) {
        marca = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(marca[position], listener)
    }

    override fun getItemCount(): Int {
        return marca.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: Marca, listener: OnItemClickListener.MarcaListener) = with(itemView) {
            textViewNombre.text = String.format("%s", m.nombre)
            itemView.setOnClickListener { view -> listener.onItemClick(m, view, adapterPosition) }
        }
    }
}