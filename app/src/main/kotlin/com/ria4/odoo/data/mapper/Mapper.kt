package com.ria4.odoo.data.mapper

import com.ria4.odoo.data.response.*
import com.ria4.odoo.domain.entity.*
import com.ria4.odoo.domain.entity.ServerVersion
import com.ria4.odoo.presentation.utils.extensions.emptyString
import com.ria4.odoo.presentation.utils.extensions.toHtml
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gson-based mapper converting JSON-RPC response DTOs to domain entities.
 * / Mappeur basé sur Gson convertissant les DTO JSON-RPC en entités du domaine.
 */
class Mapper {

//    @JvmName("translateShotEntity")
//    fun translate(shotResponseList: List<ShotResponse>): List<Shot> {
//        return shotResponseList
//                .asSequence()
//                .map {
//                    translate(it)
//                }
//                .toList()
//    }

    fun translate(serverVersion: com.ria4.odoo.data.response.ServerVersion): ServerVersion {
        return ServerVersion(
                serverVersion.serverVersion,
                serverVersion.serverVersionInfo?.joinToString("-") ?: "",
                serverVersion.serverSerie,
                serverVersion.protocolVersion)
    }

}