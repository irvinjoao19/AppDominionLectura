package com.dsige.lectura.dominion.data.local.dao

import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.Servicio

@Dao
interface ServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServicioTask(c: Servicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServicioListTask(c: List<Servicio>)

    @Update
    fun updateServicioTask(vararg c: Servicio)

    @Delete
    fun deleteServicioTask(c: Servicio)

    @Query("SELECT * FROM Servicio WHERE estado= 1 ")
    fun getServicioTask(): List<Servicio>

    @Query("DELETE FROM Servicio")
    fun deleteAll()

}