package com.ria4.odoo.presentation.navigation

import androidx.fragment.app.Fragment


/**
 * Wrapper pairing a Fragment with its BackStrategy in the navigation stack.
 * / Wrapper associant un Fragment à sa stratégie de retour dans la pile de navigation.
 */
data class Screen(val fragment: Fragment, val backStrategy: BackStrategy)