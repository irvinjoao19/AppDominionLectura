package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.SuministroCortes
import com.dsige.lectura.dominion.data.local.model.SuministroReconexion

@Dao
interface ReconexionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuministroReconexionTask(c: SuministroReconexion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuministroReconexionListTask(c: List<SuministroReconexion>)

    @Update
    fun updateSuministroReconexionTask(vararg c: SuministroReconexion)

    @Delete
    fun deleteSuministroReconexionTask(c: SuministroReconexion)

    @Query("SELECT * FROM SuministroReconexion WHERE estado= 1 ")
    fun getSuministroReconexionTask(): List<SuministroReconexion>

    @Query("DELETE FROM SuministroReconexion")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM SuministroReconexion WHERE activo = 1")
    fun getSuministroReconexionSize(): Int

    @Query("SELECT * FROM SuministroReconexion WHERE estado=:e AND activo =:a ORDER BY orden ASC")
    fun getSuministroReconexion(e: Int, a: Int): LiveData<List<SuministroReconexion>>

    @Query("SELECT orden FROM SuministroReconexion WHERE activo = 1  ORDER BY orden ASC")
    fun getSuministroPrimero(): Int

    @Query("SELECT orden FROM SuministroReconexion WHERE activo = 1 ORDER BY orden DESC")
    fun getSuministroUltimoActivo(): Int

    @Query("SELECT orden FROM SuministroReconexion WHERE orden <:orden AND activo = 1 ORDER BY orden DESC")
    fun getSuministroLeftTask(orden: Int): Int

    @Query("SELECT orden FROM SuministroReconexion WHERE activo = 1  ORDER BY orden DESC")
    fun getSuministroUltimo(): Int

    @Query("SELECT orden FROM SuministroReconexion WHERE activo = 1 ORDER BY orden ASC")
    fun getSuministroPrimeroActivo(): Int

    @Query("SELECT orden FROM SuministroReconexion WHERE orden >:orden AND activo = 1 ORDER BY orden ASC")
    fun getSuministroRightTask(orden: Int): Int

    @Query("UPDATE SuministroReconexion SET activo =:i WHERE iD_Suministro =:id")
    fun updateActivoSuministroReconexion(id: Int, i: Int)

    @Query("SELECT * FROM SuministroReconexion WHERE orden=:orden")
    fun suministroReconexionByOrden(orden: Int): SuministroReconexion

    @Query("SELECT * FROM SuministroReconexion WHERE iD_Suministro =:id")
    fun getSuministroReconexionById(id: Int): LiveData<SuministroReconexion>
}