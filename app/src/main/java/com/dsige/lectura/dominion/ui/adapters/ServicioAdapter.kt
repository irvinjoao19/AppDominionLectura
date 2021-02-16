package com.dsige.lectura.dominion.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Servicio
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_menu.view.*

class ServicioAdapter(private var listener: OnItemClickListener.ServicesListener) :
    RecyclerView.Adapter<ServicioAdapter.ViewHolder>() {

    private var servicios = emptyList<Servicio>()

    fun addItems(list: List<Servicio>) {
        servicios = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(servicios[position], listener)
    }

    override fun getItemCount(): Int {
        return servicios.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Servicio, listener: OnItemClickListener.ServicesListener) =
            with(itemView) {
                when (s.nombre_servicio) {
                    "Lectura" -> {
                        imageViewPhoto.setImageResource(R.drawable.ic_lectura)
                        imageViewPhoto.badgeValue = s.size
                        textViewTitulo.text = s.nombre_servicio
                    }
                    "Relectura" -> {
                        imageViewPhoto.badgeValue = s.size
                        imageViewPhoto.setImageResource(R.drawable.ic_relectura)
                        textViewTitulo.text = s.nombre_servicio
                    }
                    "Cortes" -> {
                        imageViewPhoto.badgeValue = s.size
                        imageViewPhoto.setImageResource(R.drawable.ic_cortes)
                        textViewTitulo.text = s.nombre_servicio
                    }
                    "Reconexiones" -> {
                        imageViewPhoto.badgeValue = s.size
                        imageViewPhoto.setImageResource(R.drawable.ic_reconexiones)
                        textViewTitulo.text = s.nombre_servicio
                    }
//                    "Distribucion" -> {
//                        imageViewPhoto.badgeValue = s.size
//                        imageViewPhoto.setImageResource(R.drawable.ic_reparto)
//                        textViewTitulo.text = s.nombre_servicio
//                    }
                    "Grandes Clientes" -> {
                        imageViewPhoto.badgeValue = s.size
                        imageViewPhoto.setImageResource(R.drawable.ic_grandes_clientes)
                        textViewTitulo.text = s.nombre_servicio
                    }
                    else -> textViewTitulo.text = s.nombre_servicio
                }
                itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            }
    }
}