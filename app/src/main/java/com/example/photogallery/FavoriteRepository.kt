package com.example.photogallery

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteRepository(application: Application) {

    private val dao = FavoriteDatabase.getInstance(application).favoriteDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val favorites: Flow<List<FavoritePhoto>> = dao.getAll()

    fun add(photo: PicsumPhoto) {
        scope.launch {
            dao.insert(
                FavoritePhoto(
                    id = photo.id.toString(),
                    author = photo.title,
                    url = "https://robohash.org/${photo.id}.png?size=400x400&set=set1"
                )
            )
        }
    }

    fun clearAll() {
        scope.launch { dao.deleteAll() }
    }
}