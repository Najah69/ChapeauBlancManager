package com.ria4.odoo.presentation.screen.auth

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.ria4.odoo.data.request.BaseRPCRequest
import com.ria4.odoo.data.request.CommonRPCRequest
import com.ria4.odoo.di.scope.PerActivity
import com.ria4.odoo.domain.fetcher.result_listener.RequestType
import com.ria4.odoo.domain.interactor.UserInteractor
import com.ria4.odoo.presentation.base_mvp.api.ApiPresenter
import com.ria4.odoo.presentation.utils.extensions.log
import javax.inject.Inject

/**
 * Authentication presenter — handles server version fetch, OAuth flow, and session init.
 * Presentateur d'authentification — gere la recuperation de la version serveur, le flux OAuth et l'initialisation de session.
 */
@PerActivity
class AuthPresenter @Inject constructor(private val userInteractor: UserInteractor)
    : ApiPresenter<AuthContract.View>(), AuthContract.Presenter {

    /** Shows loading if a previous AUTH request succeeded. / Affiche le chargement si une requete AUTH precedente a reussi. */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    fun onStart() {
        if (AUTH statusIs SUCCESS)
            view?.showLoading()
    }

    /** Fetches server version then DB list on screen init. / Recupere la version serveur puis la liste des DB a l'initialisation de l'ecran. */
    override fun init() {
        // Fetch server version / Recuperer la version du serveur
        fetch(userInteractor.getServerVersion(), COMMON) {
            // Display version on screen / Afficher la version a l'ecran
            view?.showVersion(it.serverVersion)
            // Continue fetching DB list / Continuer a recuperer la liste des DB
            fetch(userInteractor.listDb(), COMMON) {it2 ->
                log(it2)
            }
        }
    }

    /** Starts the OAuth login flow (currently commented out). / Demarre le flux de connexion OAuth (actuellement en commentaire). */
    override fun makeLogin() {
//        view?.startOAuthIntent(Uri.parse(ApiConstants.LOGIN_OAUTH_URL))

    }

    /** Extracts OAuth code from redirect intent for token exchange. / Extrait le code OAuth de l'intent de redirection pour l'echange de token. */
    override fun checkLogin(resultIntent: Intent?) {
        val userCode: String? = resultIntent?.data?.getQueryParameter("code")
//        userCode?.let {
//            fetch(userInteractor.getUser(it), AUTH) {
//                view?.hideLoading()
//                view?.openHomeActivity()
//            }
//        }
    }

    /** Shows loading UI when AUTH request starts. / Affiche l'UI de chargement quand la requete AUTH demarre. */
    override fun onRequestStart(requestType: RequestType) {
        super.onRequestStart(requestType)
        if (requestType == AUTH) {
            view?.showLoading()
        }
    }

    /** Hides loading and shows error message on request failure. / Cache le chargement et affiche le message d'erreur en cas d'echec. */
    override fun onRequestError(errorMessage: String?) {
        view?.apply {
            hideLoading()
            showError(errorMessage)
        }
    }
}