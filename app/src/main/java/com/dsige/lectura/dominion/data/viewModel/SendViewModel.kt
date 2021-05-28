package com.dsige.lectura.dominion.data.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.lectura.dominion.data.local.model.*
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SendViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()
    val servicios: MutableLiveData<List<Servicio>> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun sendPhoto(context: Context) {
        val photos = roomRepository.getAllPhotos(context)
        photos.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { i ->
                val b = MultipartBody.Builder()
                b.setType(MultipartBody.FORM)
                val file = File(i)
                if (file.exists()) {
                    b.addFormDataPart(
                        "files",
                        file.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    )
                }
                val body = b.build()
                Observable.zip(
                    Observable.just(i), roomRepository.sendPhotos(body),
                    { _, t ->
                        t
                    })
            }
        }.subscribeOn(Schedulers.computation())
            .delay(600, TimeUnit.MILLISECONDS)
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
                    mensajeSuccess.postValue("Fotos Enviados")
                }
            })
    }

    fun sendFiles(context: Context) {
        val files = roomRepository.getPhotoTaskFiles(context)
        files.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val b = MultipartBody.Builder()
                b.setType(MultipartBody.FORM)
                val file = File(Util.getFolder(context), a.rutaFoto)
                if (file.exists()) {
                    b.addFormDataPart(
                        "files", file.name,
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"), file
                        )
                    )
                }
                val body = b.build()
                Observable.zip(
                    Observable.just(a), roomRepository.sendPhotos(body),
                    { _, t ->
                        t
                    })
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: String) {}
                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        mensajeError.postValue(t.message())
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Ok"
                }
            })
    }

      fun sendSuministro() {
        val register: Observable<List<Registro>> = roomRepository.getRegistrosTask()
        register.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val json = Gson().toJson(a)
//                Log.i("TAG", json)
                val body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
                Observable.zip(
                    Observable.just(a),
                    roomRepository.sendRegistro(body), { _, m -> m })
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    mensajeSuccess.value = "Registros Enviados"
                }

                override fun onNext(t: Mensaje) {
                    updateEnableRegistro(t)
                }

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }
            })
    }

    private fun updateEnableRegistro(t: Mensaje) {
        roomRepository.updateEnableRegistro(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    mensajeSuccess.value = t.mensaje
                }
            })
    }

    fun getRegistros(): LiveData<List<Registro>> {
        return roomRepository.getRegistros()
    }

    fun getPhotos(): LiveData<List<Photo>> {
        return roomRepository.getPhotos()
    }
}