package com.ria4.odoo.presentation.widget.navigation_view

/** Click listener interface for navigation drawer items, providing the selected item and its position. / Interface d'écouteur de clic pour les éléments du tiroir de navigation, fournissant l'élément sélectionné et sa position. */
interface ItemClickListener {

    operator fun invoke(item: NavigationItem, position: Int)
}