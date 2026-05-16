package com.chapeaublanc.manager.core.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chapeaublanc.manager.domain.model.Company
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "odoo_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_URL = stringPreferencesKey("odoo_url")
        private val KEY_DB = stringPreferencesKey("odoo_db")
        private val KEY_LOGIN = stringPreferencesKey("odoo_login")
        private val KEY_SESSION_ID = stringPreferencesKey("session_id")
        private val KEY_UID = intPreferencesKey("uid")
        private val KEY_COMPANY_ID = intPreferencesKey("current_company_id")
        private val KEY_COMPANY_NAME = stringPreferencesKey("current_company_name")
        private val KEY_COMPANY_IDS = stringPreferencesKey("company_ids")
    }

    data class Session(
        val url: String = "",
        val db: String = "",
        val login: String = "",
        val sessionId: String = "",
        val uid: Int = 0,
        val currentCompany: Company = Company(0, "Chapeau Blanc Group"),
        val companyIds: List<Int> = emptyList(),
        val isAuthenticated: Boolean = false
    )

    val session: Flow<Session> = context.dataStore.data.map { prefs ->
        Session(
            url = prefs[KEY_URL] ?: "",
            db = prefs[KEY_DB] ?: "",
            login = prefs[KEY_LOGIN] ?: "",
            sessionId = prefs[KEY_SESSION_ID] ?: "",
            uid = prefs[KEY_UID] ?: 0,
            currentCompany = Company(
                id = prefs[KEY_COMPANY_ID] ?: 4,
                name = prefs[KEY_COMPANY_NAME] ?: "Chapeau Blanc Group"
            ),
            companyIds = prefs[KEY_COMPANY_IDS]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
            isAuthenticated = prefs[KEY_SESSION_ID] != null
        )
    }

    suspend fun saveAuth(url: String, db: String, login: String, sessionId: String, uid: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_URL] = url
            prefs[KEY_DB] = db
            prefs[KEY_LOGIN] = login
            prefs[KEY_SESSION_ID] = sessionId
            prefs[KEY_UID] = uid
        }
    }

    suspend fun saveCompanyIds(ids: List<Int>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_COMPANY_IDS] = ids.joinToString(",")
        }
    }

    suspend fun setCurrentCompany(companyId: Int, companyName: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_COMPANY_ID] = companyId
            prefs[KEY_COMPANY_NAME] = companyName
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
