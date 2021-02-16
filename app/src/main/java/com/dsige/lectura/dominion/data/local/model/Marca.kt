package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Marca {
    @PrimaryKey
    var marcaMedidorId: Int = 0
    var nombre: String = ""
}