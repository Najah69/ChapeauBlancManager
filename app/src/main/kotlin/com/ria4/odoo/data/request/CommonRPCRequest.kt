package com.ria4.odoo.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Common service JSON-RPC request (method + service="common"). / Requete JSON-RPC du service common (method + service="common"). */
open class CommonRPCRequest(@SerializedName("method") @Expose val method: String): BaseRPCRequest() {

    @SerializedName("service")
    @Expose
    val service: String = "common"
}

