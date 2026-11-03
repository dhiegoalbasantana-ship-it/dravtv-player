package com.dravtv.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dravtv.player.data.model.Category
import com.dravtv.player.data.model.VodMovie

@Composable
fun VodScreen(
    categories: List<Category>,
    movies: List<VodMovie>,
    selectedCategoryId: String?,
    onSelectCategory: (String) -> Unit,
    onSelectMovie: (VodMovie) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredMovies = remember(movies, searchQuery) {
        if (searchQuery.isBlank()) {
            movies
        } else {
            movies.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
    ) {
        // Lateral de Categorias
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .background(Color(0xFF111722))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color(0xFF1E2638), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Gêneros de Filmes",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { category ->
                    val isSelected = category.categoryId == selectedCategoryId
                    var isFocused by remember { mutableStateOf(false) }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable()
                            .onFocusChanged { isFocused = it.isFocused }
                            .clickable { onSelectCategory(category.categoryId) }
                            .border(
                                width = if (isFocused) 2.dp else 0.dp,
                                color = if (isFocused) Color(0xFF9C27B0) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        color = when {
                            isSelected -> Color(0xFF9C27B0)
                            isFocused -> Color(0xFF1D2638)
                            else -> Color(0xFF161E2E)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = category.categoryName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Grade de Filmes
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filmes (${filteredMovies.size})",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                // Campo de Busca
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar filme...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9C27B0))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF9C27B0),
                        unfocusedBorderColor = Color(0xFF232D42)
                    ),
                    modifier = Modifier.width(300.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onSelectMovie(movie) }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: VodMovie,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                color = if (isFocused) Color(0xFF9C27B0) else Color(0xFF222C40),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) Color(0xFF1E283C) else Color(0xFF131926)
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1A2234)),
                contentAlignment = Alignment.Center
            ) {
                if (!movie.streamIcon.isNullOrBlank()) {
                    AsyncImage(
                        model = movie.streamIcon,
                        contentDescription = movie.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(50.dp)
                    )
                }

                if (!movie.rating.isNullOrBlank() && movie.rating != "0") {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = movie.rating,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
