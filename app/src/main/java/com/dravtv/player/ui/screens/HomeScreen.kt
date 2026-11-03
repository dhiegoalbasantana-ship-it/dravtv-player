package com.dravtv.player.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dravtv.player.R
import com.dravtv.player.ui.theme.*

@Composable
fun HomeScreen(
    username: String,
    serverCode: String,
    expiryDate: String?,
    onNavigateLive: () -> Unit,
    onNavigateVod: () -> Unit,
    onNavigateSeries: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F1522), Color(0xFF070A0F))
                )
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar com Logo Oficial
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_dravtv_3d),
                        contentDescription = "DravTV",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "DRAV.TV",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Usuário: $username • Rota: $serverCode ${expiryDate?.let { "• Vence: $it" } ?: ""}",
                            color = Color(0xFF00E5FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .background(Color(0xFF1B2232), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sair",
                        tint = Color(0xFFFF5252)
                    )
                }
            }

            // Cards Grid (Live TV, Filmes, Séries)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DravDashboardCard(
                    title = "CANAIS AO VIVO",
                    subtitle = "Futebol, Notícias, Variedades e 24h",
                    icon = Icons.Default.LiveTv,
                    gradient = listOf(Color(0xFFFF9100), Color(0xFFFF3D00)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateLive
                )

                DravDashboardCard(
                    title = "FILMES (VOD)",
                    subtitle = "Lançamentos dos cinemas e clássicos em HD/4K",
                    icon = Icons.Default.Movie,
                    gradient = listOf(Color(0xFF9C27B0), Color(0xFF673AB7)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateVod
                )

                DravDashboardCard(
                    title = "SÉRIES",
                    subtitle = "Todas as temporadas e episódios atualizados",
                    icon = Icons.Default.Tv,
                    gradient = listOf(Color(0xFF00E5FF), Color(0xFF0091EA)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateSeries
                )
            }
        }
    }
}

@Composable
fun DravDashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxHeight()
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                color = if (isFocused) gradient.first() else Color(0xFF232D42),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) Color(0xFF1E273A) else Color(0xFF131926)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        Brush.linearGradient(gradient),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF8B949E),
                    fontSize = 13.sp
                )
            }
        }
    }
}
