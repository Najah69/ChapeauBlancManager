package com.ria4.odoo.presentation.screen.home

import androidx.fragment.app.Fragment
import com.ria4.odoo.domain.entity.User
import com.ria4.odoo.presentation.base_mvp.base.BaseContract
import com.ria4.odoo.presentation.navigation.NavigationState

/**
 * MVP contract for the home screen — defines View (drawer, toolbar, navigation) and Presenter interfaces.
 * Contrat MVP pour l'ecran d'accueil — definit les interfaces View (drawer, toolbar, navigation) et Presenter.
 */
interface HomeContract {

    interface View : BaseContract.View {

        /** Opens the default home fragment. / Ouvre le fragment d'accueil par defaut. */
        fun openHomeFragment()

        /** Navigates to login screen after logout. / Navigue vers l'ecran de connexion apres deconnexion. */
        fun openLoginActivity()

        /** Sets arc button to back-arrow mode. / Passe le bouton arc en mode fleche retour. */
        fun setArcArrowState()

        /** Sets arc button to hamburger menu mode. / Passe le bouton arc en mode menu hamburger. */
        fun setArcHamburgerIconState()

        /** Updates the animated toolbar title. / Met a jour le titre anime de la toolbar. */
        fun setToolBarTitle(title: String)

        /** Populates drawer header with user avatar and info. / Remplit l'en-tete du drawer avec l'avatar et les infos utilisateur. */
        fun updateDrawerInfo(user: User)

        /** Highlights the given navigation item in the drawer. / Surligne l'element de navigation donne dans le drawer. */
        fun checkNavigationItem(position: Int)
    }

    interface Presenter : BaseContract.Presenter<View> {

        /** Logs out the current user and clears cache. / Deconnecte l'utilisateur actuel et vide le cache. */
        fun logOut()

        /** Persists the current navigation state. / Persiste l'etat actuel de navigation. */
        fun saveNavigatorState(state: NavigationState?)

        /** Retrieves the saved navigation state. / Recupere l'etat de navigation sauvegarde. */
        fun getNavigatorState(): NavigationState?

        /** Handles toolbar title and icon changes when fragments change. / Gere les changements de titre et d'icone de la toolbar lors des changements de fragment. */
        fun handleFragmentChanges(currentTag: String,fragment: Fragment)

        /** Switches arc icon to arrow when drawer opens. / Passe l'icone arc en fleche quand le drawer s'ouvre. */
        fun handleDrawerOpen()

        /** Switches arc icon back to hamburger when drawer closes. / Repasse l'icone arc en hamburger quand le drawer se ferme. */
        fun handleDrawerClose()
    }
}