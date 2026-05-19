package com.ria4.odoo.domain.repository

import com.ria4.odoo.data.request.CommonRPCRequest
import com.ria4.odoo.data.request.DbRPCRequest
import com.ria4.odoo.domain.entity.ServerVersion
import io.reactivex.Flowable

/** Domain-layer contract for common Odoo endpoints (server version, DB listing). / Contrat de couche domaine pour les endpoints Odoo communs (version serveur, liste des DB). */
interface CommonRepository {

    fun getServerVersion(request: CommonRPCRequest): Flowable<ServerVersion>

    fun listDb(request: DbRPCRequest): Flowable<Array<String>>

}