package com.example.localdatabaseroom.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kullanıcı veritabanı tablosu entity sınıfı
 * Room Database tarafından "users" tablosu olarak oluşturulur
 */
@Entity(tableName = "users") // Tablo adını belirtir
data class UserEntity(
    @PrimaryKey(autoGenerate = true) // Otomatik artan birincil anahtar
    val id: Int = 0, // Varsayılan değer 0, Room otomatik artırır
    
    val name: String, // Kullanıcı adı - boş olamaz
    
    val status: String, // Kullanıcı durumu: "Önbellek" veya "Çevrimiçi"
    
    val lastSyncTime: Long = System.currentTimeMillis() // Son senkronizasyon zamanı (milisaniye)
) 