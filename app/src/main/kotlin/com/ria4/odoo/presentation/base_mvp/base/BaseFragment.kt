package com.ria4.odoo.presentation.base_mvp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.armcha.arch.BaseMVPFragment
import com.ria4.odoo.di.component.ActivityComponent
import com.ria4.odoo.presentation.navigation.BackStrategy
import com.ria4.odoo.presentation.navigation.Navigator
import com.ria4.odoo.presentation.utils.Experimental
import com.ria4.odoo.presentation.utils.extensions.emptyString
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import javax.inject.Inject

/**
 * Base fragment — wires navigator, DI, layout inflation, and fragment navigation helpers.
 * Fragment de base — connecte le navigateur, l'injection de dependances, le gonflage de layout et les helpers de navigation entre fragments.
 */
abstract class BaseFragment<V : BaseContract.View, P : BaseContract.Presenter<V>> : BaseMVPFragment<V, P>() {

    @Inject
    lateinit var navigator: Navigator

    protected lateinit var activityComponent: ActivityComponent
    protected lateinit var activity: BaseActivity<*, *>

    /** Extracts activity component and injects dependencies on attach. / Extrait le composant d'activite et injecte les dependances a l'attachement. */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.activity = context
            activityComponent = activity.activityComponent
            injectDependencies()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    /** Navigates to a reified fragment with optional back-strategy control. / Navigue vers un fragment reifie avec controle optionnel de la strategie de retour. */
    inline fun <reified T : Fragment> goTo(keepState: Boolean = true,
                                           withCustomAnimation: Boolean = false,
                                           arg: Bundle = Bundle.EMPTY,
                                           @Experimental
                                           backStrategy: BackStrategy = BackStrategy.KEEP) {
        navigator.goTo<T>(keepState = keepState, withCustomAnimation = withCustomAnimation, arg = arg, backStrategy = backStrategy)
    }

    protected abstract fun injectDependencies()

    protected abstract val layoutResId: Int

    open fun getTitle(): String = emptyString

    fun showDialog(title: String, message: String, buttonText: String = "Close") {
        activity.showDialog(title, message, buttonText)
    }

    fun showErrorDialog(message: String?, buttonText: String = "Close") {
        activity.showDialog(getString(com.ria4.odoo.R.string.error_title), message ?: emptyString, buttonText)
    }
}