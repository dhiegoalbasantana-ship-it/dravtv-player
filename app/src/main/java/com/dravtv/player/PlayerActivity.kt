package com.dravtv.player

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dravtv.player.ui.player.PlayerScreen
import com.dravtv.player.ui.theme.DravTVTheme

class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPlayerRunning = true

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val streamUrl = intent.getStringExtra("EXTRA_URL") ?: ""
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "DravTV"

        setContent {
            DravTVTheme {
                PlayerScreen(
                    streamUrl = streamUrl,
                    title = title,
                    onBack = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isPlayerRunning = false
    }

    companion object {
        @Volatile
        var isPlayerRunning = false

        fun start(context: Context, url: String, title: String) {
            if (isPlayerRunning) return
            isPlayerRunning = true
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra("EXTRA_URL", url)
                putExtra("EXTRA_TITLE", title)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }
}
