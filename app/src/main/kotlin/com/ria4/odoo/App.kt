package com.ria4.odoo

import android.os.StrictMode
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import android.util.Log
//import com.squareup.leakcanary.LeakCanary
import com.ria4.odoo.di.component.ApplicationComponent
import com.ria4.odoo.di.component.DaggerApplicationComponent
import com.ria4.odoo.di.module.ApplicationModule
import com.ria4.odoo.domain.entity.User
import com.ria4.odoo.presentation.utils.extensions.MR2orAbove
import com.ria4.odoo.presentation.utils.extensions.unSafeLazy
import com.ria4.odoo.presentation.utils.extensions.log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException


/**
 * Application class — initializes Dagger DI, Fresco, logger, StrictMode, and RxJava error handler.
 * Classe Application — initialise Dagger DI, Fresco, le logger, StrictMode et le gestionnaire d'erreurs RxJava.
 */
class App : MultiDexApplication() {

    val applicationComponent: ApplicationComponent by unSafeLazy {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    // Current user / Utilisateur courant
    var user: User? = null

    companion object {
        lateinit var instance: App
    }
//
//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(base)
//        MultiDex.install(this)
//    }

    override fun onCreate() {
        super.onCreate()

//        MultiDex.install(this)

//        if (LeakCanary.isInAnalyzerProcess(this)) return
//        LeakCanary.install(this)
        instance = this
        initLogger()

        Fresco.initialize(this);

        // detectFileUriExposure requires API 18+, otherwise it throws "exposed beyond app through ClipData.Item.getUri()" / detectFileUriExposure necessite API 18+, sinon leve "exposed beyond app through ClipData.Item.getUri()"
        MR2orAbove {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
        }

        setRxJavaErrorHandler()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) Log.d("CBManager", "App started")
    }

    private fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(object : Consumer<Throwable> {
            override fun accept(e: Throwable) {
//                var e = e
                if (e is UndeliverableException) {
//                    e = e.cause
                }
                if (e is IOException) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return
                }
                if (e is InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return
                }
                if (e is NullPointerException || e is IllegalArgumentException) {
                    // that's likely a bug in the application
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    return
                }
                if (e is IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    return
                }
                log("Undeliverable exception")
                log(e)
            }
        })
    }
}
