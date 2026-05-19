package com.ria4.odoo.presentation.screen.auth

import android.content.Intent
import android.net.Uri
import com.ria4.odoo.presentation.base_mvp.api.ApiContract

/**
 * MVP contract for the authentication screen — defines View and Presenter interfaces.
 * Contrat MVP pour l'ecran d'authentification — definit les interfaces View et Presenter.
 */
interface AuthContract {

    interface View : ApiContract.View {

        /** Shows the Odoo server version on the UI. / Affiche la version du serveur Odoo sur l'UI. */
        fun showVersion(version: String)

        /** Starts OAuth flow by opening the given URI in a browser. / Lance le flux OAuth en ouvrant l'URI donnee dans un navigateur. */
        fun startOAuthIntent(uri: Uri)

        /** Navigates to the home screen after successful login. / Navigue vers l'ecran d'accueil apres connexion reussie. */
        fun openHomeActivity()
    }

    interface Presenter : ApiContract.Presenter<View> {

        /** Initializes the auth screen — fetches server version and DB list. / Initialise l'ecran d'auth — recupere la version serveur et la liste des DB. */
        fun init()

        /** Triggers the OAuth login flow. / Declenche le flux de connexion OAuth. */
        fun makeLogin()

        /** Processes the OAuth redirect intent to extract the auth code. / Traite l'intent de redirection OAuth pour extraire le code d'auth. */
        fun checkLogin(resultIntent: Intent?)
    }
}