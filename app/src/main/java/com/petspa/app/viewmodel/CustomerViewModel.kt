package com.petspa.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petspa.app.model.*
import com.petspa.app.repository.PetSpaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val repo: PetSpaRepository
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

    // Sử dụng flow từ repository để đồng bộ với bộ nhớ máy
    val bookingDraft: StateFlow<BookingDraft> = repo.bookingDraft
        .stateIn(viewModelScope, SharingStarted.Eagerly, BookingDraft())
    
    val lastRoute: StateFlow<String?> = repo.lastRoute
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateDraft(draft: BookingDraft) {
        viewModelScope.launch {
            repo.saveBookingDraft(draft)
        }
    }

    fun saveLastRoute(route: String?) {
        viewModelScope.launch {
            repo.saveLastRoute(route)
        }
    }

    var localPets = mutableListOf<Pet>()

    init {
        observeAll()
        loadHome()
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
                val booking = repo.createBooking(bookingDraft.value)
                val updatedBooking = booking.copy(
                    paymentStatus = if (payType == "full") "fully-paid" else "partially-paid",
                    paymentMethod = method
                )
                _confirmBookingState.value = UiState.Success(updatedBooking)
                updateDraft(BookingDraft())
            } catch (e: Exception) {
                _confirmBookingState.value = UiState.Error(e.message ?: "Lỗi xác nhận đặt lịch")
            }
        }
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

    fun getStaff() = repo.getStaffForBooking()
    fun getTimeSlots() = repo.getTimeSlots()
    fun getBookedSlots(date: String) = repo.getBookedSlots(date)
}
