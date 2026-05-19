package com.ria4.odoo.di.module

import com.google.gson.JsonObject


/** Interface for Gson-deserialized objects that can accept unknown JSON properties after parsing. / Interface pour les objets désérialisés par Gson pouvant accepter les propriétés JSON inconnues après parsing. */
interface IUnknownPropertiesConsumer {
    fun acceptUnknownProperties(jsonObject: JsonObject)
}