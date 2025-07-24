package com.example.localdatabaseroom.database

import android.content.Context
import androidx.room.*
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.entities.CacheEntity
import com.example.localdatabaseroom.dao.UserDao
import com.example.localdatabaseroom.dao.CacheDao

@Database(
    entities = [UserEntity::class, CacheEntity::class], 
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cacheDao(): CacheDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "offline_app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
} 