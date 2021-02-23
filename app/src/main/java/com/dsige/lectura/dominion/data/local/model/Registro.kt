package com.dsige.lectura.dominion.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Registro {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var iD_Registro: Int = 0
    var iD_Operario: Int = 0
    var iD_Suministro: Int =
        0  // TODO --> suministro_medidor en tipo 6 // EN REPARTO ID_OOBSERVACION
    var suministro_Numero: Int = 0
    var iD_TipoLectura: Int = 0
    var registro_Fecha_SQLITE: String = ""
    var registro_Latitud: String = ""
    var registro_Longitud: String = ""
    var registro_Lectura: String = ""
    var registro_Confirmar_Lectura: String = ""
    var registro_Observacion: String = ""
    var grupo_Incidencia_Codigo: String = "0"
    var grupoIndicenciaCodigoNombre: String = ""
    var registro_TieneFoto: String = ""
    var registro_TipoProceso: String = ""
    var fecha_Sincronizacion_Android: String = ""
    var registro_Constancia: String = ""
    var registro_Desplaza: String = ""
    var codigo_Resultado: String = ""
    var tipo: Int = 0
    var orden: Int = 0
    var estado: Int = 0
    var horaActa: String = ""
    var suministroCliente: String = ""
    var suministroDireccion: String = ""
    var lecturaManual: Int = 0
    var motivoId: Int = 0
    var parentId: Int = 0
    var parentIdNombre: String = ""
    var causaNombre :String = ""
    var artefactoNombre :String = ""
    var motivoNombre: String = ""

    @Ignore
    var photos: List<Photo>? = null


    //nuevo
    var pidePhoto: String = ""
    var pideLectura: String = ""

}