package com.ria4.odoo.domain.fetcher

/**
 * Sealed class representing reactive request lifecycle states (IDLE, LOADING, SUCCESS, ERROR, EMPTY_SUCCESS).
 * / Classe scellée représentant les états réactifs d'une requête (repos, chargement, succès, erreur, succès vide).
 */
sealed class Status {

    object LOADING : Status()
    object ERROR : Status()
    object SUCCESS : Status()
    object EMPTY_SUCCESS : Status()
    object IDLE : Status()
}