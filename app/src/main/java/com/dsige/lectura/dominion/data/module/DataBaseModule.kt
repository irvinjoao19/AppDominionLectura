package com.dsige.lectura.dominion.data.module

import android.app.Application
import androidx.room.Room
import com.dsige.lectura.dominion.data.local.AppDataBase
import com.dsige.lectura.dominion.data.local.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(application: Application): AppDataBase {
        if (AppDataBase.INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (AppDataBase.INSTANCE == null) {
                    AppDataBase.INSTANCE = Room.databaseBuilder(
                        application.applicationContext,
                        AppDataBase::class.java, AppDataBase.DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return AppDataBase.INSTANCE!!
    }

    @Provides
    internal fun provideUsuarioDao(appDataBase: AppDataBase): UsuarioDao {
        return appDataBase.usuarioDao()
    }

    @Provides
    internal fun provideServicioDao(appDataBase: AppDataBase): ServicioDao {
        return appDataBase.servicioDao()
    }

    @Provides
    internal fun provideLecturaDao(appDataBase: AppDataBase): LecturaDao {
        return appDataBase.lecturaDao()
    }

    @Provides
    internal fun provideCorteDao(appDataBase: AppDataBase): CorteDao {
        return appDataBase.corteDao()
    }

    @Provides
    internal fun provideReconexionDao(appDataBase: AppDataBase): ReconexionDao {
        return appDataBase.reconexionDao()
    }

    @Provides
    internal fun provideRegistroDao(appDataBase: AppDataBase): RegistroDao {
        return appDataBase.registroDao()
    }

    @Provides
    internal fun providePhotoDao(appDataBase: AppDataBase): PhotoDao {
        return appDataBase.photoDao()
    }

    @Provides
    internal fun provideMotivoDao(appDataBase: AppDataBase): MotivoDao {
        return appDataBase.motivoDao()
    }

    @Provides
    internal fun provideDetalleGrupoDao(appDataBase: AppDataBase): DetalleGrupoDao {
        return appDataBase.detalleGrupoDao()
    }

    @Provides
    internal fun provideGrandesClientesDao(appDataBase: AppDataBase): GrandesClientesDao {
        return appDataBase.grandesClientesDao()
    }

    @Provides
    internal fun provideMarcaDao(appDataBase: AppDataBase): MarcaDao {
        return appDataBase.marcaDao()
    }

    @Provides
    internal fun provideOperarioGpsDao(appDataBase: AppDataBase): OperarioGpsDao {
        return appDataBase.operarioGpsDao()
    }

}