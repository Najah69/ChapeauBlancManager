package com.chapeaublanc.manager.core.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.chapeaublanc.manager.BuildConfig
import com.chapeaublanc.manager.core.debug.ChapeauBlancLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class OtaUpdateManager(private val context: Context) {

    companion object {
        private const val REPO_OWNER = "Najah69"
        private const val REPO_NAME = "ChapeauBlancManager"
    }

    suspend fun checkForUpdate(token: String?): UpdateResult = withContext(Dispatchers.IO) {
        val t = token ?: return@withContext UpdateResult.NoToken
        ChapeauBlancLogger.log("OTA", "Vérification mise à jour...")

        try {
            val url = URL("https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "token $t")
            conn.setRequestProperty("Accept", "application/vnd.github+json")

            if (conn.responseCode != 200) {
                return@withContext UpdateResult.TokenError("HTTP ${conn.responseCode}")
            }

            val body = conn.inputStream.bufferedReader().readText()
            val release = JSONObject(body)
            val tagName = release.optString("tag_name")
            val releaseBody = release.optString("body")

            val versionCode = parseVersionCode(tagName)
                ?: return@withContext UpdateResult.NoRelease("Tag invalide: $tagName")

            if (versionCode <= BuildConfig.VERSION_CODE) {
                ChapeauBlancLogger.log("OTA", "À jour (build ${BuildConfig.VERSION_CODE})")
                return@withContext UpdateResult.UpToDate
            }

            val assets = release.getJSONArray("assets")
            var apkUrl: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.optString("name").endsWith(".apk")) {
                    apkUrl = asset.optString("browser_download_url")
                    break
                }
            }

            if (apkUrl == null) {
                return@withContext UpdateResult.NoRelease("Pas d'APK dans la release")
            }

            val sha256Regex = Regex("sha256:\\s*([0-9a-fA-F]{64})")
            val checksum = sha256Regex.find(releaseBody)?.groupValues?.getOrNull(1)

            ChapeauBlancLogger.log("OTA", "Nouvelle version: $tagName (build $versionCode)")
            UpdateResult.UpdateAvailable(tagName, versionCode, apkUrl, checksum, releaseBody)
        } catch (e: java.net.UnknownHostException) {
            UpdateResult.NetworkError("Pas d'accès internet")
        } catch (e: Exception) {
            UpdateResult.NetworkError(e.message ?: "Erreur inconnue")
        }
    }

    suspend fun downloadAndInstall(
        update: UpdateResult.UpdateAvailable
    ): InstallResult = withContext(Dispatchers.IO) {
        ChapeauBlancLogger.log("OTA", "Téléchargement ${update.tagName}...")

        try {
            val cacheDir = context.externalCacheDir ?: context.cacheDir
            val apkFile = File(cacheDir, "update_${update.tagName}.apk")

            val url = URL(update.apkUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 60_000
            conn.readTimeout = 120_000
            conn.connect()
            conn.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (update.sha256 != null) {
                val actual = sha256Hex(apkFile)
                if (!actual.equals(update.sha256, ignoreCase = true)) {
                    apkFile.delete()
                    ChapeauBlancLogger.logError("OTA", "Checksum mismatch: expected ${update.sha256}, got $actual")
                    return@withContext InstallResult.ChecksumMismatch
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ChapeauBlancLogger.log("OTA", "Installation lancée")
            InstallResult.Launched
        } catch (e: Exception) {
            ChapeauBlancLogger.logError("OTA", "Échec téléchargement", e)
            InstallResult.DownloadError(e.message ?: "Erreur inconnue")
        }
    }

    private fun parseVersionCode(tag: String): Int? {
        val match = Regex("v[\\d.]+-(\\d+)").find(tag)
            ?: Regex("v(\\d+)").find(tag)
        return match?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun sha256Hex(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } > 0) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

sealed class UpdateResult {
    data class UpdateAvailable(
        val tagName: String,
        val versionCode: Int,
        val apkUrl: String,
        val sha256: String?,
        val body: String
    ) : UpdateResult()
    data object UpToDate : UpdateResult()
    data object NoToken : UpdateResult()
    data class NoRelease(val message: String) : UpdateResult()
    data class TokenError(val message: String) : UpdateResult()
    data class NetworkError(val message: String) : UpdateResult()
}

sealed class InstallResult {
    data object Launched : InstallResult()
    data object ChecksumMismatch : InstallResult()
    data class DownloadError(val message: String) : InstallResult()
}
