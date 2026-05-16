package com.chapeaublanc.manager.core.debug

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.chapeaublanc.manager.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ChapeauBlancLogger {

    private val tags = listOf(
        "ChapeauBlanc", "OdooRPC", "OdooAuth", "OdooRepo",
        "GenericList", "GenericForm", "HomeVM", "LoginVM",
        "NavGraph", "PUSH", "OTA", "OdooApp"
    )

    private val buffer = StringBuilder()
    private val bufferLock = Any()
    private const val MAX_BUFFER = 500_000

    // --- Public API ---

    fun log(tag: String, message: String, data: Any? = null) {
        val line = buildLogLine(tag, message, data)
        Log.d(tag, line)
        appendToBuffer(line)
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        val line = buildLogLine(tag, message, null) +
            (throwable?.let { "\n  ${it.stackTraceToString().take(2000)}" } ?: "")
        Log.e(tag, line)
        appendToBuffer("[ERROR] $line")
    }

    fun clearSessionBuffer() {
        synchronized(bufferLock) { buffer.clear() }
    }

    suspend fun exportToFile(context: Context): File {
        val content = buildLogContent()
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(context.getExternalFilesDir(null), "chapeaublanc_debug_$ts.txt")
        file.writeText(content)
        return file
    }

    suspend fun exportAndShare(context: Context) {
        val file = exportToFile(context)
        val uri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Partager les logs"))
    }

    /** Upload logs to a GitHub Gist (secret). Requires a PAT with gist scope. */
    suspend fun exportAndUploadGist(context: Context, token: String?): GistUploadResult = withContext(Dispatchers.IO) {
        val t = token ?: return@withContext GistUploadResult.TokenError("Token Gist non configuré")
        val content = buildLogContent()
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "chapeaublanc_debug_$ts.txt"

        try {
            val body = JSONObject().apply {
                put("description", "Chapeau Blanc Manager debug log — $ts")
                put("public", false)
                put("files", JSONObject().apply {
                    put(filename, JSONObject().apply {
                        put("content", content)
                    })
                })
            }

            val url = URL("https://api.github.com/gists")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "token $t")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.doOutput = true
            conn.outputStream.write(body.toString().toByteArray())

            when (conn.responseCode) {
                201 -> {
                    val response = conn.inputStream.bufferedReader().readText()
                    val htmlUrl = JSONObject(response).optString("html_url")
                    GistUploadResult.Success(htmlUrl)
                }
                401, 403 -> GistUploadResult.TokenError("Token invalide ou scope manquant (HTTP ${conn.responseCode})")
                else -> GistUploadResult.UnknownError(conn.responseCode, conn.errorStream?.bufferedReader()?.readText() ?: "")
            }
        } catch (e: java.net.UnknownHostException) {
            GistUploadResult.NetworkError("Pas d'accès internet: ${e.message}")
        } catch (e: java.net.SocketTimeoutException) {
            GistUploadResult.NetworkError("Timeout réseau: ${e.message}")
        } catch (e: Exception) {
            GistUploadResult.NetworkError("Erreur: ${e.message}")
        }
    }

    // --- Internals ---

    private fun buildLogLine(tag: String, message: String, data: Any?): String {
        val ts = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        val dataStr = data?.let { " | $it" } ?: ""
        return "$ts [$tag] $message$dataStr"
    }

    private fun appendToBuffer(line: String) {
        synchronized(bufferLock) {
            buffer.append(line).append("\n")
            if (buffer.length > MAX_BUFFER) {
                buffer.delete(0, 100_000)
                buffer.insert(0, "[buffer trimmed]\n")
            }
        }
    }

    private fun buildLogContent(): String = buildString {
        appendLine("=== Chapeau Blanc Manager Debug Log ===")
        appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        appendLine("Build type: ${BuildConfig.BUILD_TYPE}")
        appendLine("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
        appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (SDK ${Build.VERSION.SDK_INT})")
        appendLine()

        appendLine("=== Session Buffer ===")
        synchronized(bufferLock) { append(buffer.toString()) }
        appendLine()

        appendLine("=== Logcat (filtered) ===")
        append(captureLogcat())
    }

    private fun captureLogcat(): String {
        return try {
            val tagFilter = tags.joinToString(" ") { "$it:*" }
            val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-s", tagFilter))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val lines = reader.readLines()
            reader.close()
            if (lines.size > 3000) {
                buildString {
                    appendLine("[logcat truncated: ${lines.size} lines, showing last 3000]")
                    lines.takeLast(3000).forEach { appendLine(it) }
                }
            } else {
                lines.joinToString("\n")
            }
        } catch (e: Exception) {
            "logcat capture failed: ${e.message}"
        }
    }
}

sealed class GistUploadResult {
    data class Success(val gistUrl: String) : GistUploadResult()
    data class TokenError(val message: String) : GistUploadResult()
    data class NetworkError(val message: String) : GistUploadResult()
    data class UnknownError(val code: Int, val body: String) : GistUploadResult()
}
