package com.ria4.odoo.presentation.screen.dispatch

import android.content.Intent
import com.ria4.odoo.presentation.base_mvp.base.BaseActivity
import com.ria4.odoo.presentation.screen.auth.AuthActivity
import com.ria4.odoo.presentation.screen.home.HomeActivity
import com.ria4.odoo.presentation.utils.extensions.start
import javax.inject.Inject

/**
 * Dispatch / entry-point activity — prevents re-instantiation from launcher, delegates routing to presenter.
 * Activite de dispatch / point d'entree — empeche la re-instantiation depuis le lanceur, delegue le routage au presentateur.
 */
class DispatchActivity : BaseActivity<DispatchContract.View, DispatchContract.Presenter>(), DispatchContract.View {

    @Inject
    protected lateinit var dispatchPresenter: DispatchPresenter

    override fun injectDependencies() {
        activityComponent.inject(this)
    }

    override fun initPresenter() = dispatchPresenter

    /** Prevents creating a new task instance when launched from the desktop shortcut. / Empeche de creer une nouvelle instance de tache lors du lancement depuis le raccourci bureau. */
    override fun taskGuard() {
        // Avoid re-instantiating entry-point activity when launched from desktop / Eviter de re-instancier l'activite point d'entree lors du lancement depuis le bureau
        if (!this.isTaskRoot) { // Current class is not the root of the task, so there is a prior instance / La classe actuelle n'est pas la racine de la tache, donc une instance precedente existe
            intent?.run {
                if (this.hasCategory(Intent.CATEGORY_LAUNCHER)
                        && Intent.ACTION_MAIN == this.action) { // Current class was launched from desktop / La classe actuelle a ete lancee depuis le bureau
                    finish() // Finish this activity and bring the existing one to front / Termine cette activite et ramene l'existante au premier plan
                    return
                }
            }
        }
    }

    override fun openHomeActivity() {
        start<HomeActivity>()
        finish()
    }

    override fun openLoginActivity() {
        start<AuthActivity>()
        finish()
    }

    override fun showError(message: String?) {
        showErrorDialog(message)
    }

}
