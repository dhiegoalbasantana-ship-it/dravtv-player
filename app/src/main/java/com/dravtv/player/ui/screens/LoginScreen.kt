package com.dravtv.player.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030207))
    ) {
        // Auras neon de fundo
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFF8A00).copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.CenterStart)
                .offset(x = 100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF9D00FF).copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(360.dp)
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
                .padding(horizontal = 24.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // BARRA DE STATUS SUPERIOR (ULTRA COMPACTA)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(6.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
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
                        text = "Ultra HD 4K • IPTV Engine",
                        color = Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Preencher Teste",
                        color = Color(0xFFFFD000),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                serverCode = "4100"
                                user = "testedrav"
                                pass = "32652901781"
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(6.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF9D00FF),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentTime,
                                color = Color.White,
                                fontSize = 10.sp,
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
                // COLUNA ESQUERDA: BRANDING DRAV•TV (40% LARGURA)
                Column(
                    modifier = Modifier
                        .weight(0.40f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_dravtv_3d),
                        contentDescription = "DRAV•TV",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "DRAV•TV",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "VENHA PARA O FUTURO",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                            )
                        ) {
                            Text(
                                text = "🎬 +15.000 Canais & VOD",
                                color = Color(0xFFCCCCCC),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                            )
                        ) {
                            Text(
                                text = "⚡ Anti-Trava P2P",
                                color = Color(0xFF00E5FF),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // COLUNA DIREITA: QUADRADO GLASSMORPHISM COM TUDO DENTRO SEM ROLAGEM (60% LARGURA)
                Card(
                    modifier = Modifier
                        .weight(0.60f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0C1C).copy(alpha = 0.94f)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFF8A00).copy(alpha = 0.7f), Color(0xFF9D00FF).copy(alpha = 0.7f), Color(0xFF00F0FF).copy(alpha = 0.8f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Título do Quadrado
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Acesso ao Painel",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00F0FF))
                                )
                            }
                            Text(
                                text = "Informe código, usuário e senha",
                                color = Color(0xFF8B949E),
                                fontSize = 9.sp
                            )
                        }

                        // Seletores Rápidos de Rota (Botões Compactos)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            quickRoutes.forEach { (code, label) ->
                                val isSelected = serverCode == code
                                var isFocused by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
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
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { serverCode = code }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = code,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = label,
                                            color = if (isSelected) Color(0xFF111111) else Color(0xFF888888),
                                            fontSize = 7.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Linha 1: Código + Usuário Lado a Lado (Design Compacto e Seguro)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CompactInputField(
                                value = serverCode,
                                onValueChange = { serverCode = it },
                                placeholder = "Código / Rota",
                                icon = Icons.Default.Dns,
                                iconColor = Color(0xFFFF8A00),
                                modifier = Modifier.weight(0.38f)
                            )

                            CompactInputField(
                                value = user,
                                onValueChange = { user = it },
                                placeholder = "Usuário",
                                icon = Icons.Default.Person,
                                iconColor = Color(0xFF9D00FF),
                                modifier = Modifier.weight(0.62f)
                            )
                        }

                        // Linha 2: Senha com Ícone de Olho
                        CompactInputField(
                            value = pass,
                            onValueChange = { pass = it },
                            placeholder = "Senha",
                            icon = Icons.Default.Lock,
                            iconColor = Color(0xFF00F0FF),
                            isPassword = true,
                            isPasswordVisible = isPasswordVisible,
                            onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Linha 3: Checkbox Mostrar Senha
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
                                        uncheckedColor = Color(0xFF777777)
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mostrar senha digitada",
                                    color = Color(0xFFCCCCCC),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFFF5252),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        // Linha 4: BOTÃO ENTRAR NO DRAV•TV (100% VISÍVEL DENTRO DO QUADRADO, ZERO ROLAGEM)
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
                                .height(44.dp)
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
                                            fontSize = 13.5.sp,
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
                    }
                }
            }

            // ==========================================
            // RODAPÉ INFERIOR (ULTRA COMPACTO)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "© 2026 DRAV•TV • Todos os direitos reservados",
                    color = Color(0xFF555555),
                    fontSize = 9.sp
                )
                Text(
                    text = "Compatível com Smart TV, TV Box & Celular Horizontal",
                    color = Color(0xFF555555),
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun CompactInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) Color(0xFF161428) else Color(0xFF0A0914))
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = if (isFocused) Color(0xFF00E5FF) else Color(0xFF2A2645),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused) Color(0xFF00E5FF) else iconColor,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF6E6A8A),
                        fontSize = 11.sp
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    cursorBrush = SolidColor(Color(0xFF00E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }

            if (isPassword && onTogglePassword != null) {
                IconButton(
                    onClick = onTogglePassword,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Mostrar senha",
                        tint = if (isPasswordVisible) Color(0xFF00E5FF) else Color(0xFF888888),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
