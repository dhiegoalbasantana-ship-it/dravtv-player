package com.dravtv.player.data.model

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
) {
    val safeIcon: String?
        get() = sanitizeImageUrl(streamIcon)
}

data class VodMovie(
    @SerializedName("num") val num: Any?,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String?,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("rating_5based") val rating5Based: Double?,
    @SerializedName("container_extension") val containerExtension: String? = "mp4",
    @SerializedName("category_id") val categoryId: String?
) {
    val safeIcon: String?
        get() = sanitizeImageUrl(streamIcon)
}

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
) {
    val safeIcon: String?
        get() = sanitizeImageUrl(cover)
}

fun sanitizeImageUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    var clean = url.trim()
    if (clean.startsWith("http://image.tmdb.org")) {
        clean = clean.replace("http://image.tmdb.org", "https://image.tmdb.org")
    }
    return clean
}
