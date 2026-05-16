package com.chapeaublanc.manager

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.chapeaublanc.manager.core.debug.ChapeauBlancLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OdooApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Increase logcat ring buffer to 4MB (prevents truncation during debug capture)
        try {
            Runtime.getRuntime().exec(arrayOf("logcat", "-G", "4M"))
        } catch (_: Exception) { }
        ChapeauBlancLogger.log("OdooApp", "Application started v${BuildConfig.VERSION_NAME}")
    }
}
