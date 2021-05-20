package com.dsige.lectura.dominion.data.module

import com.dsige.lectura.dominion.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

abstract class FragmentBindingModule {

    @Module
    abstract class Main {
        @ContributesAndroidInjector
        internal abstract fun providMainFragment(): MainFragment

        @ContributesAndroidInjector
        internal abstract fun providSendFragment(): SendFragment

        @ContributesAndroidInjector
        internal abstract fun providRecuperacionFragment(): RecuperacionFragment
    }

    @Module
    abstract class Client {
        @ContributesAndroidInjector
        internal abstract fun providGeneralClientFragment(): GeneralClientFragment

        @ContributesAndroidInjector
        internal abstract fun providFileFragment(): FileFragment
    }
}