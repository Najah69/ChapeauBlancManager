package com.ria4.odoo.di.component

import com.ria4.odoo.di.module.ActivityModule
import com.ria4.odoo.di.module.ApiModule
import com.ria4.odoo.di.module.ApplicationModule
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger singleton component providing Application-scoped dependencies (API, DB, prefs).
 * / Composant Dagger singleton fournissant les dépendances globales (API, base, préfs).
 */
@Singleton
@Component(modules = [(ApplicationModule::class), (ApiModule::class)])
interface ApplicationComponent {

    operator fun plus(activityModule: ActivityModule): ActivityComponent
}