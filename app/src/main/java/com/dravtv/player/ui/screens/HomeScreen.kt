package com.dravtv.player.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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

@Composable
fun HomeScreen(
    username: String,
    serverCode: String,
    expiryDate: String?,
    onNavigateLive: () -> Unit,
    onNavigateVod: () -> Unit,
    onNavigateSeries: () -> Unit,
    onNavigateSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagem de Fundo Cinema Oficial do DravTV
        Image(
            painter = painterResource(id = R.drawable.home_cinema_bg),
            contentDescription = "DravTV Cinema",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Camada de gradiente escuro suave para leitura perfeita dos botões
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar com Logo e Botões de Ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info do Usuário
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFFF9100), Color(0xFF00E5FF)))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Usuário: $username • Rota: $serverCode ${expiryDate?.let { "• Válido: $it" } ?: ""}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Botões Configurações e Sair
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(
                        onClick = onNavigateSettings,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            tint = Color(0xFF00E5FF)
                        )
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sair",
                            tint = Color(0xFFFF5252)
                        )
                    }
                }
            }

            // Área Central de Destaque - Os 3 Botões 3D Brilhantes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão 1: Canais Ao Vivo (Roxo Neon)
                    Glowing3DButton(
                        title = "CANAIS",
                        subtitle = "AO VIVO ((•))",
                        icon = Icons.Default.LiveTv,
                        glowColor = Color(0xFFB347FF),
                        gradientColors = listOf(Color(0xFF6A0DAD), Color(0xFF2C0959)),
                        onClick = onNavigateLive
                    )

                    // Botão 2: Filmes (Laranja/Dourado Neon)
                    Glowing3DButton(
                        title = "FILMES",
                        subtitle = "LANÇAMENTOS & VOD",
                        icon = Icons.Default.Movie,
                        glowColor = Color(0xFFFF9100),
                        gradientColors = listOf(Color(0xFFFF6D00), Color(0xFF7A3200)),
                        onClick = onNavigateVod
                    )

                    // Botão 3: Séries (Azul Neon)
                    Glowing3DButton(
                        title = "SÉRIES",
                        subtitle = "TEMPORADAS",
                        icon = Icons.Default.VideoLibrary,
                        glowColor = Color(0xFF00E5FF),
                        gradientColors = listOf(Color(0xFF0091EA), Color(0xFF003865)),
                        onClick = onNavigateSeries
                    )
                }
            }
        }
    }
}

@Composable
fun Glowing3DButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    glowColor: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.09f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "buttonScale"
    )

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(230.dp)
            .scale(scale)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .shadow(
                elevation = if (isFocused) 26.dp else 10.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = glowColor,
                ambientColor = glowColor
            )
            .border(
                width = if (isFocused) 4.dp else 1.5.dp,
                brush = Brush.linearGradient(
                    if (isFocused) listOf(glowColor, Color.White, glowColor)
                    else listOf(glowColor.copy(alpha = 0.7f), Color.Transparent)
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (isFocused) listOf(glowColor.copy(alpha = 0.85f), gradientColors[1])
                        else gradientColors
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Ícone com brilho interno
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.dp, glowColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isFocused) Color.White else glowColor,
                        modifier = Modifier.size(46.dp)
                    )
                }

                // Textos
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = if (isFocused) Color.White else Color(0xFFDDDDDD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
