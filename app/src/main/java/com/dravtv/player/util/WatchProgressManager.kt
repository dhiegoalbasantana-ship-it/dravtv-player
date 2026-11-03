package com.dravtv.player.util

import android.content.Context
import android.content.SharedPreferences

data class MovieProgress(
    val positionMs: Long,
    val durationMs: Long,
    val percentage: Int,
    val isCompleted: Boolean
)

data class SeriesProgress(
    val season: Int,
    val episodeNum: Int,
    val episodeId: String,
    val episodeTitle: String,
    val positionMs: Long,
    val durationMs: Long
)

object WatchProgressManager {

    private const val PREFS_NAME = "dravtv_watch_progress"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // --- FILMES (VOD) ---

    fun saveMovieProgress(context: Context, streamId: Int, positionMs: Long, durationMs: Long) {
        if (streamId <= 0 || durationMs <= 0) return
        val percentage = ((positionMs.toDouble() / durationMs.toDouble()) * 100.0).toInt().coerceIn(0, 100)
        val isCompleted = percentage >= 90 // Se assistiu 90% ou mais, marca como concluído

        prefs(context).edit()
            .putLong("vod_${streamId}_pos", positionMs)
            .putLong("vod_${streamId}_dur", durationMs)
            .putInt("vod_${streamId}_pct", percentage)
            .putBoolean("vod_${streamId}_comp", isCompleted)
            .putLong("vod_${streamId}_time", System.currentTimeMillis())
            .apply()
    }

    fun getMovieProgress(context: Context, streamId: Int): MovieProgress {
        val p = prefs(context)
        val pos = p.getLong("vod_${streamId}_pos", 0L)
        val dur = p.getLong("vod_${streamId}_dur", 0L)
        val pct = p.getInt("vod_${streamId}_pct", 0)
        val comp = p.getBoolean("vod_${streamId}_comp", false)
        return MovieProgress(pos, dur, pct, comp)
    }

    // --- SÉRIES & EPISÓDIOS ---

    fun saveSeriesProgress(
        context: Context,
        seriesId: Int,
        season: Int,
        episodeNum: Int,
        episodeId: String,
        episodeTitle: String,
        positionMs: Long,
        durationMs: Long
    ) {
        val p = prefs(context)
        val editor = p.edit()

        val epPct = if (durationMs > 0) ((positionMs.toDouble() / durationMs.toDouble()) * 100.0).toInt() else 0
        val isEpCompleted = epPct >= 90 || (positionMs > 60_000 && durationMs <= 0)

        // Salva status do episódio individual
        editor.putBoolean("ep_${episodeId}_watched", isEpCompleted || positionMs > 60_000)
        editor.putLong("ep_${episodeId}_pos", positionMs)
        editor.putLong("ep_${episodeId}_dur", durationMs)

        // Salva ponteiro da série ("onde o cliente parou na série")
        if (seriesId > 0) {
            editor.putInt("series_${seriesId}_season", season)
            editor.putInt("series_${seriesId}_ep_num", episodeNum)
            editor.putString("series_${seriesId}_ep_id", episodeId)
            editor.putString("series_${seriesId}_ep_title", episodeTitle)
            editor.putLong("series_${seriesId}_pos", positionMs)
            editor.putLong("series_${seriesId}_dur", durationMs)
            editor.putLong("series_${seriesId}_time", System.currentTimeMillis())
        }

        editor.apply()
    }

    fun getSeriesProgress(context: Context, seriesId: Int): SeriesProgress? {
        val p = prefs(context)
        if (!p.contains("series_${seriesId}_ep_id")) return null

        val season = p.getInt("series_${seriesId}_season", 1)
        val epNum = p.getInt("series_${seriesId}_ep_num", 1)
        val epId = p.getString("series_${seriesId}_ep_id", "") ?: ""
        val epTitle = p.getString("series_${seriesId}_ep_title", "Episódio $epNum") ?: "Episódio $epNum"
        val pos = p.getLong("series_${seriesId}_pos", 0L)
        val dur = p.getLong("series_${seriesId}_dur", 0L)

        return SeriesProgress(season, epNum, epId, epTitle, pos, dur)
    }

    fun isEpisodeWatched(context: Context, episodeId: String): Boolean {
        return prefs(context).getBoolean("ep_${episodeId}_watched", false)
    }

    fun getEpisodePosition(context: Context, episodeId: String): Long {
        return prefs(context).getLong("ep_${episodeId}_pos", 0L)
    }
}
