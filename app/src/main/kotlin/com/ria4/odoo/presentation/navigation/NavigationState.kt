package com.ria4.odoo.presentation.navigation

/**
 * Serializable data class holding the navigator stack state (active tag, first tag, animation flag).
 * / Data class sérialisable contenant l'état de la pile de navigation (tag actif, premier tag, animation).
 */
data class NavigationState constructor(
        var activeTag: String? = null,
        var firstTag: String? = null,
        var isCustomAnimationUsed: Boolean = false) {

    fun clear() {
        activeTag = null
        firstTag = null
    }
}