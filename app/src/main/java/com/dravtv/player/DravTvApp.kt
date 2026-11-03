package com.dravtv.player

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class DravTvApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val userAgentInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "okhttp/4.12.0")
                .header("Accept", "*/*")
                .build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .followRedirects(true)
            .followSslRedirects(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
