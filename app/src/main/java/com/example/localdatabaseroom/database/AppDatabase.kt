package com.example.localdatabaseroom.database

import android.content.Context
import androidx.room.*
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.entities.CacheEntity
import com.example.localdatabaseroom.dao.UserDao
import com.example.localdatabaseroom.dao.CacheDao

/**
 * Ana Room Database sınıfı
 * Uygulamanın tüm veritabanı tablolarını ve DAO'larını yönetir
 */
@Database(
    entities = [UserEntity::class, CacheEntity::class], // Veritabanındaki tablolar
    version = 2, // Veritabanı versiyonu - şema değiştiğinde artırılır
    exportSchema = false // Şema export'unu devre dışı bırak (geliştirme için)
)
abstract class AppDatabase : RoomDatabase() {
    
    // DAO'lara erişim sağlayan abstract metodlar
    abstract fun userDao(): UserDao // Kullanıcı işlemleri için DAO
    abstract fun cacheDao(): CacheDao // Önbellek işlemleri için DAO
    
    companion object {
        // Singleton pattern için volatile değişken
        // Volatile: Çoklu thread'lerde güvenli erişim sağlar
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Singleton Database instance'i döndürür
         * Eğer instance yoksa oluşturur, varsa mevcutını döndürür
         * Thread-safe: synchronized blok ile eş zamanlı erişim engellenir
         */
        fun getDatabase(context: Context): AppDatabase {
            // Eğer instance varsa direkt döndür (hızlı yol)
            return INSTANCE ?: synchronized(this) {
                // Çifte kontrol - synchronized içinde tekrar kontrol et
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Application context kullan (memory leak önlemi)
                    AppDatabase::class.java, // Database sınıfı
                    "offline_app_database" // Veritabanı dosya adı
                ).fallbackToDestructiveMigration() // Migration hatasında veritabanını yeniden oluştur
                 .build() // Database'i inşa et
                
                INSTANCE = instance // Singleton instance'i kaydet
                instance // Oluşturulan instance'i döndür
            }
        }
    }
} 