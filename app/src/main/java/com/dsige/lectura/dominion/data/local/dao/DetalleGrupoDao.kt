package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.DetalleGrupo

@Dao
interface DetalleGrupoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetalleGrupoTask(c: DetalleGrupo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetalleGrupoListTask(c: List<DetalleGrupo>)

    @Update
    fun updateDetalleGrupoTask(vararg c: DetalleGrupo)

    @Delete
    fun deleteDetalleGrupoTask(c: DetalleGrupo)

    @Query("SELECT * FROM DetalleGrupo")
    fun getDetalleGrupo(): LiveData<List<DetalleGrupo>>

    @Query("DELETE FROM DetalleGrupo")
    fun deleteAll()

    @Query("SELECT * FROM DetalleGrupo WHERE id_Servicio=:e")
    fun getDetalleGrupoByLectura(e: Int): LiveData<List<DetalleGrupo>>

    @Query("SELECT * FROM DetalleGrupo WHERE id_Servicio=:e")
    fun getDetalleGrupoByFirstLectura(e: Int): DetalleGrupo

    @Query("SELECT * FROM DetalleGrupo WHERE id_Servicio=:e AND grupo =:s")
    fun getDetalleGrupoByMotivo(e: Int, s: String): LiveData<List<DetalleGrupo>>

    @Query("SELECT * FROM DetalleGrupo WHERE id_Servicio=:e AND grupo =:s")
    fun getDetalleGrupoByMotivoTask(e: Int, s: String): DetalleGrupo

    @Query("SELECT * FROM DetalleGrupo WHERE parentId=:i")
    fun getDetalleGrupoByParentId(i: Int): LiveData<List<DetalleGrupo>>

    @Query("SELECT * FROM DetalleGrupo WHERE iD_DetalleGrupo =:i")
    fun getDetalleGrupoById(i: Int): DetalleGrupo
}