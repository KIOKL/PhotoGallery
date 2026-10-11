package com.example.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.photogallery.ui.theme.PhotoGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoGalleryTheme {
                MainContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: PhotoGalleryViewModel = viewModel()) {
    var showSearch by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showFavorites) "Избранное" else "PhotoGallery") },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (showFavorites) "Все фото" else "Избранное") },
                            onClick = {
                                showFavorites = !showFavorites
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Удалить из избранного") },
                            onClick = {
                                viewModel.clearFavorites()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (showSearch && !showFavorites) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Фильтр по названию") }
                )
            }

            if (showFavorites) {
                FavoritesGrid(viewModel)
            } else {
                PhotoGrid(viewModel, query)
            }
        }
    }
}

@Composable
fun PhotoGrid(viewModel: PhotoGalleryViewModel, query: String) {
    val photos by viewModel.photos.collectAsState()
    val filteredPhotos = if (query.isBlank()) {
        photos
    } else {
        photos.filter { it.title.contains(query, ignoreCase = true) }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        items(filteredPhotos) { photo ->
            AsyncImage(
                model = "https://placehold.co/400x400/3498DB/FFFFFF/png?text=${photo.id}",
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { viewModel.addFavorite(photo) }
            )
        }
    }
}

@Composable
fun FavoritesGrid(viewModel: PhotoGalleryViewModel) {
    val favorites by viewModel.favorites.observeAsState(emptyList())
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        items(favorites) { fav ->
            AsyncImage(
                model = "https://placehold.co/400x400/3498DB/FFFFFF/png?text=${fav.id}",
                contentDescription = fav.author,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}