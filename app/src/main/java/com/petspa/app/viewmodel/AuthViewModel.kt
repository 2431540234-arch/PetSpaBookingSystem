package com.petspa.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petspa.app.data.AuthSession
import com.petspa.app.model.AuthResponse
import com.petspa.app.model.UiState
import com.petspa.app.repository.PetSpaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: PetSpaRepository) : ViewModel() {
    val session: StateFlow<AuthSession?> = repository.authSession
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _loginState = MutableStateFlow<UiState<AuthResponse>?>(null)
    val loginState: StateFlow<UiState<AuthResponse>?> = _loginState.asStateFlow()

    private val _checking = MutableStateFlow(true)
    val checking: StateFlow<Boolean> = _checking.asStateFlow()

    init {
        viewModelScope.launch {
            repository.authSession.collect {
                _checking.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val response = repository.login(email, password)
                _loginState.value = UiState.Success(response)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Đăng nhập thất bại")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _loginState.value = null
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}
