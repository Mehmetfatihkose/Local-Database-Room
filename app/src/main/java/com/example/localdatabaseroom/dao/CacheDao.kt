package com.example.localdatabaseroom.dao

import androidx.room.*
import com.example.localdatabaseroom.entities.CacheEntity

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache WHERE key = :key")
    suspend fun getCache(key: String): CacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: CacheEntity)

    @Query("DELETE FROM cache")
    suspend fun clearCache()
} 