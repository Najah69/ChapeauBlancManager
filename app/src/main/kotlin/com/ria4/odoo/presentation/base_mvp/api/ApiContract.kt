package com.ria4.odoo.presentation.base_mvp.api

import com.ria4.odoo.presentation.base_mvp.base.BaseContract

/**
 * API-enhanced MVP contract — adds loading/error UI hooks (show/hideLoading, showError) on top of BaseContract.
 * Contrat MVP enrichi API — ajoute des hooks UI de chargement/erreur (show/hideLoading, showError) par-dessus BaseContract.
 */
interface ApiContract {

    interface View : BaseContract.View {

        fun showLoading() {}

        fun hideLoading() {}

        fun showError(message: String?) {}
    }

    interface Presenter<V : BaseContract.View> : BaseContract.Presenter<V>
}