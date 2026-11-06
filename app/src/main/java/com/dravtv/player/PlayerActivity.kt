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

    companion object {
        fun start(context: Context, url: String, title: String) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra("EXTRA_URL", url)
                putExtra("EXTRA_TITLE", title)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
