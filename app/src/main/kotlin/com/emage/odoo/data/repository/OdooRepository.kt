package com.emage.odoo.data.repository

import com.emage.odoo.core.network.JsonRPCProvider
import com.emage.odoo.core.network.OdooApiService
import com.emage.odoo.domain.model.AppMenuItem
import com.emage.odoo.domain.model.Company
import com.emage.odoo.domain.model.FieldMeta
import com.emage.odoo.domain.model.SearchResult
import com.emage.odoo.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OdooRepository @Inject constructor(
    private val api: OdooApiService,
    private val rpc: JsonRPCProvider
) {

    private val companyCtx: Map<String, Any>
        get() = mapOf("allowed_company_ids" to listOf(rpc.uid ?: 0))

    suspend fun login(url: String, dbName: String, username: String, password: String): UserProfile {
        rpc.baseUrl = url
        rpc.db = dbName
        rpc.password = password
        val session = api.authenticate(dbName, username, password)
        rpc.uid = session.uid
        rpc.sessionId = session.session_id

        val userData = api.read("res.users", listOf(session.uid),
            listOf("name", "login", "company_id", "company_ids"), companyCtx)
        val user = userData.firstOrNull() ?: throw Exception("User not found")

        val companyIds = (user["company_ids"] as? List<*>)?.mapNotNull { (it as? Int) } ?: emptyList()
        val companies = if (companyIds.isNotEmpty()) {
            api.read("res.company", companyIds, listOf("name"), companyCtx).map {
                Company(
                    id = (it["id"] as? Int) ?: 0,
                    name = (it["name"] as? String) ?: "Unknown",
                    isParent = (it["id"] as? Int) == 4
                )
            }
        } else emptyList()

        val defaultCompanyId = (user["company_id"] as? List<*>)?.firstOrNull() as? Int ?: 0
        val defaultCompany = companies.find { it.id == defaultCompanyId } ?: companies.firstOrNull() ?: Company(0, "")

        return UserProfile(
            id = session.uid,
            name = (user["name"] as? String) ?: username,
            login = username,
            companies = companies,
            defaultCompany = defaultCompany
        )
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
                id = (m["id"] as? Int) ?: 0,
                name = (m["name"] as? String) ?: "",
                sequence = (m["sequence"] as? Int) ?: 0,
                action = (m["action"] as? String) ?: ""
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
                id = (m["id"] as? Int) ?: 0,
                name = (m["name"] as? String) ?: "",
                parentId = (m["parent_id"] as? List<*>)?.firstOrNull() as? Int ?: 0,
                sequence = (m["sequence"] as? Int) ?: 0,
                action = (m["action"] as? String) ?: ""
            )
        }?.sortedBy { it.sequence } ?: emptyList()
    }

    suspend fun resolveModelForAction(actionId: Int): String? {
        val raw = api.read("ir.actions.act_window", listOf(actionId), listOf("res_model"), companyCtx)
        return (raw.firstOrNull()?.get("res_model") as? String)
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
        val total = (raw["length"] as? Int) ?: records.size
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
                label = (data["string"] as? String) ?: name,
                type = (data["type"] as? String) ?: "char",
                required = (data["required"] as? Boolean) ?: false,
                readonly = (data["readonly"] as? Boolean) ?: false,
                relation = (data["relation"] as? String) ?: "",
                options = ((data["selection"] as? List<*>)?.map {
                    val pair = it as? List<*> ?: return@map listOf("", "")
                    (pair.getOrNull(0) as? String).orEmpty() to (pair.getOrNull(1) as? String).orEmpty()
                } ?: emptyList())
            )
        }.toList()
    }

    suspend fun getCompanyContext(companyId: Int): Map<String, Any> {
        return mapOf(
            "allowed_company_ids" to listOf(companyId),
            "force_company" to companyId
        )
    }
}
