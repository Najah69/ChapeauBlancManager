package com.emage.odoo.core.network

import com.emage.odoo.core.jsonrpc.JsonRPC
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonRPCProvider @Inject constructor() {
    var baseUrl: String = ""
    var db: String? = null
    var uid: Int? = null
    var password: String? = null
    var sessionId: String? = null

    private var rpc: JsonRPC? = null

    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val cookie = sessionId?.let { "session_id=$it" }
                val request = if (cookie != null) {
                    chain.request().newBuilder()
                        .header("Cookie", cookie)
                        .build()
                } else chain.request()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun get(): JsonRPC {
        if (baseUrl.isBlank()) throw IllegalStateException("Odoo URL not configured")
        return rpc ?: JsonRPC(baseUrl, httpClient).also { rpc = it }
    }

    fun reset() {
        rpc = null
        sessionId = null
        uid = null
        db = null
        password = null
    }

    fun isAuthenticated(): Boolean = uid != null && db != null && sessionId != null
}
