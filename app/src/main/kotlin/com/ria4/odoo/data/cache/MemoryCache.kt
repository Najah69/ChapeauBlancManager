package com.ria4.odoo.data.cache

import android.util.LruCache
import com.ria4.odoo.domain.fetcher.result_listener.RequestType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory LRU cache for API responses, keyed by RequestType.
 * / Cache mémoire LRU pour les réponses d'API, indexé par RequestType.
 */
@Singleton
class MemoryCache @Inject constructor() : LruCache<RequestType, Any>(1024 * 1024 * 2)/* 2 MB */ {

    inline infix fun <reified V> getCacheForType(key: RequestType) = get(key) as V

    infix fun hasCacheFor(requestType: RequestType) = getCacheForType<Any?>(requestType) != null

    infix fun clearCacheFor(requestType: RequestType) {
        remove(requestType)
    }

    fun clearCache() {
        evictAll()
    }
}