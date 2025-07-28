package com.example.localdatabaseroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch
import com.example.localdatabaseroom.database.AppDatabase
import com.example.localdatabaseroom.repository.UserRepository
import com.example.localdatabaseroom.entities.UserEntity

class MainActivity : ComponentActivity() {
    
    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    
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
        
        setContent {
            MaterialTheme {
                OfflineDataScreen()
            }
        }
    }
    
    @Composable
    fun OfflineDataScreen() {
        val scope = rememberCoroutineScope()
        
        // UI state değişkenleri
        var lastSync by remember { mutableStateOf("Hiçbir zaman") }
        var cachedUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
        var onlineUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        
        // Uygulama başladığında veri yükle
        LaunchedEffect(Unit) {
            isLoading = true
            try {
                loadData { sync, cached, online ->
                    lastSync = sync
                    cachedUsers = cached
                    onlineUsers = online
                    errorMessage = null
                }
            } catch (e: Exception) {
                errorMessage = "Veri yüklenirken hata: ${e.message}"
            } finally {
                isLoading = false
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Başlık
            Text(
                text = "Çevrimdışı Destek Uygulaması",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            // Hata mesajı göster
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Yükleme göstergesi
            if (isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yükleniyor...")
                    }
                }
            }
            
            // Son senkronizasyon bilgisi
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Son Senkronizasyon: $lastSync",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Butonlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                userRepository.syncData()
                                loadData { sync, cached, online ->
                                    lastSync = sync
                                    cachedUsers = cached
                                    onlineUsers = online
                                    errorMessage = null
                                }
                            } catch (e: Exception) {
                                errorMessage = "Senkronizasyon hatası: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Şimdi Senkronize Et")
                }
                
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                userRepository.clearCache()
                                loadData { sync, cached, online ->
                                    lastSync = sync
                                    cachedUsers = cached
                                    onlineUsers = online
                                    errorMessage = null
                                }
                            } catch (e: Exception) {
                                errorMessage = "Önbellek temizleme hatası: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Önbelleği Temizle")
                }
            }
            
            // Önbellekteki kullanıcılar
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Önbellekteki Kullanıcılar (${cachedUsers.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (cachedUsers.isEmpty()) {
                        Text(
                            text = "Önbellekte kullanıcı yok",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(150.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(cachedUsers) { user ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(user.name)
                                    Text(
                                        text = user.status,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Çevrimiçi kullanıcılar
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Çevrimiçi Kullanıcılar (${onlineUsers.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(onlineUsers) { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(user.name)
                                Text(
                                    text = user.status,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Veri yükleme helper fonksiyonu
     * Repository'den verileri alır ve callback ile UI'a gönderir
     * Suspend fonksiyon: Coroutine içinde çağrılmalı
     * 
     * @param callback UI state'lerini güncellemek için callback fonksiyonu
     *                 Parametreler: (lastSync, cachedUsers, onlineUsers)
     */
    private suspend fun loadData(
        callback: (String, List<UserEntity>, List<UserEntity>) -> Unit
    ) {
        // Repository'den verileri paralel olarak al (suspend fonksiyonlar)
        val lastSync = userRepository.getLastSyncTime()  // Son senkronizasyon zamanı
        val cachedUsers = userRepository.getCachedUsers() // Önbellekteki kullanıcılar
        val onlineUsers = userRepository.getOnlineUsers() // Simüle edilmiş çevrimiçi kullanıcılar
        
        // Callback'i çağırarak UI state'lerini güncelle
        callback(lastSync, cachedUsers, onlineUsers)
    }
} // MainActivity sonu