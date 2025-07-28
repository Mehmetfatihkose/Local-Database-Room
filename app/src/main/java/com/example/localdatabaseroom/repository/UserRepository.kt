package com.example.localdatabaseroom.repository

import kotlinx.coroutines.flow.Flow
import com.example.localdatabaseroom.dao.UserDao
import com.example.localdatabaseroom.dao.CacheDao
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.entities.CacheEntity
import com.example.localdatabaseroom.utils.Result
import com.example.localdatabaseroom.utils.safeCall
import com.example.localdatabaseroom.utils.AppException

/**
 * Kullanıcı veri katmanı repository sınıfı
 * DAO'lar ile UI katmanı arasında soyutlama sağlar
 * İş mantığını (business logic) içerir
 */
class UserRepository(
    private val userDao: UserDao, // Kullanıcı veritabanı erişimi
    private val cacheDao: CacheDao // Önbellek veritabanı erişimi
) {
    
    /**
     * Tüm kullanıcıları Flow olarak döndürür
     * UI katmanı bu Flow'u observe ederek otomatik güncellemeleri alır
     */
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    
    /**
     * Sadece önbellekteki kullanıcıları getirir
     * Status = "Önbellek" olan kayıtları filtreler
     */
    suspend fun getCachedUsers(): Result<List<UserEntity>> = safeCall {
        userDao.getUsersByStatus("Önbellek")
    }
    
    /**
     * Simüle edilmiş çevrimiçi kullanıcı verilerini döndürür
     * Gerçek uygulamada bu veriler API'den gelir
     * @return 12 adet çevrimiçi kullanıcı listesi
     */
    suspend fun getOnlineUsers(): Result<List<UserEntity>> = safeCall {
        // Simüle edilmiş çevrimiçi veri - gerçek uygulamada API çağrısı olur
        listOf(
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
    
    /**
     * Veri senkronizasyon işlemini gerçekleştirir
     * 1. Çevrimiçi kullanıcıları al
     * 2. İlk 5'ini önbellek olarak işaretle
     * 3. Eski önbellek verilerini temizle
     * 4. Yeni önbellek verilerini kaydet
     * 5. Son senkronizasyon zamanını güncelle
     */
    suspend fun syncData(): Result<Unit> = safeCall {
        val onlineUsersResult = getOnlineUsers() // Çevrimiçi verileri al
        
        when (onlineUsersResult) {
            is Result.Success -> {
                // İlk 5 kullanıcıyı al ve durumlarını "Önbellek" yap
                val cachedUsers = onlineUsersResult.data.take(5).map { 
                    it.copy(status = "Önbellek") // Kopya oluştur ve status'u değiştir
                }
                
                userDao.clearCachedUsers() // Eski önbellek verilerini temizle
                userDao.insertUsers(cachedUsers) // Yeni önbellek verilerini kaydet
                
                // Son senkronizasyon zamanını önbellek tablosuna kaydet
                cacheDao.insertCache(
                    CacheEntity("last_sync", System.currentTimeMillis().toString())
                )
            }
            is Result.Error -> {
                throw AppException.SyncException("Senkronizasyon başarısız", onlineUsersResult.exception)
            }
        }
    }
    
    /**
     * Tüm önbellek verilerini temizler
     * Hem kullanıcı önbelleğini hem de cache tablosunu temizler
     */
    suspend fun clearCache(): Result<Unit> = safeCall {
        userDao.clearCachedUsers() // Önbellekteki kullanıcıları sil
        cacheDao.clearCache() // Cache tablosunu temizle
    }
    
    /**
     * Son senkronizasyon zamanını hesaplar ve string olarak döndürür
     * @return "X dakika önce" veya "Hiçbir zaman" (eğer hiç senkronize edilmemişse)
     */
    suspend fun getLastSyncTime(): Result<String> = safeCall {
        val cache = cacheDao.getCache("last_sync") // Son senkronizasyon zamanını al
        
        if (cache != null) {
            // Zaman farkını hesapla (milisaniye -> dakika)
            val timeDiff = (System.currentTimeMillis() - cache.value.toLong()) / 60000
            "$timeDiff dakika önce" // Kullanıcı dostu format
        } else {
            "Hiçbir zaman" // Hiç senkronize edilmemiş
        }
    }
} 