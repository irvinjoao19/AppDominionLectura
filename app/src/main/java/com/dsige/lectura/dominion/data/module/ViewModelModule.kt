package com.dsige.lectura.dominion.data.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dsige.lectura.dominion.data.viewModel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UsuarioViewModel::class)
    internal abstract fun bindUserViewModel(usuarioViewModel: UsuarioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SuministroViewModel::class)
    internal abstract fun bindSuministroViewModel(suministroViewModel: SuministroViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClienteViewModel::class)
    internal abstract fun bindClienteViewModel(clienteViewModel: ClienteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SendViewModel::class)
    internal abstract fun bindSendViewModel(sendViewModel: SendViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}