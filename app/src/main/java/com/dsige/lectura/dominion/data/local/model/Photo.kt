package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Photo {

    @PrimaryKey(autoGenerate = true)
    var iD_Foto: Int = 0
    var conformidad: Int =
        0 // TODO se usara para mostrar la foto de acta de conformidad 0 = normal , 1 = acta , 2 = firma
    var iD_Suministro: Int = 0
    var rutaFoto: String = ""
    var fecha_Sincronizacion_Android: String = ""
    var tipo: Int = 0
    var estado: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var firm: Int = 0
    var tipoFirma: String = ""
}