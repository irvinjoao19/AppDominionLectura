package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.SuministroLectura

@Dao
interface LecturaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuministroLecturaTask(c: SuministroLectura)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuministroLecturaListTask(c: List<SuministroLectura>)

    @Update
    fun updateSuministroLecturaTask(vararg c: SuministroLectura)

    @Delete
    fun deleteSuministroLecturaTask(c: SuministroLectura)

    @Query("SELECT * FROM SuministroLectura WHERE estado= 1 ")
    fun getSuministroLecturaTask(): List<SuministroLectura>

    @Query("SELECT COUNT(*) FROM SuministroLectura WHERE estado= 1 AND activo = 1")
    fun getSuministroLecturaSize(): Int

    @Query("SELECT COUNT(*) FROM SuministroLectura WHERE estado= 2 AND activo = 1")
    fun getSuministroRelecturaSize(): Int

    @Query("DELETE FROM SuministroLectura")
    fun deleteAll()


    @Query("SELECT COUNT(*) FROM SuministroLectura WHERE estado= 1 AND activo = 1 AND flagObservada = 0  AND suministro_TipoProceso = '1' ")
    fun getSuministroLecturaNormalSize(): Int

    @Query("SELECT COUNT(*) FROM SuministroLectura WHERE estado= 1 AND activo = 1 AND flagObservada = 1 AND suministro_TipoProceso = '1' ")
    fun getSuministroObservadaSize(): Int

    @Query("SELECT COUNT(*) FROM SuministroLectura WHERE estado= 1 AND activo = 1  AND suministro_TipoProceso = '9' ")
    fun getSuministroReclamosSize(): Int

    @Query("SELECT * FROM SuministroLectura WHERE suministro_TipoProceso=:t AND estado=:e AND activo =:a AND flagObservada =:o ORDER BY orden ASC")
    fun getSuministroLectura(t: String, e: Int, a: Int, o: Int): LiveData<List<SuministroLectura>>

    @Query("SELECT * FROM SuministroLectura WHERE suministro_TipoProceso=:e AND activo =:a ORDER BY orden ASC")
    fun getSuministroReclamos(e: String, a: Int): LiveData<List<SuministroLectura>>

    @Query("SELECT * FROM SuministroLectura WHERE orden=:orden")
    fun suministroLecturaByOrden(orden: Int): SuministroLectura

    @Query("SELECT orden FROM SuministroLectura WHERE suministro_TipoProceso=:tipo AND estado =:e AND flagObservada=:f AND activo = 1  ORDER BY orden DESC")
    fun getSuministroUltimo(tipo: String, e: Int, f: Int): Int

    @Query("SELECT orden FROM SuministroLectura WHERE suministro_TipoProceso=:tipo AND estado =:e AND flagObservada=:f AND activo = 1 ORDER BY orden ASC")
    fun getSuministroPrimeroActivo(tipo: String, e: Int, f: Int): Int

    @Query("SELECT orden FROM SuministroLectura WHERE  suministro_TipoProceso=:tipo AND estado =:e AND flagObservada=:f AND activo = 1 AND orden >:orden  ORDER BY orden ASC")
    fun getSuministroRightTask(tipo: String, e: Int, f: Int,orden: Int): Int

    @Query("SELECT orden FROM SuministroLectura WHERE suministro_TipoProceso=:tipo AND estado =:e AND flagObservada=:f AND activo = 1  ORDER BY orden ASC")
    fun getSuministroPrimero(tipo: String, e: Int, f: Int): Int

    @Query("SELECT orden FROM SuministroLectura WHERE suministro_TipoProceso=:tipo AND estado =:e AND flagObservada=:f AND activo = 1 ORDER BY orden DESC")
    fun getSuministroUltimoActivo(tipo: String, e: Int, f: Int): Int

    @Query("SELECT orden FROM SuministroLectura WHERE suministro_TipoProceso=:tipo AND estado =:e AND flagObservada=:f AND activo = 1 AND orden <:orden ORDER BY orden DESC")
    fun getSuministroLeftTask(tipo: String, e: Int, f: Int,orden: Int): Int

    @Query("UPDATE SuministroLectura SET activo =:i WHERE iD_Suministro =:id")
    fun updateActivoSuministroLectura(id: Int, i: Int)

    @Query("SELECT * FROM SuministroLectura WHERE iD_Suministro =:id")
    fun getSuministroLecturaById(id: Int): LiveData<SuministroLectura>
}