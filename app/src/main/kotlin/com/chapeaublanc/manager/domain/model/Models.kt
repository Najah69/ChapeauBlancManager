package com.chapeaublanc.manager.domain.model

data class Company(
    val id: Int,
    val name: String,
    val isParent: Boolean = false
)

data class UserProfile(
    val id: Int,
    val name: String,
    val login: String,
    val sessionId: String = "",
    val companies: List<Company> = emptyList(),
    val defaultCompany: Company = Company(0, "")
)

data class AppMenuItem(
    val id: Int,
    val name: String,
    val parentId: Int = 0,
    val action: String = "",
    val modelName: String = "",
    val sequence: Int = 0,
    val children: List<AppMenuItem> = emptyList()
)

data class SearchResult(
    val records: List<Map<String, Any?>>,
    val totalCount: Int,
    val offset: Int
)

data class FieldMeta(
    val name: String,
    val label: String,
    val type: String,
    val required: Boolean = false,
    val readonly: Boolean = false,
    val relation: String = "",
    val options: List<Pair<String, String>> = emptyList()
)
