package com.dsige.lectura.dominion.data.local.model

open class Sync {
    var servicios: List<Servicio>? = ArrayList()
    //    var parametros: List<Parametro>? = null
    var suministroLecturas: List<SuministroLectura>? = ArrayList()
    var suministroCortes: List<SuministroCortes>? = ArrayList()

    //    var tipoLecturas: List<TipoLectura>? = null
    var detalleGrupos: List<DetalleGrupo>? = ArrayList()
    var suministroReconexiones: List<SuministroReconexion>? = ArrayList()
    var motivos: List<Motivo>? = ArrayList()

    //    var estados: List<Estado>? = null
//    var formatos: List<Formato>? = null
    var clientes: List<GrandesClientes>? = ArrayList()
    var marcas: List<Marca>? = ArrayList()
}