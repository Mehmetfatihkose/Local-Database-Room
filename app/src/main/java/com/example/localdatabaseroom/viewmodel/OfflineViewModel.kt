package com.example.localdatabaseroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.localdatabaseroom.repository.UserRepository
import com.example.localdatabaseroom.entities.UserEntity

data class OfflineUiState(
    val cachedUsers: List<UserEntity> = emptyList(),
    val onlineUsers: List<UserEntity> = emptyList(),
    val lastSyncTime: String = "Never",
    val isLoading: Boolean = false
)

class OfflineViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OfflineUiState())
    val uiState: StateFlow<OfflineUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val cachedUsers = userRepository.getCachedUsers()
            val onlineUsers = userRepository.getOnlineUsers()
            val lastSync = userRepository.getLastSyncTime()
            
            _uiState.value = OfflineUiState(
                cachedUsers = cachedUsers,
                onlineUsers = onlineUsers,
                lastSyncTime = lastSync,
                isLoading = false
            )
        }
    }
    
    fun syncData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userRepository.syncData()
            loadData()
        }
    }
    
    fun clearCache() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userRepository.clearCache()
            loadData()
        }
    }
}