package com.ria4.odoo.di.scope

import javax.inject.Scope

/**
 * Dagger scope annotation marking bindings that live for the duration of a single Activity.
 * / Annotation de portée Dagger pour les liaisons durant le cycle de vie d'une Activity.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity