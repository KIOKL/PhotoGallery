package com.example.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    var selectedPhoto by remember { mutableStateOf<PicsumPhoto?>(null) }

    val photo = selectedPhoto

    if (photo != null) {
        PhotoDetailScreen(
            photo = photo,
            onBack = { selectedPhoto = null },
            onAddFavorite = { viewModel.addFavorite(photo) }
        )
    } else {
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
            Column(modifier = Modifier.padding(innerPadding)) {
                if (showSearch && !showFavorites) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        placeholder = { Text("Фильтр по названию или id") },
                        singleLine = true
                    )
                }

                if (showFavorites) {
                    FavoritesGrid(viewModel)
                } else {
                    PhotoGrid(
                        viewModel = viewModel,
                        query = query,
                        onPhotoClick = { selectedPhoto = it }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoGrid(
    viewModel: PhotoGalleryViewModel,
    query: String,
    onPhotoClick: (PicsumPhoto) -> Unit
) {
    val photos by viewModel.photos.collectAsState()
    val filteredPhotos = if (query.isBlank()) {
        photos
    } else {
        photos.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.id.toString().contains(query)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        items(filteredPhotos) { photo ->
            AsyncImage(
                model = "https://robohash.org/${photo.id}.png?size=400x400&set=set1",
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { onPhotoClick(photo) }
            )
        }
    }
}

@Composable
fun FavoritesGrid(viewModel: PhotoGalleryViewModel) {
    val favorites by viewModel.favorites.collectAsState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        items(favorites) { fav ->
            AsyncImage(
                model = "https://robohash.org/${fav.id}.png?size=400x400&set=set1",
                contentDescription = fav.author,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: PicsumPhoto,
    onBack: () -> Unit,
    onAddFavorite: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = photo.title,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Добавить в избранное") },
                            onClick = {
                                onAddFavorite()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://robohash.org/${photo.id}.png?size=600x600&set=set1",
                contentDescription = photo.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}