package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class DetalleGrupo {

    @PrimaryKey
    var id: Int = 0
    var iD_DetalleGrupo: Int = 0
    var grupo: String = ""
    var codigo: String = ""
    var descripcion: String = ""
    var abreviatura: String = ""
    var estado: String = ""
    var descripcionGrupo: String = ""
    var pideFoto: String = ""
    var noPideFoto: String = ""
    var pideLectura: String = ""
    var id_Servicio: Int = 0
    var parentId: Int = 0
    var ubicaMedidor: Int = 0
}