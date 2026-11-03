package com.dravtv.player.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("user_info") val userInfo: UserInfo?,
    @SerializedName("server_info") val serverInfo: ServerInfo?
)

data class UserInfo(
    @SerializedName("username") val username: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("exp_date") val expDate: String?,
    @SerializedName("is_trial") val isTrial: String?,
    @SerializedName("active_cons") val activeConnections: String?,
    @SerializedName("max_connections") val maxConnections: String?,
    @SerializedName("auth") val auth: Int?
)

data class ServerInfo(
    @SerializedName("url") val url: String?,
    @SerializedName("port") val port: String?,
    @SerializedName("https_port") val httpsPort: String?,
    @SerializedName("server_protocol") val protocol: String?,
    @SerializedName("timezone") val timezone: String?
)

data class Category(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int = 0
)

data class Channel(
    @SerializedName("num") val num: Any?,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String?,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("epg_channel_id") val epgChannelId: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("container_extension") val extension: String? = "ts"
)

data class VodMovie(
    @SerializedName("num") val num: Any?,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String?,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("rating_5based") val rating5Based: Double?,
    @SerializedName("container_extension") val containerExtension: String? = "mkv",
    @SerializedName("category_id") val categoryId: String?
)

data class SeriesItem(
    @SerializedName("num") val num: Any?,
    @SerializedName("name") val name: String,
    @SerializedName("series_id") val seriesId: Int,
    @SerializedName("cover") val cover: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("category_id") val categoryId: String?
)

data class SeriesInfoResponse(
    @SerializedName("seasons") val seasons: List<SeasonItem>?,
    @SerializedName("info") val info: SeriesInfoDetail?,
    @SerializedName("episodes") val episodes: JsonElement?
)

data class SeasonItem(
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("season_number") val seasonNumber: Int?
)

data class SeriesInfoDetail(
    @SerializedName("name") val name: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("rating") val rating: String?
)

data class EpisodeItem(
    @SerializedName("id") val id: String,
    @SerializedName("episode_num") val episodeNum: Int?,
    @SerializedName("title") val title: String,
    @SerializedName("container_extension") val containerExtension: String? = "mkv",
    @SerializedName("season") val season: Int?
)
