package com.ria4.odoo.data.exception;

const val UNKNOWN_EXCEPTION = -1001
const val NO_EXPECTED_DATA_EXCEPTION = -1002

/** Base exception for API errors carrying an error code. / Exception de base pour les erreurs API portant un code d'erreur. */
open class ApiException: Throwable {
    val code: Int

    constructor(message:String?): super(message) {
        this.code = UNKNOWN_EXCEPTION
    }

    constructor(code:Int, message:String?): super(message) {
        this.code = code
    }
}

/** Exception thrown when a network failure occurs during an API request. / Exception levée lorsqu'une défaillance réseau survient lors d'une requête API. */
class NetworkErrorException: ApiException {
    constructor(message:String?): super(message)
}