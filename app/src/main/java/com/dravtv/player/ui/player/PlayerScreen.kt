package com.dravtv.player.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.media3.ui.SubtitleView

data class AudioTrackOption(
    val group: Tracks.Group,
    val trackIndex: Int,
    val label: String,
    val isSelected: Boolean
)

data class SubtitleTrackOption(
    val group: Tracks.Group?,
    val trackIndex: Int,
    val label: String,
    val isSelected: Boolean
)

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    streamUrl: String,
    title: String,
    startPositionMs: Long = 0L,
    onProgressUpdate: ((positionMs: Long, durationMs: Long) -> Unit)? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var currentTracks by remember { mutableStateOf<Tracks?>(null) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("IPTVSmartersPro")
            .setAllowCrossProtocolRedirects(true)
            .setKeepPostFor302Redirects(true)
            .setConnectTimeoutMs(25000)
            .setReadTimeoutMs(25000)

        val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            if (startPositionMs > 0) {
                seekTo(startPositionMs)
            }
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    currentTracks = tracks
                }
            })
        }
    }

    // Salva progresso periodicamente a cada 4 segundos
    LaunchedEffect(exoPlayer) {
        while (true) {
            kotlinx.coroutines.delay(4000L)
            if (exoPlayer.playbackState == Player.STATE_READY) {
                val pos = exoPlayer.currentPosition
                val dur = exoPlayer.duration
                if (pos > 0) {
                    onProgressUpdate?.invoke(pos, dur)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            val pos = exoPlayer.currentPosition
            val dur = exoPlayer.duration
            if (pos > 0) {
                onProgressUpdate?.invoke(pos, dur)
            }
            exoPlayer.release()
        }
    }

    // Processa faixas de Áudio disponíveis com nomes claros (Dublado / Original / Dual)
    val audioOptions = remember(currentTracks) {
        val list = mutableListOf<AudioTrackOption>()
        var count = 0
        currentTracks?.groups?.filter { it.type == C.TRACK_TYPE_AUDIO }?.forEach { group ->
            for (i in 0 until group.length) {
                val format = group.getTrackFormat(i)
                count++
                val lang = format.language?.lowercase()
                val friendlyName = when {
                    lang == "por" || lang == "pt" -> "Português (Dublado)"
                    lang == "eng" || lang == "en" -> "Inglês (Original)"
                    lang == "spa" || lang == "es" -> "Espanhol"
                    !format.label.isNullOrBlank() -> format.label!!
                    count == 1 -> "Áudio 1 - Principal (Dublado)"
                    count == 2 -> "Áudio 2 - Secundário (Dual Áudio / Original)"
                    else -> "Faixa de Áudio $count"
                }

                list.add(
                    AudioTrackOption(
                        group = group,
                        trackIndex = i,
                        label = friendlyName,
                        isSelected = group.isTrackSelected(i)
                    )
                )
            }
        }
        list
    }

    // Processa faixas de Legendas disponíveis (Dual Legenda / Closed Caption)
    val subtitleOptions = remember(currentTracks) {
        val list = mutableListOf<SubtitleTrackOption>()
        val hasAnySelected = currentTracks?.groups?.filter { it.type == C.TRACK_TYPE_TEXT }?.any { group ->
            (0 until group.length).any { group.isTrackSelected(it) }
        } ?: false

        // Opção padrão de Desativar
        list.add(
            SubtitleTrackOption(
                group = null,
                trackIndex = -1,
                label = "Desativar Legendas",
                isSelected = !hasAnySelected
            )
        )

        var count = 0
        currentTracks?.groups?.filter { it.type == C.TRACK_TYPE_TEXT }?.forEach { group ->
            for (i in 0 until group.length) {
                val format = group.getTrackFormat(i)
                count++
                val lang = format.language?.lowercase()
                val friendlyName = when {
                    lang == "por" || lang == "pt" -> "Português"
                    lang == "eng" || lang == "en" -> "Inglês (Original)"
                    lang == "spa" || lang == "es" -> "Espanhol"
                    !format.label.isNullOrBlank() -> format.label!!
                    count == 1 -> "Legenda 1 (Português)"
                    count == 2 -> "Legenda 2 (Inglês / Original)"
                    else -> "Legenda $count"
                }

                list.add(
                    SubtitleTrackOption(
                        group = group,
                        trackIndex = i,
                        label = friendlyName,
                        isSelected = group.isTrackSelected(i)
                    )
                )
            }
        }
        list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Player com renderizador de legendas estilizadas
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true

                    subtitleView?.apply {
                        setUserDefaultStyle()
                        setFixedTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24f)
                        setStyle(
                            CaptionStyleCompat(
                                android.graphics.Color.WHITE,
                                android.graphics.Color.argb(160, 0, 0, 0),
                                android.graphics.Color.TRANSPARENT,
                                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                                android.graphics.Color.BLACK,
                                null
                            )
                        )
                        setBottomPaddingFraction(0.08f)
                    }

                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Superior de Controles (Voltar, Trocar Áudio e Trocar Legenda)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var isBackFocused by remember { mutableStateOf(false) }
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .focusable()
                        .onFocusChanged { isBackFocused = it.isFocused }
                        .border(
                            width = if (isBackFocused) 2.dp else 0.dp,
                            color = if (isBackFocused) Color(0xFF00E5FF) else Color.Transparent,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .background(Color.Black.copy(alpha = 0.75f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botões de Áudio e Legenda com suporte a D-pad (Controle Remoto)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Botão de Áudio (Dual Áudio)
                var isAudioFocused by remember { mutableStateOf(false) }
                Button(
                    onClick = { showAudioDialog = true },
                    modifier = Modifier
                        .focusable()
                        .onFocusChanged { isAudioFocused = it.isFocused }
                        .border(
                            width = if (isAudioFocused) 2.dp else 0.dp,
                            color = if (isAudioFocused) Color(0xFF00E5FF) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = "Áudio",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (audioOptions.size > 1) "Áudio (Dual)" else "Áudio",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botão de Legendas (Dual Legenda)
                var isSubFocused by remember { mutableStateOf(false) }
                Button(
                    onClick = { showSubtitleDialog = true },
                    modifier = Modifier
                        .focusable()
                        .onFocusChanged { isSubFocused = it.isFocused }
                        .border(
                            width = if (isSubFocused) 2.dp else 0.dp,
                            color = if (isSubFocused) Color(0xFFFF9100) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "Legendas",
                        tint = Color(0xFFFF9100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Legenda",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Modal Seleção de Áudio (Dublado / Original / Dual)
        if (showAudioDialog) {
            AlertDialog(
                onDismissRequest = { showAudioDialog = false },
                title = {
                    Text(
                        "Selecionar Idioma / Áudio",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    if (audioOptions.isEmpty()) {
                        Text("Este canal/filme possui apenas 1 faixa de áudio.", color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(audioOptions) { _, item ->
                                var isItemFocused by remember { mutableStateOf(false) }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusable()
                                        .onFocusChanged { isItemFocused = it.isFocused }
                                        .clickable {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setOverrideForType(
                                                    TrackSelectionOverride(item.group.mediaTrackGroup, item.trackIndex)
                                                )
                                                .build()
                                            Toast.makeText(context, "Áudio: ${item.label}", Toast.LENGTH_SHORT).show()
                                            showAudioDialog = false
                                        }
                                        .border(
                                            width = if (isItemFocused) 2.dp else 0.dp,
                                            color = if (isItemFocused) Color(0xFF00E5FF) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (item.isSelected) Color(0xFF00E5FF) else Color(0xFF1E2638),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.label + if (item.isSelected) " (Em Reprodução)" else "",
                                        color = if (item.isSelected) Color.Black else Color.White,
                                        fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAudioDialog = false }) {
                        Text("Fechar", color = Color(0xFF00E5FF))
                    }
                },
                containerColor = Color(0xFF121722)
            )
        }

        // Modal Seleção de Legendas (Dual Legenda / CC)
        if (showSubtitleDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleDialog = false },
                title = {
                    Text(
                        "Selecionar Legenda",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    if (subtitleOptions.size <= 1) {
                        Text("Nenhuma legenda externa ou embutida foi detectada nesta transmissão.", color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(subtitleOptions) { _, item ->
                                var isItemFocused by remember { mutableStateOf(false) }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusable()
                                        .onFocusChanged { isItemFocused = it.isFocused }
                                        .clickable {
                                            if (item.group == null) {
                                                // Desativa legendas
                                                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                                                .build()
                                                Toast.makeText(context, "Legendas desativadas", Toast.LENGTH_SHORT).show()
                                            } else {
                                                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                                .setOverrideForType(
                                                    TrackSelectionOverride(item.group.mediaTrackGroup, item.trackIndex)
                                                )
                                                .build()
                                                Toast.makeText(context, "Legenda: ${item.label}", Toast.LENGTH_SHORT).show()
                                            }
                                            showSubtitleDialog = false
                                        }
                                        .border(
                                            width = if (isItemFocused) 2.dp else 0.dp,
                                            color = if (isItemFocused) Color(0xFFFF9100) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (item.isSelected) Color(0xFFFF9100) else Color(0xFF1E2638),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.label + if (item.isSelected) " (Ativa)" else "",
                                        color = if (item.isSelected) Color.Black else Color.White,
                                        fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSubtitleDialog = false }) {
                        Text("Fechar", color = Color(0xFFFF9100))
                    }
                },
                containerColor = Color(0xFF121722)
            )
        }
    }
}
