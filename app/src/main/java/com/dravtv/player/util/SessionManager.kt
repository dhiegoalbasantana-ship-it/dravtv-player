package com.dravtv.player.util

import android.content.Context
import android.content.SharedPreferences

data class SavedLogin(
    val serverCode: String,
    val username: String,
    val password: String,
    val expiryDate: String?,
    val isLoggedIn: Boolean
)

object SessionManager {

    private const val PREFS_NAME = "dravtv_session"
    private const val KEY_SERVER_CODE = "server_code"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_EXPIRY_DATE = "expiry_date"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(
        context: Context,
        serverCode: String,
        username: String,
        password: String,
        expiryDate: String? = null
    ) {
        prefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_SERVER_CODE, serverCode)
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_EXPIRY_DATE, expiryDate)
            .apply()
    }

    fun getLastLogin(context: Context): SavedLogin {
        val p = prefs(context)
        return SavedLogin(
            serverCode = p.getString(KEY_SERVER_CODE, "4100") ?: "4100",
            username = p.getString(KEY_USERNAME, "") ?: "",
            password = p.getString(KEY_PASSWORD, "") ?: "",
            expiryDate = p.getString(KEY_EXPIRY_DATE, null),
            isLoggedIn = p.getBoolean(KEY_IS_LOGGED_IN, false)
        )
    }

    fun clearSession(context: Context) {
        // Marca como deslogado mas mantém usuário e senha salvos no cache para facilitar na TV Box
        prefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val session = getLastLogin(context)
        return session.isLoggedIn && session.username.isNotBlank() && session.password.isNotBlank()
    }
}
