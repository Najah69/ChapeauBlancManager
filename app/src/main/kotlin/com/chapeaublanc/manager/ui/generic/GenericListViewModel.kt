package com.chapeaublanc.manager.ui.generic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chapeaublanc.manager.data.repository.OdooRepository
import com.chapeaublanc.manager.domain.model.FieldMeta
import com.chapeaublanc.manager.domain.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenericListUiState(
    val isLoading: Boolean = true,
    val modelName: String = "",
    val modelLabel: String = "",
    val records: List<Map<String, Any?>> = emptyList(),
    val fields: List<FieldMeta> = emptyList(),
    val totalCount: Int = 0,
    val offset: Int = 0,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GenericListViewModel @Inject constructor(
    private val repo: OdooRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val modelName: String = savedStateHandle["model"] ?: ""
    private val modelLabel: String = savedStateHandle["label"] ?: modelName

    private val _state = MutableStateFlow(GenericListUiState())
    val state: StateFlow<GenericListUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val fields = repo.getFields(modelName)
                val fieldNames = fields.map { it.name }.take(6) // limit columns
                val result = repo.searchModel(modelName, fields = fieldNames, limit = 80)
                _state.value = _state.value.copy(
                    isLoading = false,
                    modelName = modelName,
                    modelLabel = modelLabel,
                    records = result.records,
                    fields = fields,
                    totalCount = result.totalCount,
                    offset = 0
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            val current = _state.value
            if (current.isLoadingMore || current.records.size >= current.totalCount) return@launch
            _state.value = current.copy(isLoadingMore = true)
            try {
                val nextOffset = current.offset + 80
                val fieldNames = current.fields.map { it.name }.take(6)
                val result = repo.searchModel(
                    modelName, fields = fieldNames, offset = nextOffset, limit = 80
                )
                _state.value = _state.value.copy(
                    isLoadingMore = false,
                    records = current.records + result.records,
                    offset = nextOffset
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingMore = false,
                    error = e.message
                )
            }
        }
    }

    fun createRecord(values: Map<String, Any>) {
        viewModelScope.launch {
            try {
                repo.createRecord(modelName, values)
                load()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
