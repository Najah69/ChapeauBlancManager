package com.ria4.odoo.data.network

import com.ria4.odoo.data.pref.Preferences
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that injects Content-Type: application/json when missing.
 * / Intercepteur OkHttp injectant Content-Type: application/json si absent.
 */
class JsonHeaderInterceptor constructor(private val preferences: Preferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val contentType = chain.request().header("Content-Type")
        if (contentType.isNullOrEmpty()) {
            val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
            return chain.proceed(request)
        }

        return chain.proceed(chain.request())
    }
}