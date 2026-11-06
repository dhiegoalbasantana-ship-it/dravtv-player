package com.dravtv.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebViewAssetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Ponte JavaScript nativa entre a interface Google AI Studio e o hardware do Android.
 */
class XtreamBridge(private val context: Context, private val client: OkHttpClient) {
    private val prefs = context.getSharedPreferences("dravtv_session_pref", Context.MODE_PRIVATE)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastPlayTime = 0L

    @JavascriptInterface
    fun savePreference(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    @JavascriptInterface
    fun getPreference(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    @JavascriptInterface
    fun fetchUrl(urlStr: String): String {
        return try {
            val req = Request.Builder()
                .url(urlStr)
                .header("User-Agent", "IPTVSmartersPro")
                .header("Accept", "*/*")
                .header("Connection", "Keep-Alive")
                .build()
            client.newCall(req).execute().use { resp ->
                resp.body?.string() ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JavascriptInterface
    fun postUrl(urlStr: String, jsonBody: String, authToken: String): String {
        return try {
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = jsonBody.toRequestBody(mediaType)
            val builder = Request.Builder()
                .url(urlStr)
                .post(body)
                .header("User-Agent", "IPTVSmartersPro")
                .header("Accept", "application/json")
                .header("Connection", "Keep-Alive")
            if (authToken.isNotEmpty()) {
                builder.header("Authorization", "Bearer $authToken")
            }
            client.newCall(builder.build()).execute().use { resp ->
                resp.body?.string() ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JavascriptInterface
    fun playMedia(urlStr: String, title: String, type: String) {
        val now = System.currentTimeMillis()
        if (now - lastPlayTime < 2500L) {
            // Ignora múltiplos cliques rápidos ou duplo clique para não abrir múltiplas telas
            return
        }
        lastPlayTime = now
        if (urlStr.isBlank() || PlayerActivity.isPlayerRunning) return
        mainHandler.post {
            PlayerActivity.start(context, urlStr, title)
        }
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    private val httpClient: OkHttpClient by lazy {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
        } catch (e: Exception) {
            OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        webView = WebView(this)
        setContentView(webView)

        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain("appassets.androidplatform.net")
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = "IPTVSmartersPro"
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.addJavascriptInterface(XtreamBridge(this, httpClient), "AndroidBridge")

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return super.onConsoleMessage(consoleMessage)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val url = request.url.toString()

                // 1. Arquivos estáticos locais do Studio via WebViewAssetLoader
                if (url.contains("appassets.androidplatform.net")) {
                    val path = request.url.path ?: ""
                    if (path.contains("/aistudio/")) {
                        val fileName = request.url.lastPathSegment ?: ""
                        try {
                            val stream = assets.open("web/assets/aistudio/$fileName")
                            return WebResourceResponse("image/png", "UTF-8", stream)
                        } catch (_: Exception) {}
                    }
                    if (path.endsWith("logo_dravtv.png")) {
                        try {
                            val stream = assets.open("web/assets/logo_dravtv.png")
                            return WebResourceResponse("image/png", "UTF-8", stream)
                        } catch (_: Exception) {}
                    }
                    val intercepted = assetLoader.shouldInterceptRequest(request.url)
                    if (intercepted != null) return intercepted
                }

                // 2. Intercepta chamadas de API do Xtream Codes / Servidor gm08.top
                if (url.contains("player_api.php") || url.contains("revenda.pixbot.link") || url.contains("/api/xtream/")) {
                    try {
                        val targetUrl = if (url.contains("targetUrl=")) {
                            java.net.URLDecoder.decode(url.substringAfter("targetUrl="), "UTF-8")
                        } else {
                            url
                        }
                        val okReq = Request.Builder()
                            .url(targetUrl)
                            .header("User-Agent", "IPTVSmartersPro")
                            .header("Accept", "*/*")
                            .header("Connection", "Keep-Alive")
                            .build()
                        val okResp = httpClient.newCall(okReq).execute()
                        val bodyBytes = okResp.body?.bytes() ?: ByteArray(0)
                        val contentType = okResp.header("Content-Type", "application/json") ?: "application/json"
                        val mime = if (contentType.contains(";")) contentType.substringBefore(";") else contentType
                        return WebResourceResponse(
                            mime.trim(),
                            "UTF-8",
                            200,
                            "OK",
                            mapOf(
                                "Access-Control-Allow-Origin" to "*",
                                "Access-Control-Allow-Methods" to "GET, POST, OPTIONS",
                                "Access-Control-Allow-Headers" to "*"
                            ),
                            ByteArrayInputStream(bodyBytes)
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 3. Intercepta Capas e Imagens externas (pn96.eu, tmdb, logos) para liberação instantânea sem Erro 403
                val isImage = url.contains("pn96.eu") ||
                              url.contains("tmdb.org") ||
                              url.contains("/logo/") ||
                              url.contains(".jpg") ||
                              url.contains(".jpeg") ||
                              url.contains(".png") ||
                              url.contains(".webp")

                if (isImage && !url.contains("appassets.androidplatform.net")) {
                    try {
                        val targetUrl = if (url.contains("/original/") && url.contains("tmdb.org")) {
                            url.replace("/original/", "/w500/")
                        } else {
                            url
                        }
                        val okReq = Request.Builder()
                            .url(targetUrl)
                            .header("User-Agent", "IPTVSmartersPro")
                            .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                            .header("Referer", "http://gm08.top/")
                            .build()
                        val okResp = httpClient.newCall(okReq).execute()
                        if (okResp.isSuccessful && okResp.body != null) {
                            val bodyBytes = okResp.body!!.bytes()
                            val contentType = okResp.header("Content-Type") ?: "image/jpeg"
                            val mime = if (contentType.contains(";")) contentType.substringBefore(";").trim() else contentType.trim()
                            return WebResourceResponse(
                                mime,
                                null,
                                200,
                                "OK",
                                mapOf(
                                    "Access-Control-Allow-Origin" to "*",
                                    "Cache-Control" to "public, max-age=86400"
                                ),
                                ByteArrayInputStream(bodyBytes)
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideSystemUI()
            }
        }

        // Carrega o app oficial do Google AI Studio embutido
        webView.loadUrl("https://appassets.androidplatform.net/assets/web/index.html")

        // Ping silencioso periódico a cada 60s (WatchHub Session Control)
        lifecycleScope.launch(Dispatchers.IO) {
            val prefs = getSharedPreferences("dravtv_session_pref", Context.MODE_PRIVATE)
            val anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt1bmRrYWlpd21vd21pdmR2aHpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5MjAzOTcsImV4cCI6MjA5MTQ5NjM5N30.5QP-JCmsiKGJlLbSGf60eMhu0eKYnxjt4-j94TopAcA"
            while (true) {
                delay(60000L)
                try {
                    val sessionStr = prefs.getString("dravtv_session", null)
                    if (!sessionStr.isNullOrBlank()) {
                        val sessionJson = JSONObject(sessionStr)
                        val username = sessionJson.optString("username")
                        if (username.isNotBlank()) {
                            val jsonBody = JSONObject().apply {
                                put("p_client_id", username)
                            }
                            val req = Request.Builder()
                                .url("https://kundkaiiwmowmivdvhzj.supabase.co/rest/v1/rpc/ping_session")
                                .header("apikey", anonKey)
                                .header("Authorization", "Bearer $anonKey")
                                .header("Content-Type", "application/json")
                                .post(jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                                .build()
                            httpClient.newCall(req).execute().close()
                        }
                    }
                } catch (_: Exception) {}
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            webView.dispatchKeyEvent(event)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
