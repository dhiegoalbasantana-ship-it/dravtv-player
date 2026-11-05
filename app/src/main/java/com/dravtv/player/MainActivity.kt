package com.dravtv.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.webkit.WebViewAssetLoader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class XtreamBridge(private val context: Context) {
    @JavascriptInterface
    fun fetchUrl(urlStr: String): String {
        return try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 15000
            conn.readTimeout = 40000
            conn.setRequestProperty("User-Agent", "IPTVSmartersPro")
            conn.setRequestProperty("Accept", "*/*")
            conn.doInput = true
            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val reader = BufferedReader(InputStreamReader(inputStream ?: return ""))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()
            conn.disconnect()
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Força paisagem (Landscape) para TVs e dispositivos móveis
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Mantém tela sempre ligada e em tela cheia imersiva (cinema/IPTV)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        webView = WebView(this)
        setContentView(webView)

        // AssetLoader seguro para carregar arquivos locais sem bloqueio de CORS / ES Modules
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
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = "Mozilla/5.0 (Linux; Android 14; DravTV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 IPTVSmartersPro"
        }

        // Bridge nativo Android para o Xtream Codes (evita CORS, Mixed Content e bloqueio Cloudflare)
        webView.addJavascriptInterface(XtreamBridge(this), "AndroidBridge")

        // Suporte a reprodução de vídeo em tela cheia e alertas
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
                return intercepted ?: super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideSystemUI()
            }
        }

        // Carrega o app do Google AI Studio embutido no aplicativo
        webView.loadUrl("https://appassets.androidplatform.net/assets/web/index.html")

        // Tratamento do botão Voltar
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
        // Envia teclas do controle remoto da TV diretamente para navegação no aplicativo
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
