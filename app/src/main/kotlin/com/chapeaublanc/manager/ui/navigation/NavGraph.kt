package com.chapeaublanc.manager.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chapeaublanc.manager.core.auth.SessionManager
import com.chapeaublanc.manager.data.repository.OdooRepository
import com.chapeaublanc.manager.domain.model.AppMenuItem
import com.chapeaublanc.manager.ui.auth.LoginScreen
import com.chapeaublanc.manager.ui.generic.GenericFormScreen
import com.chapeaublanc.manager.ui.generic.GenericListScreen
import com.chapeaublanc.manager.ui.home.HomeScreen

object Routes {
    const val LOGIN = "login"
    const val COMPANY_PICKER = "company_picker"
    const val HOME = "home"
    const val GENERIC_LIST = "list/{model}/{label}"
    const val GENERIC_FORM = "form/{model}/{id}/{label}"

    fun list(model: String, label: String) = "list/$model/$label"
    fun form(model: String, id: Int = 0, label: String = "") = "form/$model/$id/$label"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager,
    repo: OdooRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val scope = rememberCoroutineScope()
            HomeScreen(
                onMenuClick = { menu ->
                    scope.launch {
                        handleMenuClick(navController, repo, menu)
                    }
                }
            )
        }

        composable(
            Routes.GENERIC_LIST,
            arguments = listOf(
                navArgument("model") { type = NavType.StringType },
                navArgument("label") { type = NavType.StringType }
            )
        ) {
            GenericListScreen(
                onBack = { navController.popBackStack() },
                onRecordClick = { model, id ->
                    navController.navigate(Routes.form(model, id, ""))
                }
            )
        }

        composable(
            Routes.GENERIC_FORM,
            arguments = listOf(
                navArgument("model") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType },
                navArgument("label") { type = NavType.StringType }
            )
        ) {
            GenericFormScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private suspend fun handleMenuClick(
    navController: NavHostController,
    repo: OdooRepository,
    menu: AppMenuItem
) {
    when {
        menu.action.contains(",") -> {
            val actionId = menu.action.split(",").getOrNull(1)?.trim()?.toIntOrNull()
            if (actionId != null) {
                val modelName = repo.resolveModelForAction(actionId)
                    ?: modelHintFromMenuName(menu.name)
                navController.navigate(Routes.list(modelName, menu.name))
            }
        }

        menu.modelName.isNotBlank() -> {
            navController.navigate(Routes.list(menu.modelName, menu.name))
        }

        else -> {
            navController.navigate(Routes.list("ir.ui.menu", menu.name))
        }
    }
}

private fun modelHintFromMenuName(name: String): String = when {
    name.contains("CRM", ignoreCase = true) -> "crm.lead"
    name.contains("Vente", ignoreCase = true) -> "sale.order"
    name.contains("Factur", ignoreCase = true) -> "account.move"
    name.contains("Contact", ignoreCase = true) -> "res.partner"
    name.contains("Stock", ignoreCase = true) -> "stock.picking"
    name.contains("Projet", ignoreCase = true) -> "project.project"
    name.contains("Tâche", ignoreCase = true) -> "project.task"
    name.contains("Photo", ignoreCase = true) -> "photo.photo"
    name.contains("Particip", ignoreCase = true) -> "photo.participant"
    else -> "ir.ui.menu"
}
