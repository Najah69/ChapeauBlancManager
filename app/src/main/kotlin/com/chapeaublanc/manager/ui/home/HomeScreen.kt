package com.chapeaublanc.manager.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chapeaublanc.manager.domain.model.AppMenuItem
import com.chapeaublanc.manager.domain.model.Company

private val menuIcons = mapOf(
    "crm" to Icons.Outlined.Group,
    "sale" to Icons.Outlined.ShoppingCart,
    "account" to Icons.Outlined.AccountBalance,
    "stock" to Icons.Outlined.Inventory,
    "project" to Icons.Outlined.TaskAlt,
    "pos" to Icons.Outlined.PointOfSale,
    "calendar" to Icons.Outlined.CalendarMonth,
    "contacts" to Icons.Outlined.Contacts,
    "mail" to Icons.Outlined.Chat,
    "website" to Icons.Outlined.Web,
    "photo" to Icons.Outlined.PhotoCamera,
    "dashboard" to Icons.Outlined.Dashboard,
    "settings" to Icons.Outlined.Settings,
    "default" to Icons.Outlined.Apps
)

private fun iconForMenu(menu: AppMenuItem): ImageVector {
    val name = menu.name.lowercase()
    return menuIcons.entries.firstOrNull { name.contains(it.key) }?.value
        ?: Icons.Outlined.Apps
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMenuClick: (AppMenuItem) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showCompanySwitcher by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Chapeau Blanc", style = MaterialTheme.typography.titleMedium)
                        Text(
                            state.currentCompany.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showCompanySwitcher = true }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Changer de société")
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Erreur: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Réessayer")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.menuItems) { menu ->
                    MenuCard(menu = menu, onClick = { onMenuClick(menu) })
                }
            }
        }
    }

    if (showCompanySwitcher) {
        CompanySwitcherSheet(
            companies = state.companies,
            currentCompany = state.currentCompany,
            onSelect = { company ->
                viewModel.switchCompany(company)
                showCompanySwitcher = false
            },
            onDismiss = { showCompanySwitcher = false }
        )
    }
}

@Composable
private fun MenuCard(menu: AppMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = iconForMenu(menu),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                menu.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySwitcherSheet(
    companies: List<Company>,
    currentCompany: Company,
    onSelect: (Company) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sociétés",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            companies.forEach { company ->
                ListItem(
                    headlineContent = { Text(company.name) },
                    supportingContent = {
                        if (company.isParent) Text("Holding") else Text("Filiale")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = if (company.isParent)
                                Icons.Default.CorporateFare else Icons.Default.Business,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onSelect(company) },
                    colors = if (company.id == currentCompany.id) {
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    } else ListItemDefaults.colors()
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
