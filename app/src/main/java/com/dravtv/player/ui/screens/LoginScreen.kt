package com.dravtv.player.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dravtv.player.R

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
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF161F30), Color(0xFF090D14)),
                    radius = 1400f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Layout Horizontal Otimizado para Smart TV e TV Box (Widescreen 16:9)
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111722)),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFF9100), Color(0xFF9C27B0), Color(0xFF00E5FF))
                )
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // COLUNA DA ESQUERDA (Branding, Logo 3D Oficial e Slogan)
                Column(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight()
                        .background(Color(0xFF0B0F17))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_dravtv_3d),
                        contentDescription = "DravTV Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "DRAV.TV",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "VENHA PARA O FUTURO",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color(0xFF162030),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "IPTV Player Ultra HD • 4K",
                            color = Color(0xFF8B949E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // COLUNA DA DIREITA (Formulário Horizontal com Botão 100% Visível)
                Column(
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight()
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Selecione o Servidor:",
                        color = Color(0xFFCCCCCC),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Linha com Seletores Rápidos de Rota
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickRoutes.forEach { (code, label) ->
                            val isSelected = serverCode == code
                            var isFocused by remember { mutableStateOf(false) }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .focusable()
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .background(
                                        when {
                                            isSelected -> Color(0xFF00E5FF)
                                            isFocused -> Color(0xFF26324A)
                                            else -> Color(0xFF1A2234)
                                        }
                                    )
                                    .border(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = when {
                                            isFocused -> Color.White
                                            isSelected -> Color(0xFF00E5FF)
                                            else -> Color(0xFF2E3B52)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { serverCode = code }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = code,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color(0xFF111111) else Color(0xFF888888),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linha com Código do Servidor + Usuário lado a lado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = serverCode,
                            onValueChange = { serverCode = it },
                            label = { Text("Código / Rota", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Dns, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0xFF2E3B52)
                            ),
                            modifier = Modifier.weight(0.38f)
                        )

                        OutlinedTextField(
                            value = user,
                            onValueChange = { user = it },
                            label = { Text("Usuário", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF9100),
                                unfocusedBorderColor = Color(0xFF2E3B52)
                            ),
                            modifier = Modifier.weight(0.62f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Campo de Senha
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Senha", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha",
                                    tint = if (isPasswordVisible) Color(0xFF00E5FF) else Color(0xFF8B949E),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF9C27B0),
                            unfocusedBorderColor = Color(0xFF2E3B52)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Checkbox Mostrar Senha
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                            .clickable { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Checkbox(
                            checked = isPasswordVisible,
                            onCheckedChange = { isPasswordVisible = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00E5FF),
                                uncheckedColor = Color(0xFF8B949E)
                            )
                        )
                        Text(
                            text = "Mostrar senha digitada",
                            color = Color(0xFFCCCCCC),
                            fontSize = 11.sp
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // BOTÃO ENTRAR NO DRAV TV (100% Visível na tela e com foco destacado para controle remoto)
                    Button(
                        onClick = {
                            if (serverCode.isNotBlank() && user.isNotBlank() && pass.isNotBlank()) {
                                isLoading = true
                                errorMessage = null
                                onLoginSuccess(serverCode.trim(), user.trim(), pass.trim())
                            } else {
                                errorMessage = "Preencha o código do servidor, usuário e senha."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .focusable()
                            .onFocusChanged { isButtonFocused = it.isFocused }
                            .border(
                                width = if (isButtonFocused) 3.dp else 0.dp,
                                color = if (isButtonFocused) Color(0xFF00E5FF) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF9100), Color(0xFF9C27B0), Color(0xFF00E5FF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ENTRAR NO DRAV TV",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        letterSpacing = 1.sp
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
