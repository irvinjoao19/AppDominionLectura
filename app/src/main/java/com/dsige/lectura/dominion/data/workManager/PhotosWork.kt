package com.dsige.lectura.dominion.data.workManager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.lectura.dominion.data.local.repository.AppRepository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Provider

class PhotosWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        sendPhoto(context)
        return Result.success()
    }

    class Factory @Inject constructor(private val repository: Provider<AppRepository>) :
        ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return PhotosWork(appContext, params, repository.get())
        }
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: String) {}
                override fun onError(t: Throwable) {}
                override fun onComplete() {}
            })
    }
}