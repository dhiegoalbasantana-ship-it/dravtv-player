package com.dravtv.player.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

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
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var currentTracks by remember { mutableStateOf<Tracks?>(null) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    currentTracks = tracks
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Processa faixas de Áudio disponíveis
    val audioOptions = remember(currentTracks) {
        val list = mutableListOf<AudioTrackOption>()
        currentTracks?.groups?.filter { it.type == C.TRACK_TYPE_AUDIO }?.forEach { group ->
            for (i in 0 until group.length) {
                val format = group.getTrackFormat(i)
                val lang = format.language?.uppercase() ?: "Áudio"
                val name = format.label ?: if (format.channelCount > 0) "$lang (${format.channelCount}ch)" else "$lang [Faixa ${i + 1}]"
                list.add(
                    AudioTrackOption(
                        group = group,
                        trackIndex = i,
                        label = name,
                        isSelected = group.isTrackSelected(i)
                    )
                )
            }
        }
        list
    }

    // Processa faixas de Legendas disponíveis
    val subtitleOptions = remember(currentTracks) {
        val list = mutableListOf<SubtitleTrackOption>()
        // Opção Desativar Legendas
        val hasAnySelected = currentTracks?.groups?.filter { it.type == C.TRACK_TYPE_TEXT }?.any { group ->
            (0 until group.length).any { group.isTrackSelected(it) }
        } ?: false

        list.add(
            SubtitleTrackOption(
                group = null,
                trackIndex = -1,
                label = "Desativada",
                isSelected = !hasAnySelected
            )
        )

        currentTracks?.groups?.filter { it.type == C.TRACK_TYPE_TEXT }?.forEach { group ->
            for (i in 0 until group.length) {
                val format = group.getTrackFormat(i)
                val lang = format.language?.uppercase() ?: "Legenda"
                val name = format.label ?: "$lang [Faixa ${i + 1}]"
                list.add(
                    SubtitleTrackOption(
                        group = group,
                        trackIndex = i,
                        label = name,
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
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Superior com Controles Rápidos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.65f), androidx.compose.foundation.shape.CircleShape)
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

            // Botões de Áudio e Legenda
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Botão de Áudio (Dublado / Original / Dual)
                Button(
                    onClick = { showAudioDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.65f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = "Áudio",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Áudio", fontSize = 12.sp, color = Color.White)
                }

                // Botão de Legendas
                Button(
                    onClick = { showSubtitleDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.65f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "Legendas",
                        tint = Color(0xFFFF9100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Legenda", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Modal Seleção de Áudio
        if (showAudioDialog) {
            AlertDialog(
                onDismissRequest = { showAudioDialog = false },
                title = { Text("Selecionar Faixa de Áudio", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    if (audioOptions.isEmpty()) {
                        Text("Apenas 1 faixa de áudio disponível.", color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(audioOptions) { _, item ->
                                var isFocused by remember { mutableStateOf(false) }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusable()
                                        .onFocusChanged { isFocused = it.isFocused }
                                        .clickable {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setOverrideForType(
                                                    TrackSelectionOverride(item.group.mediaTrackGroup, item.trackIndex)
                                                )
                                                .build()
                                            showAudioDialog = false
                                        }
                                        .border(
                                            width = if (isFocused) 2.dp else 0.dp,
                                            color = if (isFocused) Color(0xFF00E5FF) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (item.isSelected) Color(0xFF00E5FF) else Color(0xFF1E2638),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.label + if (item.isSelected) " (Ativo)" else "",
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

        // Modal Seleção de Legendas
        if (showSubtitleDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleDialog = false },
                title = { Text("Selecionar Legendas", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(subtitleOptions) { _, item ->
                            var isFocused by remember { mutableStateOf(false) }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusable()
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .clickable {
                                        if (item.group == null) {
                                            // Desativa legendas
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                                                .build()
                                        } else {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                                .setOverrideForType(
                                                    TrackSelectionOverride(item.group.mediaTrackGroup, item.trackIndex)
                                                )
                                                .build()
                                        }
                                        showSubtitleDialog = false
                                    }
                                    .border(
                                        width = if (isFocused) 2.dp else 0.dp,
                                        color = if (isFocused) Color(0xFFFF9100) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                color = if (item.isSelected) Color(0xFFFF9100) else Color(0xFF1E2638),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.label + if (item.isSelected) " (Ativo)" else "",
                                    color = if (item.isSelected) Color.Black else Color.White,
                                    fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(14.dp)
                                )
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
