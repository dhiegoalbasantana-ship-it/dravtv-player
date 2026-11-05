package com.dravtv.player.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object XtreamRepository {

    // Mapeamento de códigos de servidor (Watch / DravTV)
    val ROUTE_CODES = mapOf(
        // Numéricos padrão
        "4100" to "http://gm08.top",
        "4102" to "http://r2.gm08.top",
        "4103" to "http://r3.gm08.top",
        "4104" to "http://inter.gm08.top",
        
        // Aliases personalizados DravTV (gm08.top)
        "drav01" to "http://gm08.top",
        "drav02" to "http://r2.gm08.top",
        "drav03" to "http://r3.gm08.top",
        "dravinter" to "http://inter.gm08.top",

        // Aliases natv.fm
        "dravbr" to "http://natv.fm",
        "dravbr01" to "http://natv.fm",
        "dravbr02" to "http://r2.natv.fm",
        "dravinter02" to "http://inter.natv.fm",

        // Padrão de contingência
        "default" to "http://gm08.top"
    )

    private var currentHost: String = ""
    private var apiService: XtreamApiService? = null

    fun resolveServerUrl(codeOrUrl: String): String {
        val trimmed = codeOrUrl.trim().lowercase()
        return ROUTE_CODES[trimmed] ?: when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> codeOrUrl.trim()
            trimmed.contains(".") -> "http://${codeOrUrl.trim()}"
            else -> ROUTE_CODES["default"]!!
        }
    }

    fun getApi(hostOrCode: String): XtreamApiService {
        val resolvedHost = resolveServerUrl(hostOrCode)
        val sanitizedHost = if (resolvedHost.endsWith("/")) resolvedHost else "$resolvedHost/"

        if (sanitizedHost == currentHost && apiService != null) {
            return apiService!!
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(object : javax.net.ssl.X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        val sslContext = javax.net.ssl.SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }

        // Interceptor obrigatório IPTVSmartersPro para evitar erro 403 Forbidden
        val userAgentInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "IPTVSmartersPro")
                .header("Accept", "*/*")
                .header("Connection", "Keep-Alive")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as javax.net.ssl.X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(logging)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(sanitizedHost)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        currentHost = sanitizedHost
        apiService = retrofit.create(XtreamApiService::class.java)
        return apiService!!
    }

    fun buildLiveStreamUrl(hostOrCode: String, user: String, pass: String, streamId: Int, ext: String = "ts"): String {
        val host = resolveServerUrl(hostOrCode).trimEnd('/')
        val cleanExt = ext.trim().removePrefix(".")
        return "$host/live/$user/$pass/$streamId.$cleanExt"
    }

    fun buildVodStreamUrl(hostOrCode: String, user: String, pass: String, streamId: Int, ext: String = "mkv"): String {
        val host = resolveServerUrl(hostOrCode).trimEnd('/')
        val cleanExt = ext.trim().removePrefix(".")
        return "$host/movie/$user/$pass/$streamId.$cleanExt"
    }

    fun buildSeriesStreamUrl(hostOrCode: String, user: String, pass: String, streamId: Int, ext: String = "mkv"): String {
        val host = resolveServerUrl(hostOrCode).trimEnd('/')
        val cleanExt = ext.trim().removePrefix(".")
        return "$host/series/$user/$pass/$streamId.$cleanExt"
    }
}
