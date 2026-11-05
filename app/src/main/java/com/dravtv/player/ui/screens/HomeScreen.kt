package com.dravtv.player.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    var showProfileModal by remember { mutableStateOf(false) }

    // 15 Avatares oficiais DravTV
    val avatarList = listOf(
        R.drawable.avatar_omniman to "Omni-Man",
        R.drawable.avatar_rhaenyra to "Rhaenyra Targaryen",
        R.drawable.avatar_mickey to "Mickey Mouse",
        R.drawable.avatar_daenerys to "Daenerys Targaryen",
        R.drawable.avatar_gumball to "Gumball",
        R.drawable.avatar_bojack to "BoJack Horseman",
        R.drawable.avatar_batman to "Batman",
        R.drawable.avatar_stitch to "Stitch",
        R.drawable.avatar_spiderman to "Homem-Aranha",
        R.drawable.avatar_gojo to "Satoru Gojo",
        R.drawable.avatar_luffy to "Luffy Gear 5",
        R.drawable.avatar_wednesday to "Wandinha Addams",
        R.drawable.avatar_grogu to "Baby Yoda (Grogu)",
        R.drawable.avatar_bossbaby to "Poderoso Chefinho",
        R.drawable.avatar_eleven to "Eleven (Onze)"
    )
    var selectedAvatarRes by remember { mutableStateOf(R.drawable.avatar_omniman) }

    // Estados de Foco para Controle Remoto Android TV
    var isHeroFocused by remember { mutableStateOf(false) }
    var isLiveFocused by remember { mutableStateOf(true) }
    var isVodFocused by remember { mutableStateOf(false) }
    var isSeriesFocused by remember { mutableStateOf(false) }
    var isProfileFocused by remember { mutableStateOf(false) }
    var isSettingsFocused by remember { mutableStateOf(false) }
    var isLogoutFocused by remember { mutableStateOf(false) }

    val liveFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            liveFocusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    val cleanDateText = remember(expiryDate) {
        if (expiryDate.isNullOrBlank()) {
            "Ativa"
        } else {
            expiryDate.trim().split(" ").firstOrNull() ?: expiryDate.trim()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A11))
    ) {
        // Fundo com iluminação cinematográfica do WatchHub
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "WatchHub Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay suave para leitura e contraste perfeitos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xDD070A11),
                            Color(0xBB0A0F1D),
                            Color(0xEE05070D)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 18.dp)
        ) {
            // ==========================================
            // 1. TOP HEADER BAR (WatchHub Style)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Oficial DravTV 3D
                Image(
                    painter = painterResource(id = R.drawable.logo_dravtv_official),
                    contentDescription = "DravTV Official Logo",
                    modifier = Modifier
                        .height(46.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "DRAV•TV",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "WATCHHUB EDITION",
                        color = Color(0xFF00F0FF),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Badges do WatchHub (Top 10 Filmes / Séries)
                Image(
                    painter = painterResource(id = R.drawable.top10filmes),
                    contentDescription = "Top 10 Filmes",
                    modifier = Modifier
                        .height(28.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.top10series),
                    contentDescription = "Top 10 Séries",
                    modifier = Modifier
                        .height(28.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Fit
                )

                // Badge de Vencimento Real da Lista (Neon Green)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x3300FF88),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88)),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF88))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Validade: $cleanDateText",
                            color = Color(0xFF00FF88),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Perfil do Usuário com Avatar WatchHub
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isProfileFocused) Color(0x4400F0FF) else Color(0x33101726))
                        .border(
                            width = if (isProfileFocused) 2.dp else 1.dp,
                            color = if (isProfileFocused) Color(0xFF00F0FF) else Color(0x33546E7A),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showProfileModal = true }
                        .focusable()
                        .onFocusChanged { isProfileFocused = it.isFocused }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = selectedAvatarRes),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = username.ifBlank { "Usuário" },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Botão Configurações
                HeaderIconButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    isFocused = isSettingsFocused,
                    onFocusChanged = { isSettingsFocused = it },
                    onClick = onNavigateSettings
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Botão Sair
                HeaderIconButton(
                    icon = Icons.Default.ExitToApp,
                    contentDescription = "Sair",
                    isFocused = isLogoutFocused,
                    onFocusChanged = { isLogoutFocused = it },
                    onClick = onLogout,
                    tint = Color(0xFFFF5252)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 2. HERO SPOTLIGHT CARD (WatchHub Featured Banner)
            // ==========================================
            val heroScale by animateFloatAsState(
                targetValue = if (isHeroFocused) 1.01f else 1.0f,
                animationSpec = tween(120),
                label = "heroScale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .scale(heroScale)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = if (isHeroFocused) 2.dp else 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0x6600F0FF),
                                Color(0x337928CA),
                                Color(0x22FFFFFF)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigateLive() }
                    .focusable()
                    .onFocusChanged { isHeroFocused = it.isFocused },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x880C111E))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Gradiente de fundo do Banner
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF0F1829),
                                        Color(0xCC162238),
                                        Color(0x33000000)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 22.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.badge_4k_ultra_hd),
                                    contentDescription = "4K Ultra HD",
                                    modifier = Modifier.height(20.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "DESTAQUES DRAVTV • STREAMING PREMIUM",
                                    color = Color(0xFF00F0FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "A melhor grade de Canais Ao Vivo, Filmes e Séries",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Alta estabilidade, resolução ultra nítida e reprodução instantânea com tecnologia WatchHub.",
                                color = Color(0xFF90A4AE),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Botão Assistir Agora
                        Button(
                            onClick = onNavigateLive,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isHeroFocused) Color(0xFF00F0FF) else Color(0xFF0072FF)
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isHeroFocused) Color(0xFF050811) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ASSISTIR AGORA",
                                color = if (isHeroFocused) Color(0xFF050811) else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. OS 3 CARDS HERO PRINCIPAIS DO WATCHHUB
            // (CANAIS AO VIVO, FILMES, SÉRIES)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CARD 1: CANAIS AO VIVO
                WatchHubMainCard(
                    title = "CANAIS AO VIVO",
                    subtitle = "Esportes, Notícias e TV em Tempo Real",
                    badgeText = "AO VIVO",
                    badgeColor = Color(0xFFFF1744),
                    gradientColors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF)),
                    glowColor = Color(0xFF00F0FF),
                    icon = Icons.Default.LiveTv,
                    isFocused = isLiveFocused,
                    focusRequester = liveFocusRequester,
                    onFocusChanged = { isLiveFocused = it },
                    onClick = onNavigateLive,
                    modifier = Modifier.weight(1f)
                )

                // CARD 2: FILMES (VOD)
                WatchHubMainCard(
                    title = "FILMES",
                    subtitle = "Lançamentos e Clássicos em 4K",
                    badgeText = "TOP 10",
                    badgeColor = Color(0xFFFF9100),
                    gradientColors = listOf(Color(0xFFFF007F), Color(0xFF7928CA)),
                    glowColor = Color(0xFFFF007F),
                    icon = Icons.Default.Movie,
                    isFocused = isVodFocused,
                    onFocusChanged = { isVodFocused = it },
                    onClick = onNavigateVod,
                    modifier = Modifier.weight(1f)
                )

                // CARD 3: SÉRIES
                WatchHubMainCard(
                    title = "SÉRIES",
                    subtitle = "Temporadas Completas e Animes",
                    badgeText = "COMPLETO",
                    badgeColor = Color(0xFF00E676),
                    gradientColors = listOf(Color(0xFF7928CA), Color(0xFF4338CA)),
                    glowColor = Color(0xFFB388FF),
                    icon = Icons.Default.Tv,
                    isFocused = isSeriesFocused,
                    onFocusChanged = { isSeriesFocused = it },
                    onClick = onNavigateSeries,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ==========================================
        // 4. MODAL DE SELEÇÃO DE PERFIL / AVATARES DO WATCHHUB
        // ==========================================
        if (showProfileModal) {
            Dialog(onDismissRequest = { showProfileModal = false }) {
                Card(
                    modifier = Modifier
                        .widthIn(min = 380.dp, max = 520.dp)
                        .border(1.5.dp, Color(0xFF00F0FF), RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xF50B101D)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selecione seu Avatar",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showProfileModal = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid dos 15 Avatares oficiais do DravTV
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(avatarList) { (resId, name) ->
                                val isSelected = selectedAvatarRes == resId
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0x6600F0FF) else Color(0x22FFFFFF))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF00F0FF) else Color(0x44FFFFFF),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedAvatarRes = resId
                                            showProfileModal = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = name,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0x33101726),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33455A64))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Conta: $username",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Servidor: $serverCode",
                                    color = Color(0xFF90A4AE),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Vencimento: $cleanDateText",
                                    color = Color(0xFF00FF88),
                                    fontSize = 11.sp,
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

