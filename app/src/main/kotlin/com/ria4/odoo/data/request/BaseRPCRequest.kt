package com.ria4.odoo.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Base JSON-RPC request — holds serialized args and context map for Odoo wire format.
 * Requete JSON-RPC de base — contient les args serialises et la map de contexte pour le format wire Odoo.
 */
open class BaseRPCRequest {

    @SerializedName("args")
    @Expose
    var args: MutableList<Any> = mutableListOf()

    @SerializedName("context")
    @Expose
    var context: MutableMap<String, Any> = mutableMapOf()
}

