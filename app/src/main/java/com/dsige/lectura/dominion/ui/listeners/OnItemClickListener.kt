package com.dsige.lectura.dominion.ui.listeners

import android.view.View
import com.dsige.lectura.dominion.data.local.model.*

interface OnItemClickListener {

    interface PhotoListener {
        fun onItemClick(f: Photo, view: View, position: Int)
    }

    interface MotivoListener {
        fun onItemClick(m: Motivo, view: View, position: Int)
    }

    interface DetalleGrupoListener {
        fun onItemClick(d: DetalleGrupo, view: View, position: Int)
    }

    interface LecturaListener {
        fun onItemClick(s: SuministroLectura, v: View, position: Int)
    }

    interface ReconexionListener {
        fun onItemClick(s: SuministroReconexion, v: View, position: Int)
    }


    interface CorteListener {
        fun onItemClick(s: SuministroCortes, v: View, position: Int)
    }

    interface MenuListener {
        fun onItemClick(m: MenuPrincipal, v: View, position: Int)
    }

    interface ServicesListener {
        fun onItemClick(s: Servicio, v: View, position: Int)
    }

//    interface FormatoListener {
//        fun onItemClick(f: Formato, v: View, position: Int)
//    }

    interface ClientesListener {
        fun onItemClick(c: GrandesClientes, v: View, position: Int)
    }

    interface MarcaListener {
        fun onItemClick(m: Marca, v: View, position: Int)
    }

}