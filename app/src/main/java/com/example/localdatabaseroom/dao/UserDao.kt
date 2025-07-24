package com.example.localdatabaseroom.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.localdatabaseroom.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users WHERE status = :status")
    suspend fun getUsersByStatus(status: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE status = 'Önbellek'")
    suspend fun clearCachedUsers()
} 