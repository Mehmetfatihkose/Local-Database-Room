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
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.localdatabaseroom.database.AppDatabase
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app_database_v2"
        ).build()
        userRepository = UserRepository(db.userDao(), db.cacheDao())

        setContent {
            MaterialTheme {
                OfflineDataScreen()
            }
        }
    }

    @Composable
    fun OfflineDataScreen() {
        var lastSync by remember { mutableStateOf("Never") }
        var cachedUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
        var onlineUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }

        LaunchedEffect(Unit) {
            loadData { sync, cached, online ->
                lastSync = sync
                cachedUsers = cached
                onlineUsers = online
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📂 Çevrimdışı Destek Uygulaması",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔌 Senkronizasyon Durumu: Son senkronizasyon $lastSync",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "🗂 Önbellek Kullanıcıları (${cachedUsers.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.height(100.dp)
                    ) {
                        items(cachedUsers) { user ->
                            Text(
                                text = "👤 ${user.name}   [🗂 Önbellek]",
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🔗 Çevrimiçi Kullanıcılar (${onlineUsers.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.height(100.dp)
                    ) {
                        items(onlineUsers) { user ->
                            Text(
                                text = "👤 ${user.name}   [🔗 Çevrimiçi]",
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            syncData()
                            loadData { sync, cached, online ->
                                lastSync = sync
                                cachedUsers = cached
                                onlineUsers = online
                            }
                        }
                    }
                ) {
                    Text("↻ Şimdi Senkronize Et")
                }

                Button(
                    onClick = {
                        lifecycleScope.launch {
                            clearCache()
                            loadData { sync, cached, online ->
                                lastSync = sync
                                cachedUsers = cached
                                onlineUsers = online
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("❌ Önbelleği Temizle")
                }
            }
        }
    }

    private suspend fun loadData(onResult: (String, List<UserEntity>, List<UserEntity>) -> Unit) {
        val cached = withContext(Dispatchers.IO) {
            userRepository.getCachedUsers()
        }
        val online = withContext(Dispatchers.IO) {
            userRepository.getOnlineUsers()
        }
        val lastSync = withContext(Dispatchers.IO) {
            userRepository.getLastSyncTime()
        }
        onResult(lastSync, cached, online)
    }

    private suspend fun syncData() {
        withContext(Dispatchers.IO) {
            userRepository.syncData()
        }
    }

    private suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            userRepository.clearCache()
        }
    }




}