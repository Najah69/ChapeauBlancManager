package com.ria4.odoo.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Object service JSON-RPC request (service="object") for model operations. / Requete JSON-RPC du service object (service="object") pour les operations sur les modeles. */
open class ModelRPCRequest(@SerializedName("method") @Expose val method: String): BaseRPCRequest() {

    @SerializedName("service")
    @Expose
    val service: String = "object"
}

