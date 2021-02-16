package com.dsige.lectura.dominion.helper

open class Mensaje {

    var codigoBase: Int = 0
    var codigoRetorno: Int = 0
    var codigoAlterno  : Int = 0
    var codigo: Int = 0
    var mensaje: String = ""
    var detalle: List<MensajeDetalle> = ArrayList()
}