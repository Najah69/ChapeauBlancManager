package com.ria4.odoo.di.component

import com.ria4.odoo.di.module.ActivityModule
import com.ria4.odoo.di.scope.PerActivity
import com.ria4.odoo.presentation.screen.auth.AuthActivity
import com.ria4.odoo.presentation.screen.dispatch.DispatchActivity
import com.ria4.odoo.presentation.screen.home.HomeActivity
import dagger.Subcomponent

/**
 * Dagger subcomponent scoped to Activity lifecycle, injected into Activities.
 * / Sous-composant Dagger lié au cycle de vie Activity, injecté dans les Activity.
 */

@PerActivity
@Subcomponent(modules = [(ActivityModule::class)])
interface ActivityComponent {

    fun inject(homeActivity: HomeActivity)

    fun inject(authActivity: AuthActivity)

    fun inject(dispatchActivity: DispatchActivity)

}