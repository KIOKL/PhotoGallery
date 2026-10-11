package com.example.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {

    private val _photos = MutableStateFlow<List<PicsumPhoto>>(emptyList())
    val photos: StateFlow<List<PicsumPhoto>> = _photos.asStateFlow()

    init {
        fetchPhotos()
    }

    fun fetchPhotos() {
        viewModelScope.launch {
            try {
                _photos.value = FlickrApiClient.service.getPhotos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}