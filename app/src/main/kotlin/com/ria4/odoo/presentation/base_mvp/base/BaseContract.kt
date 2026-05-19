package com.ria4.odoo.presentation.base_mvp.base

import io.armcha.arch.BaseMVPContract

/**
 * Root MVP contract — minimal View and Presenter interfaces extending the BaseMVP library.
 * Contrat MVP racine — interfaces View et Presenter minimales etendant la librairie BaseMVP.
 */
interface BaseContract {

    interface View : BaseMVPContract.View

    interface Presenter<V : BaseMVPContract.View> : BaseMVPContract.Presenter<V>
}