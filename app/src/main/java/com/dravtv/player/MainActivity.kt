package com.dravtv.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.dravtv.player.data.api.XtreamRepository
import com.dravtv.player.data.model.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

                // Parental Control State
                var isParentalControlEnabled by remember { mutableStateOf(true) }
                var parentalPin by remember { mutableStateOf("0000") }

                fun isAdultCategory(name: String): Boolean {
                    val lower = name.lowercase()
                    return lower.contains("xxx") || lower.contains("adult") ||
                           lower.contains("hot") || lower.contains("playboy") ||
                           lower.contains("sexy") || lower.contains("venus") ||
                           lower.contains("for adult")
                }

                fun filterCategories(categories: List<Category>): List<Category> {
                    return if (!isParentalControlEnabled) categories
                    else categories.filter { !isAdultCategory(it.categoryName) }
                }

                // Live TV State
                var rawLiveCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var liveChannels by remember { mutableStateOf<List<Channel>>(emptyList()) }
                var selectedLiveCategoryId by remember { mutableStateOf<String?>(null) }

                // VOD Movies State
                var rawVodCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var vodMovies by remember { mutableStateOf<List<VodMovie>>(emptyList()) }
                var selectedVodCategoryId by remember { mutableStateOf<String?>(null) }
                var isLoadingMovies by remember { mutableStateOf(false) }

                // Series State
                var rawSeriesCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
                var seriesList by remember { mutableStateOf<List<SeriesItem>>(emptyList()) }
                var selectedSeriesCategoryId by remember { mutableStateOf<String?>(null) }
                var selectedSeriesDetail by remember { mutableStateOf<SeriesItem?>(null) }
                var currentEpisodes by remember { mutableStateOf<List<EpisodeItem>>(emptyList()) }
                var isLoadingSeries by remember { mutableStateOf(false) }
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

                                            // Carrega categorias de canais
                                            val catRes = api.getLiveCategories(user, pass)
                                            if (catRes.isSuccessful) {
                                                rawLiveCategories = catRes.body() ?: emptyList()
                                                val filtered = filterCategories(rawLiveCategories)
                                                if (filtered.isNotEmpty()) {
                                                    selectedLiveCategoryId = filtered.first().categoryId
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
                                currentScreen = Screen.Vod
                                lifecycleScope.launch {
                                    try {
                                        val api = XtreamRepository.getApi(currentServerCode)
                                        val catRes = api.getVodCategories(savedUser, savedPass)
                                        if (catRes.isSuccessful) {
                                            rawVodCategories = catRes.body() ?: emptyList()
                                            val filtered = filterCategories(rawVodCategories)
                                            val firstCat = filtered.firstOrNull()?.categoryId
                                            selectedVodCategoryId = firstCat
                                            if (firstCat != null) {
                                                isLoadingMovies = true
                                                val moviesRes = api.getVodStreams(savedUser, savedPass, categoryId = firstCat)
                                                if (moviesRes.isSuccessful) {
                                                    vodMovies = moviesRes.body() ?: emptyList()
                                                }
                                                isLoadingMovies = false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        isLoadingMovies = false
                                    }
                                }
                            },
                            onNavigateSeries = {
                                currentScreen = Screen.Series
                                lifecycleScope.launch {
                                    try {
                                        val api = XtreamRepository.getApi(currentServerCode)
                                        val catRes = api.getSeriesCategories(savedUser, savedPass)
                                        if (catRes.isSuccessful) {
                                            rawSeriesCategories = catRes.body() ?: emptyList()
                                            val filtered = filterCategories(rawSeriesCategories)
                                            val firstCat = filtered.firstOrNull()?.categoryId
                                            selectedSeriesCategoryId = firstCat
                                            if (firstCat != null) {
                                                isLoadingSeries = true
                                                val seriesRes = api.getSeries(savedUser, savedPass, categoryId = firstCat)
                                                if (seriesRes.isSuccessful) {
                                                    seriesList = seriesRes.body() ?: emptyList()
                                                }
                                                isLoadingSeries = false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        isLoadingSeries = false
                                    }
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
                        val activeCategories = filterCategories(rawLiveCategories)
                        val filteredChannels = remember(selectedLiveCategoryId, liveChannels) {
                            if (selectedLiveCategoryId == null) liveChannels
                            else liveChannels.filter { it.categoryId == selectedLiveCategoryId }
                        }

                        LiveTvScreen(
                            categories = activeCategories,
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
                        val activeCategories = filterCategories(rawVodCategories)

                        VodScreen(
                            categories = activeCategories,
                            movies = vodMovies,
                            isLoadingMovies = isLoadingMovies,
                            selectedCategoryId = selectedVodCategoryId,
                            onSelectCategory = { catId ->
                                selectedVodCategoryId = catId
                                isLoadingMovies = true
                                lifecycleScope.launch {
                                    try {
                                        val api = XtreamRepository.getApi(currentServerCode)
                                        val moviesRes = api.getVodStreams(savedUser, savedPass, categoryId = catId)
                                        if (moviesRes.isSuccessful) {
                                            vodMovies = moviesRes.body() ?: emptyList()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isLoadingMovies = false
                                    }
                                }
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
                        val activeCategories = filterCategories(rawSeriesCategories)

                        SeriesScreen(
                            categories = activeCategories,
                            seriesList = seriesList,
                            isLoadingSeries = isLoadingSeries,
                            selectedCategoryId = selectedSeriesCategoryId,
                            onSelectCategory = { catId ->
                                selectedSeriesCategoryId = catId
                                isLoadingSeries = true
                                lifecycleScope.launch {
                                    try {
                                        val api = XtreamRepository.getApi(currentServerCode)
                                        val seriesRes = api.getSeries(savedUser, savedPass, categoryId = catId)
                                        if (seriesRes.isSuccessful) {
                                            seriesList = seriesRes.body() ?: emptyList()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isLoadingSeries = false
                                    }
                                }
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
                            isParentalControlEnabled = isParentalControlEnabled,
                            onToggleParentalControl = { enabled, pin ->
                                isParentalControlEnabled = enabled
                                parentalPin = pin
                            },
                            parentalPin = parentalPin,
                            onRefreshServer = {
                                lifecycleScope.launch {
                                    val api = XtreamRepository.getApi(currentServerCode)
                                    val catRes = api.getLiveCategories(savedUser, savedPass)
                                    if (catRes.isSuccessful) {
                                        rawLiveCategories = catRes.body() ?: emptyList()
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
