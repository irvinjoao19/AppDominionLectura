package com.dsige.lectura.dominion.data.module

import com.dsige.lectura.dominion.ui.activities.*
import com.dsige.lectura.dominion.ui.activities.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Main::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindSuministroActity(): SuministroActivity

    @ContributesAndroidInjector
    internal abstract fun bindPendingLocationMapsActivity(): PendingLocationMapsActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Client::class])
    internal abstract fun bindBigClientsActivity(): BigClientsActivity

    @ContributesAndroidInjector
    internal abstract fun bindFormLecturaActity(): FormLecturaActivity

    @ContributesAndroidInjector
    internal abstract fun bindFormCorteActity(): FormCorteActivity

    @ContributesAndroidInjector
    internal abstract fun bindFormReconexionActity(): FormReconexionActivity

    @ContributesAndroidInjector
    internal abstract fun bindReconexionFirmActivity(): ReconexionFirmActivity

    @ContributesAndroidInjector
    internal abstract fun bindFirmActivity(): FirmActivity

    @ContributesAndroidInjector
    internal abstract fun bindPhotoActity(): PhotoActivity
}