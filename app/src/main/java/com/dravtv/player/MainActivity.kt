package com.dravtv.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.dravtv.player.data.api.XtreamRepository
import com.dravtv.player.data.model.*
import com.dravtv.player.ui.player.PlayerScreen
import com.dravtv.player.ui.screens.*
import com.dravtv.player.ui.theme.DravTVTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object LiveTv : Screen()
    object Vod : Screen()
    object Series : Screen()
    object Settings : Screen()
    data class Player(val url: String, val title: String, val previousScreen: Screen) : Screen()
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

                // VOD Movies State
                var vodCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var vodMovies by remember { mutableStateOf<List<VodMovie>>(emptyList()) }
                var selectedVodCategoryId by remember { mutableStateOf<String?>(null) }

                // Series State
                var seriesCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var seriesList by remember { mutableStateOf<List<SeriesItem>>(emptyList()) }
                var selectedSeriesCategoryId by remember { mutableStateOf<String?>(null) }
                var selectedSeriesDetail by remember { mutableStateOf<SeriesItem?>(null) }
                var currentEpisodes by remember { mutableStateOf<List<EpisodeItem>>(emptyList()) }
                var isLoadingEpisodes by remember { mutableStateOf(false) }

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

                                            // Carrega categorias iniciais de canais
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
                                    val moviesRes = api.getVodStreams(savedUser, savedPass)
                                    if (moviesRes.isSuccessful) {
                                        vodMovies = moviesRes.body() ?: emptyList()
                                    }
                                    currentScreen = Screen.Vod
                                }
                            },
                            onNavigateSeries = {
                                lifecycleScope.launch {
                                    val api = XtreamRepository.getApi(currentServerCode)
                                    val catRes = api.getSeriesCategories(savedUser, savedPass)
                                    if (catRes.isSuccessful) {
                                        seriesCategories = catRes.body() ?: emptyList()
                                        if (seriesCategories.isNotEmpty()) {
                                            selectedSeriesCategoryId = seriesCategories.first().categoryId
                                        }
                                    }
                                    val seriesRes = api.getSeries(savedUser, savedPass)
                                    if (seriesRes.isSuccessful) {
                                        seriesList = seriesRes.body() ?: emptyList()
                                    }
                                    currentScreen = Screen.Series
                                }
                            },
                            onNavigateSettings = {
                                currentScreen = Screen.Settings
                            },
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

                    is Screen.Series -> {
                        val filteredSeriesList = remember(selectedSeriesCategoryId, seriesList) {
                            if (selectedSeriesCategoryId == null) seriesList
                            else seriesList.filter { it.categoryId == selectedSeriesCategoryId }
                        }

                        SeriesScreen(
                            categories = seriesCategories,
                            seriesList = filteredSeriesList,
                            selectedCategoryId = selectedSeriesCategoryId,
                            onSelectCategory = { catId ->
                                selectedSeriesCategoryId = catId
                            },
                            onSelectSeries = { series ->
                                selectedSeriesDetail = series
                                isLoadingEpisodes = true
                                currentEpisodes = emptyList()
                                lifecycleScope.launch {
                                    try {
                                        val api = XtreamRepository.getApi(currentServerCode)
                                        val res = api.getSeriesInfo(savedUser, savedPass, series.seriesId)
                                        if (res.isSuccessful) {
                                            val episodesMap = res.body()?.episodes
                                            val parsedList = mutableListOf<EpisodeItem>()
                                            if (episodesMap != null && episodesMap.isJsonObject) {
                                                val jsonObject = episodesMap.asJsonObject
                                                for (key in jsonObject.keySet()) {
                                                    val seasonArr = jsonObject.getAsJsonArray(key)
                                                    seasonArr?.forEach { epElem ->
                                                        val obj = epElem.asJsonObject
                                                        parsedList.add(
                                                            EpisodeItem(
                                                                id = obj.get("id")?.asString ?: "",
                                                                episodeNum = obj.get("episode_num")?.asInt,
                                                                title = obj.get("title")?.asString ?: "Episódio",
                                                                containerExtension = obj.get("container_extension")?.asString ?: "mkv",
                                                                season = obj.get("season")?.asInt
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                            currentEpisodes = parsedList
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isLoadingEpisodes = false
                                    }
                                }
                            },
                            selectedSeries = selectedSeriesDetail,
                            episodes = currentEpisodes,
                            isLoadingEpisodes = isLoadingEpisodes,
                            onPlayEpisode = { ep ->
                                val streamUrl = XtreamRepository.buildSeriesStreamUrl(
                                    hostOrCode = currentServerCode,
                                    user = savedUser,
                                    pass = savedPass,
                                    streamId = ep.id.toIntOrNull() ?: 0,
                                    ext = ep.containerExtension ?: "mp4"
                                )
                                currentScreen = Screen.Player(streamUrl, ep.title, Screen.Series)
                            },
                            onCloseSeriesDetail = {
                                selectedSeriesDetail = null
                                currentEpisodes = emptyList()
                            },
                            onBack = {
                                currentScreen = Screen.Home
                            }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            username = savedUser,
                            serverCode = currentServerCode,
                            expiryDate = expiryDateFormatted,
                            onRefreshServer = {
                                lifecycleScope.launch {
                                    val api = XtreamRepository.getApi(currentServerCode)
                                    val catRes = api.getLiveCategories(savedUser, savedPass)
                                    if (catRes.isSuccessful) {
                                        liveCategories = catRes.body() ?: emptyList()
                                    }
                                    val streamRes = api.getLiveStreams(savedUser, savedPass)
                                    if (streamRes.isSuccessful) {
                                        liveChannels = streamRes.body() ?: emptyList()
                                    }
                                }
                            },
                            onLogout = {
                                currentScreen = Screen.Login
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
                                currentScreen = screen.previousScreen
                            }
                        )
                    }
                }
            }
        }
    }
}
