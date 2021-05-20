package com.dsige.lectura.dominion.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dsige.lectura.dominion.R
import com.dsige.lectura.dominion.data.local.model.Photo
import com.dsige.lectura.dominion.helper.Util
import com.dsige.lectura.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_recupera_foto.view.*
import java.io.File

class RecuperaPhotoAdapter(private var listener: OnItemClickListener.PhotoListener) :
    RecyclerView.Adapter<RecuperaPhotoAdapter.ViewHolder>() {

    private var photos = emptyList<Photo>()

    fun addItems(list: List<Photo>) {
        photos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_recupera_foto, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position], listener)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(photo: Photo, listener: OnItemClickListener.PhotoListener) = with(itemView) {
            val f = File(Util.getFolder(itemView.context), photo.rutaFoto)
            if (f.exists()) {
                imgPhoto.setImageResource(R.drawable.ic_ok)
            } else {
                imgPhoto.setImageResource(R.drawable.ic_error)
            }
            textView1.text = photo.iD_Suministro.toString()
            textView2.text = photo.rutaFoto
            itemView.setOnClickListener { v -> listener.onItemClick(photo, v, adapterPosition) }
        }
    }
}