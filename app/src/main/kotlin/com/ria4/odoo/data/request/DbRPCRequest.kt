package com.ria4.odoo.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** DB service JSON-RPC request — allowed methods: 'db_exist', 'list', 'list_lang', 'server_version'. / Requete JSON-RPC du service DB — methodes autorisees : 'db_exist', 'list', 'list_lang', 'server_version'. */
open class DbRPCRequest(@SerializedName("method") @Expose val method: String): BaseRPCRequest() {

    @SerializedName("service")
    @Expose
    val service: String = "db"
}

