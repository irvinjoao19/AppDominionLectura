package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.SuministroCortes

@Dao
interface CorteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuministroCortesTask(c: SuministroCortes)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuministroCortesListTask(c: List<SuministroCortes>)

    @Update
    fun updateSuministroCortesTask(vararg c: SuministroCortes)

    @Delete
    fun deleteSuministroCortesTask(c: SuministroCortes)

    @Query("SELECT * FROM SuministroCortes WHERE estado= 1 ")
    fun getSuministroCortesTask(): List<SuministroCortes>

    @Query("DELETE FROM SuministroCortes")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM SuministroCortes WHERE activo = 1")
    fun getSuministroCorteSize(): Int

    @Query("SELECT * FROM SuministroCortes WHERE estado=:e AND activo =:a ORDER BY orden ASC")
    fun getSuministroCortes(e: Int, a: Int): LiveData<List<SuministroCortes>>

    @Query("SELECT orden FROM SuministroCortes WHERE activo = 1  ORDER BY orden ASC")
    fun getSuministroPrimero(): Int

    @Query("SELECT orden FROM SuministroCortes WHERE activo = 1 ORDER BY orden DESC")
    fun getSuministroUltimoActivo(): Int

    @Query("SELECT orden FROM SuministroCortes WHERE orden <:orden AND activo = 1 ORDER BY orden DESC")
    fun getSuministroLeftTask(orden: Int): Int

    @Query("SELECT orden FROM SuministroCortes WHERE activo = 1  ORDER BY orden DESC")
    fun getSuministroUltimo(): Int

    @Query("SELECT orden FROM SuministroCortes WHERE activo = 1 ORDER BY orden ASC")
    fun getSuministroPrimeroActivo(): Int

    @Query("SELECT orden FROM SuministroCortes WHERE orden >:orden AND activo = 1 ORDER BY orden ASC")
    fun getSuministroRightTask(orden: Int): Int

    @Query("UPDATE SuministroCortes SET activo =:i WHERE iD_Suministro =:id")
    fun updateActivoSuministroCortes(id: Int, i: Int)

    @Query("SELECT * FROM SuministroCortes WHERE orden=:orden")
    fun suministroCorteByOrden(orden: Int): SuministroCortes

    @Query("SELECT * FROM SuministroCortes WHERE iD_Suministro =:id")
    fun getSuministroCorteById(id: Int): LiveData<SuministroCortes>
}