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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var showUserModal by remember { mutableStateOf(false) }

    // Estados de Foco para Controle Remoto de TV
    var isLiveFocused by remember { mutableStateOf(true) }
    var isVodFocused by remember { mutableStateOf(false) }
    var isSeriesFocused by remember { mutableStateOf(false) }
    var isProfileFocused by remember { mutableStateOf(false) }
    var isSettingsFocused by remember { mutableStateOf(false) }

    val liveFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            liveFocusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    // Extrai apenas a data limpa (ex: "30/11/2026") para caber perfeitamente no badge da arte
    val cleanDateText = remember(expiryDate) {
        if (expiryDate.isNullOrBlank()) {
            "Ativa"
        } else {
            expiryDate.trim().split(" ").firstOrNull() ?: expiryDate.trim()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030207))
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // ==========================================
        // 1. BANNER OFICIAL DA TELA INICIAL (100% DA TELA, SEM BARRAS PRETAS)
        // O logo é 100% ESTÁTICO, direto da arte original, sem sobreposições.
        // ==========================================
        Image(
            painter = painterResource(id = R.drawable.banner_home),
            contentDescription = "Tela Inicial DravTV",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // ==========================================
        // 2. VENCIMENTO REAL DA LISTA (Substitui o "31/12/2026" fixo)
        // ==========================================
        val dateLeft = screenW * (605f / 1024f)
        val dateTop = screenH * (64f / 576f)
        val dateWidth = screenW * (120f / 1024f)
        val dateHeight = screenH * (25f / 576f)

        Box(
            modifier = Modifier
                .offset(x = dateLeft, y = dateTop)
                .width(dateWidth)
                .height(dateHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cleanDateText,
                color = Color(0xFF00FF88),
                fontSize = (screenH.value * 0.027f).sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        // ==========================================
        // 3. BOTÕES DO TOPO DIREITO: PERFIL E AJUSTES
        // ==========================================
        val topBarY = screenH * (35f / 576f)
        val profileSize = screenH * (53f / 576f)

        // Botão de Perfil do Usuário (x: 840..895)
        val profileLeft = screenW * (840f / 1024f)
        Box(
            modifier = Modifier
                .offset(x = profileLeft, y = topBarY)
                .size(profileSize)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showUserModal = true }
                .focusable()
                .onFocusChanged { isProfileFocused = it.isFocused }
                .border(
                    width = if (isProfileFocused) 3.dp else 1.dp,
                    color = if (isProfileFocused) Color(0xFF00F0FF) else Color.Transparent,
                    shape = CircleShape
                )
                .background(if (isProfileFocused) Color(0x3300F0FF) else Color.Transparent)
        )

        // Botão de Configurações (x: 915..970)
        val settingsLeft = screenW * (915f / 1024f)
        Box(
            modifier = Modifier
                .offset(x = settingsLeft, y = topBarY)
                .size(profileSize)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigateSettings() }
                .focusable()
                .onFocusChanged { isSettingsFocused = it.isFocused }
                .border(
                    width = if (isSettingsFocused) 3.dp else 1.dp,
                    color = if (isSettingsFocused) Color(0xFF00F0FF) else Color.Transparent,
                    shape = CircleShape
                )
                .background(if (isSettingsFocused) Color(0x3300F0FF) else Color.Transparent)
        )

        // ==========================================
        // 4. OS 3 CARDS HERO INTERATIVOS (CANAIS, FILMES, SÉRIES)
        // ==========================================
        val cardsY = screenH * (210f / 576f)
        val cardsHeight = screenH * (285f / 576f)

        // ------------------------------------------
        // CARD 1: CANAIS (TV AO VIVO)
        // ------------------------------------------
        val card1Left = screenW * (79f / 1024f)
        val card1Width = screenW * (280f / 1024f)
        val liveScale by animateFloatAsState(
            targetValue = if (isLiveFocused) 1.05f else 1.0f,
            animationSpec = tween(150),
            label = "liveScale"
        )

        Box(
            modifier = Modifier
                .offset(x = card1Left, y = cardsY)
                .width(card1Width)
                .height(cardsHeight)
                .scale(liveScale)
                .clip(RoundedCornerShape(32.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigateLive() }
                .focusable()
                .focusRequester(liveFocusRequester)
                .onFocusChanged { isLiveFocused = it.isFocused }
                .border(
                    width = if (isLiveFocused) 3.5.dp else 1.dp,
                    brush = if (isLiveFocused) Brush.horizontalGradient(
                        listOf(Color(0xFFE040FB), Color(0xFF9D00FF), Color(0xFF00F0FF))
                    ) else SolidColor(Color.Transparent),
                    shape = RoundedCornerShape(32.dp)
                )
                .background(if (isLiveFocused) Color(0x229D00FF) else Color.Transparent)
        )

        // ------------------------------------------
        // CARD 2: FILMES (CINEMA VOD)
        // ------------------------------------------
        val card2Left = screenW * (363f / 1024f)
        val card2Width = screenW * (291f / 1024f)
        val vodScale by animateFloatAsState(
            targetValue = if (isVodFocused) 1.05f else 1.0f,
            animationSpec = tween(150),
            label = "vodScale"
        )

        Box(
            modifier = Modifier
                .offset(x = card2Left, y = cardsY)
                .width(card2Width)
                .height(cardsHeight)
                .scale(vodScale)
                .clip(RoundedCornerShape(32.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigateVod() }
                .focusable()
                .onFocusChanged { isVodFocused = it.isFocused }
                .border(
                    width = if (isVodFocused) 3.5.dp else 1.dp,
                    brush = if (isVodFocused) Brush.horizontalGradient(
                        listOf(Color(0xFFFFD000), Color(0xFFFF8A00), Color(0xFFFF007A))
                    ) else SolidColor(Color.Transparent),
                    shape = RoundedCornerShape(32.dp)
                )
                .background(if (isVodFocused) Color(0x22FF8A00) else Color.Transparent)
        )

        // ------------------------------------------
        // CARD 3: SÉRIES (MARATONAS)
        // ------------------------------------------
        val card3Left = screenW * (653f / 1024f)
        val card3Width = screenW * (291f / 1024f)
        val seriesScale by animateFloatAsState(
            targetValue = if (isSeriesFocused) 1.05f else 1.0f,
            animationSpec = tween(150),
            label = "seriesScale"
        )

        Box(
            modifier = Modifier
                .offset(x = card3Left, y = cardsY)
                .width(card3Width)
                .height(cardsHeight)
                .scale(seriesScale)
                .clip(RoundedCornerShape(32.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigateSeries() }
                .focusable()
                .onFocusChanged { isSeriesFocused = it.isFocused }
                .border(
                    width = if (isSeriesFocused) 3.5.dp else 1.dp,
                    brush = if (isSeriesFocused) Brush.horizontalGradient(
                        listOf(Color(0xFF00F0FF), Color(0xFF0057FF), Color(0xFF9D00FF))
                    ) else SolidColor(Color.Transparent),
                    shape = RoundedCornerShape(32.dp)
                )
                .background(if (isSeriesFocused) Color(0x2200F0FF) else Color.Transparent)
        )

        // ==========================================
        // 5. MODAL DE DETALHES DO USUÁRIO & LOGOFF
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF100E22)),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFF8A00), Color(0xFF9D00FF), Color(0xFF00F0FF))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF8A00), Color(0xFF9D00FF), Color(0xFF00F0FF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = username.take(2).uppercase().ifBlank { "TV" },
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = username.ifBlank { "Cliente DravTV" },
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Assinatura IPTV Premium Ativa",
                            color = Color(0xFF00F0FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = Color(0xFF16152F),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Servidor / Rota:", color = Color(0xFF8B949E), fontSize = 12.sp)
                                    Text(serverCode.ifBlank { "Padrão" }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Vencimento Completo:", color = Color(0xFF8B949E), fontSize = 12.sp)
                                    Text(expiryDate ?: "Ativa", color = Color(0xFF00FF88), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

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
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sair / Trocar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    showUserModal = false
                                    onNavigateSettings()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263248)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ajustes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
