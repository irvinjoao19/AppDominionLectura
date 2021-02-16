package com.dsige.lectura.dominion.data.workManager

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.lectura.dominion.data.local.model.Registro
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
import javax.inject.Provider

class LecturaWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        sendFiles(context)
        return Result.success()
    }

    class Factory @Inject constructor(private val repository: Provider<AppRepository>) :
        ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return LecturaWork(appContext, params, repository.get())
        }
    }

    private fun sendFiles(context: Context) {
        val files = roomRepository.getPhotoTaskFiles()
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
                override fun onError(t: Throwable) {}
                override fun onComplete() {
                    sendSuministro()
                }
            })
    }

    private fun sendSuministro() {
        val register: Observable<List<Registro>> = roomRepository.getRegistrosTask()
        register.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val json = Gson().toJson(a)
                Log.i("TAG", json)
                val body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
                Observable.zip(
                    Observable.just(a),
                    roomRepository.sendRegistro(body), { _, m -> m })
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(t: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: Mensaje) {
                    updateEnableRegistro(t)
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
                override fun onComplete() {}
            })
    }
}