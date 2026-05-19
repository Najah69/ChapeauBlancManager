package com.ria4.odoo.presentation.utils.extensions

import com.luseen.logger.Logger


/** Convenience logging extension functions wrapping the Logger utility with inline log-and-return support. / Fonctions d'extension de journalisation pratiques enveloppant l'utilitaire Logger avec support inline log-and-return. */

inline fun log(message: () -> Any?) {
    Logger.log(message())
}

inline fun <reified T> T.withLog(): T {
    log("${T::class.java.simpleName} $this")
    return this
}

fun log(vararg message: () -> Any?) {
    message.forEach {
        log(it())
    }
}

fun log(message: Any?) {
    Logger.log(message)
}