package com.ria4.odoo.domain.fetcher.result_listener

/**
 * Listener interface for reactive request lifecycle callbacks (start, error) with optional RequestType.
 * / Interface d'écoute pour les callbacks réactifs de requête (début, erreur) avec RequestType optionnel.
 */
interface ResultListener {

    fun onRequestStart(){}

    fun onRequestStart(requestType: RequestType){}

    fun onRequestError(errorMessage: String?){}

    fun onRequestError(requestType: RequestType, errorMessage: String?){}
}