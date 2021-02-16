package com.dsige.lectura.dominion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dsige.lectura.dominion.data.local.dao.*
import com.dsige.lectura.dominion.data.local.model.*

@Database(
    entities = [
        Usuario::class,
        OperarioGps::class,
        Servicio::class,
        SuministroLectura::class,
        SuministroCortes::class,
        SuministroReconexion::class,
        Registro::class,
        Photo::class,
        Motivo::class,
        DetalleGrupo::class,
        GrandesClientes::class,
        Marca::class
    ],
    version = 2, // version 1 en play store
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun servicioDao(): ServicioDao
    abstract fun lecturaDao(): LecturaDao
    abstract fun corteDao(): CorteDao
    abstract fun reconexionDao(): ReconexionDao


    abstract fun registroDao(): RegistroDao
    abstract fun photoDao(): PhotoDao
    abstract fun motivoDao(): MotivoDao
    abstract fun detalleGrupoDao(): DetalleGrupoDao
    abstract fun grandesClientesDao(): GrandesClientesDao
    abstract fun marcaDao(): MarcaDao

    abstract fun operarioGpsDao(): OperarioGpsDao

    companion object {
        @Volatile
        var INSTANCE: AppDataBase? = null
        val DB_NAME = "reparto_db"
    }

    fun getDatabase(context: Context): AppDataBase {
        if (INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "reparto-db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return INSTANCE!!
    }
}