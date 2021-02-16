package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class SuministroReconexion {

    @PrimaryKey
    var iD_Suministro: Int = 0
    var suministro_Numero: String = ""
    var suministro_Medidor: String = ""
    var suministro_Cliente: String = ""
    var suministro_Direccion: String = ""
    var suministro_UnidadLectura: String = ""
    var suministro_TipoProceso: String = ""
    var suministro_LecturaMinima: String = ""
    var suministro_LecturaMaxima: String = ""
    var suministro_Fecha_Reg_Movil: String = ""
    var suministro_UltimoMes: String = ""
    var suministro_NoCortar: Int = 0
    var estado: Int = 0
    var suministroOperario_Orden: Int = 0
    var orden: Int = 0
    var activo: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var telefono: String = ""
    var nota: String = ""
    var fechaAsignacion: String = ""
    var primeraReconexion: String = ""
    var avisoCorte: String = ""
}