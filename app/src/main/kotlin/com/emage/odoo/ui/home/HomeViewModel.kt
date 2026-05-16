package com.emage.odoo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emage.odoo.core.auth.SessionManager
import com.emage.odoo.data.repository.OdooRepository
import com.emage.odoo.domain.model.AppMenuItem
import com.emage.odoo.domain.model.Company
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val currentCompany: Company = Company(4, "Chapeau Blanc Group"),
    val companies: List<Company> = emptyList(),
    val menuItems: List<AppMenuItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: OdooRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadSession()
        loadMenus()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = sessionManager.session.first()
            _state.value = _state.value.copy(
                currentCompany = session.currentCompany
            )
        }
    }

    private fun loadMenus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val menus = repo.fetchMenus()
                _state.value = _state.value.copy(
                    isLoading = false,
                    menuItems = menus.sortedBy { it.sequence }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun switchCompany(company: Company) {
        viewModelScope.launch {
            sessionManager.setCurrentCompany(company.id, company.name)
            _state.value = _state.value.copy(currentCompany = company)
            loadMenus()
        }
    }

    fun refresh() {
        loadMenus()
    }
}
