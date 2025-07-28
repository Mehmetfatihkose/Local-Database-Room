# Çevrimdışı Destek Uygulaması (Room Database)

Android Room Database kullanarak çevrimdışı veri depolama ve senkronizasyon özelliklerini gösteren modern bir Jetpack Compose uygulaması.

## 🚀 Özellikler

- ✅ **Çevrimdışı Veri Depolama**: Room Database ile yerel veri saklama
- ✅ **Veri Senkronizasyonu**: Çevrimiçi verilerden önbelleğe alma
- ✅ **Modern UI**: Jetpack Compose ve Material3 tasarım
- ✅ **Türkçe Dil Desteği**: Tam Türkçe arayüz
- ✅ **MVVM Mimarisi**: Temiz kod yapısı
- ✅ **Performanslı Listeler**: LazyColumn ile optimize edilmiş görüntüleme

## 📱 Ekran Görüntüleri

Uygulama 3 ana bölümden oluşur:
- **Son Senkronizasyon Bilgisi**: En son ne zaman veri alındığını gösterir
- **Önbellekteki Kullanıcılar**: Yerel olarak saklanan 5 kullanıcı
- **Çevrimiçi Kullanıcılar**: Simüle edilmiş 12 çevrimiçi kullanıcı

## 🏗️ Proje Yapısı

```
app/src/main/java/com/example/localdatabaseroom/
├── entities/
│   ├── UserEntity.kt      # Kullanıcı veri modeli
│   └── CacheEntity.kt     # Önbellek veri modeli
├── dao/
│   ├── UserDao.kt         # Kullanıcı veritabanı işlemleri
│   └── CacheDao.kt        # Önbellek veritabanı işlemleri
├── database/
│   └── AppDatabase.kt     # Room Database yapılandırması
├── repository/
│   └── UserRepository.kt  # Veri katmanı ve iş mantığı
└── MainActivity.kt        # Ana ekran ve Compose UI
```

## 🛠️ Kullanılan Teknolojiler

- **Kotlin**: Ana programlama dili
- **Jetpack Compose**: Modern UI framework
- **Room Database**: Yerel veritabanı çözümü
- **Material3**: Google'ın tasarım sistemi
- **Coroutines**: Asenkron programlama
- **MVVM**: Mimari deseni

## 📊 Veritabanı Yapısı

### Users Tablosu
- `id`: Otomatik artan birincil anahtar
- `name`: Kullanıcı adı
- `status`: Durum ("Önbellek" veya "Çevrimiçi")
- `lastSyncTime`: Son senkronizasyon zamanı

### Cache Tablosu
- `key`: Önbellek anahtarı (birincil anahtar)
- `value`: Önbellek değeri
- `timestamp`: Oluşturulma zamanı

## 🎯 Nasıl Çalışır?

### 1. Uygulama Başlangıcı
- Room Database otomatik olarak oluşturulur
- Mevcut önbellek verileri yüklenir
- 12 simüle edilmiş çevrimiçi kullanıcı gösterilir

### 2. Senkronizasyon
- "Şimdi Senkronize Et" butonuna basın
- Çevrimiçi kullanıcılardan ilk 5'i önbelleğe kaydedilir
- Son senkronizasyon zamanı güncellenir

### 3. Önbellek Yönetimi
- "Önbelleği Temizle" ile tüm yerel veri silinir
- Önbellek boş olduğunda uygun mesaj gösterilir

## 🔧 Kurulum

1. **Projeyi klonlayın**
   ```bash
   git clone [repository-url]
   ```

2. **Android Studio'da açın**
   - File → Open → Proje klasörünü seçin

3. **Çalıştırın**
   - Run butonuna basın veya Shift+F10

## 📋 Gereksinimler

- **Android Studio**: Arctic Fox veya üzeri
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Kotlin**: 1.9.0 veya üzeri

## 🎨 UI/UX Özellikleri

- **Responsive Tasarım**: Tüm ekran boyutlarına uyum
- **Material3 Renk Şeması**: Tutarlı ve modern görünüm
- **Smooth Animasyonlar**: Akıcı geçişler
- **Accessibility**: Erişilebilirlik desteği
- **Dark Mode**: Sistem temasına uyum

## 🚦 Durum Yönetimi

- **Compose State**: remember ve mutableStateOf kullanımı
- **LaunchedEffect**: Uygulama başlangıcında veri yükleme
- **Coroutine Scope**: Asenkron işlemler için
- **Callback Pattern**: Veri güncellemelerinde

## 📈 Performans Optimizasyonları

- **LazyColumn**: Sadece görünen elemanları render eder
- **State Hoisting**: Gereksiz recomposition'ları önler
- **Database Caching**: Hızlı veri erişimi
- **Memory Management**: Efficient resource kullanımı

## 🔄 Veri Akışı

```
UI Layer (Compose) ↔ Repository ↔ DAO ↔ Room Database
                                ↓
                         Cache Management
```

## 🧪 Test Senaryoları

1. **İlk Açılış**: Boş önbellek durumu
2. **Senkronizasyon**: 5 kullanıcının önbelleğe alınması
3. **Tekrar Senkronizasyon**: Önbelleğin güncellenmesi
4. **Önbellek Temizleme**: Tüm verilerin silinmesi
5. **Uygulama Yeniden Başlatma**: Verilerin korunması

## 📝 Geliştirme Notları

- Tüm dosyalarda detaylı yorum satırları mevcut
- MVVM mimarisi ile temiz kod yapısı
- Repository pattern ile veri katmanı soyutlaması
- Singleton pattern ile database yönetimi
- Error handling için genişletilebilir yapı

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit edin (`git commit -m 'Add amazing feature'`)
4. Push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## 📄 Lisans

Bu proje eğitim amaçlı oluşturulmuştur.

## 📞 İletişim

Sorularınız için issue açabilir veya pull request gönderebilirsiniz.

---

**Not**: Bu uygulama Android Room Database ve Jetpack Compose öğrenmek isteyenler için ideal bir başlangıç projesidir. Kod içerisindeki detaylı yorum satırları ile her adım açıklanmıştır.