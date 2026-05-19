package com.ria4.odoo.presentation.screen.dispatch

import com.ria4.odoo.presentation.base_mvp.base.BaseContract

/**
 * MVP contract for the dispatch screen — defines View (task guard, navigation) and Presenter interfaces.
 * Contrat MVP pour l'ecran de dispatch — definit les interfaces View (garde de tache, navigation) et Presenter.
 */
interface DispatchContract {

    interface View : BaseContract.View {

        /** Prevents re-instantiation of the entry-point activity when launched from desktop. / Empeche la re-instantiation de l'activite point d'entree lors du lancement depuis le bureau. */
        fun taskGuard()

        /** Navigates to the home screen. / Navigue vers l'ecran d'accueil. */
        fun openHomeActivity()

        /** Navigates to the login screen. / Navigue vers l'ecran de connexion. */
        fun openLoginActivity()

        /** Shows an error dialog with the given message. / Affiche un dialogue d'erreur avec le message donne. */
        fun showError(message: String?)
    }

    interface Presenter : BaseContract.Presenter<View>
}