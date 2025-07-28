package com.example.localdatabaseroom.screens

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
import androidx.navigation.NavController
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.localdatabaseroom.database.AppDatabase
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext

@Composable
fun UsersScreen(navController: NavController) {
    val context = LocalContext.current
    var onlineUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app_database_v2"
        ).build()
        val userRepository = UserRepository(db.userDao(), db.cacheDao())
        
        withContext(Dispatchers.IO) {
            onlineUsers = userRepository.getOnlineUsers()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👥 Kullanıcılar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            TextButton(onClick = { navController.popBackStack() }) {
                Text("← Geri")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "🔗 Çevrimiçi Kullanıcılar (${onlineUsers.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyColumn {
            items(onlineUsers) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "👤 ${user.name}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}