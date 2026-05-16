package com.chapeaublanc.manager.ui.generic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericFormScreen(
    viewModel: GenericFormViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.recordId == 0) "Nouveau" else state.modelLabel
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.save() },
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Enregistrer")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.isSaved -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Enregistré", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBack) { Text("Retour") }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    state.fields.forEach { field ->
                        if (field.name != "id" && !field.readonly) {
                            val value = state.values[field.name]
                            Spacer(Modifier.height(8.dp))

                            when {
                                field.type == "many2one" || field.type == "many2many" -> {
                                    // For relation fields, show a simple text input with the ID
                                    OutlinedTextField(
                                        value = value?.toString() ?: "",
                                        onValueChange = { viewModel.setValue(field.name, it) },
                                        label = { Text("${field.label}${if (field.required) " *" else ""}") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                field.type == "selection" && field.options.isNotEmpty() -> {
                                    var expanded by remember { mutableStateOf(false) }
                                    val currentLabel = field.options
                                        .firstOrNull { it.first == value?.toString() }?.second
                                        ?: value?.toString() ?: ""

                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = it }
                                    ) {
                                        OutlinedTextField(
                                            value = currentLabel,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("${field.label}${if (field.required) " *" else ""}") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                            },
                                            modifier = Modifier.fillMaxWidth().menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            field.options.forEach { (key, label) ->
                                                DropdownMenuItem(
                                                    text = { Text(label) },
                                                    onClick = {
                                                        viewModel.setValue(field.name, key)
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                field.type == "boolean" -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Switch(
                                            checked = value == true,
                                            onCheckedChange = { viewModel.setValue(field.name, it) }
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("${field.label}${if (field.required) " *" else ""}")
                                    }
                                }

                                field.type == "integer" || field.type == "float" || field.type == "monetary" -> {
                                    OutlinedTextField(
                                        value = value?.toString() ?: "",
                                        onValueChange = { viewModel.setValue(field.name, it) },
                                        label = { Text("${field.label}${if (field.required) " *" else ""}") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = if (field.type == "integer")
                                                KeyboardType.Number else KeyboardType.Decimal
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                field.type == "text" || field.type == "html" -> {
                                    OutlinedTextField(
                                        value = value?.toString() ?: "",
                                        onValueChange = { viewModel.setValue(field.name, it) },
                                        label = { Text("${field.label}${if (field.required) " *" else ""}") },
                                        minLines = 3,
                                        maxLines = 8,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                else -> {
                                    OutlinedTextField(
                                        value = value?.toString() ?: "",
                                        onValueChange = { viewModel.setValue(field.name, it) },
                                        label = { Text("${field.label}${if (field.required) " *" else ""}") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    if (state.error != null) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            state.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
