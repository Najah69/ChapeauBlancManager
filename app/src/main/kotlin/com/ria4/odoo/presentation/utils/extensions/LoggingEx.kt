package com.ria4.odoo.presentation.utils.extensions

import android.util.Log

inline fun log(message: () -> Any?) {
    Log.d("CBManager", message()?.toString() ?: "null")
}

inline fun <reified T> T.withLog(): T {
    log { "${T::class.java.simpleName} $this" }
    return this
}

fun log(vararg message: () -> Any?) {
    message.forEach { log(it) }
}

fun log(message: Any?) {
    Log.d("CBManager", message?.toString() ?: "null")
}