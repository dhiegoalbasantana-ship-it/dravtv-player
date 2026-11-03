package com.dravtv.player.util

import android.content.Context
import coil.request.CachePolicy
import coil.request.ImageRequest

object ImageHelper {

    fun cleanPosterUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        var clean = url.trim().removeSurrounding("\"").removeSurrounding("'")

        // Otimiza imagens do TMDB de 'original' (10MB-20MB) para 'w500' (~100KB) para não travar a TV Box
        if (clean.contains("tmdb.org") && clean.contains("/original/")) {
            clean = clean.replace("/original/", "/w500/")
        }

        return clean
    }

    fun buildRequest(context: Context, url: String?): ImageRequest {
        val finalUrl = cleanPosterUrl(url)
        return ImageRequest.Builder(context)
            .data(finalUrl)
            .allowHardware(false)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}
