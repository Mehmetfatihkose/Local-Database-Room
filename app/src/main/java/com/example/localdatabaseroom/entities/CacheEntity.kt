package com.example.localdatabaseroom.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Önbellek veritabanı tablosu entity sınıfı
 * Anahtar-değer çiftlerini saklar (key-value storage)
 * Örnek: "last_sync" -> "1703123456789"
 */
@Entity(tableName = "cache") // Tablo adını "cache" olarak belirtir
data class CacheEntity(
    @PrimaryKey // Birincil anahtar - otomatik artmaz, manuel belirlenir
    val key: String, // Önbellek anahtarı (örn: "last_sync", "user_count")
    
    val value: String, // Önbellek değeri (her zaman string olarak saklanır)
    
    val timestamp: Long = System.currentTimeMillis() // Kayıt oluşturma zamanı
) 