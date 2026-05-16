package com.emage.odoo.ui.generic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericListScreen(
    viewModel: GenericListViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onRecordClick: (model: String, id: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Detect when scrolled near bottom for load more
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.isLoadingMore) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.modelLabel.ifBlank { state.modelName }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && state.records.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Erreur: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.load() }) { Text("Réessayer") }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "${state.totalCount} enregistrement(s)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(state.records) { record ->
                    val id = (record["id"] as? Int) ?: 0
                    val displayName = record["display_name"]?.toString()
                        ?: record["name"]?.toString()
                        ?: record["x_name"]?.toString()
                        ?: "#$id"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecordClick(state.modelName, id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            // Show first 3 non-id, non-display fields
                            state.fields
                                .filter { it.name != "id" && it.name != "display_name" && it.name != "name" }
                                .take(3)
                                .forEach { field ->
                                    val value = record[field.name]
                                    if (value != null && value != false && value != "" && value != 0) {
                                        val display = when (value) {
                                            is List<*> -> value.joinToString(", ") { it.toString().take(60) }
                                            is Map<*, *> -> (value["name"] ?: value["id"] ?: value.toString()).toString()
                                            else -> value.toString()
                                        }.take(80)
                                        Text(
                                            "${field.label}: $display",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                        }
                    }
                }

                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}
