package com.ria4.odoo.domain.fetcher.result_listener

/**
 * Enum of request categories used as cache keys and listener discriminators (AUTH, COMMON, DB, MODEL, etc.).
 * / Enum des catégories de requêtes servant de clés de cache et discriminateurs (AUTH, COMMON, DB, MODEL, etc.).
 */
enum class RequestType {

    COMMON,
    DB,
    AUTH,
    MODEL,
    DOWNLOAD,
    TYPE_NONE
}