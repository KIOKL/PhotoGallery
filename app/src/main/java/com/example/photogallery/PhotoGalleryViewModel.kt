package com.example.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FavoriteRepository(application)

    private val _photos = MutableStateFlow<List<PicsumPhoto>>(emptyList())
    val photos: StateFlow<List<PicsumPhoto>> = _photos.asStateFlow()

    val favorites: StateFlow<List<FavoritePhoto>> = repo.favorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init { fetchPhotos() }

    fun fetchPhotos() {
        viewModelScope.launch {
            try {
                val all = FlickrApiClient.service.getPhotos()
                _photos.value = all.take(30)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addFavorite(photo: PicsumPhoto) = repo.add(photo)
    fun clearFavorites() = repo.clearAll()
}