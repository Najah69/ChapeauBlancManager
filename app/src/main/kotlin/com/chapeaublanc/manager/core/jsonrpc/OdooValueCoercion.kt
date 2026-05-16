package com.chapeaublanc.manager.core.jsonrpc

/** Safe number coercion for Odoo JSON-RPC values — Moshi parses all JSON numbers as Double. */
fun Any?.safeInt(): Int? = when (this) {
    is Int -> this
    is Double -> if (this == this.toLong().toDouble()) this.toInt() else null
    is Long -> this.toInt()
    is Number -> this.toInt()
    is String -> this.toIntOrNull()
    else -> null
}

fun Any?.safeIntOr(def: Int): Int = safeInt() ?: def
fun Any?.safeString(): String? = this as? String
fun Any?.safeStringOr(def: String): String = safeString() ?: def
fun Any?.safeBool(): Boolean? = this as? Boolean
fun Any?.safeBoolOr(def: Boolean): Boolean = safeBool() ?: def
