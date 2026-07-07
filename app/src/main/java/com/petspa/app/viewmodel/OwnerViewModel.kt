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

class OwnerViewModel(
    private val repo: PetSpaRepository
) : ViewModel() {

    private val _customers = MutableStateFlow<UiState<List<Customer>>>(UiState.Loading)
    val customers: StateFlow<UiState<List<Customer>>> = _customers.asStateFlow()

    private val _staff = MutableStateFlow<UiState<List<StaffMember>>>(UiState.Loading)
    val staff: StateFlow<UiState<List<StaffMember>>> = _staff.asStateFlow()

    private val _services = MutableStateFlow<UiState<List<Service>>>(UiState.Loading)
    val services: StateFlow<UiState<List<Service>>> = _services.asStateFlow()

    private val _bookings = MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val bookings: StateFlow<UiState<List<Booking>>> = _bookings.asStateFlow()

    private val _requests = MutableStateFlow<UiState<List<StaffRequest>>>(UiState.Loading)
    val requests: StateFlow<UiState<List<StaffRequest>>> = _requests.asStateFlow()

    private val _dailyRevenue = MutableStateFlow<UiState<List<RevenuePoint>>>(UiState.Loading)
    val dailyRevenue: StateFlow<UiState<List<RevenuePoint>>> = _dailyRevenue.asStateFlow()

    private val _monthlyRevenue = MutableStateFlow<UiState<List<RevenuePoint>>>(UiState.Loading)
    val monthlyRevenue: StateFlow<UiState<List<RevenuePoint>>> = _monthlyRevenue.asStateFlow()

    private val _topServices = MutableStateFlow<UiState<List<TopService>>>(UiState.Loading)
    val topServices: StateFlow<UiState<List<TopService>>> = _topServices.asStateFlow()

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
                Log.d("FCM", "Owner Token synced: $token")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to sync owner token", e)
            }
        }
    }

    private fun observeAll() {
        viewModelScope.launch { repo.observeCustomers().collectLatest { _customers.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeStaff().collectLatest { _staff.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeServices().collectLatest { _services.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeBookings().collectLatest { _bookings.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeStaffRequests().collectLatest { _requests.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeDailyRevenue().collectLatest { _dailyRevenue.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeMonthlyRevenue().collectLatest { _monthlyRevenue.value = UiState.Success(it) } }
        viewModelScope.launch { repo.observeTopServices().collectLatest { _topServices.value = UiState.Success(it) } }
    }

    fun loadDashboard() {
        loadBookings()
        loadDailyRevenue()
        loadCustomers()
        loadStaff()
        loadServices()
        loadRequests()
    }

    fun loadCustomers() = viewModelScope.launch { repo.refreshCustomers() }
    fun loadStaff() = viewModelScope.launch { repo.refreshStaff() }
    fun loadServices() = viewModelScope.launch { repo.refreshServices() }
    fun loadBookings() = viewModelScope.launch { repo.refreshBookings() }
    fun loadRequests() = viewModelScope.launch { repo.refreshStaffRequests() }
    fun loadDailyRevenue() = viewModelScope.launch { repo.refreshRevenue() }
    fun loadMonthlyRevenue() = viewModelScope.launch { repo.refreshRevenue() }
    fun loadTopServices() = viewModelScope.launch { repo.refreshRevenue() }
    fun loadReports() = viewModelScope.launch { repo.refreshRevenue() }

    fun addCustomer(c: Customer) = viewModelScope.launch { repo.addCustomer(c) }
    fun updateCustomer(c: Customer) = viewModelScope.launch { repo.updateCustomer(c.id, c) }
    fun deleteCustomer(id: String) = viewModelScope.launch { repo.deleteCustomer(id) }

    fun addStaff(s: StaffMember) = viewModelScope.launch { repo.addStaff(s) }
    fun updateStaff(s: StaffMember) = viewModelScope.launch { repo.updateStaff(s.id, s) }
    fun deleteStaff(id: String) = viewModelScope.launch { repo.deleteStaff(id) }

    fun addService(s: Service) = viewModelScope.launch { repo.addService(s) }
    fun updateService(s: Service) = viewModelScope.launch { repo.updateService(s.id, s) }
    fun deleteService(id: String) = viewModelScope.launch { repo.deleteService(id) }

    fun approveRequest(id: String) = viewModelScope.launch { repo.updateStaffRequestStatus(id, "approved") }
    fun rejectRequest(id: String) = viewModelScope.launch { repo.updateStaffRequestStatus(id, "rejected") }
}
