package com.chapeaublanc.manager.core.jsonrpc

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.atomic.AtomicInteger

class JsonRPC(private val baseUrl: String, private val httpClient: OkHttpClient) {

    private val moshi: Moshi = Moshi.Builder().build()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val idCounter = AtomicInteger(0)

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> call(
        service: String,
        method: String,
        args: List<Any>,
        kwargs: Map<String, Any> = emptyMap(),
        resultType: Class<T>
    ): T = withContext(Dispatchers.IO) {
        val requestId = idCounter.incrementAndGet()
        val payload = mapOf(
            "jsonrpc" to "2.0",
            "method" to "call",
            "params" to mapOf(
                "service" to service,
                "method" to method,
                "args" to args,
                "kwargs" to kwargs
            ),
            "id" to requestId
        )

        val json = moshi.adapter(Map::class.java).toJson(payload)
        val body = json.toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url("$baseUrl/jsonrpc")
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw OdooRpcException("Empty response")

        if (!response.isSuccessful) {
            throw OdooRpcException("HTTP ${response.code}: $responseBody")
        }

        val responseMap = moshi.adapter(Map::class.java).fromJson(responseBody)
            ?: throw OdooRpcException("Invalid JSON response")

        if (responseMap.containsKey("error")) {
            val error = responseMap["error"] as? Map<*, *>
            val message = error?.get("data")?.let { (it as? Map<*, *>)?.get("name") ?: it.toString() }
                ?: error?.get("message")?.toString()
                ?: "RPC Error"
            throw OdooRpcException(message.toString())
        }

        val result = responseMap["result"] ?: throw OdooRpcException("No result in response")

        when {
            resultType == Map::class.java -> result as T
            resultType == List::class.java -> result as T
            resultType == Int::class.java -> (result as Number).toInt() as T
            resultType == Boolean::class.java -> result as T
            resultType == String::class.java -> result as T
            resultType == Double::class.java -> (result as Number).toDouble() as T
            resultType == Long::class.java -> (result as Number).toLong() as T
            else -> {
                val adapter = moshi.adapter(resultType)
                adapter.fromJsonValue(result) as T
                    ?: throw OdooRpcException("Failed to parse result as ${resultType.simpleName}")
            }
        }
    }
}

class OdooRpcException(message: String, cause: Throwable? = null) : Exception(message, cause)
