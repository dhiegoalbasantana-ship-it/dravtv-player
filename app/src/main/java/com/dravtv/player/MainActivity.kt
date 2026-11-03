package com.dravtv.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.dravtv.player.data.api.XtreamRepository
import com.dravtv.player.data.model.Category
import com.dravtv.player.data.model.Channel
import com.dravtv.player.data.model.VodMovie
import com.dravtv.player.ui.player.PlayerScreen
import com.dravtv.player.ui.screens.HomeScreen
import com.dravtv.player.ui.screens.LiveTvScreen
import com.dravtv.player.ui.screens.LoginScreen
import com.dravtv.player.ui.screens.VodScreen
import com.dravtv.player.ui.theme.DravTVTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object LiveTv : Screen()
    object Vod : Screen()
    data class Player(val url: String, val title: String, val returnScreen: Screen) : Screen()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DravTVTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

                var currentServerCode by remember { mutableStateOf("4100") }
                var savedUser by remember { mutableStateOf("") }
                var savedPass by remember { mutableStateOf("") }
                var expiryDateFormatted by remember { mutableStateOf<String?>(null) }

                // Live TV State
                var liveCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var liveChannels by remember { mutableStateOf<List<Channel>>(emptyList()) }
                var selectedLiveCategoryId by remember { mutableStateOf<String?>(null) }

                // VOD State
                var vodCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var vodMovies by remember { mutableStateOf<List<VodMovie>>(emptyList()) }
                var selectedVodCategoryId by remember { mutableStateOf<String?>(null) }

                when (val screen = currentScreen) {
                    is Screen.Login -> {
                        LoginScreen(
                            onLoginSuccess = { serverCode, user, pass ->
                                currentServerCode = serverCode
                                savedUser = user
                                savedPass = pass

                                lifecycleScope.launch {
                                    try {
                                        val api = XtreamRepository.getApi(serverCode)
                                        val authRes = api.login(user, pass)
                                        if (authRes.isSuccessful && authRes.body()?.userInfo?.auth == 1) {
                                            val rawExp = authRes.body()?.userInfo?.expDate
                                            expiryDateFormatted = rawExp?.toLongOrNull()?.let { timestamp ->
                                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                                sdf.format(Date(timestamp * 1000L))
                                            }

                                            // Carrega categorias de canais
                                            val catRes = api.getLiveCategories(user, pass)
                                            if (catRes.isSuccessful) {
                                                liveCategories = catRes.body() ?: emptyList()
                                                if (liveCategories.isNotEmpty()) {
                                                    selectedLiveCategoryId = liveCategories.first().categoryId
                                                }
                                            }

                                            currentScreen = Screen.Home
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        )
                    }

                    is Screen.Home -> {
                        HomeScreen(
                            username = savedUser,
                            serverCode = currentServerCode,
                            expiryDate = expiryDateFormatted,
                            onNavigateLive = {
                                lifecycleScope.launch {
                                    val api = XtreamRepository.getApi(currentServerCode)
                                    val streamRes = api.getLiveStreams(savedUser, savedPass)
                                    if (streamRes.isSuccessful) {
                                        liveChannels = streamRes.body() ?: emptyList()
                                    }
                                    currentScreen = Screen.LiveTv
                                }
                            },
                            onNavigateVod = {
                                lifecycleScope.launch {
                                    val api = XtreamRepository.getApi(currentServerCode)
                                    val catRes = api.getVodCategories(savedUser, savedPass)
                                    if (catRes.isSuccessful) {
                                        vodCategories = catRes.body() ?: emptyList()
                                        if (vodCategories.isNotEmpty()) {
                                            selectedVodCategoryId = vodCategories.first().categoryId
                                        }
                                    }
                                    val vodRes = api.getVodStreams(savedUser, savedPass)
                                    if (vodRes.isSuccessful) {
                                        vodMovies = vodRes.body() ?: emptyList()
                                    }
                                    currentScreen = Screen.Vod
                                }
                            },
                            onNavigateSeries = { /* Em desenvolvimento */ },
                            onLogout = {
                                currentScreen = Screen.Login
                            }
                        )
                    }

                    is Screen.LiveTv -> {
                        val filteredChannels = remember(selectedLiveCategoryId, liveChannels) {
                            if (selectedLiveCategoryId == null) liveChannels
                            else liveChannels.filter { it.categoryId == selectedLiveCategoryId }
                        }

                        LiveTvScreen(
                            categories = liveCategories,
                            channels = filteredChannels,
                            selectedCategoryId = selectedLiveCategoryId,
                            onSelectCategory = { catId ->
                                selectedLiveCategoryId = catId
                            },
                            onSelectChannel = { channel ->
                                val streamUrl = XtreamRepository.buildLiveStreamUrl(
                                    hostOrCode = currentServerCode,
                                    user = savedUser,
                                    pass = savedPass,
                                    streamId = channel.streamId,
                                    ext = channel.extension ?: "ts"
                                )
                                currentScreen = Screen.Player(streamUrl, channel.name, Screen.LiveTv)
                            },
                            onBack = {
                                currentScreen = Screen.Home
                            }
                        )
                    }

                    is Screen.Vod -> {
                        val filteredMovies = remember(selectedVodCategoryId, vodMovies) {
                            if (selectedVodCategoryId == null) vodMovies
                            else vodMovies.filter { it.categoryId == selectedVodCategoryId }
                        }

                        VodScreen(
                            categories = vodCategories,
                            movies = filteredMovies,
                            selectedCategoryId = selectedVodCategoryId,
                            onSelectCategory = { catId ->
                                selectedVodCategoryId = catId
                            },
                            onSelectMovie = { movie ->
                                val streamUrl = XtreamRepository.buildVodStreamUrl(
                                    hostOrCode = currentServerCode,
                                    user = savedUser,
                                    pass = savedPass,
                                    streamId = movie.streamId,
                                    ext = movie.containerExtension ?: "mp4"
                                )
                                currentScreen = Screen.Player(streamUrl, movie.name, Screen.Vod)
                            },
                            onBack = {
                                currentScreen = Screen.Home
                            }
                        )
                    }

                    is Screen.Player -> {
                        PlayerScreen(
                            streamUrl = screen.url,
                            title = screen.title,
                            onBack = {
                                currentScreen = screen.returnScreen
                            }
                        )
                    }
                }
            }
        }
    }
}
