package com.example.photogallery

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritePhoto(
    @PrimaryKey val id: String,
    val author: String,
    val url: String
)