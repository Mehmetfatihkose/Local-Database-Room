# Çevrimdışı Destek Uygulaması (Room Database)

Bu proje, Android Room Database kullanarak çevrimdışı veri depolama ve senkronizasyon özelliklerini gösteren bir Jetpack Compose uygulamasıdır.

## 📁 Proje Yapısı

```
app/src/main/java/com/example/localdatabaseroom/
├── entities/          # Veritabanı tabloları
│   ├── UserEntity.kt     # Kullanıcı tablosu
│   └── CacheEntity.kt    # Önbellek tablosu
├── dao/              # Veritabanı erişim nesneleri
│   ├── UserDao.kt        # Kullanıcı DAO
│   └── CacheDao.kt       # Önbellek DAO
├── database/         # Veritabanı yapılandırması
│   └── AppDatabase.kt    # Room Database
├── repository/       # Veri katmanı
│   ├── UserRepository.kt # Kullanıcı repository
│   └── CacheRepository.kt # Önbellek repository
├── viewmodel/        # ViewModel katmanı
│   └── OfflineViewModel.kt # UI state yönetimi
└── MainActivity.kt   # Ana aktivite (Compose UI)
```

## 🏗️ Mimari

Proje **MVVM (Model-View-ViewModel)** mimarisini kullanır:

- **Model**: Entity sınıfları (UserEntity, CacheEntity)
- **View**: Jetpack Compose UI (MainActivity)
- **ViewModel**: UI state yönetimi
- **Repository**: Veri katmanı soyutlaması
- **DAO**: Veritabanı işlemleri

## 📊 Veritabanı Şeması

### UserEntity Tablosu
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,      // Kullanıcı adı
    val status: String     // Durum: "Önbellek" veya "Çevrimiçi"
)
```

### CacheEntity Tablosu
```kotlin
@Entity(tableName = "cache")
data class CacheEntity(
    @PrimaryKey
    val key: String,       // Önbellek anahtarı
    val value: String      // Önbellek değeri
)
```

## 🔧 Ana Bileşenler

### 1. UserDao - Kullanıcı Veritabanı İşlemleri
```kotlin
@Dao
interface UserDao {
    // Tüm kullanıcıları Flow olarak getir
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
    
    // Duruma göre kullanıcıları getir
    @Query("SELECT * FROM users WHERE status = :status")
    suspend fun getUsersByStatus(status: String): List<UserEntity>
    
    // Kullanıcıları ekle/güncelle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    // Önbellekteki kullanıcıları temizle
    @Query("DELETE FROM users WHERE status = 'Önbellek'")
    suspend fun clearCachedUsers()
}
```

### 2. UserRepository - Veri Katmanı
```kotlin
class UserRepository(private val userDao: UserDao, private val cacheDao: CacheDao) {
    
    // Önbellekteki kullanıcıları getir
    suspend fun getCachedUsers(): List<UserEntity> = 
        userDao.getUsersByStatus("Önbellek")
    
    // Simüle edilmiş çevrimiçi kullanıcılar
    suspend fun getOnlineUsers(): List<UserEntity> = listOf(...)
    
    // Veri senkronizasyonu
    suspend fun syncData() {
        val onlineUsers = getOnlineUsers()
        val cachedUsers = onlineUsers.take(5).map { 
            it.copy(status = "Önbellek") 
        }
        userDao.clearCachedUsers()
        userDao.insertUsers(cachedUsers)
        // Son senkronizasyon zamanını kaydet
        cacheDao.insertCache(CacheEntity("last_sync", System.currentTimeMillis().toString()))
    }
    
    // Önbelleği temizle
    suspend fun clearCache() {
        userDao.clearCachedUsers()
        cacheDao.clearCache()
    }
}
```

### 3. MainActivity - Jetpack Compose UI
```kotlin
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Room Database oluştur
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, 
            "app_database_v2"
        ).build()
        
        // Repository'yi başlat
        userRepository = UserRepository(db.userDao(), db.cacheDao())
        
        // Compose UI'ı ayarla
        setContent {
            MaterialTheme {
                OfflineDataScreen()
            }
        }
    }
    
    @Composable
    fun OfflineDataScreen() {
        // UI state değişkenleri
        var lastSync by remember { mutableStateOf("Hiçbir zaman") }
        var cachedUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
        var onlineUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
        
        // Uygulama başladığında veri yükle
        LaunchedEffect(Unit) {
            loadData { sync, cached, online ->
                lastSync = sync
                cachedUsers = cached
                onlineUsers = online
            }
        }
        
        // UI bileşenleri: Başlık, kartlar, butonlar
        Column { ... }
    }
}
```

## 🚀 Özellikler

### ✅ Çevrimdışı Veri Depolama
- Room Database ile yerel veri saklama
- Kullanıcı verilerini önbellekte tutma
- Uygulama kapatılsa bile veriler korunur

### ✅ Veri Senkronizasyonu
- "Şimdi Senkronize Et" butonu ile manuel senkronizasyon
- Çevrimiçi verilerden 5 tanesini önbelleğe alma
- Son senkronizasyon zamanını takip etme

### ✅ Önbellek Yönetimi
- "Önbelleği Temizle" butonu ile tüm önbelleği silme
- Önbellekteki ve çevrimiçi kullanıcıları ayrı gösterme
- Kullanıcı sayılarını dinamik olarak gösterme

### ✅ Modern UI
- Jetpack Compose ile modern arayüz
- Material3 tasarım sistemi
- Türkçe dil desteği
- Responsive tasarım

## 🛠️ Kullanılan Teknolojiler

- **Kotlin**: Ana programlama dili
- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Yerel veritabanı
- **Coroutines**: Asenkron programlama
- **Material3**: UI tasarım sistemi
- **MVVM**: Mimari deseni

## 📱 Kullanım

1. **Uygulama Başlatma**: Uygulama açıldığında mevcut veriler yüklenir
2. **Senkronizasyon**: "Şimdi Senkronize Et" butonuna basarak yeni verileri önbelleğe alın
3. **Önbellek Temizleme**: "Önbelleği Temizle" butonuna basarak tüm önbelleği silin
4. **Veri Görüntüleme**: Önbellekteki ve çevrimiçi kullanıcıları ayrı listelerde görün

## 🔄 Veri Akışı

1. **Başlangıç**: Uygulama açıldığında Repository'den veriler çekilir
2. **Senkronizasyon**: Çevrimiçi veriler alınır ve önbelleğe kaydedilir
3. **Görüntüleme**: UI, Repository'den gelen verileri gösterir
4. **Temizleme**: Kullanıcı isteğiyle önbellek temizlenir

Bu uygulama, Android'de çevrimdışı veri yönetimi ve Room Database kullanımının temel örneklerini gösterir.