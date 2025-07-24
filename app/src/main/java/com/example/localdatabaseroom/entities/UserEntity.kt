package com.example.localdatabaseroom.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val status: String, // "Cached" veya "Online"
    val lastSyncTime: Long = System.currentTimeMillis()
) 