package com.dravtv.player.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dravtv.player.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LoginScreen(
    initialServerCode: String = "4100",
    initialUser: String = "",
    initialPass: String = "",
    onLoginSuccess: (serverCode: String, user: String, pass: String) -> Unit
) {
    var serverCode by remember { mutableStateOf(if (initialServerCode.isNotBlank()) initialServerCode else "4100") }
    var user by remember { mutableStateOf(initialUser) }
    var pass by remember { mutableStateOf(initialPass) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isButtonFocused by remember { mutableStateOf(false) }

    // Relógio ao vivo atualizado
    var currentTime by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(10000L)
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
    }

    val quickRoutes = listOf(
        "4100" to "Principal",
        "4102" to "Rota 2",
        "4103" to "Rota 3",
        "4104" to "Inter",
        "dravbr" to "BR Natv"
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030207))
    ) {
        // Luzes e auras de fundo inspiradas no protótipo oficial
        Box(
            modifier = Modifier
                .size(380.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFF8A00).copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.CenterStart)
                .offset(x = 100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF9D00FF).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00F0FF).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // BARRA DE STATUS SUPERIOR (COMPACTA)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Servidor Online",
                                color = Color(0xFFEEEEEE),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Ultra HD 4K • IPTV Engine v4.2",
                        color = Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão de Teste Rápido
                    Text(
                        text = "Preencher Teste",
                        color = Color(0xFFFFD000),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                serverCode = "4100"
                                user = "testedrav"
                                pass = "32652901781"
                            }
                            .padding(4.dp)
                    )

                    // Relógio
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF9D00FF),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentTime,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ==========================================
            // CONTEÚDO CENTRAL: 2 COLUNAS HORIZONTAIS
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // COLUNA DA ESQUERDA: IDENTIDADE DRAV•TV (42% LARGURA)
                Column(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFF8A00).copy(alpha = 0.2f),
                                            Color(0xFF9D00FF).copy(alpha = 0.2f),
                                            Color(0xFF00F0FF).copy(alpha = 0.15f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.logo_dravtv_3d),
                            contentDescription = "DRAV•TV",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(115.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "DRAV•TV",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "VENHA PARA O FUTURO",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                            )
                        ) {
                            Text(
                                text = "🎬 +15.000 Canais & VOD",
                                color = Color(0xFFCCCCCC),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                            )
                        ) {
                            Text(
                                text = "⚡ Anti-Trava P2P",
                                color = Color(0xFF00E5FF),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // COLUNA DA DIREITA: CARD GLASSMORPHISM DE LOGIN (58% LARGURA)
                Card(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight(0.96f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0D1E).copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(18.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFF8A00).copy(alpha = 0.7f), Color(0xFF9D00FF).copy(alpha = 0.7f), Color(0xFF00F0FF).copy(alpha = 0.8f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 22.dp, vertical = 14.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Cabeçalho do Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Acesso ao Painel",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00F0FF))
                                    )
                                }
                                Text(
                                    text = "Informe seu código, usuário e senha",
                                    color = Color(0xFF8B949E),
                                    fontSize = 10.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFF8A00).copy(alpha = 0.2f), Color(0xFF9D00FF).copy(alpha = 0.2f), Color(0xFF00F0FF).copy(alpha = 0.2f))
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Seletores Rápidos de Rota
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickRoutes.forEach { (code, label) ->
                                val isSelected = serverCode == code
                                var isFocused by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(7.dp))
                                        .focusable()
                                        .onFocusChanged { isFocused = it.isFocused }
                                        .background(
                                            when {
                                                isSelected -> Color(0xFF00E5FF)
                                                isFocused -> Color(0xFF26324A)
                                                else -> Color(0xFF161427)
                                            }
                                        )
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = when {
                                                isFocused -> Color.White
                                                isSelected -> Color(0xFF00E5FF)
                                                else -> Color(0xFF2A2645)
                                            },
                                            shape = RoundedCornerShape(7.dp)
                                        )
                                        .clickable { serverCode = code }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = code,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = label,
                                            color = if (isSelected) Color(0xFF111111) else Color(0xFF888888),
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Campos: Código da Operadora + Usuário em Linha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = serverCode,
                                onValueChange = { serverCode = it },
                                label = { Text("Código / Rota", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Dns, contentDescription = null, tint = Color(0xFFFF8A00), modifier = Modifier.size(17.dp))
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0xFF2A2645),
                                    focusedLabelColor = Color(0xFF00E5FF),
                                    unfocusedLabelColor = Color(0xFF888888)
                                ),
                                modifier = Modifier.weight(0.38f)
                            )

                            OutlinedTextField(
                                value = user,
                                onValueChange = { user = it },
                                label = { Text("Usuário", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9D00FF), modifier = Modifier.size(17.dp))
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0xFF2A2645),
                                    focusedLabelColor = Color(0xFF00E5FF),
                                    unfocusedLabelColor = Color(0xFF888888)
                                ),
                                modifier = Modifier.weight(0.62f)
                            )
                        }

                        // Campo de Senha
                        OutlinedTextField(
                            value = pass,
                            onValueChange = { pass = it },
                            label = { Text("Senha", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(17.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha",
                                        tint = if (isPasswordVisible) Color(0xFF00E5FF) else Color(0xFF888888),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0xFF2A2645),
                                focusedLabelColor = Color(0xFF00E5FF),
                                unfocusedLabelColor = Color(0xFF888888)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Checkbox Mostrar Senha
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                            ) {
                                Checkbox(
                                    checked = isPasswordVisible,
                                    onCheckedChange = { isPasswordVisible = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF00E5FF),
                                        uncheckedColor = Color(0xFF888888)
                                    )
                                )
                                Text(
                                    text = "Mostrar senha",
                                    color = Color(0xFFDDDDDD),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Surface(
                                color = Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isPasswordVisible) "Senha visível" else "Senha oculta",
                                    color = if (isPasswordVisible) Color(0xFF00E5FF) else Color(0xFF888888),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFFF5252),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // BOTÃO ENTRAR (GRADIENTE OFICIAL DRAV•TV)
                        Button(
                            onClick = {
                                if (serverCode.isNotBlank() && user.isNotBlank() && pass.isNotBlank()) {
                                    isLoading = true
                                    errorMessage = null
                                    onLoginSuccess(serverCode.trim(), user.trim(), pass.trim())
                                } else {
                                    errorMessage = "Preencha o código, usuário e senha."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .focusable()
                                .onFocusChanged { isButtonFocused = it.isFocused }
                                .border(
                                    width = if (isButtonFocused) 3.dp else 0.dp,
                                    color = if (isButtonFocused) Color(0xFF00E5FF) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFF9900), Color(0xFFFF2E93), Color(0xFF8F00FF), Color(0xFF00E5FF))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "ENTRAR NO DRAV•TV",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Rodapé do Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Controle ou toque", color = Color(0xFF6E7681), fontSize = 9.sp)
                            Text("Engine: Pro 4K", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ==========================================
            // RODAPÉ INFERIOR DA TELA
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "© 2026 DRAV•TV • Todos os direitos reservados",
                    color = Color(0xFF555555),
                    fontSize = 10.sp
                )
                Text(
                    text = "Widescreen • Compatível com Smart TV, TV Box & Mobile",
                    color = Color(0xFF555555),
                    fontSize = 10.sp
                )
            }
        }
    }
}
