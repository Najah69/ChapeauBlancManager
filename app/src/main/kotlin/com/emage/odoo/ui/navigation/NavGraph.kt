package com.emage.odoo.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.emage.odoo.core.auth.SessionManager
import com.emage.odoo.domain.model.AppMenuItem
import com.emage.odoo.domain.model.Company
import com.emage.odoo.domain.model.UserProfile
import com.emage.odoo.ui.auth.CompanyPickerScreen
import com.emage.odoo.ui.auth.LoginScreen
import com.emage.odoo.ui.generic.GenericFormScreen
import com.emage.odoo.ui.generic.GenericListScreen
import com.emage.odoo.ui.home.HomeScreen

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
    sessionManager: SessionManager
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
            HomeScreen(
                onMenuClick = { menu ->
                    handleMenuClick(navController, menu)
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

private fun handleMenuClick(navController: NavHostController, menu: AppMenuItem) {
    when {
        menu.action.contains(",") -> {
            // Action is like "ir.actions.act_window,123"
            val actionId = menu.action.split(",").getOrNull(1)?.trim()?.toIntOrNull()
            if (actionId != null) {
                // Navigate to a sub-menu resolution screen or directly to model
                // For now, go to generic list with menu info
                val modelHint = when {
                    menu.name.contains("CRM", ignoreCase = true) -> "crm.lead"
                    menu.name.contains("Vente", ignoreCase = true) -> "sale.order"
                    menu.name.contains("Factur", ignoreCase = true) -> "account.move"
                    menu.name.contains("Contact", ignoreCase = true) -> "res.partner"
                    menu.name.contains("Stock", ignoreCase = true) -> "stock.picking"
                    menu.name.contains("Projet", ignoreCase = true) -> "project.project"
                    menu.name.contains("Tâche", ignoreCase = true) -> "project.task"
                    menu.name.contains("Photo", ignoreCase = true) -> "photo.photo"
                    menu.name.contains("Particip", ignoreCase = true) -> "photo.participant"
                    else -> "ir.ui.menu"
                }
                navController.navigate(Routes.list(modelHint, menu.name))
            }
        }

        menu.modelName.isNotBlank() -> {
            navController.navigate(Routes.list(menu.modelName, menu.name))
        }

        else -> {
            // Generic fallback — navigate by menu name
            navController.navigate(Routes.list("ir.ui.menu", menu.name))
        }
    }
}
