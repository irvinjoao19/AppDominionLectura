package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.Registro

@Dao
interface RegistroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroTask(c: Registro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroListTask(c: List<Registro>)

    @Update
    fun updateRegistroTask(vararg c: Registro)

    @Delete
    fun deleteRegistroTask(c: Registro)

    @Query("SELECT * FROM Registro WHERE estado= 1 ")
    fun getRegistroTask(): List<Registro>

    @Query("SELECT * FROM Registro WHERE estado= 1 ")
    fun getRegistros(): LiveData<List<Registro>>

    @Query("DELETE FROM Registro")
    fun deleteAll()

    @Query("SELECT * FROM Registro WHERE orden=:o AND tipo=:t ")
    fun getRegistroByOrden(o: Int, t: Int): LiveData<Registro>

    @Query("SELECT * FROM Registro WHERE iD_Suministro=:id ")
    fun getRegistroBySuministroTask(id: Int): Registro

    @Query("SELECT * FROM Registro WHERE orden =:o AND tipo=:t")
    fun getConfirmRegistro(o: Int, t: Int): Registro

    @Query("SELECT * FROM Registro WHERE iD_Suministro=:id ")
    fun getRegistroBySuministro(id: Int): LiveData<Registro>

    @Query("UPDATE Registro  SET estado = :e WHERE iD_Suministro=:id AND tipo=:t")
    fun updateRegistroActive(id: Int, t: Int,e:Int)



}