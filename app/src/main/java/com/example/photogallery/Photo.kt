package com.example.photogallery

import com.squareup.moshi.Json

data class PicsumPhoto(
    val id: Int,
    @Json(name = "albumId") val albumId: Int,
    val title: String,
    @Json(name = "url") val downloadUrl: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String
)