package com.ria4.odoo.presentation.navigation

import com.ria4.odoo.presentation.utils.Experimental

/**
 * Navigation back strategy: KEEP retains the fragment on back, DESTROY removes it.
 * / Stratégie de retour : KEEP conserve le fragment au retour, DESTROY le supprime.
 */
@Experimental
sealed class BackStrategy {

    object KEEP : BackStrategy()
    object DESTROY : BackStrategy()
}