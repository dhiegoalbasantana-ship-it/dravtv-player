package com.dravtv.player.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dravtv.player.R
import java.text.SimpleDateFormat
import java.util.*

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
    var showUserModal by remember { mutableStateOf(false) }

    var currentTime by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(10000L)
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030207))
    ) {
        // Luzes e auras de fundo reativas
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFF8A00).copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF9D00FF).copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00F0FF).copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // TOPO: LOGO DRAV•TV + RELÓGIO + USUÁRIO + CONFIGURAÇÕES (COMPACTO)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Identidade Visual DRAV•TV
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_dravtv_3d),
                        contentDescription = "DRAV•TV",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DRAV•TV",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF00E676).copy(alpha = 0.15f),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00E5FF)))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E676))
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "4K LIVE",
                                        color = Color(0xFF00E676),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "VENHA PARA O FUTURO",
                            color = Color(0xFF00E5FF),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Ações do Topo: Relógio + Perfil Discreto + Configurações
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Relógio
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = currentTime,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Botão Discreto de Usuário
                    var isUserBtnFocused by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier
                            .focusable()
                            .onFocusChanged { isUserBtnFocused = it.isFocused }
                            .clickable { showUserModal = true }
                            .border(
                                width = if (isUserBtnFocused) 2.dp else 1.dp,
                                color = if (isUserBtnFocused) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        color = if (isUserBtnFocused) Color(0xFF1B2436) else Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFF9100), Color(0xFF9C27B0), Color(0xFF00E5FF))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = username.take(2).uppercase().ifBlank { "TV" },
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = username.ifBlank { "Cliente" },
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Rota $serverCode",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF8B949E),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Botão Discreto de Configurações
                    var isSettingsFocused by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .focusable()
                            .onFocusChanged { isSettingsFocused = it.isFocused }
                            .clickable { onNavigateSettings() }
                            .border(
                                width = if (isSettingsFocused) 2.dp else 1.dp,
                                color = if (isSettingsFocused) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        color = if (isSettingsFocused) Color(0xFF1B2436) else Color.White.copy(alpha = 0.06f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações",
                                tint = if (isSettingsFocused) Color(0xFF00E5FF) else Color(0xFFCCCCCC),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // CENTRO: 3 HERO PORTAL CARDS (100% RESPONSIVOS, CABEM EM TV E CELULAR)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CARD 1: CANAIS (TV AO VIVO) - Tema Ouro / Laranja Solar
                HeroPortalCard(
                    modifier = Modifier.weight(1f),
                    title = "CANAIS",
                    subtitle = "Esportes • Abertos • 4K UHD",
                    badgeText = "AO VIVO",
                    counterText = "+2.400 Canais",
                    themeColor = Color(0xFFFFB800),
                    secondaryColor = Color(0xFFFF8A00),
                    cardIcon = Icons.Default.LiveTv,
                    onClick = onNavigateLive
                )

                // CARD 2: FILMES (CINEMA VOD) - Tema Magenta / Roxo Elétrico
                HeroPortalCard(
                    modifier = Modifier.weight(1f),
                    title = "FILMES",
                    subtitle = "Lançamentos • Dublados • IMAX",
                    badgeText = "CINEMA 4K",
                    counterText = "+18.500 Filmes",
                    themeColor = Color(0xFFE040FB),
                    secondaryColor = Color(0xFF9D00FF),
                    cardIcon = Icons.Default.Movie,
                    onClick = onNavigateVod
                )

                // CARD 3: SÉRIES (MARATONAS) - Tema Ciano / Azul Futurista
                HeroPortalCard(
                    modifier = Modifier.weight(1f),
                    title = "SÉRIES",
                    subtitle = "Originais • Doramas • Animes",
                    badgeText = "MARATONAS",
                    counterText = "+4.200 Séries",
                    themeColor = Color(0xFF00F0FF),
                    secondaryColor = Color(0xFF0057FF),
                    cardIcon = Icons.Default.VideoLibrary,
                    onClick = onNavigateSeries
                )
            }

            // ==========================================
            // RODAPÉ: STATUS DA CONTA & DICAS DE NAVEGAÇÃO (COMPACTO)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Status: Ativa • Expira em: ${expiryDate ?: "Indeterminado"}",
                        color = Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Navegue pelo controle remoto • [OK] Selecionar",
                    color = Color(0xFF6E7681),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ==========================================
        // MODAL DE DETALHES DO USUÁRIO & LOGOFF
        // ==========================================
        if (showUserModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { showUserModal = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(380.dp)
                        .clickable(enabled = false) {},
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111722)),
                    shape = RoundedCornerShape(18.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFF9100), Color(0xFF9C27B0), Color(0xFF00E5FF))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF9100), Color(0xFF9C27B0), Color(0xFF00E5FF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = username.take(2).uppercase().ifBlank { "TV" },
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = username,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Assinatura IPTV Premium",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = Color(0xFF182234),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Rota de Servidor:", color = Color(0xFF8B949E), fontSize = 11.sp)
                                    Text(serverCode, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Data de Expiração:", color = Color(0xFF8B949E), fontSize = 11.sp)
                                    Text(expiryDate ?: "Ativa", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    showUserModal = false
                                    onLogout()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Trocar de Conta", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { showUserModal = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263248)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Fechar", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroPortalCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String,
    counterText: String,
    themeColor: Color,
    secondaryColor: Color,
    cardIcon: ImageVector,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "heroCardScale"
    )

    Card(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .shadow(
                elevation = if (isFocused) 22.dp else 8.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = themeColor,
                ambientColor = themeColor
            )
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                brush = Brush.linearGradient(
                    if (isFocused) listOf(themeColor, Color.White, themeColor)
                    else listOf(themeColor.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (isFocused) listOf(
                            themeColor.copy(alpha = 0.28f),
                            secondaryColor.copy(alpha = 0.35f),
                            Color(0xFF0A0916)
                        )
                        else listOf(
                            Color(0xFF16132C).copy(alpha = 0.88f),
                            Color(0xFF0B0918).copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Topo do Card: Badge + Contador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = themeColor.copy(alpha = 0.2f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(themeColor, Color.White.copy(alpha = 0.4f)))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = themeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }

                    Text(
                        text = counterText,
                        color = Color(0xFFCCCCCC),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Centro: Arte do Ícone
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(themeColor.copy(alpha = 0.2f), secondaryColor.copy(alpha = 0.15f))
                                )
                            )
                            .border(1.dp, themeColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = cardIcon,
                            contentDescription = title,
                            tint = if (isFocused) Color.White else themeColor,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // Base: Título + Subtítulo + Botão Play
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = if (isFocused) themeColor else Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = subtitle,
                            color = Color(0xFFAAAAAA),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isFocused) themeColor else themeColor.copy(alpha = 0.25f))
                            .border(1.dp, themeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Acessar",
                            tint = if (isFocused) Color.Black else Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
