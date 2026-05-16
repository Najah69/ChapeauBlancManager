package com.chapeaublanc.manager.core.network

import com.chapeaublanc.manager.core.jsonrpc.JsonRPC
import com.chapeaublanc.manager.core.jsonrpc.OdooRpcException
import com.chapeaublanc.manager.core.jsonrpc.OdooVersion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OdooApiService @Inject constructor(
    private val rpcClientProvider: JsonRPCProvider
) {

    private val rpc: JsonRPC get() = rpcClientProvider.get()

    /** Authenticate and get user ID. */
    suspend fun authenticate(
        db: String,
        login: String,
        password: String
    ): Int {
        return rpc.call(
            service = "common",
            method = "authenticate",
            args = listOf(db, login, password, emptyMap<String, Any>()),
            kwargs = emptyMap(),
            resultType = Int::class.java
        )
    }

    /** Get server version. */
    suspend fun version(): OdooVersion {
        return rpc.call(
            service = "common",
            method = "version",
            args = emptyList(),
            kwargs = emptyMap(),
            resultType = OdooVersion::class.java
        )
    }

    /** List databases. */
    suspend fun listDatabases(): List<String> {
        return rpc.call(
            service = "db",
            method = "list",
            args = emptyList(),
            kwargs = emptyMap(),
            resultType = List::class.java
        ) as List<String>
    }

    /** Generic search_read on any model. */
    suspend fun searchRead(
        model: String,
        domain: List<Any> = emptyList(),
        fields: List<String> = emptyList(),
        offset: Int = 0,
        limit: Int = 80,
        order: String = "id desc",
        context: Map<String, Any> = emptyMap()
    ): Map<String, Any?> {
        val kwargs = mapOf(
            "context" to context,
            "fields" to fields,
            "offset" to offset,
            "limit" to limit,
            "order" to order
        )
        return rpc.call(
            service = "object",
            method = "execute_kw",
            args = listOf(
                rpcClientProvider.db ?: throw OdooRpcException("No database selected"),
                rpcClientProvider.uid ?: throw OdooRpcException("Not authenticated"),
                rpcClientProvider.password ?: "",
                model,
                "search_read",
                listOf(domain),
                kwargs
            ),
            kwargs = emptyMap(),
            resultType = Map::class.java
        ) as Map<String, Any?>
    }

    /** Generic search_count. */
    suspend fun searchCount(
        model: String,
        domain: List<Any> = emptyList(),
        context: Map<String, Any> = emptyMap()
    ): Int {
        return rpc.call(
            service = "object",
            method = "execute_kw",
            args = listOf(
                rpcClientProvider.db ?: throw OdooRpcException("No database selected"),
                rpcClientProvider.uid ?: throw OdooRpcException("Not authenticated"),
                rpcClientProvider.password ?: "",
                model,
                "search_count",
                listOf(domain),
                mapOf("context" to context)
            ),
            kwargs = emptyMap(),
            resultType = Int::class.java
        )
    }

    /** Read a single record. */
    suspend fun read(
        model: String,
        ids: List<Int>,
        fields: List<String> = emptyList(),
        context: Map<String, Any> = emptyMap()
    ): List<Map<String, Any?>> {
        return rpc.call(
            service = "object",
            method = "execute_kw",
            args = listOf(
                rpcClientProvider.db ?: throw OdooRpcException("No database selected"),
                rpcClientProvider.uid ?: throw OdooRpcException("Not authenticated"),
                rpcClientProvider.password ?: "",
                model,
                "read",
                listOf(ids),
                mapOf("context" to context, "fields" to fields)
            ),
            kwargs = emptyMap(),
            resultType = List::class.java
        ) as List<Map<String, Any?>>
    }

    /** Create a record and return its ID. */
    suspend fun create(
        model: String,
        values: Map<String, Any>,
        context: Map<String, Any> = emptyMap()
    ): Int {
        return rpc.call(
            service = "object",
            method = "execute_kw",
            args = listOf(
                rpcClientProvider.db ?: throw OdooRpcException("No database selected"),
                rpcClientProvider.uid ?: throw OdooRpcException("Not authenticated"),
                rpcClientProvider.password ?: "",
                model,
                "create",
                listOf(values),
                mapOf("context" to context)
            ),
            kwargs = emptyMap(),
            resultType = Int::class.java
        )
    }

    /** Write values to records. */
    suspend fun write(
        model: String,
        ids: List<Int>,
        values: Map<String, Any>,
        context: Map<String, Any> = emptyMap()
    ): Boolean {
        return rpc.call(
            service = "object",
            method = "execute_kw",
            args = listOf(
                rpcClientProvider.db ?: throw OdooRpcException("No database selected"),
                rpcClientProvider.uid ?: throw OdooRpcException("Not authenticated"),
                rpcClientProvider.password ?: "",
                model,
                "write",
                listOf(ids, values),
                mapOf("context" to context)
            ),
            kwargs = emptyMap(),
            resultType = Boolean::class.java
        )
    }

    /** Get fields metadata for a model. */
    suspend fun fieldsGet(
        model: String,
        attributes: List<String> = listOf("type", "string", "required", "readonly", "relation", "selection")
    ): Map<String, Map<String, Any?>> {
        return rpc.call(
            service = "object",
            method = "execute_kw",
            args = listOf(
                rpcClientProvider.db ?: throw OdooRpcException("No database selected"),
                rpcClientProvider.uid ?: throw OdooRpcException("Not authenticated"),
                rpcClientProvider.password ?: "",
                model,
                "fields_get",
                emptyList<Any>(),
                mapOf("attributes" to attributes)
            ),
            kwargs = emptyMap(),
            resultType = Map::class.java
        ) as Map<String, Map<String, Any?>>
    }
}
