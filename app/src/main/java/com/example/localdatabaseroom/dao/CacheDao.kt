package com.example.localdatabaseroom.dao

import androidx.room.*
import com.example.localdatabaseroom.entities.CacheEntity

/**
 * Önbellek veritabanı erişim nesnesi (Data Access Object)
 * CacheEntity tablosu için CRUD işlemlerini tanımlar
 * Anahtar-değer çiftlerini yönetir
 */
@Dao // Room DAO annotation
interface CacheDao {
    
    /**
     * Belirli bir anahtar ile önbellek değerini getirir
     * @param key Aranacak önbellek anahtarı (örn: "last_sync")
     * @return CacheEntity nesnesi veya null (bulunamazsa)
     */
    @Query("SELECT * FROM cache WHERE key = :key") // Tek kayıt getir
    suspend fun getCache(key: String): CacheEntity?

    /**
     * Önbellek değerini ekler veya günceller
     * REPLACE stratejisi: Aynı key varsa değerini günceller
     * @param cache Eklenecek/güncellenecek önbellek nesnesi
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Aynı key varsa güncelle
    suspend fun insertCache(cache: CacheEntity)

    /**
     * Tüm önbellek verilerini siler
     * Genellikle uygulama sıfırlanması veya önbellek temizleme için kullanılır
     */
    @Query("DELETE FROM cache") // Tüm kayıtları sil
    suspend fun clearCache()
} 