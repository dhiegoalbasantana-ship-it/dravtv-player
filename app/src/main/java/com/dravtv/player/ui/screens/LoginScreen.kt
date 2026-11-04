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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dravtv.player.R

@Composable
fun LoginScreen(
    initialServerCode: String = "",
    initialUser: String = "",
    initialPass: String = "",
    onLoginSuccess: (serverCode: String, user: String, pass: String) -> Unit
) {
    // Conforme instrução expressa do usuário:
    // "o codigo deixa em branco e quando a pessoa clicar ela digita o codigo que ela quer sem aparecer nenhum codigo"
    var serverCode by remember { mutableStateOf(initialServerCode) }
    var user by remember { mutableStateOf(initialUser) }
    var pass by remember { mutableStateOf(initialPass) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val userFocusRequester = remember { FocusRequester() }
    val passFocusRequester = remember { FocusRequester() }
    val btnFocusRequester = remember { FocusRequester() }

    var isCodeFocused by remember { mutableStateOf(false) }
    var isUserFocused by remember { mutableStateOf(false) }
    var isPassFocused by remember { mutableStateOf(false) }
    var isButtonFocused by remember { mutableStateOf(false) }

    fun submitLogin() {
        if (serverCode.isBlank()) {
            errorMessage = "Digite o código do servidor."
            return
        }
        if (user.isBlank()) {
            errorMessage = "Digite seu usuário."
            return
        }
        if (pass.isBlank()) {
            errorMessage = "Digite sua senha."
            return
        }
        errorMessage = null
        isLoading = true
        onLoginSuccess(serverCode.trim(), user.trim(), pass.trim())
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030207)),
        contentAlignment = Alignment.Center
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // Calcula proporção 16:9 perfeita para televisores e celulares horizontais
        val bannerW = if (screenW / screenH > 16f / 9f) {
            screenH * (16f / 9f)
        } else {
            screenW
        }
        val bannerH = bannerW / (16f / 9f)

        Box(
            modifier = Modifier
                .width(bannerW)
                .height(bannerH)
        ) {
            // ==========================================
            // 1. BANNER OFICIAL DE LOGIN NA ÍNTEGRA
            // ==========================================
            Image(
                painter = painterResource(id = R.drawable.banner_login),
                contentDescription = "Tela de Login DravTV",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            // Dimensões relativas à proporção original do banner (1024 x 576):
            // Inputs: x=565, width=385, height=54
            val boxLeft = bannerW * (565f / 1024f)
            val boxWidth = bannerW * (385f / 1024f)
            val boxHeight = bannerH * (54f / 576f)

            // ==========================================
            // CAMPO 1: CÓDIGO (Em branco por padrão)
            // ==========================================
            val codeTop = bannerH * (172f / 576f)
            Box(
                modifier = Modifier
                    .offset(x = boxLeft, y = codeTop)
                    .width(boxWidth)
                    .height(boxHeight)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        if (isCodeFocused) Color(0x3300F0FF)
                        else if (serverCode.isNotEmpty()) Color(0xFF131428)
                        else Color.Transparent
                    )
                    .border(
                        width = if (isCodeFocused) 2.dp else 1.dp,
                        brush = if (isCodeFocused) Brush.horizontalGradient(listOf(Color(0xFFFF8A00), Color(0xFF00F0FF)))
                                else SolidColor(Color.Transparent),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .onFocusChanged { isCodeFocused = it.isFocused },
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = serverCode,
                    onValueChange = { serverCode = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = (bannerH.value * 0.038f).sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { userFocusRequester.requestFocus() }
                    ),
                    cursorBrush = SolidColor(Color(0xFF00F0FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = boxWidth * 0.14f, end = 16.dp)
                )
            }

            // ==========================================
            // CAMPO 2: USUÁRIO
            // ==========================================
            val userTop = bannerH * (238f / 576f)
            Box(
                modifier = Modifier
                    .offset(x = boxLeft, y = userTop)
                    .width(boxWidth)
                    .height(boxHeight)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        if (isUserFocused) Color(0x3300F0FF)
                        else if (user.isNotEmpty()) Color(0xFF131428)
                        else Color.Transparent
                    )
                    .border(
                        width = if (isUserFocused) 2.dp else 1.dp,
                        brush = if (isUserFocused) Brush.horizontalGradient(listOf(Color(0xFFFF8A00), Color(0xFF00F0FF)))
                                else SolidColor(Color.Transparent),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .onFocusChanged { isUserFocused = it.isFocused },
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = user,
                    onValueChange = { user = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = (bannerH.value * 0.038f).sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passFocusRequester.requestFocus() }
                    ),
                    cursorBrush = SolidColor(Color(0xFF00F0FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = boxWidth * 0.14f, end = 16.dp)
                        .focusRequester(userFocusRequester)
                )
            }

            // ==========================================
            // CAMPO 3: SENHA (+ Ícone de Revelar Senha)
            // ==========================================
            val passTop = bannerH * (308f / 576f)
            Box(
                modifier = Modifier
                    .offset(x = boxLeft, y = passTop)
                    .width(boxWidth)
                    .height(boxHeight)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        if (isPassFocused) Color(0x3300F0FF)
                        else if (pass.isNotEmpty()) Color(0xFF131428)
                        else Color.Transparent
                    )
                    .border(
                        width = if (isPassFocused) 2.dp else 1.dp,
                        brush = if (isPassFocused) Brush.horizontalGradient(listOf(Color(0xFFFF8A00), Color(0xFF00F0FF)))
                                else SolidColor(Color.Transparent),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .onFocusChanged { isPassFocused = it.isFocused },
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = (bannerH.value * 0.038f).sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            submitLogin()
                        }
                    ),
                    cursorBrush = SolidColor(Color(0xFF00F0FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = boxWidth * 0.14f, end = boxWidth * 0.16f)
                        .focusRequester(passFocusRequester)
                )

                // Botão de Olho Interativo no canto direito do campo
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(boxHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isPasswordVisible = !isPasswordVisible },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Mostrar Senha",
                        tint = if (isPasswordVisible) Color(0xFF00F0FF) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size((boxHeight.value * 0.42f).dp)
                    )
                }
            }

            // ==========================================
            // BOTÃO: ENTRAR (Perfeitamente posicionado sobre o banner)
            // ==========================================
            val btnLeft = bannerW * (580f / 1024f)
            val btnWidth = bannerW * (340f / 1024f)
            val btnTop = bannerH * (388f / 576f)
            val btnHeight = bannerH * (67f / 576f)

            val btnScale by animateFloatAsState(
                targetValue = if (isButtonFocused) 1.05f else 1.0f,
                animationSpec = tween(150),
                label = "btnScale"
            )

            Box(
                modifier = Modifier
                    .offset(x = btnLeft, y = btnTop)
                    .width(btnWidth)
                    .height(btnHeight)
                    .scale(btnScale)
                    .clip(RoundedCornerShape(34.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isLoading
                    ) { submitLogin() }
                    .focusable()
                    .focusRequester(btnFocusRequester)
                    .onFocusChanged { isButtonFocused = it.isFocused }
                    .border(
                        width = if (isButtonFocused) 3.dp else 1.dp,
                        brush = if (isButtonFocused) Brush.horizontalGradient(
                            listOf(Color(0xFF00F0FF), Color(0xFFFFD000), Color(0xFFFF007A))
                        ) else SolidColor(Color.Transparent),
                        shape = RoundedCornerShape(34.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                }
            }

            // ==========================================
            // MENSAGEM DE ERRO (Se houver)
            // ==========================================
            errorMessage?.let { error ->
                Box(
                    modifier = Modifier
                        .offset(x = boxLeft, y = bannerH * (470f / 576f))
                        .width(boxWidth)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xDDCC0000))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
