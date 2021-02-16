package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.Motivo

@Dao
interface MotivoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMotivoTask(c: Motivo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMotivoListTask(c: List<Motivo>)

    @Update
    fun updateMotivoTask(vararg c: Motivo)

    @Delete
    fun deleteMotivoTask(c: Motivo)

    @Query("SELECT * FROM Motivo")
    fun getMotivos(): LiveData<List<Motivo>>

    @Query("DELETE FROM Motivo")
    fun deleteAll()

}