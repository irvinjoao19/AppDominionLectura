package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Motivo {

    @PrimaryKey
    var motivoId: Int = 0
    var grupo: String = ""
    var codigo: Int = 0
    var descripcion: String = ""
}