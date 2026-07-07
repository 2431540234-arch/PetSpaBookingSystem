package com.petspa.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petspa.app.model.*
import com.petspa.app.repository.PetSpaRepository
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import okhttp3.MultipartBody
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StaffViewModel(
    private val repo: PetSpaRepository
) : ViewModel() {

    private val _bookings = MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val bookings: StateFlow<UiState<List<Booking>>> = _bookings.asStateFlow()

    private val _shifts = MutableStateFlow<UiState<List<Shift>>>(UiState.Loading)
    val shifts: StateFlow<UiState<List<Shift>>> = _shifts.asStateFlow()

    private val _notifications = MutableStateFlow<UiState<List<NotificationItem>>>(UiState.Loading)
    val notifications: StateFlow<UiState<List<NotificationItem>>> = _notifications.asStateFlow()

    private val _profile = MutableStateFlow<UiState<TechnicianProfile>>(UiState.Loading)
    val profile: StateFlow<UiState<TechnicianProfile>> = _profile.asStateFlow()

    private val _imageUploadState = MutableStateFlow<UiState<String>?>(null)
    val imageUploadState: StateFlow<UiState<String>?> = _imageUploadState.asStateFlow()

    fun uploadImage(filePart: MultipartBody.Part, type: String) {
        viewModelScope.launch {
            _imageUploadState.value = UiState.Loading
            val url = repo.uploadImage(filePart, type)
            if (url != null) {
                _imageUploadState.value = UiState.Success(url)
            } else {
                _imageUploadState.value = UiState.Error("Upload thất bại")
            }
        }
    }

    fun clearImageUploadState() {
        _imageUploadState.value = null
    }

    fun updateProfile(name: String, phone: String, address: String, avatar: String?) {
        viewModelScope.launch {
            try {
                val current = (profile.value as? UiState.Success)?.data
                if (current != null) {
                    val updated = current.copy(name = name, phone = phone, address = address, avatar = avatar ?: current.avatar)
                    repo.updateTechnicianProfile(updated)
                    loadProfile()
                }
            } catch (e: Exception) {
                Log.e("Staff", "Update failed", e)
            }
        }
    }

    init {
        observeAll()
        loadDashboard()
        syncFcmToken()
    }

    private fun syncFcmToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                repo.updateFcmToken(token)
                Log.d("FCM", "Staff Token synced: $token")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to sync staff token", e)
            }
        }
    }

    private fun observeAll() {
        viewModelScope.launch {
            repo.observeBookings().collectLatest { _bookings.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repo.observeNotifications().collectLatest { _notifications.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repo.observeShifts().collectLatest { _shifts.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repo.observeTechnicianProfile().collectLatest { it?.let { _profile.value = UiState.Success(it) } }
        }
    }

    fun loadDashboard() {
        loadBookings()
        loadShifts()
        loadNotifications()
        loadProfile()
    }

    fun loadBookings() = viewModelScope.launch { repo.refreshBookings() }
    fun loadShifts() = viewModelScope.launch { repo.refreshShifts() }
    fun loadNotifications() = viewModelScope.launch { repo.refreshNotifications() }
    fun loadProfile() = viewModelScope.launch { repo.refreshTechnicianProfile() }
}
