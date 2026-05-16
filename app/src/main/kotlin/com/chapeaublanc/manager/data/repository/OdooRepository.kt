package com.chapeaublanc.manager.data.repository

import com.chapeaublanc.manager.core.auth.SessionManager
import com.chapeaublanc.manager.core.jsonrpc.safeBoolOr
import com.chapeaublanc.manager.core.jsonrpc.safeInt
import com.chapeaublanc.manager.core.jsonrpc.safeIntOr
import com.chapeaublanc.manager.core.jsonrpc.safeString
import com.chapeaublanc.manager.core.jsonrpc.safeStringOr
import com.chapeaublanc.manager.core.network.JsonRPCProvider
import com.chapeaublanc.manager.core.network.OdooApiService
import com.chapeaublanc.manager.domain.model.AppMenuItem
import com.chapeaublanc.manager.domain.model.Company
import com.chapeaublanc.manager.domain.model.FieldMeta
import com.chapeaublanc.manager.domain.model.SearchResult
import com.chapeaublanc.manager.domain.model.UserProfile
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OdooRepository @Inject constructor(
    private val api: OdooApiService,
    private val rpc: JsonRPCProvider,
    private val sessionManager: SessionManager
) {
    private var companyIds: List<Int> = emptyList()

    private val companyCtx: Map<String, Any>
        get() = mapOf("allowed_company_ids" to companyIds)

    fun setupUrl(url: String) {
        rpc.baseUrl = url
    }

    suspend fun listDatabases(): List<String> = api.listDatabases()

    suspend fun login(url: String, dbName: String, username: String, password: String): UserProfile {
        rpc.baseUrl = url
        rpc.db = dbName
        rpc.password = password
        val session = api.authenticate(dbName, username, password)
        rpc.uid = session.uid
        rpc.sessionId = session.session_id

        val userData = api.read("res.users", listOf(session.uid),
            listOf("name", "login", "company_id", "company_ids"), emptyMap())
        val user = userData.firstOrNull() ?: throw Exception("User not found")

        val ids = (user["company_ids"] as? List<*>)?.mapNotNull { it.safeInt() } ?: emptyList()
        companyIds = ids
        sessionManager.saveCompanyIds(ids)

        val companies = if (ids.isNotEmpty()) {
            api.read("res.company", ids, listOf("name", "parent_id"), companyCtx).map {
                val pid = it["parent_id"]
                Company(
                    id = it["id"].safeIntOr(0),
                    name = it["name"].safeStringOr("Unknown"),
                    isParent = when (pid) {
                        is Boolean -> !pid
                        is Number -> pid.toInt() == 0
                        null -> it["id"].safeIntOr(0) == 4
                        else -> it["id"].safeIntOr(0) == 4
                    }
                )
            }
        } else emptyList()

        val defaultCompanyId = (user["company_id"] as? List<*>)?.firstOrNull().safeIntOr(0)
        val defaultCompany = companies.find { it.id == defaultCompanyId }
            ?: companies.firstOrNull()
            ?: Company(0, "")

        return UserProfile(
            id = session.uid,
            name = user["name"].safeStringOr(username),
            login = username,
            sessionId = session.session_id ?: "",
            companies = companies,
            defaultCompany = defaultCompany
        )
    }

    suspend fun restoreSession() {
        val session = sessionManager.session.first()
        if (!session.isAuthenticated) return
        rpc.baseUrl = session.url
        rpc.db = session.db
        rpc.sessionId = session.sessionId
        rpc.uid = session.uid
        companyIds = session.companyIds
    }

    suspend fun fetchCompanies(): List<Company> {
        if (companyIds.isEmpty()) return emptyList()
        return api.read("res.company", companyIds, listOf("name", "parent_id"), companyCtx).map {
            val pid = it["parent_id"]
            Company(
                id = it["id"].safeIntOr(0),
                name = it["name"].safeStringOr("Unknown"),
                isParent = when (pid) {
                    is Boolean -> !pid
                    is Number -> pid.toInt() == 0
                    null -> it["id"].safeIntOr(0) == 4
                    else -> it["id"].safeIntOr(0) == 4
                }
            )
        }
    }

    suspend fun fetchMenus(): List<AppMenuItem> {
        val raw = api.searchRead("ir.ui.menu",
            domain = listOf(listOf("parent_id", "=", false)),
            fields = listOf("id", "name", "parent_id", "action", "sequence", "child_id"),
            context = companyCtx
        )
        return (raw["records"] as? List<*>)?.mapNotNull { r ->
            val m = r as? Map<*, *> ?: return@mapNotNull null
            AppMenuItem(
                id = m["id"].safeIntOr(0),
                name = m["name"].safeStringOr(""),
                sequence = m["sequence"].safeIntOr(0),
                action = m["action"].safeStringOr("")
            )
        } ?: emptyList()
    }

    suspend fun fetchChildMenus(parentId: Int): List<AppMenuItem> {
        val raw = api.searchRead("ir.ui.menu",
            domain = listOf(listOf("parent_id", "=", parentId)),
            fields = listOf("id", "name", "parent_id", "action", "sequence"),
            context = companyCtx
        )
        return (raw["records"] as? List<*>)?.mapNotNull { r ->
            val m = r as? Map<*, *> ?: return@mapNotNull null
            AppMenuItem(
                id = m["id"].safeIntOr(0),
                name = m["name"].safeStringOr(""),
                parentId = (m["parent_id"] as? List<*>)?.firstOrNull().safeIntOr(0),
                sequence = m["sequence"].safeIntOr(0),
                action = m["action"].safeStringOr("")
            )
        }?.sortedBy { it.sequence } ?: emptyList()
    }

    suspend fun resolveModelForAction(actionId: Int): String? {
        val raw = api.read("ir.actions.act_window", listOf(actionId), listOf("res_model"), companyCtx)
        return raw.firstOrNull()?.get("res_model")?.safeString()
    }

    suspend fun searchModel(
        model: String,
        domain: List<Any> = emptyList(),
        fields: List<String> = emptyList(),
        offset: Int = 0,
        limit: Int = 80,
        order: String = "id desc"
    ): SearchResult {
        val raw = api.searchRead(model, domain, fields, offset, limit, order, companyCtx)
        val records = (raw["records"] as? List<*>)?.mapNotNull { it as? Map<String, Any?> } ?: emptyList()
        val total = raw["length"].safeIntOr(records.size)
        return SearchResult(records, total, offset)
    }

    suspend fun readRecord(model: String, id: Int, fields: List<String> = emptyList()): Map<String, Any?> {
        return api.read(model, listOf(id), fields, companyCtx).firstOrNull() ?: emptyMap()
    }

    suspend fun createRecord(model: String, values: Map<String, Any>): Int {
        return api.create(model, values, companyCtx)
    }

    suspend fun writeRecord(model: String, id: Int, values: Map<String, Any>): Boolean {
        return api.write(model, listOf(id), values, companyCtx)
    }

    suspend fun getFields(model: String): List<FieldMeta> {
        val raw = api.fieldsGet(model)
        return raw.map { (name, data) ->
            FieldMeta(
                name = name,
                label = data["string"].safeStringOr(name),
                type = data["type"].safeStringOr("char"),
                required = data["required"].safeBoolOr(false),
                readonly = data["readonly"].safeBoolOr(false),
                relation = data["relation"].safeStringOr(""),
                options = ((data["selection"] as? List<*>)?.map {
                    val pair = it as? List<*> ?: return@map ("" to "")
                    (pair.getOrNull(0).safeStringOr("")) to (pair.getOrNull(1).safeStringOr(""))
                } ?: emptyList())
            )
        }.toList()
    }
}
