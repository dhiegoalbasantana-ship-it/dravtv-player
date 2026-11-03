package com.dravtv.player.util

import android.content.Context
import coil.request.ImageRequest

object ImageHelper {

    fun cleanPosterUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        var clean = url.trim()
        
        // Corrige URLs do TMDB para HTTPS seguro
        if (clean.startsWith("http://image.tmdb.org")) {
            clean = clean.replace("http://image.tmdb.org", "https://image.tmdb.org")
        } else if (clean.startsWith("http://") && clean.contains("tmdb.org")) {
            clean = clean.replace("http://", "https://")
        }

        // Redimensiona imagens TMDB de 'original' (pesadas) para 'w500' (otimizadas)
        if (clean.contains("/original/")) {
            clean = clean.replace("/original/", "/w500/")
        }

        return clean
    }

    fun buildRequest(context: Context, url: String?): ImageRequest {
        val finalUrl = cleanPosterUrl(url)
        return ImageRequest.Builder(context)
            .data(finalUrl)
            .crossfade(true)
            .build()
    }
}