@Composable
private fun WatchHubMainCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    gradientColors: List<Color>,
    glowColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFocused: Boolean,
    focusRequester: FocusRequester? = null,
    onFocusChanged: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(120),
        label = "cardScale"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isFocused) gradientColors else listOf(Color(0xFF101726), Color(0xFF0B101D))
                )
            )
            .border(
                width = if (isFocused) 3.5.dp else 1.5.dp,
                color = if (isFocused) glowColor else Color(0x33455A64),
                shape = RoundedCornerShape(22.dp)
            )
            .shadow(
                elevation = if (isFocused) 24.dp else 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .focusable()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row do Card: Badge + Ícone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isFocused) Color(0x44FFFFFF) else Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Bottom Section: Título e Subtítulo
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = if (isFocused) Color(0xFFF0F4F8) else Color(0xFF90A4AE),
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ACESSAR",
                        color = if (isFocused) Color.White else Color(0xFF00F0FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isFocused) Color.White else Color(0xFF00F0FF),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isFocused: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onClick: () -> Unit,
    tint: Color = Color.White
) {
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.15f else 1.0f,
        animationSpec = tween(100),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (isFocused) Color(0x4400F0FF) else Color(0x33101726))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color(0xFF00F0FF) else Color(0x33546E7A),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .focusable()
            .onFocusChanged { onFocusChanged(it.isFocused) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isFocused) Color(0xFF00F0FF) else tint,
            modifier = Modifier.size(19.dp)
        )
    }
}
