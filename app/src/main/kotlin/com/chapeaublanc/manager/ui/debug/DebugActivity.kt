package com.chapeaublanc.manager.ui.debug

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chapeaublanc.manager.BuildConfig
import com.chapeaublanc.manager.core.debug.ChapeauBlancLogger
import com.chapeaublanc.manager.core.debug.GistUploadResult
import com.chapeaublanc.manager.core.update.OtaUpdateManager
import com.chapeaublanc.manager.core.update.UpdateResult
import com.chapeaublanc.manager.ui.theme.OdooNativeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DebugActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OdooNativeTheme {
                DebugScreen(context = this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugScreen(context: Context) {
    val scope = rememberCoroutineScope()
    var gistToken by remember { mutableStateOf("") }
    var otaToken by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("") }
    var isWorking by remember { mutableStateOf(false) }
    var updateAvailable by remember { mutableStateOf<UpdateResult.UpdateAvailable?>(null) }
    val otaManager = remember { OtaUpdateManager(context) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Debug & Logs") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Version: ${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})",
                style = MaterialTheme.typography.labelMedium)

            Divider()

            // --- Gist ---
            Text("GitHub Gist (logs)", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = gistToken,
                onValueChange = { gistToken = it },
                label = { Text("Token GitHub (scope gist)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            isWorking = true
                            when (val r = ChapeauBlancLogger.exportAndUploadGist(context, gistToken.ifBlank { null })) {
                                is GistUploadResult.Success -> statusText = "Gist créé: ${r.gistUrl}"
                                is GistUploadResult.TokenError -> statusText = "Erreur token: ${r.message}"
                                is GistUploadResult.NetworkError -> statusText = "Erreur réseau: ${r.message}"
                                is GistUploadResult.UnknownError -> statusText = "Erreur ${r.code}: ${r.body}"
                            }
                            isWorking = false
                        }
                    },
                    enabled = !isWorking
                ) { Text("Upload Gist") }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isWorking = true
                            ChapeauBlancLogger.exportAndShare(context)
                            statusText = "Partage des logs..."
                            isWorking = false
                        }
                    },
                    enabled = !isWorking
                ) { Text("Partager") }

                OutlinedButton(
                    onClick = {
                        ChapeauBlancLogger.clearSessionBuffer()
                        statusText = "Buffer vidé"
                    },
                    enabled = !isWorking
                ) { Text("Clear") }
            }

            Divider()

            // --- OTA ---
            Text("OTA Update", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = otaToken,
                onValueChange = { otaToken = it },
                label = { Text("Token GitHub (scope repo)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            isWorking = true; statusText = "Recherche de mise à jour..."
                            when (val r = otaManager.checkForUpdate(otaToken.ifBlank { null })) {
                                is UpdateResult.UpdateAvailable -> {
                                    statusText = "Nouvelle version: ${r.tagName}"
                                    updateAvailable = r
                                }
                                is UpdateResult.UpToDate -> statusText = "Application à jour"
                                is UpdateResult.NoToken -> statusText = "Token requis"
                                is UpdateResult.NoRelease -> statusText = r.message
                                is UpdateResult.TokenError -> statusText = "Token invalide: ${r.message}"
                                is UpdateResult.NetworkError -> statusText = "Erreur réseau: ${r.message}"
                            }
                            isWorking = false
                        }
                    },
                    enabled = !isWorking
                ) { Text("Vérifier") }

                if (updateAvailable != null) {
                    Button(
                        onClick = {
                            scope.launch {
                                isWorking = true; statusText = "Téléchargement..."
                                updateAvailable?.let { otaManager.downloadAndInstall(it) }
                                isWorking = false
                            }
                        },
                        enabled = !isWorking,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) { Text("Installer") }
                }
            }

            Divider()

            // --- Status ---
            if (isWorking) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (statusText.isNotBlank()) {
                Text(
                    statusText,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
