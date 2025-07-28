package com.example.localdatabaseroom.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.localdatabaseroom.entities.UserEntity

/**
 * Kullanıcı veritabanı erişim nesnesi (Data Access Object)
 * UserEntity tablosu için CRUD işlemlerini tanımlar
 */
@Dao // Room DAO annotation - veritabanı erişim metodlarını içerir
interface UserDao {
    
    /**
     * Tüm kullanıcıları Flow olarak getirir
     * Flow: Reaktif programlama - veri değiştiğinde otomatik güncellenir
     * UI katmanı bu Flow'u observe ederek anlık güncellemeleri alır
     */
    @Query("SELECT * FROM users") // SQL sorgusu - tüm kayıtları getir
    fun getAllUsersFlow(): Flow<List<UserEntity>>
    
    /**
     * Belirli duruma sahip kullanıcıları getirir
     * @param status Kullanıcı durumu ("Önbellek" veya "Çevrimiçi")
     * @return Filtrelenmiş kullanıcı listesi
     */
    @Query("SELECT * FROM users WHERE status = :status") // Parametreli sorgu
    suspend fun getUsersByStatus(status: String): List<UserEntity>

    /**
     * Kullanıcı listesini veritabanına ekler veya günceller
     * REPLACE stratejisi: Aynı ID varsa günceller, yoksa yeni ekler
     * @param users Eklenecek/güncellenecek kullanıcı listesi
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Çakışma durumunda değiştir
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * Sadece önbellekteki kullanıcıları siler
     * Çevrimiçi kullanıcılar silinmez, sadece önbellek temizlenir
     */
    @Query("DELETE FROM users WHERE status = 'Önbellek'") // Koşullu silme
    suspend fun clearCachedUsers()
} 