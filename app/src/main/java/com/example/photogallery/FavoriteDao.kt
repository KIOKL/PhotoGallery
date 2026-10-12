package com.example.photogallery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: FavoritePhoto)

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoritePhoto>>

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}