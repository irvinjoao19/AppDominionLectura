package com.dsige.lectura.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.lectura.dominion.data.local.model.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoTask(c: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoListTask(c: List<Photo>)

    @Update
    fun updatePhotoTask(vararg c: Photo)

    @Delete
    fun deletePhotoTask(c: Photo)

    @Query("SELECT * FROM Photo WHERE iD_Suministro=:id AND estado= 1 ")
    fun getPhotoTask(id:Int): List<Photo>

    @Query("SELECT * FROM Photo WHERE estado= 1 ")
    fun getPhotosTask(): List<Photo>

    @Query("SELECT * FROM Photo WHERE estado= 1 ")
    fun getPhotos(): LiveData<List<Photo>>

    @Query("DELETE FROM Photo")
    fun deleteAll()

    @Query("SELECT * FROM Photo WHERE iD_Suministro=:id AND tipo=:t AND conformidad=:i AND estado != 2 ORDER BY iD_Foto ASC")
    fun getPhotoAllBySuministro(id: Int, t: Int, i: Int): LiveData<List<Photo>>

    @Query("UPDATE Photo SET estado = 0 WHERE iD_Suministro=:id")
    fun updateEnablePhotos(id: Int)

    @Query("SELECT * FROM Photo WHERE iD_Suministro=:id AND firm = 1 ")
    fun getPhotoFirm(id: Int): LiveData<List<Photo>>
}