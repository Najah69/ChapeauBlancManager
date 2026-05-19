package com.ria4.odoo.presentation.widget.navigation_view

import com.ria4.odoo.R

/** Data class representing a navigation drawer item with a NavigationId, icon resource, selection state, and icon tint color. / Classe de données représentant un élément du tiroir de navigation avec un NavigationId, une ressource d'icône, un état de sélection et une couleur de teinte d'icône. */
data class NavigationItem(val item: NavigationId,
                          val icon: Int,
                          var isSelected: Boolean = false,
                          val itemIconColor: Int = R.color.navigation_item_color) {

    val name: String
        get() = item.name

    val id: NavigationId
        get() = item
}