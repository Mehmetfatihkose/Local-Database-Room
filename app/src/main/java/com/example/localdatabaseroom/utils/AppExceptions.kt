package com.example.localdatabaseroom.utils

/**
 * Uygulama özel hata türleri
 */
sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    class DatabaseException(message: String, cause: Throwable? = null) : AppException(message, cause)
    
    class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
    
    class SyncException(message: String, cause: Throwable? = null) : AppException(message, cause)
    
    class CacheException(message: String, cause: Throwable? = null) : AppException(message, cause)
}

/**
 * Hata mesajlarını kullanıcı dostu formata çevirir
 */
fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is AppException.DatabaseException -> "Veritabanı hatası: ${this.message}"
        is AppException.NetworkException -> "Bağlantı hatası: ${this.message}"
        is AppException.SyncException -> "Senkronizasyon hatası: ${this.message}"
        is AppException.CacheException -> "Önbellek hatası: ${this.message}"
        else -> "Beklenmeyen hata: ${this.message ?: "Bilinmeyen hata"}"
    }
}