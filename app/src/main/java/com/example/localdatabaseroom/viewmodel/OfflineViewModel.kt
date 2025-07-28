package com.example.localdatabaseroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.localdatabaseroom.repository.UserRepository
import com.example.localdatabaseroom.entities.UserEntity
import com.example.localdatabaseroom.utils.Result
import com.example.localdatabaseroom.utils.toUserFriendlyMessage

data class OfflineUiState(
    val cachedUsers: List<UserEntity> = emptyList(),
    val onlineUsers: List<UserEntity> = emptyList(),
    val lastSyncTime: String = "Never",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class OfflineViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OfflineUiState())
    val uiState: StateFlow<OfflineUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val cachedUsersResult = userRepository.getCachedUsers()
            val onlineUsersResult = userRepository.getOnlineUsers()
            val lastSyncResult = userRepository.getLastSyncTime()
            
            var errorMessage: String? = null
            val cachedUsers = when (cachedUsersResult) {
                is Result.Success -> cachedUsersResult.data
                is Result.Error -> {
                    errorMessage = cachedUsersResult.exception.toUserFriendlyMessage()
                    emptyList()
                }
            }
            
            val onlineUsers = when (onlineUsersResult) {
                is Result.Success -> onlineUsersResult.data
                is Result.Error -> {
                    errorMessage = onlineUsersResult.exception.toUserFriendlyMessage()
                    emptyList()
                }
            }
            
            val lastSync = when (lastSyncResult) {
                is Result.Success -> lastSyncResult.data
                is Result.Error -> {
                    errorMessage = lastSyncResult.exception.toUserFriendlyMessage()
                    "Bilinmiyor"
                }
            }
            
            _uiState.value = OfflineUiState(
                cachedUsers = cachedUsers,
                onlineUsers = onlineUsers,
                lastSyncTime = lastSync,
                isLoading = false,
                errorMessage = errorMessage
            )
        }
    }
    
    fun syncData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            when (val result = userRepository.syncData()) {
                is Result.Success -> {
                    loadData()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.toUserFriendlyMessage()
                    )
                }
            }
        }
    }
    
    fun clearCache() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            when (val result = userRepository.clearCache()) {
                is Result.Success -> {
                    loadData()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.toUserFriendlyMessage()
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}