package com.ria4.odoo.presentation.screen.home

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.ria4.odoo.App
import com.ria4.odoo.di.scope.PerActivity
import com.ria4.odoo.domain.entity.User
import com.ria4.odoo.domain.interactor.UserInteractor
import com.ria4.odoo.presentation.base_mvp.api.ApiPresenter
import com.ria4.odoo.presentation.base_mvp.base.BaseFragment
import com.ria4.odoo.presentation.navigation.NavigationState
import com.ria4.odoo.presentation.utils.extensions.emptyString
import com.ria4.odoo.presentation.widget.navigation_view.NavigationId
import javax.inject.Inject

/**
 * Home screen presenter — manages toolbar state (arc icon, title), drawer open/close transitions, fragment navigation, and logout.
 * Presentateur de l'ecran d'accueil — gere l'etat de la toolbar (icone arc, titre), les transitions d'ouverture/fermeture du drawer, la navigation entre fragments et la deconnexion.
 */
@PerActivity
class HomePresenter @Inject constructor(private val userInteractor: UserInteractor)
    : ApiPresenter<HomeContract.View>(), HomeContract.Presenter {

    private var isArcIcon = false
    private var user: User? = null
    private var isDrawerOpened = false
    private var activeTitle = emptyString
    private var state: NavigationState? = null
    private var currentNavigationSelectedItem = 0

    /** Restores toolbar icon state and title, updates drawer user info on create. / Restaure l'etat de l'icone de la toolbar et le titre, met a jour les infos utilisateur du drawer a la creation. */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (isArcIcon || isDrawerOpened) {
            view?.setArcArrowState()
        } else {
            view?.setArcHamburgerIconState()
        }
        view?.setToolBarTitle(activeTitle)
        user?.let {
            view?.updateDrawerInfo(it)
        }
    }

    /** Initializes user from app instance and opens home fragment. / Initialise l'utilisateur depuis l'instance app et ouvre le fragment d'accueil. */
    override fun onPresenterCreate() {
        super.onPresenterCreate()
//        fetch(userInteractor.getAuthenticatedUser()) {
//            user = it
//            view?.updateDrawerInfo(it)
//        }
        user = App.instance.user
//        view?.updateDrawerInfo(user!!)
        view?.openHomeFragment()
    }

    /** Clears user cache on destroy. / Vide le cache utilisateur a la destruction. */
    override fun onPresenterDestroy() {
        super.onPresenterDestroy()
        userInteractor.clearCache()
    }

    /** Updates toolbar title and navigation item check on fragment change. / Met a jour le titre de la toolbar et coche l'element de navigation lors du changement de fragment. */
    override fun handleFragmentChanges(currentTag: String, fragment: Fragment) {
        val tag = if (fragment is BaseFragment<*, *>) {
            fragment.getTitle()
        } else {
            emptyString
        }

        view?.setToolBarTitle(tag)
        activeTitle = tag
        if (isArcIcon) {
            isArcIcon = false
            view?.setArcHamburgerIconState()
        }

        val checkPosition = when (tag) {
            NavigationId.HOME.name -> 0
            NavigationId.ABOUT.name -> 1
            else -> currentNavigationSelectedItem
        }

        if (currentNavigationSelectedItem != checkPosition) {
            currentNavigationSelectedItem = checkPosition
            view?.checkNavigationItem(currentNavigationSelectedItem)
        }
    }

    /** Switches to arrow icon when drawer opens. / Passe en icone fleche quand le drawer s'ouvre. */
    /** Switches to arrow icon when drawer opens. / Passe en icone fleche quand le drawer s'ouvre. */
    override fun handleDrawerOpen() {
        if (!isArcIcon)
            view?.setArcArrowState()
        isDrawerOpened = true
    }

    override fun handleDrawerClose() {
        if (!isArcIcon && isDrawerOpened)
            view?.setArcHamburgerIconState()
        isDrawerOpened = false
    }

    /** Logs out via interactor and navigates to login screen. / Deconnecte via l'interactor et navigue vers l'ecran de connexion. */
    override fun logOut() {
        userInteractor.logOut()
        view?.openLoginActivity()
    }

    /** Stores navigator state in memory. / Stocke l'etat du navigateur en memoire. */
    override fun saveNavigatorState(state: NavigationState?) {
        this.state = state
    }

    /** Returns saved navigator state from memory. / Retourne l'etat du navigateur sauvegarde en memoire. */
    override fun getNavigatorState(): NavigationState? {
        return state
    }
}