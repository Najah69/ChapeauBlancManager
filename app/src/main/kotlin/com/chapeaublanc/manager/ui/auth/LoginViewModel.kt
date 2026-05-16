package com.chapeaublanc.manager.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chapeaublanc.manager.core.auth.SessionManager
import com.chapeaublanc.manager.data.repository.OdooRepository
import com.chapeaublanc.manager.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val databases: List<String> = emptyList(),
    val selectedDb: String = "",
    val userProfile: UserProfile? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: OdooRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun fetchDatabases(url: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                repo.setupUrl(url)
                val dbs = repo.listDatabases()
                _state.value = _state.value.copy(
                    isLoading = false,
                    databases = dbs
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Impossible de lister les bases: ${e.message}"
                )
            }
        }
    }

    fun login(url: String, db: String, username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val profile = repo.login(url, db, username, password)
                sessionManager.saveAuth(url, db, username, profile.sessionId, profile.id)
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userProfile = profile
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Échec de connexion: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
