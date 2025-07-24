package com.example.localdatabaseroom.repository

import com.example.localdatabaseroom.dao.CacheDao
import com.example.localdatabaseroom.entities.CacheEntity

class CacheRepository(private val cacheDao: CacheDao) {
    suspend fun getCache(key: String) = cacheDao.getCache(key)
    suspend fun insertCache(cache: CacheEntity) = cacheDao.insertCache(cache)
    suspend fun clearCache() = cacheDao.clearCache()
}