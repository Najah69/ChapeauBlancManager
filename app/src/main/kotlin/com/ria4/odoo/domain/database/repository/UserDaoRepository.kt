package com.ria4.odoo.domain.database.repository

import com.ria4.odoo.domain.entity.User
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Domain-layer contract for Room-backed user DAO operations (insert, query, observe).
 * / Contrat du domaine pour les opérations DAO User via Room (insertion, requête, observation).
 */
interface UserDaoRepository {

    fun isUserRepoEmpty(): Observable<Boolean>

    fun insertUsers(users: List<User>): Observable<List<Long>>

    fun updateUser(user: User): Observable<Int>

    fun loadUsers(): Observable<List<User>>

    fun loadCurrentUser(): Single<User>

}