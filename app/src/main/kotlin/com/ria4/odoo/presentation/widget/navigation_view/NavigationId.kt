package com.ria4.odoo.presentation.widget.navigation_view

import com.ria4.odoo.presentation.utils.extensions.emptyString

/** Sealed class defining navigation destination identifiers with optional display names. / Classe scellée définissant les identifiants de destination de navigation avec des noms d'affichage optionnels. */
sealed class NavigationId(val name: String = emptyString, val fullName: String = emptyString) {

    object HOME : NavigationId("主界面")
    object ABOUT : NavigationId("关于")
    object LOG_OUT : NavigationId("注销")
}