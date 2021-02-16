package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.GrandesClientes

@Dao
interface GrandesClientesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrandesClientesTask(c: GrandesClientes)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrandesClientesListTask(c: List<GrandesClientes>)

    @Update
    fun updateGrandesClientesTask(vararg c: GrandesClientes)

    @Delete
    fun deleteGrandesClientesTask(c: GrandesClientes)

    @Query("SELECT * FROM GrandesClientes WHERE estado= 1 ")
    fun getGrandesClientesTask(): List<GrandesClientes>

    @Query("DELETE FROM GrandesClientes")
    fun deleteAll()

    @Query("SELECT * FROM GrandesClientes WHERE clienteId=:id ")
    fun getClienteById(id: Int): LiveData<GrandesClientes>

    @Query("SELECT * FROM GrandesClientes WHERE clienteId=:id ")
    fun getClienteByIdTask(id: Int): GrandesClientes

    @Query("UPDATE GrandesClientes SET estado = 7 WHERE clienteId=:id ")
    fun updateClienteEstado(id: Int)

    @Query("SELECT * FROM GrandesClientes WHERE estado != 7")
    fun getGrandesClientes(): LiveData<List<GrandesClientes>>

    @Query("SELECT COUNT(*) FROM GrandesClientes WHERE estado != 7")
    fun getGrandesClientesSize(): Int

}