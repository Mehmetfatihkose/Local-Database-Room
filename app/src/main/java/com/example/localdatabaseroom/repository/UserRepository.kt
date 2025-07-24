package com.example.localdatabaseroom.repository

import kotlinx.coroutines.flow.Flow
import com.example.localdatabaseroom.dao.UserDao
import com.example.localdatabaseroom.dao.CacheDao
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.entities.CacheEntity

class UserRepository(private val userDao: UserDao, private val cacheDao: CacheDao) {
    
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    
    suspend fun getCachedUsers(): List<UserEntity> = userDao.getUsersByStatus("Önbellek")
    
    suspend fun getOnlineUsers(): List<UserEntity> {
        // Simulated online data
        return listOf(
            UserEntity(name = "Ahmet Yılmaz", status = "Çevrimiçi"),
            UserEntity(name = "Ayşe Demir", status = "Çevrimiçi"),
            UserEntity(name = "Mehmet Kaya", status = "Çevrimiçi"),
            UserEntity(name = "Fatma Şahin", status = "Çevrimiçi"),
            UserEntity(name = "Ali Özkan", status = "Çevrimiçi"),
            UserEntity(name = "Zeynep Arslan", status = "Çevrimiçi"),
            UserEntity(name = "Mustafa Çelik", status = "Çevrimiçi"),
            UserEntity(name = "Elif Doğan", status = "Çevrimiçi"),
            UserEntity(name = "Hasan Koç", status = "Çevrimiçi"),
            UserEntity(name = "Merve Aydın", status = "Çevrimiçi"),
            UserEntity(name = "Emre Güneş", status = "Çevrimiçi"),
            UserEntity(name = "Seda Polat", status = "Çevrimiçi")
        )
    }
    
    suspend fun syncData() {
        val onlineUsers = getOnlineUsers()
        val cachedUsers = onlineUsers.take(5).map { it.copy(status = "Önbellek") }
        userDao.clearCachedUsers()
        userDao.insertUsers(cachedUsers)
        cacheDao.insertCache(CacheEntity("last_sync", System.currentTimeMillis().toString()))
    }
    
    suspend fun clearCache() {
        userDao.clearCachedUsers()
        cacheDao.clearCache()
    }
    
    suspend fun getLastSyncTime(): String {
        val cache = cacheDao.getCache("last_sync")
        return if (cache != null) {
            val timeDiff = (System.currentTimeMillis() - cache.value.toLong()) / 60000
            "$timeDiff dakika önce"
        } else {
            "Hiçbir zaman"
        }
    }
} 