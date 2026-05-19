package com.ria4.odoo.presentation.screen.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.ria4.odoo.R
import com.ria4.odoo.presentation.base_mvp.base.BaseActivity
import com.ria4.odoo.presentation.screen.home.HomeActivity
import com.ria4.odoo.presentation.utils.extensions.onClick
import com.ria4.odoo.presentation.utils.extensions.showToast
import com.ria4.odoo.presentation.utils.extensions.start
import com.ria4.odoo.presentation.utils.extensions.takeColor
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.progress_bar.*
import javax.inject.Inject

/**
 * OAuth authentication screen — handles server version display, token-based login, and OAuth redirect flow.
 * Ecran d'authentification OAuth — gere l'affichage de la version serveur, la connexion par token et le flux de redirection OAuth.
 */
class AuthActivity : BaseActivity<AuthContract.View, AuthContract.Presenter>(), AuthContract.View {

    @Inject
    protected lateinit var authPresenter: AuthPresenter

    override fun initPresenter(): AuthContract.Presenter = authPresenter

    override fun injectDependencies() {
        activityComponent.inject(this)
    }

    /** Inflates layout and wires login button click to presenter. / Gonfle le layout et lie le clic du bouton login au presentateur. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        progressBar.backgroundCircleColor = takeColor(com.ria4.odoo.R.color.colorPrimary)
        login.onClick {
            presenter.makeLogin()
        }

        presenter.init()
    }

    /** Displays server version string on the auth screen. / Affiche la version du serveur sur l'ecran d'auth. */
    override fun showVersion(version: String) {
        textView2.text = textView2.text.toString() + version
    }

    /** Shows loading indicator and disables login button. / Affiche l'indicateur de chargement et desactive le bouton login. */
    override fun showLoading() {
        progressBar.start()
        login.isClickable = false
    }

    /** Hides loading indicator and re-enables login button. / Cache l'indicateur de chargement et reactive le bouton login. */
    override fun hideLoading() {
        progressBar.stop()
        login.isClickable = true
    }

    /** Dismisses the progress bar on destroy to avoid leaks. / Ferme la barre de progression a la destruction pour eviter les fuites. */
    override fun onDestroy() {
        progressBar.dismiss()
        super.onDestroy()
    }

    /** Shows a dialog with the given error message. / Affiche un dialogue avec le message d'erreur donne. */
    override fun showError(message: String?) {
        showErrorDialog(message)
    }

    /** Launches an external browser for the OAuth authorization URI. / Lance un navigateur externe pour l'URI d'autorisation OAuth. */
    override fun startOAuthIntent(uri: Uri) {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    /** Navigates to HomeActivity after successful authentication and shows a toast. / Navigue vers HomeActivity apres authentification reussie et affiche un toast. */
    override fun openHomeActivity() {
        start<HomeActivity>()
        finish()
        showToast(getString(com.ria4.odoo.R.string.logged_in_message))
    }

    /** Delegates OAuth redirect intent processing to the presenter. / Delegue le traitement de l'intent de redirection OAuth au presentateur. */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        presenter.checkLogin(intent)
    }

}
