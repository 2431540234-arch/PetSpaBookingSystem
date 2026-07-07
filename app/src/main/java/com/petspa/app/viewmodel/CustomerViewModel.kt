package com.petspa.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petspa.app.model.*
import com.petspa.app.network.dto.response.PaymentResponseDto
import com.petspa.app.network.dto.response.PaymentTransactionDto
import com.petspa.app.repository.PetSpaRepository
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import okhttp3.MultipartBody
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CustomerViewModel(
    private val repo: PetSpaRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _pets = MutableStateFlow<UiState<List<Pet>>>(UiState.Loading)
    val pets: StateFlow<UiState<List<Pet>>> = _pets.asStateFlow()

    private val _services = MutableStateFlow<UiState<List<Service>>>(UiState.Loading)
    val services: StateFlow<UiState<List<Service>>> = _services.asStateFlow()

    private val _bookings = MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val bookings: StateFlow<UiState<List<Booking>>> = _bookings.asStateFlow()

    private val _notifications = MutableStateFlow<UiState<List<NotificationItem>>>(UiState.Loading)
    val notifications: StateFlow<UiState<List<NotificationItem>>> = _notifications.asStateFlow()

    private val _user = MutableStateFlow<UiState<User>>(UiState.Loading)
    val user: StateFlow<UiState<User>> = _user.asStateFlow()

    private val _notifSettings = MutableStateFlow<UiState<NotifSettings>>(UiState.Loading)
    val notifSettings: StateFlow<UiState<NotifSettings>> = _notifSettings.asStateFlow()

    private val _confirmBookingState = MutableStateFlow<UiState<Booking>?>(null)
    val confirmBookingState: StateFlow<UiState<Booking>?> = _confirmBookingState.asStateFlow()

    private val _paymentState = MutableStateFlow<UiState<PaymentResponseDto>?>(null)
    val paymentState: StateFlow<UiState<PaymentResponseDto>?> = _paymentState.asStateFlow()

    private val _paymentStatus = MutableStateFlow<UiState<PaymentTransactionDto>?>(null)
    val paymentStatus: StateFlow<UiState<PaymentTransactionDto>?> = _paymentStatus.asStateFlow()

    private val _availableStaff = MutableStateFlow<UiState<List<StaffAvailabilityResponse>>>(UiState.Loading)
    val availableStaff: StateFlow<UiState<List<StaffAvailabilityResponse>>> = _availableStaff.asStateFlow()

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

    // Sử dụng flow từ repository để đồng bộ với bộ nhớ máy
    val bookingDraft: StateFlow<BookingDraft> = repo.bookingDraft
        .stateIn(viewModelScope, SharingStarted.Eagerly, BookingDraft())
    
    val lastRoute: StateFlow<String?> = repo.lastRoute
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateDraft(draft: BookingDraft) {
        val oldDraft = bookingDraft.value
        viewModelScope.launch {
            repo.saveBookingDraft(draft)
            
            // Nếu đổi nhân viên, reset giờ đã chọn
            if (oldDraft.staffId != draft.staffId) {
                repo.saveBookingDraft(draft.copy(time = ""))
            }
        }
    }

    fun saveLastRoute(route: String?) {
        viewModelScope.launch {
            repo.saveLastRoute(route)
        }
    }

    fun loadAvailableStaff(date: String) {
        if (date.isEmpty()) return
        viewModelScope.launch {
            _availableStaff.value = UiState.Loading
            try {
                val staff = repo.getAvailableStaff(date)
                _availableStaff.value = UiState.Success(staff)
            } catch (e: Exception) {
                _availableStaff.value = UiState.Error(e.message ?: "Lỗi tải danh sách nhân viên")
            }
        }
    }

    var localPets = mutableListOf<Pet>()

    init {
        observeAll()
        loadHome()
        syncFcmToken()
        
        // Tự động tải lại nhân viên khi ngày đổi
        viewModelScope.launch {
            bookingDraft.map { it.date }.distinctUntilChanged().collect { date ->
                loadAvailableStaff(date)
            }
        }
    }

    private fun observeAll() {
        viewModelScope.launch {
            repo.observePets().collectLatest {
                localPets = it.toMutableList()
                _pets.value = UiState.Success(it)
            }
        }
        viewModelScope.launch {
            repo.observeServices().collectLatest { _services.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repo.observeBookings().collectLatest { _bookings.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repo.observeNotifications().collectLatest { _notifications.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repo.observeUser().collectLatest { it?.let { _user.value = UiState.Success(it) } }
        }
        viewModelScope.launch {
            repo.observeNotifSettings().collectLatest { it?.let { _notifSettings.value = UiState.Success(it) } }
        }
    }

    fun loadHome() {
        loadPets()
        loadBookings()
        loadServices()
        loadNotifications()
        loadUser()
        loadNotifSettings()
    }

    fun loadPets() = viewModelScope.launch { repo.refreshPets() }
    fun loadBookings() = viewModelScope.launch { repo.refreshBookings() }
    fun loadServices() = viewModelScope.launch { repo.refreshServices() }
    fun loadNotifications() = viewModelScope.launch { repo.refreshNotifications() }
    fun loadUser() = viewModelScope.launch { repo.refreshUser() }
    fun loadNotifSettings() = viewModelScope.launch { repo.refreshNotifSettings() }

    fun clearConfirmBookingState() {
        _confirmBookingState.value = null
    }

    fun confirmBooking(payType: String, method: String) {
        viewModelScope.launch {
            _confirmBookingState.value = UiState.Loading
            try {
                // 1. Tạo Booking
                val booking = repo.createBooking(bookingDraft.value)
                
                // Save ID for recovery
                savedStateHandle["last_booking_id"] = booking.id
                
                // Trả về booking thành công, UI sẽ dựa vào phương thức để gọi thanh toán tiếp
                _confirmBookingState.value = UiState.Success(booking)
                updateDraft(BookingDraft())
            } catch (e: Exception) {
                _confirmBookingState.value = UiState.Error(e.message ?: "Lỗi xác nhận đặt lịch")
            }
        }
    }

    fun createPayment(bookingId: Long, gateway: String) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            try {
                val response = repo.createPayment(bookingId, gateway.uppercase())
                _paymentState.value = UiState.Success(response)
            } catch (e: Exception) {
                _paymentState.value = UiState.Error(e.message ?: "Lỗi khởi tạo thanh toán")
            }
        }
    }

    fun checkPaymentStatus(bookingId: Long) {
        viewModelScope.launch {
            _paymentStatus.value = UiState.Loading
            try {
                val status = repo.getPaymentStatus(bookingId)
                _paymentStatus.value = UiState.Success(status)
                
                if (status.paymentStatus == "SUCCESS" || status.paymentStatus == "FULLY_PAID") {
                    loadBookings()
                }
            } catch (e: Exception) {
                _paymentStatus.value = UiState.Error(e.message ?: "Lỗi kiểm tra trạng thái")
            }
        }
    }

    private val _paymentHistory = MutableStateFlow<UiState<List<PaymentTransactionDto>>>(UiState.Loading)
    val paymentHistory: StateFlow<UiState<List<PaymentTransactionDto>>> = _paymentHistory.asStateFlow()

    fun loadPaymentHistory() {
        viewModelScope.launch {
            _paymentHistory.value = UiState.Loading
            try {
                // Assuming we can get all transactions for the user or a list
                // If not, we might need a new API. For now, let's assume we can fetch list of bookings and then their status
                // Or just use the bookings list as history proxy.
                // Let's assume a real API exists or we mock it.
                val bookings = repo.getCustomerBookings()
                // In a real app, we'd have a specific transactions API.
                _paymentHistory.value = UiState.Success(emptyList()) // Placeholder
            } catch (e: Exception) {
                _paymentHistory.value = UiState.Error(e.message ?: "Lỗi tải lịch sử")
            }
        }
    }

    fun clearPaymentState() {
        _paymentState.value = null
        _paymentStatus.value = null
    }

    fun updateNotifSettings(settings: NotifSettings) {
        viewModelScope.launch {
            try {
                repo.updateNotifSettings(settings)
            } catch (e: Exception) {
                _notifSettings.value = UiState.Error(e.message ?: "Lỗi")
            }
        }
    }

    fun updateUser(name: String, phone: String, gender: String, dob: String, address: String, avatarUrl: String?) {
        viewModelScope.launch {
            try {
                repo.updateCustomerUser(name, phone, gender, dob, address, avatarUrl)
                loadUser()
            } catch (e: Exception) {
                Log.e("User", "Update failed", e)
            }
        }
    }

    fun addPet(pet: Pet) = viewModelScope.launch { repo.addPet(pet) }
    fun updatePet(pet: Pet) = viewModelScope.launch { repo.updatePet(pet.id, pet) }
    fun deletePet(petId: String) = viewModelScope.launch { repo.deletePet(petId) }

    fun markAllNotificationsRead() {
        val current = _notifications.value
        if (current is UiState.Success) {
            _notifications.value = UiState.Success(current.data.map { it.copy(read = true) })
        }
    }

    fun cancelBooking(bookingId: String) = viewModelScope.launch { repo.cancelBooking(bookingId) }
    fun rescheduleBooking(bookingId: String, newDate: String, newTime: String) =
        viewModelScope.launch { repo.rescheduleBooking(bookingId, newDate, newTime) }

    fun getStaff(): List<StaffMember> {
        val state = _availableStaff.value
        return if (state is UiState.Success) {
            state.data.map { 
                StaffMember(
                    id = it.staffId.toString(),
                    name = it.staffName,
                    specialty = it.specialty,
                    emoji = it.avatar.ifEmpty { "👤" }
                )
            }
        } else emptyList()
    }

    fun getTimeSlots(): List<String> {
        val draft = bookingDraft.value
        val state = availableStaff.value
        return if (state is UiState.Success) {
            repo.getTimeSlots(draft.staffId, draft.date, state.data)
        } else emptyList()
    }

    private fun syncFcmToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                repo.updateFcmToken(token)
                Log.d("FCM", "Token synced to backend: $token")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to sync token", e)
            }
        }
    }

    fun getBookedSlots(date: String): List<String> = emptyList()
}
