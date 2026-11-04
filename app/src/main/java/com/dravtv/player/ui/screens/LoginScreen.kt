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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.style.TextAlign
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
    var serverCode by remember { mutableStateOf(initialServerCode) }
    var user by remember { mutableStateOf(initialUser) }
    var pass by remember { mutableStateOf(initialPass) }
    var rememberCredentials by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val serverFocusRequester = remember { FocusRequester() }
    val userFocusRequester = remember { FocusRequester() }
    val passFocusRequester = remember { FocusRequester() }
    val btnFocusRequester = remember { FocusRequester() }

    var isServerFocused by remember { mutableStateOf(false) }
    var isUserFocused by remember { mutableStateOf(false) }
    var isPassFocused by remember { mutableStateOf(false) }
    var isBtnFocused by remember { mutableStateOf(false) }

    fun submitLogin() {
        if (serverCode.isBlank()) {
            errorMessage = "Digite o código do servidor ou URL."
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06090F))
    ) {
        // Fundo Cinematográfico WatchHub
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "WatchHub Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay suave com gradiente escuro para legibilidade perfeita
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33000000), Color(0xDD070A11)),
                        radius = 1600f
                    )
                )
        )

        // GlassCard Centralizado do WatchHub
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val cardScale by animateFloatAsState(
                targetValue = if (isBtnFocused) 1.01f else 1.0f,
                animationSpec = tween(150),
                label = "cardScale"
            )

            Card(
                modifier = Modifier
                    .widthIn(min = 340.dp, max = 460.dp)
                    .scale(cardScale)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0x6600F0FF),
                                Color(0x337928CA),
                                Color(0x6600F0FF)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp), ambientColor = Color(0xFF00F0FF), spotColor = Color(0xFF7928CA)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xDE0B101D)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo Oficial DravTV 3D
                    Image(
                        painter = painterResource(id = R.drawable.logo_dravtv_official),
                        contentDescription = "DravTV Official 3D Logo",
                        modifier = Modifier
                            .height(82.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "DRAV•TV PLAYER",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Acesse sua conta para continuar",
                        color = Color(0xFF90A4AE),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mensagem de Erro
                    if (errorMessage != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            color = Color(0x33FF1744),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF1744))
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Campo 1: Código do Servidor / URL
                    GlassInputField(
                        value = serverCode,
                        onValueChange = { serverCode = it },
                        placeholder = "Código do Servidor ou URL",
                        icon = Icons.Default.Dns,
                        isFocused = isServerFocused,
                        focusRequester = serverFocusRequester,
                        onFocusChanged = { isServerFocused = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { userFocusRequester.requestFocus() })
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Campo 2: Usuário
                    GlassInputField(
                        value = user,
                        onValueChange = { user = it },
                        placeholder = "Usuário",
                        icon = Icons.Default.Person,
                        isFocused = isUserFocused,
                        focusRequester = userFocusRequester,
                        onFocusChanged = { isUserFocused = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { passFocusRequester.requestFocus() })
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Campo 3: Senha
                    GlassInputField(
                        value = pass,
                        onValueChange = { pass = it },
                        placeholder = "Senha",
                        icon = Icons.Default.Lock,
                        isFocused = isPassFocused,
                        focusRequester = passFocusRequester,
                        onFocusChanged = { isPassFocused = it },
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            submitLogin()
                        })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Opção: Salvar credenciais
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { rememberCredentials = !rememberCredentials },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberCredentials,
                            onCheckedChange = { rememberCredentials = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00F0FF),
                                uncheckedColor = Color(0xFF78909C),
                                checkmarkColor = Color(0xFF06090F)
                            ),
                            modifier = Modifier.scale(0.85f)
                        )
                        Text(
                            text = "Lembrar dados de acesso",
                            color = Color(0xFFB0BEC5),
                            fontSize = 11.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão Principal: ENTRAR (WatchHub LoginButtonPrimary)
                    val btnScale by animateFloatAsState(
                        targetValue = if (isBtnFocused) 1.05f else 1.0f,
                        animationSpec = tween(120),
                        label = "btnScale"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .scale(btnScale)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF00C6FF),
                                        Color(0xFF0072FF),
                                        Color(0xFF8A2BE2)
                                    )
                                )
                            )
                            .border(
                                width = if (isBtnFocused) 2.5.dp else 1.dp,
                                color = if (isBtnFocused) Color(0xFF00FFFF) else Color(0x44FFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { submitLogin() }
                            .focusable()
                            .focusRequester(btnFocusRequester)
                            .onFocusChanged { isBtnFocused = it.isFocused },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Conectando...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "ENTRAR",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "WatchHub Layout • DravTV Player v1.2.0",
                        color = Color(0xFF546E7A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val borderColor = if (isFocused) Color(0xFF00F0FF) else Color(0x33455A64)
    val bgColor = if (isFocused) Color(0x4400E5FF) else Color(0x33101726)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused) Color(0xFF00F0FF) else Color(0xFF78909C),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF546E7A),
                        fontSize = 12.5.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { onFocusChanged(it.isFocused) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color(0xFF00F0FF)),
                    visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
            }

            if (isPassword && onTogglePassword != null) {
                IconButton(
                    onClick = onTogglePassword,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Alternar Visibilidade",
                        tint = if (isFocused) Color(0xFF00F0FF) else Color(0xFF78909C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
