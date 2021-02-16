package com.dsige.lectura.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Motivo
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class MotivoAdapter(private val listener: OnItemClickListener.MotivoListener) :
        RecyclerView.Adapter<MotivoAdapter.ViewHolder>() {

    private var motivos = emptyList<Motivo>()

    fun addItems(list: List<Motivo>) {
        motivos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(motivos[position], listener)
    }

    override fun getItemCount(): Int {
        return motivos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: Motivo, listener: OnItemClickListener.MotivoListener) = with(itemView) {
            textViewNombre.text = m.descripcion
            itemView.setOnClickListener { view -> listener.onItemClick(m, view, adapterPosition) }
        }
    }
}