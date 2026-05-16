package com.emage.odoo.core.jsonrpc

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class OdooVersion(
    val server_version: String = "",
    val protocol_version: Int = 0
)

@JsonClass(generateAdapter = false)
data class OdooSession(
    val uid: Int = 0,
    val session_id: String = "",
    val is_admin: Boolean = false,
    val user_context: Map<String, Any> = emptyMap()
)

@JsonClass(generateAdapter = false)
data class OdooCompany(
    val id: Int,
    val name: String,
    val is_parent: Boolean = false
)

@JsonClass(generateAdapter = false)
data class OdooUser(
    val id: Int,
    val name: String,
    val login: String,
    val company_id: List<Int> = emptyList(),
    val company_ids: List<Int> = emptyList()
)

@JsonClass(generateAdapter = false)
data class OdooMenuItem(
    val id: Int,
    val name: String,
    val parent_id: List<Int> = emptyList(),
    val action: String = "",
    val model: String = "",
    val sequence: Int = 0,
    val child_ids: List<Int> = emptyList()
)

@JsonClass(generateAdapter = false)
data class OdooModelInfo(
    val model: String,
    val name: String,
    val fields: Map<String, OdooFieldInfo> = emptyMap()
)

@JsonClass(generateAdapter = false)
data class OdooFieldInfo(
    val type: String = "",
    val string: String = "",
    val required: Boolean = false,
    val readonly: Boolean = false,
    val relation: String = "",
    val selection: List<List<String>> = emptyList()
)

@JsonClass(generateAdapter = false)
data class OdooSearchResult(
    val length: Int = 0,
    val records: List<Map<String, Any?>> = emptyList()
)
