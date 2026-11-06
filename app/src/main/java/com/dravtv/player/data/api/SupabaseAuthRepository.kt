package com.dravtv.player.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Camada 1 da Arquitetura do WatchHub:
 * Resolução do código de login numérico via Supabase oficial e
 * controle de sessão (ping periódico / commit).
 */
object SupabaseAuthRepository {

    private const val SUPABASE_URL = "https://kundkaiiwmowmivdvhzj.supabase.co"
    private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt1bmRrYWlpd21vd21pdmR2aHpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5MjAzOTcsImV4cCI6MjA5MTQ5NjM5N30.5QP-JCmsiKGJlLbSGf60eMhu0eKYnxjt4-j94TopAcA"

    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    private val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    private val httpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    sealed class CodeValidationResult {
        data class Success(
            val serverUrl: String,
            val probeToken: String? = null
        ) : CodeValidationResult()

        data class Error(val message: String) : CodeValidationResult()
    }

    /**
     * Valida o código do cliente chamando a Edge Function do Supabase:
     * POST /functions/v1/code-login-validate
     */
    suspend fun validateCode(
        code: String,
        user: String,
        pass: String
    ): CodeValidationResult = withContext(Dispatchers.IO) {
        val trimmedCode = code.trim()
        val trimmedUser = user.trim()
        val trimmedPass = pass.trim()

        try {
            val jsonBody = JSONObject().apply {
                put("code", trimmedCode)
                put("username", trimmedUser)
                put("password", trimmedPass)
                put("user_agent", "IPTVSmartersPro")
            }

            val request = Request.Builder()
                .url("$SUPABASE_URL/functions/v1/code-login-validate")
                .header("apikey", ANON_KEY)
                .header("Authorization", "Bearer $ANON_KEY")
                .header("Content-Type", "application/json")
                .header("User-Agent", "IPTVSmartersPro")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (responseBody.isNotBlank()) {
                val json = JSONObject(responseBody)

                // Verifica se veio server_dns ou server_url (mesmo em respostas de erro da nuvem, como xtream_origin_blocked)
                val serverDns = json.optString("server_dns").ifBlank {
                    json.optString("server_url")
                }
                val probeToken = json.optString("probe_token").ifBlank { null }

                if (serverDns.isNotBlank()) {
                    return@withContext CodeValidationResult.Success(
                        serverUrl = serverDns,
                        probeToken = probeToken
                    )
                }

                val error = json.optString("error")
                val message = when (error) {
                    "code_not_found" -> "Código inválido"
                    "code_full" -> "Código sem vaga para conta compartilhada"
                    "code_full_for_commercial" -> "Código sem vaga para conta comercial"
                    "reseller_limit_reached" -> "Revenda atingiu o limite de clientes"
                    "reseller_expired" -> "Assinatura vencida"
                    "reseller_inactive" -> "Conta da revenda suspensa"
                    "server_inactive" -> "Servidor indisponível"
                    "rate_limited" -> "Muitas tentativas, aguarde alguns minutos"
                    "username_taken" -> "Este usuário já está em uso por outro cliente"
                    "missing_fields" -> "Preencha todos os campos"
                    "xtream_rejected" -> "Servidor rejeitou as credenciais"
                    "xtream_unreachable" -> "Servidor não respondeu"
                    else -> {
                        val detail = json.optString("detail")
                        if (detail.isNotBlank()) "Erro: $detail" else null
                    }
                }

                if (message != null) {
                    return@withContext CodeValidationResult.Error(message)
                }
            }

            // Fallback: se não houve erro explícito e a nuvem não deu server_dns
            val fallbackUrl = XtreamRepository.resolveServerUrl(trimmedCode)
            CodeValidationResult.Success(serverUrl = fallbackUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback offline ou timeout de rede
            val fallbackUrl = XtreamRepository.resolveServerUrl(trimmedCode)
            CodeValidationResult.Success(serverUrl = fallbackUrl)
        }
    }

    /**
     * Confirma o login no Supabase após autenticação bem-sucedida:
     * POST /functions/v1/code-login-commit
     */
    suspend fun commitLogin(
        probeToken: String?,
        pass: String
    ) = withContext(Dispatchers.IO) {
        if (probeToken.isNullOrBlank()) return@withContext
        try {
            val jsonBody = JSONObject().apply {
                put("probe_token", probeToken)
                put("xtream_password", pass.trim())
                put("user_agent", "IPTVSmartersPro")
            }

            val request = Request.Builder()
                .url("$SUPABASE_URL/functions/v1/code-login-commit")
                .header("apikey", ANON_KEY)
                .header("Authorization", "Bearer $ANON_KEY")
                .header("Content-Type", "application/json")
                .header("User-Agent", "IPTVSmartersPro")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Envia ping silencioso a cada 60s para controle de conexões simultâneas:
     * POST /rest/v1/rpc/ping_session
     */
    suspend fun pingSession(clientId: String) = withContext(Dispatchers.IO) {
        if (clientId.isBlank()) return@withContext
        try {
            val jsonBody = JSONObject().apply {
                put("p_client_id", clientId.trim())
            }

            val request = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/rpc/ping_session")
                .header("apikey", ANON_KEY)
                .header("Authorization", "Bearer $ANON_KEY")
                .header("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
