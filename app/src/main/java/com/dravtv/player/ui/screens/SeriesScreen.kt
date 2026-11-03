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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dravtv.player.data.model.Category
import com.dravtv.player.data.model.EpisodeItem
import com.dravtv.player.data.model.SeriesItem
import com.dravtv.player.util.ImageHelper
import com.dravtv.player.util.WatchProgressManager

@Composable
fun SeriesScreen(
    categories: List<Category>,
    seriesList: List<SeriesItem>,
    isLoadingSeries: Boolean,
    selectedCategoryId: String?,
    onSelectCategory: (String) -> Unit,
    onSelectSeries: (SeriesItem) -> Unit,
    selectedSeries: SeriesItem?,
    episodes: List<EpisodeItem>,
    isLoadingEpisodes: Boolean,
    onPlayEpisode: (EpisodeItem) -> Unit,
    onCloseSeriesDetail: () -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredSeries = remember(seriesList, searchQuery) {
        if (searchQuery.isBlank()) {
            seriesList
        } else {
            seriesList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Gêneros de Séries",
                        color = Color.White,
                        fontSize = 17.sp,
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
                                    color = if (isFocused) Color(0xFF00E5FF) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = when {
                                isSelected -> Color(0xFF00E5FF)
                                isFocused -> Color(0xFF1D2638)
                                else -> Color(0xFF161E2E)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = category.categoryName,
                                color = if (isSelected) Color.Black else Color.White,
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

            // Grade de Séries
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
                        text = "Séries (${filteredSeries.size})",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar série...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00E5FF))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF232D42)
                        ),
                        modifier = Modifier.width(300.dp)
                    )
                }

                if (isLoadingSeries) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Carregando séries da categoria...", color = Color(0xFFCCCCCC), fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSeries) { series ->
                            SeriesCard(
                                series = series,
                                onClick = { onSelectSeries(series) }
                            )
                        }
                    }
                }
            }
        }

        // Modal Detalhes da Série & Episódios
        if (selectedSeries != null) {
            val seriesProgress = remember(selectedSeries.seriesId) {
                WatchProgressManager.getSeriesProgress(context, selectedSeries.seriesId)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { onCloseSeriesDetail() },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight(0.85f)
                        .clickable(enabled = false) {},
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A26)),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF00E5FF)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedSeries.name,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!selectedSeries.genre.isNullOrBlank()) {
                                    Text(
                                        text = selectedSeries.genre,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Button(
                                onClick = onCloseSeriesDetail,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A364F))
                            ) {
                                Text("Fechar")
                            }
                        }

                        // Botão para continuar de onde parou na série
                        if (seriesProgress != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val matchedEp = episodes.find { it.id == seriesProgress.episodeId }
                                        ?: episodes.firstOrNull()
                                    if (matchedEp != null) {
                                        onPlayEpisode(matchedEp)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "▶ Continuar da Temporada ${seriesProgress.season} - Ep ${seriesProgress.episodeNum}",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        if (!selectedSeries.plot.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = selectedSeries.plot,
                                color = Color(0xFFCCCCCC),
                                fontSize = 13.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Episódios (${episodes.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoadingEpisodes) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF00E5FF))
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(episodes) { ep ->
                                    var isEpFocused by remember { mutableStateOf(false) }
                                    val isWatched = remember(ep.id) {
                                        WatchProgressManager.isEpisodeWatched(context, ep.id)
                                    }
                                    val isLastPoint = seriesProgress?.episodeId == ep.id

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .focusable()
                                            .onFocusChanged { isEpFocused = it.isFocused }
                                            .clickable { onPlayEpisode(ep) }
                                            .border(
                                                width = if (isEpFocused) 2.dp else 1.dp,
                                                color = if (isEpFocused) Color(0xFF00E5FF) else if (isLastPoint) Color(0xFF00E5FF).copy(alpha = 0.5f) else Color(0xFF232D42),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isEpFocused) Color(0xFF1E283C) else Color(0xFF161E2E)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00E5FF),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = ep.title,
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (isWatched) {
                                                    Surface(
                                                        color = Color(0xFF00C853).copy(alpha = 0.9f),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    ) {
                                                        Text(
                                                            text = "✓ Assistido",
                                                            color = Color.White,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                } else if (isLastPoint) {
                                                    Surface(
                                                        color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    ) {
                                                        Text(
                                                            text = "▶ Onde parou",
                                                            color = Color.Black,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = "Assistir",
                                                    color = Color(0xFF00E5FF),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeriesCard(
    series: SeriesItem,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val progress = remember(series.seriesId) {
        WatchProgressManager.getSeriesProgress(context, series.seriesId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                color = if (isFocused) Color(0xFF00E5FF) else Color(0xFF222C40),
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
                if (!series.cover.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageHelper.buildRequest(context, series.cover),
                        contentDescription = series.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(50.dp)
                    )
                }

                // Marcação de onde parou na Série
                if (progress != null) {
                    Surface(
                        color = Color(0xFF00E5FF).copy(alpha = 0.95f),
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Parou: T${progress.season} E${progress.episodeNum}",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = series.name,
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
