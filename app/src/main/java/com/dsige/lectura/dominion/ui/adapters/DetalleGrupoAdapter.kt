package com.dsige.lectura.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.DetalleGrupo
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class DetalleGrupoAdapter(private val listener: OnItemClickListener.DetalleGrupoListener) :
        RecyclerView.Adapter<DetalleGrupoAdapter.ViewHolder>() {

    private var detalleGrupo = emptyList<DetalleGrupo>()

    fun addItems(list: List<DetalleGrupo>) {
        detalleGrupo = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(detalleGrupo[position], listener)
    }

    override fun getItemCount(): Int {
        return detalleGrupo.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(d: DetalleGrupo, listener: OnItemClickListener.DetalleGrupoListener) = with(itemView) {
            textViewNombre.text = d.descripcion
            itemView.setOnClickListener { view -> listener.onItemClick(d, view, adapterPosition) }
        }
    }
}