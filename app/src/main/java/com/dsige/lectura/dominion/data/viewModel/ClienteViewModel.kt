package com.dsige.lectura.dominion.data.viewModel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.lectura.dominion.data.local.model.GrandesClientes
import com.dsige.lectura.dominion.data.local.model.Marca
import com.dsige.lectura.dominion.data.local.model.Usuario
import com.dsige.lectura.dominion.data.local.repository.ApiError
import com.dsige.lectura.dominion.data.local.repository.AppRepository
import com.dsige.lectura.dominion.helper.Mensaje
import com.dsige.lectura.dominion.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class ClienteViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()

    val user: LiveData<Usuario>
        get() = roomRepository.getUsuario()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getClienteById(clienteId: Int): LiveData<GrandesClientes> {
        return roomRepository.getClienteById(clienteId)
    }


    fun validateCliente(c: GrandesClientes, type: Int, format: Int): Boolean {
        if (format == 1) {
            if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
                mensajeError.value = "Presionar Validar EMR"
                return false
            }

            if (c.clientePermiteAcceso.isEmpty()) {
                mensajeError.value = "Seleccione tipo de cliente"
                return false
            }
        } else {
            for (x in 1..type) {
                when (x) {
                    1 -> {
                        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
                            mensajeError.value = "Presionar Validar EMR"
                            return false
                        }

                        if (c.clientePermiteAcceso.isEmpty()) {
                            mensajeError.value = "Seleccione tipo de cliente"
                            return false
                        }

                        if (c.clientePermiteAcceso == "NO") {
                            return if (c.fotoConstanciaPermiteAcceso.isEmpty()) {
                                mensajeError.value = "Tomar foto del cliente no permite acceso"
                                false
                            } else {
                                true
                            }
                        }
                    }
                    2 -> {
                        if (c.porMezclaExplosiva.isEmpty()) {
                            mensajeError.value = "Ingresar Mezcla explosiva."
                            return false
                        }
                    }
                    3 -> {
                        if (c.porMezclaExplosiva != "0") {
                            if (c.fotoPorMezclaExplosiva.isEmpty()) {
                                mensajeError.value = "Ingresar foto por mezcla explosiva."
                                return false
                            }
                        }
                        if (c.tomaLectura.isEmpty()) {
                            mensajeError.value = "Ingresar Lectura."
                            return false
                        }
                    }
                    4 -> {
                        if (c.fotoTomaLectura.isEmpty()) {
                            mensajeError.value = "Ingresar foto por mezcla explosiva."
                            return false
                        }
                        if (c.vManoPresionEntrada.isEmpty()) {
                            mensajeError.value = "Ingresar Valor manometro presión entrada."
                            return false
                        }
                    }
                    5 -> {
                        if (c.fotovManoPresionEntrada.isEmpty()) {
                            mensajeError.value = "Ingresar foto valor manometro presión entrada."
                            return false
                        }
                        if (c.existeMedidor.isEmpty()) {
                            mensajeError.value = "Seleccione Existe Medidor"
                            return false
                        }
                        if (c.existeMedidor != "NO") {
                            if (c.vVolumenSCorreMedidor.isEmpty()) {
                                mensajeError.value =
                                    "Ingresar valor de volumen sin corregir del medidor."
                                return false
                            }
                        }
                    }
                    6 -> {
                        if (c.existeMedidor != "NO") {
                            if (c.vVolumenSCorreMedidor.isEmpty()) {
                                mensajeError.value =
                                    "Ingresar valor de volumen sin corregir del medidor."
                                return false
                            }
                            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                                mensajeError.value =
                                    "Ingresar foto de valor de volumen sin corregir del medidor."
                                return false
                            }
                        }

                        if (c.marcaCorrectorId == 0) {
                            mensajeError.value = "Ingresar Marca de corrector."
                            return false
                        }
                    }
                    7 -> {
                        if (c.fotoBateriaDescargada.isEmpty()) {
                            mensajeError.value = "Tomar foto Bateria Descargada."
                            return false
                        }
                    }
                    8 -> {
                        if (c.fotoDisplayMalogrado.isEmpty()) {
                            mensajeError.value = "Tomar foto Display Malogrado."
                            return false
                        }

                        if (c.vVolumenSCorreUC.isEmpty()) {
                            mensajeError.value =
                                "Ingresar valor de volumen sin corregir de la unidad correctora."
                            return false
                        }

                        if (c.existeMedidor != "NO") {
                            val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
                            if (a <= 0 || a > 10) {
                                if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                                    mensajeError.value = "Confirmar lectura"
                                    return false
                                }
                            }
                        }
                    }
                    9 -> {
                        if (c.vVolumenRegUC.isEmpty()) {
                            mensajeError.value =
                                "Ingresar valor de volumen registrador de la unidad correctora."
                            return false
                        }
                    }
                    10 -> {
                        if (c.fotovVolumenSCorreUC.isEmpty()) {
                            mensajeError.value =
                                "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                            return false
                        }

                        if (c.vPresionMedicionUC.isEmpty()) {
                            mensajeError.value = "Ingresar valor de la presión de medición de uc."
                            return false
                        }

                    }
                    11 -> {
                        if (c.fotovPresionMedicionUC.isEmpty()) {
                            mensajeError.value =
                                "Ingresar foto valor de la presión de medición de uc."
                            return false
                        }

                        if (c.vTemperaturaMedicionUC.isEmpty()) {
                            mensajeError.value =
                                "Ingresar valor de la temperatura de medicion de la uc."
                            return false
                        }
                    }
                    12 -> {
                        if (c.marcaCorrectorId != 1) {
                            if (c.fotovTemperaturaMedicionUC.isEmpty()) {
                                mensajeError.value =
                                    "Tomar foto de la temperatura de medicion de la uc."
                                return false
                            }
                        }

                        if (c.tiempoVidaBateria.isEmpty()) {
                            mensajeError.value = "Ingresar tiempo de vida de la bateria."
                            return false
                        }
                    }
                    13 -> {
                        if (c.marcaCorrectorId != 1) {
                            if (c.fotoTiempoVidaBateria.isEmpty()) {
                                mensajeError.value = "Tomar foto tiempo de vida de la bateria."
                                return false
                            }
                        }
                    }
                    14 -> {
                        if (c.fotoPanomarica.isEmpty()) {
                            mensajeError.value = "Ingresar foto panoramica."
                            return false
                        }

                        if (c.tieneGabinete.isEmpty()) {
                            mensajeError.value = "Ingresar tiene gabinete de temometria."
                            return false
                        }
                    }
                    15 -> {
                        if (c.tieneGabinete == "SI") {
                            if (c.foroSitieneGabinete.isEmpty()) {
                                mensajeError.value = "Tomar Foto del Cabinete."
                                return false
                            }
                        }
                        if (c.presenteCliente.isEmpty()) {
                            mensajeError.value = "Ingresar si presenta cliente."
                            return false
                        }
                        if (c.presenteCliente == "SI") {
                            if (c.contactoCliente.isEmpty()) {
                                mensajeError.value = "Ingresar nombre del cliente"
                                return false
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    fun updateCliente(c: GrandesClientes, mensaje: String, context: Context) {
        roomRepository.updateClientes(c)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }

                override fun onComplete() {
                    if (mensaje == "Cliente Actualizado") {
                        sendFileCliente(c.clienteId, mensaje, context)
                    } else {
                        mensajeSuccess.value = mensaje
                    }
                }
            })
    }

    private fun sendFileCliente(clienteId: Int, mensaje: String, context: Context) {
        val files = roomRepository.getClienteFiles(clienteId)
        files.flatMap { obs ->
            Observable.fromIterable(obs).flatMap { a ->
                val b = MultipartBody.Builder()
                if (a.isNotEmpty()) {
                    val file = File(Util.getFolder(context), a)
                    if (file.exists()) {
                        b.addFormDataPart(
                            "files", file.name,
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"), file
                            )
                        )
                    }
                }
                b.setType(MultipartBody.FORM)
                val body = b.build()
                Observable.zip(
                    Observable.just(a), roomRepository.sendPhotos(body), { _, mensaje ->
                        mensaje
                    })
            }
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: String) {}

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }

                override fun onComplete() {
                    sendCliente(clienteId, mensaje)
                }
            })
    }

    private fun sendCliente(clienteId: Int, mensaje: String) {
        val auditorias = roomRepository.getClienteTaskById(clienteId)
        auditorias.flatMap { c ->
            val json = Gson().toJson(c)
//            Log.i("TAG", json)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            Observable.zip(Observable.just(c), roomRepository.sendCliente(body), { _, mensaje ->
                mensaje
            })
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: Mensaje) {}
                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }

                override fun onComplete() {
                    mensajeSuccess.postValue(mensaje)
                }
            })
    }

    fun getMarca(): LiveData<List<Marca>> {
        return roomRepository.getMarca()
    }

    fun generarArchivo(titleImg: String, context: Context, data: Intent): Observable<String> {
        return Util.getFolderAdjunto(titleImg, context, data)
    }

    fun verificateFile(c: GrandesClientes) {
        roomRepository.getVerificateFile(c.clienteId, Util.getFecha())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: Mensaje) {
                    closeFileClienteById(c.clienteId, t)
                }

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }
            })
    }

    private fun closeFileClienteById(id: Int, t: Mensaje) {
        roomRepository.closeFileClienteById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    mensajeSuccess.postValue(t.mensaje)
                }
            })
    }
}