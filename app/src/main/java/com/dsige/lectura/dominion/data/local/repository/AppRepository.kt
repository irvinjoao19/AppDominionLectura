package com.dsige.lectura.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.dsige.lectura.dominion.data.local.model.*
import com.dsige.lectura.dominion.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody

interface AppRepository {

    //usuario
    fun getUsuario(): LiveData<Usuario>
    fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String, token: String
    ): Observable<Usuario>

    fun getUsuarioId(): Observable<Int>

    fun insertUsuario(u: Usuario): Completable
    fun deleteSesion(): Completable
    fun deleteSync(): Completable
    fun getSync(u: Int, v: String): Observable<Sync>
    fun saveSync(s: Sync): Completable


    //servicio
    fun getServices(): Observable<List<Servicio>>
    fun getTipoLectura(): Observable<IntArray>

    fun getSuministroLectura(
        estado: Int, activo: Int, observadas: Int
    ): LiveData<List<SuministroLectura>>


    fun getSuministroCortes(estado: Int, i: Int): LiveData<List<SuministroCortes>>
    fun getSuministroReconexion(estado: Int, i: Int): LiveData<List<SuministroReconexion>>
    fun getSuministroReclamos(e: String, i: Int): LiveData<List<SuministroLectura>>

    fun getRegistro(orden: Int, tipo: Int, recuperada: Int): LiveData<Registro>
    fun getMotivos(): LiveData<List<Motivo>>
    fun getDetalleGrupoByLectura(estado: Int): LiveData<List<DetalleGrupo>>
    fun getDetalleGrupoByFirstLectura(lecturaEstado: Int): Observable<DetalleGrupo>
    fun getDetalleGrupoByMotivo(estado: Int, s: String): LiveData<List<DetalleGrupo>>
    fun getDetalleGrupoByMotivoTask(estado: Int, s: String): Observable<DetalleGrupo>
    fun getDetalleGrupoByParentId(i: Int): LiveData<List<DetalleGrupo>>
    fun getDetalleGrupoById(id: Int): Observable<DetalleGrupo>

    //gps
    fun insertGps(e: OperarioGps): Completable
    fun getSendGps(): Observable<List<OperarioGps>>
    fun saveOperarioGps(e: OperarioGps): Observable<Mensaje>
    fun updateEnabledGps(t: Mensaje): Completable

    //batterry
    fun insertBattery(e: OperarioBattery): Completable
    fun getSendBattery(): Observable<List<OperarioBattery>>
    fun saveOperarioBattery(e: OperarioBattery): Observable<Mensaje>
    fun updateEnabledBattery(t: Mensaje): Completable

    //Grandes Clientes
    fun getClienteById(clienteId: Int): LiveData<GrandesClientes>
    fun getClienteTaskById(clienteId: Int): Observable<GrandesClientes>
    fun updateClientes(c: GrandesClientes): Completable
    fun getMarca(): LiveData<List<Marca>>
    fun getClienteFiles(clienteId: Int): Observable<List<String>>
    fun sendPhotos(body: RequestBody): Observable<String>
    fun sendCliente(body: RequestBody): Observable<Mensaje>
    fun getVerificateFile(clienteId: Int, fecha: String): Observable<Mensaje>
    fun closeFileClienteById(id: Int): Completable
    fun getGrandesClientes(): LiveData<List<GrandesClientes>>

    //Lectura
    fun suministroLecturaByOrden(orden: Int): Observable<SuministroLectura>
    fun getRegistroBySuministroTask(id: Int): Observable<Registro>
    fun getSuministroLeft(estado: Int, orden: Int, suministroOrden: Int): Observable<Int>
    fun getSuministroRight(estado: Int, orden: Int, suministroOrden: Int): Observable<Int>
    fun getRegistroBySuministro(id: Int): LiveData<Registro>
    fun insertRegistro(r: Registro): Completable
    fun updateRegistro(id: Int, tipo: Int, estado: Int): Completable


    //Photo
    fun getPhotoAllBySuministro(id: Int, tipo: Int, i: Int): LiveData<List<Photo>>
    fun deletePhoto(p: Photo, context: Context): Completable
    fun insertPhoto(p: Photo): Completable
    fun getPhotoTaskFile(id: Int): Observable<List<Photo>>

    //Corte y Reconexión
    fun getVerificateCorte(s: String): Observable<Mensaje>
    fun getSuministroCorteByOrdenTask(orden: Int): Observable<SuministroCortes>
    fun getSuministroReconexionByOrdenTask(orden: Int): Observable<SuministroReconexion>
    fun getPhotoFirm(id: Int): LiveData<List<Photo>>

    //Envio
    fun getRegistroByIdTask(id: Int): Observable<Registro>
    fun sendRegistro(body: RequestBody): Observable<Mensaje>
    fun updateEnableRegistro(t: Mensaje): Completable
    fun getPhotoTaskFiles(context:Context): Observable<List<Photo>>
    fun getRegistrosTask(): Observable<List<Registro>>
    fun getRegistrosLecturasTask(): Observable<List<Registro>>
    fun getRegistros(): LiveData<List<Registro>>
    fun getPhotos(): LiveData<List<Photo>>


    //Mapa
    fun getSuministroLecturaById(id: Int): LiveData<SuministroLectura>
    fun getSuministroCorteById(id: Int): LiveData<SuministroCortes>
    fun getSuministroReconexionById(id: Int): LiveData<SuministroReconexion>

    //Nuevo recuperacion de fotos
    fun getRecoveredPhotos(): Observable<List<Photo>>
    fun getAllPhotos(context: Context): Observable<ArrayList<String>>


}