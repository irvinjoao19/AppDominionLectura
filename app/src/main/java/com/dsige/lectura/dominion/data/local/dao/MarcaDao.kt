package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.Marca

@Dao
interface MarcaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarcaTask(c: Marca)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarcaListTask(c: List<Marca>)

    @Update
    fun updateMarcaTask(vararg c: Marca)

    @Delete
    fun deleteMarcaTask(c: Marca)

    @Query("SELECT * FROM Marca")
    fun getMarcas(): LiveData<List<Marca>>

    @Query("DELETE FROM Marca")
    fun deleteAll()

}