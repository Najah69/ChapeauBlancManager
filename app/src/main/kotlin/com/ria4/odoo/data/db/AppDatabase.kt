package com.ria4.odoo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ria4.odoo.domain.entity.User

/**
 * Room database definition holding User entity, version 1.
 * / Définition de la base Room contenant l'entité User, version 1.
 */
@Database(entities = [(User::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}