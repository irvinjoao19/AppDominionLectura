package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Servicio {

    @PrimaryKey
    var id_servicio: Int = 0
    var nombre_servicio: String = ""
    var estado: Int = 0
    var ubicacion: Int = 0
    var size: Int = 0
}
