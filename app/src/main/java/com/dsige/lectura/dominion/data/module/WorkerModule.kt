package com.dsige.lectura.dominion.data.module

import com.dsige.lectura.dominion.data.workManager.ChildWorkerFactory
import com.dsige.lectura.dominion.data.workManager.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(GpsWork::class)
    internal abstract fun bindGpsWork(gpsWork: GpsWork.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(LecturaWork::class)
    internal abstract fun bindLecturaWork(lecturaWork: LecturaWork.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(BatteryWork::class)
    internal abstract fun bindBatteryWork(batteryWork: BatteryWork.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(PhotosWork::class)
    internal abstract fun bindPhotosWork(photosWork: PhotosWork.Factory): ChildWorkerFactory
}