package com.example.photogallery

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteRepository(application: Application) {
    private val dao = FavoriteDatabase.getInstance(application).favoriteDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val favorites: LiveData<List<FavoritePhoto>> = dao.getAll()

    fun add(photo: PicsumPhoto) {
        scope.launch {
            dao.insert(
                FavoritePhoto(
                    id = photo.id.toString(),
                    author = photo.title,
                    url = "https://placehold.co/400x400/3498DB/FFFFFF/png?text=${photo.id}"
                )
            )
        }
    }

    fun clearAll() {
        scope.launch { dao.deleteAll() }
    }
}