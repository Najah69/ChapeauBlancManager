package com.chapeaublanc.manager.ui.generic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chapeaublanc.manager.data.repository.OdooRepository
import com.chapeaublanc.manager.domain.model.FieldMeta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenericFormUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val modelName: String = "",
    val modelLabel: String = "",
    val recordId: Int = 0,
    val values: MutableMap<String, Any?> = mutableMapOf(),
    val fields: List<FieldMeta> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class GenericFormViewModel @Inject constructor(
    private val repo: OdooRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val modelName: String = savedStateHandle["model"] ?: ""
    private val modelLabel: String = savedStateHandle["label"] ?: modelName
    private val recordId: Int = savedStateHandle["id"]?.toIntOrNull() ?: 0

    private val _state = MutableStateFlow(GenericFormUiState())
    val state: StateFlow<GenericFormUiState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val fields = repo.getFields(modelName)
                val values = if (recordId > 0) {
                    repo.readRecord(modelName, recordId, fields.map { it.name })
                        .toMutableMap()
                } else mutableMapOf()

                _state.value = _state.value.copy(
                    isLoading = false,
                    modelName = modelName,
                    modelLabel = modelLabel,
                    recordId = recordId,
                    fields = fields,
                    values = values
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun setValue(fieldName: String, value: Any?) {
        val updated = _state.value.values.toMutableMap()
        updated[fieldName] = value
        _state.value = _state.value.copy(values = updated)
    }

    fun save() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            try {
                val values = _state.value.values.filterKeys { it != "id" }
                    .mapValues { (_, v) -> v ?: false }

                if (recordId > 0) {
                    repo.writeRecord(modelName, recordId, values as Map<String, Any>)
                } else {
                    repo.createRecord(modelName, values as Map<String, Any>)
                }
                _state.value = _state.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }
}
