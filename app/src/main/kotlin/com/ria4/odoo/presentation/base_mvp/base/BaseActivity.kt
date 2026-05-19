package com.ria4.odoo.presentation.base_mvp.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import io.armcha.arch.BaseMVPActivity
import com.ria4.odoo.App
import com.ria4.odoo.di.component.ActivityComponent
import com.ria4.odoo.di.module.ActivityModule
import com.ria4.odoo.presentation.navigation.Navigator
import com.ria4.odoo.presentation.utils.S
import com.ria4.odoo.presentation.utils.extensions.emptyString
import com.ria4.odoo.presentation.utils.extensions.unSafeLazy
import com.ria4.odoo.presentation.widget.MaterialDialog
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import javax.inject.Inject

/**
 * Base activity — wires navigator, Dagger DI (ActivityComponent), back-stack handling, and dialog helpers.
 * Activite de base — connecte le navigateur, l'injection Dagger (ActivityComponent), la gestion de la pile de retour et les helpers de dialogue.
 */
abstract class BaseActivity<V : BaseContract.View, P : BaseContract.Presenter<V>>
    : BaseMVPActivity<V, P>(), Navigator.FragmentChangeListener {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var inflater: LayoutInflater

    private var dialog: MaterialDialog? = null

    val activityComponent: ActivityComponent by unSafeLazy {
        getAppComponent() + ActivityModule(this)
    }

    /** Injects dependencies, wires fragment change listener, then delegates to super. / Injecte les dependances, connecte le listener de changement de fragment, puis delegue au super. */
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        navigator.fragmentChangeListener = this
        super.onCreate(savedInstanceState)
    }

    /** Dismisses any open material dialog before destroy. / Ferme tout dialogue materiel ouvert avant la destruction. */
    @CallSuper
    override fun onDestroy() {
        dialog?.dismiss()
        super.onDestroy()
    }

    /** Pops back-stack if available, otherwise delegates to system back press. / Depile la pile de retour si disponible, sinon delegue au retour systeme. */
    @CallSuper
    override fun onBackPressed() {
        if (navigator.hasBackStack())
            navigator.goBack()
        else
            super.onBackPressed()
    }

    protected abstract fun injectDependencies()

    private fun getAppComponent() = App.instance.applicationComponent

    /** Navigates to a reified fragment, preserving state by default. / Navigue vers un fragment reifie, en preservant l'etat par defaut. */
    inline protected fun <reified T : Fragment> goTo(keepState: Boolean = true,
                                                     withCustomAnimation: Boolean = false,
                                                     arg: Bundle = Bundle.EMPTY) {
        navigator.goTo<T>(keepState = keepState,
                withCustomAnimation = withCustomAnimation,
                arg = arg)
    }

    fun showDialog(title: String, message: String, buttonText: String = "Close") {
        dialog = MaterialDialog(this).apply {
            message(message)
                    .title(title)
                    .addPositiveButton(buttonText) {
                        hide()
                    }
                    .show()
        }
    }

    fun showErrorDialog(message: String?, buttonText: String = "Close") {
        showDialog(getString(S.error_title), message ?: emptyString, buttonText)
    }
}
