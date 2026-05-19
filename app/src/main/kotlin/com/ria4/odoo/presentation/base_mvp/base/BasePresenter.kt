package com.ria4.odoo.presentation.base_mvp.base

import io.armcha.arch.BaseMVPPresenter

/**
 * Root MVP presenter — thin wrapper over the BaseMVP library presenter.
 * Presentateur MVP racine — fine surcouche du presentateur de la librairie BaseMVP.
 */
abstract class BasePresenter<V : BaseContract.View> : BaseMVPPresenter<V>(), BaseContract.Presenter<V>