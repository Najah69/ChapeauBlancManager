package com.ria4.odoo.data.db

import com.ria4.odoo.domain.database.repository.UserDaoRepository
import com.ria4.odoo.domain.entity.User
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/** Room-backed User DAO repository — wraps UserDao with RxJava for local user persistence. / Depot DAO utilisateur avec Room — enveloppe UserDao avec RxJava pour la persistance locale de l'utilisateur. */
class UserDatabaseRepository @Inject internal constructor(private val userDao: UserDao) : UserDaoRepository {
    /** Loads current user via Single (throws on null, unlike Flowable which would silently skip). / Charge l'utilisateur courant via Single (leve une erreur sur null, contrairement a Flowable qui ignorerait). */
    override fun loadCurrentUser(): Single<User> {
        // Single used because loadCurrentUser returns null → Single throws error, while Flowable would ignore / Single utilise car loadCurrentUser retourne null → Single leve une erreur, tandis que Flowable ignorerait
        // just() always runs on the current thread, so even if it looks async it's synchronous / just() s'execute toujours sur le thread courant, donc meme si ca semble asynchrone c'est synchrone
        return Single.fromCallable<User> {  userDao.loadCurrentUser() }
    }

    override fun updateUser(user: User): Observable<Int> {
        return Observable.just(userDao.update(user))
    }

    override fun isUserRepoEmpty(): Observable<Boolean> = Observable.fromCallable{ userDao.loadAll().isEmpty() }

    override fun insertUsers(users: List<User>): Observable<List<Long>> {
        return Observable.fromCallable {
            if (users.run { any { it.current } }) {
                // Mark other users' current=false in DB / Marquer les autres utilisateurs current=false dans la DB
                userDao.deactivateCurrent()
            }
            userDao.insertAll(users)
        }
    }

    override fun loadUsers(): Observable<List<User>> = Observable.fromCallable{ userDao.loadAll() }
}