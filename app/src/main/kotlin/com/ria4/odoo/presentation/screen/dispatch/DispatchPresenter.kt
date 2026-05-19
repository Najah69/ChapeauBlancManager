package com.ria4.odoo.presentation.screen.dispatch

import androidx.annotation.CallSuper
import com.ria4.odoo.App
import com.ria4.odoo.di.scope.PerActivity
import com.ria4.odoo.domain.interactor.UserInteractor
import com.ria4.odoo.presentation.base_mvp.api.ApiPresenter
import javax.inject.Inject

/**
 * Dispatch presenter — checks task root, loads current user, then routes to home or login screen on error.
 * Presentateur de dispatch — verifie la racine de tache, charge l'utilisateur courant, puis route vers l'ecran d'accueil ou de connexion en cas d'erreur.
 */
@PerActivity
class DispatchPresenter @Inject constructor(private val userInteractor: UserInteractor)
    : ApiPresenter<DispatchContract.View>(), DispatchContract.Presenter {

    /** Performs task guard, loads current user, stores in App instance, and navigates to home. / Effectue la garde de tache, charge l'utilisateur courant, le stocke dans l'instance App et navigue vers l'accueil. */
    override fun onPresenterCreate() {
        super.onPresenterCreate()

        view.taskGuard()

        fetch(userInteractor.loadCurrentUser()) {
            // Assign to Application instance / Assigner a l'instance Application
            App.instance.user = it
            // Navigate to home screen / Naviguer vers l'ecran d'accueil
            view?.openHomeActivity()
        }
    }

    /** Routes to login on null-user error, otherwise shows error dialog. / Route vers la connexion si erreur utilisateur null, sinon affiche le dialogue d'erreur. */
    override fun onRequestError(errorMessage: String?) {
        errorMessage?.run {
            // TODO: improve error routing logic / ameliorer la logique de routage des erreurs
            if (this == "The callable returned a null value") {
                view?.openLoginActivity()
                return
            }
        }
        // Show error dialog / Afficher le dialogue d'erreur
        view?.run {
            showError(errorMessage)
        }
    }
}