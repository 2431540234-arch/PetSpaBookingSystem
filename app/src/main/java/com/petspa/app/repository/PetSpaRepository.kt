package com.petspa.app.repository

import com.petspa.app.data.AuthSession
import com.petspa.app.data.BookingDataStore
import com.petspa.app.data.TokenDataStore
import com.petspa.app.model.*
import com.petspa.app.network.ApiService
import com.petspa.app.network.LoginRequest
import com.petspa.app.network.dto.request.PaymentCreateRequest
import com.petspa.app.network.dto.request.PaymentRequest
import com.petspa.app.network.dto.response.PaymentResponseDto
import com.petspa.app.network.dto.response.PaymentTransactionDto
import com.petspa.app.network.mapper.NetworkMapper.toDomain
import com.petspa.app.network.mapper.NetworkMapper.toRequest
import okhttp3.MultipartBody
import com.petspa.app.data.local.database.PetSpaDatabase
import com.petspa.app.data.local.mapper.*
import kotlinx.coroutines.flow.*

class PetSpaRepository(
    private val api: ApiService,
    private val db: PetSpaDatabase,
    private val tokenStore: TokenDataStore,
    private val bookingStore: BookingDataStore
) {
    val authSession: Flow<AuthSession?> = tokenStore.authFlow
    val bookingDraft: Flow<BookingDraft> = bookingStore.draftFlow
    val lastRoute: Flow<String?> = bookingStore.lastRouteFlow

    suspend fun saveBookingDraft(draft: BookingDraft) = bookingStore.saveDraft(draft)
    suspend fun saveLastRoute(route: String?) = bookingStore.saveLastRoute(route)
    suspend fun clearBookingDraft() = bookingStore.clear()

    // --- Observe Room Flows (UI source of truth) ---

    fun observePets(): Flow<List<Pet>> =
        db.petDao().observePets().map { list -> list.map { it.toPet() } }

    fun observeCustomers(): Flow<List<Customer>> =
        db.customerDao().observeCustomers().map { list -> list.map { it.toCustomer() } }

    fun observeStaff(): Flow<List<StaffMember>> =
        db.staffDao().observeStaff().map { list -> list.map { it.toStaff() } }

    fun observeServices(): Flow<List<Service>> =
        db.serviceDao().observeServices().map { list -> list.map { it.toService() } }

    fun observeBookings(): Flow<List<Booking>> =
        db.bookingDao().observeBookings().map { list -> list.map { it.toBooking() } }

    fun observeNotifications(): Flow<List<NotificationItem>> =
        db.notificationDao().observeNotifications().map { list -> list.map { it.toNotification() } }

    fun observeUser(): Flow<User?> =
        db.userDao().observeUsers().map { list -> list.firstOrNull()?.toUser() }

    fun observeNotifSettings(): Flow<NotifSettings?> =
        db.notifSettingsDao().observeSettings().map { it?.toNotifSettings() }

    fun observeShifts(): Flow<List<Shift>> =
        db.shiftDao().observeShifts().map { list -> list.map { it.toShift() } }

    fun observeStaffRequests(): Flow<List<StaffRequest>> =
        db.staffRequestDao().observeStaffRequests().map { list -> list.map { it.toStaffRequest() } }

    fun observeDailyRevenue(): Flow<List<RevenuePoint>> =
        db.revenueDao().observeRevenue("DAILY").map { list -> list.map { it.toRevenuePoint() } }

    fun observeMonthlyRevenue(): Flow<List<RevenuePoint>> =
        db.revenueDao().observeRevenue("MONTHLY").map { list -> list.map { it.toRevenuePoint() } }

    fun observeTopServices(): Flow<List<TopService>> =
        db.revenueDao().observeTopServices().map { list -> list.map { it.toTopService() } }

    fun observeTechnicianProfile(): Flow<TechnicianProfile?> =
        db.technicianProfileDao().observeProfile().map { it?.toDomain() }

    // --- Silent Refresh (Sync API -> Room) ---

    suspend fun refreshPets() {
        try {
            val pets = api.getPets().map { it.toDomain() }
            db.petDao().deleteAll()
            db.petDao().insertAll(pets.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshCustomers() {
        try {
            val customers = api.getCustomers()
            db.customerDao().deleteAll()
            db.customerDao().insertAll(customers.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshStaff() {
        try {
            val staff = api.getStaffList()
            db.staffDao().deleteAll()
            db.staffDao().insertAll(staff.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshServices() {
        val role = tokenStore.authFlow.firstOrNull()?.role
        try {
            val services = if (role == UserRole.OWNER) {
                api.getOwnerServices().map { it.toDomain() }
            } else {
                api.getServices().map { it.toDomain() }
            }
            db.serviceDao().deleteAll()
            db.serviceDao().insertAll(services.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshBookings() {
        val role = tokenStore.authFlow.firstOrNull()?.role
        try {
            val bookings = when (role) {
                UserRole.OWNER -> api.getOwnerBookings().map { it.toDomain() }
                UserRole.STAFF -> api.getStaffBookings().map { it.toDomain() }
                else -> api.getCustomerBookings().map { it.toDomain() }
            }
            db.bookingDao().deleteAll()
            db.bookingDao().insertAll(bookings.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshNotifications() {
        val role = tokenStore.authFlow.firstOrNull()?.role
        try {
            val notifs = if (role == UserRole.STAFF) {
                api.getStaffNotifications().map { it.toDomain() }
            } else {
                api.getCustomerNotifications().map { it.toDomain() }
            }
            db.notificationDao().deleteAll()
            db.notificationDao().insertAll(notifs.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshUser() {
        try {
            val user = api.getCustomerUser()
            db.userDao().deleteAll()
            db.userDao().insertUser(user.toEntity())
        } catch (_: Exception) {}
    }

    suspend fun refreshNotifSettings() {
        try {
            val settings = api.getNotifSettings()
            db.notifSettingsDao().saveSettings(settings.toEntity())
        } catch (_: Exception) {}
    }

    suspend fun refreshShifts() {
        try {
            val shifts = api.getShifts()
            db.shiftDao().deleteAll()
            db.shiftDao().insertAll(shifts.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshStaffRequests() {
        try {
            val requests = api.getStaffRequests()
            db.staffRequestDao().deleteAll()
            db.staffRequestDao().insertAll(requests.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshRevenue() {
        try {
            val daily = api.getDailyRevenue()
            db.revenueDao().deleteRevenueByType("DAILY")
            db.revenueDao().insertRevenue(daily.map { it.toEntity("DAILY") })

            val monthly = api.getMonthlyRevenue()
            db.revenueDao().deleteRevenueByType("MONTHLY")
            db.revenueDao().insertRevenue(monthly.map { it.toEntity("MONTHLY") })

            val top = api.getTopServices()
            db.revenueDao().deleteAllTopServices()
            db.revenueDao().insertTopServices(top.map { it.toEntity() })
        } catch (_: Exception) {}
    }

    suspend fun refreshTechnicianProfile() {
        try {
            val profile = api.getTechnicianProfile()
            db.technicianProfileDao().deleteProfile()
            db.technicianProfileDao().insertProfile(profile.toEntity())
        } catch (_: Exception) {}
    }

    // --- Legacy / One-shot API calls (Usually triggered by Refresh) ---

    suspend fun login(email: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(email.trim(), password))
        tokenStore.saveSession(response.token, response.user.role, response.user.id, response.user.name, response.user.email)
        return response
    }

    suspend fun logout() = tokenStore.clearSession()

    suspend fun getPets(): List<Pet> {
        refreshPets()
        return emptyList() // UI should observe observePets()
    }

    suspend fun addPet(pet: Pet): Pet {
        val newPet = api.addPet(pet).toDomain()
        db.petDao().insertPet(newPet.toEntity())
        return newPet
    }

    suspend fun updatePet(id: String, pet: Pet): Pet {
        val updated = api.updatePet(id, pet).toDomain()
        db.petDao().updatePet(updated.toEntity())
        return updated
    }

    suspend fun deletePet(id: String) {
        api.deletePet(id)
        db.petDao().getPetById(id)?.let { db.petDao().deletePet(it) }
    }

    suspend fun getServices(): List<Service> {
        refreshServices()
        return emptyList()
    }

    suspend fun getCustomerBookings(): List<Booking> {
        refreshBookings()
        return emptyList()
    }

    suspend fun createBooking(booking: BookingDraft): Booking {
        val newBooking = api.createBooking(booking.toRequest()).toDomain()
        db.bookingDao().insertBooking(newBooking.toEntity())
        return newBooking
    }

    suspend fun cancelBooking(id: String) {
        // Backend deleteBooking returns void; call API then update local DB
        api.cancelBooking(id)
        db.bookingDao().getBookingById(id)?.let {
            val updated = it.copy(status = "cancelled")
            db.bookingDao().updateBooking(updated)
        }
    }

    suspend fun rescheduleBooking(id: String, date: String, time: String): Booking {
        val updated = api.rescheduleBooking(id, mapOf("date" to date, "time" to time)).toDomain()
        db.bookingDao().updateBooking(updated.toEntity())
        return updated
    }

    suspend fun updatePayment(id: String, status: String, amount: Long, txId: String): Booking {
        val request = PaymentRequest(
            paymentStatus = status,
            paidAmount = java.math.BigDecimal.valueOf(amount),
            transactionId = txId
        )
        val updated = api.updatePayment(id, request).toDomain()
        db.bookingDao().updateBooking(updated.toEntity())
        return updated
    }

    suspend fun getCustomerNotifications(): List<NotificationItem> {
        refreshNotifications()
        return emptyList()
    }

    suspend fun getNotifSettings(): NotifSettings {
        refreshNotifSettings()
        return NotifSettings()
    }

    suspend fun updateNotifSettings(s: NotifSettings): NotifSettings {
        val updated = api.updateNotifSettings(s)
        db.notifSettingsDao().saveSettings(updated.toEntity())
        return updated
    }

    suspend fun getCustomerUser(): User {
        refreshUser()
        return User("", "", "", "")
    }

    suspend fun updateCustomerUser(name: String, phone: String, gender: String, dob: String, address: String, avatarUrl: String?) {
        val request = com.petspa.app.network.dto.request.UpdateUserRequest(
            name = name,
            phone = phone,
            gender = gender,
            dob = dob,
            address = address,
            avatarUrl = avatarUrl
        )
        val updated = api.updateCustomerUser(request)
        db.userDao().insertUser(updated.toEntity())
    }

    suspend fun updateFcmToken(token: String) {
        try {
            api.updateFcmToken(mapOf("token" to token))
        } catch (_: Exception) {}
    }

    suspend fun getStaffBookings(): List<Booking> {
        refreshBookings()
        return emptyList()
    }

    suspend fun getShifts(): List<Shift> {
        refreshShifts()
        return emptyList()
    }

    suspend fun getStaffNotifications(): List<NotificationItem> {
        refreshNotifications()
        return emptyList()
    }

    suspend fun getTechnicianProfile(): TechnicianProfile {
        refreshTechnicianProfile()
        return TechnicianProfile("", "", "", "", "", "", emptyList(), "", "", "", "")
    }

    suspend fun updateTechnicianProfile(profile: TechnicianProfile): TechnicianProfile {
        val updated = api.updateTechnicianProfile(profile)
        db.technicianProfileDao().insertProfile(updated.toEntity())
        return updated
    }

    suspend fun getCustomers(): List<Customer> {
        refreshCustomers()
        return emptyList()
    }

    suspend fun addCustomer(c: Customer): Customer {
        val newCustomer = api.addCustomer(c)
        db.customerDao().insertCustomer(newCustomer.toEntity())
        return newCustomer
    }

    suspend fun updateCustomer(id: String, c: Customer): Customer {
        val updated = api.updateCustomer(id, c)
        db.customerDao().updateCustomer(updated.toEntity())
        return updated
    }

    suspend fun deleteCustomer(id: String) {
        api.deleteCustomer(id)
        db.customerDao().getCustomerById(id)?.let { db.customerDao().deleteCustomer(it) }
    }

    suspend fun getStaffList(): List<StaffMember> {
        refreshStaff()
        return emptyList()
    }

    suspend fun addStaff(s: StaffMember): StaffMember {
        val newStaff = api.addStaff(s)
        db.staffDao().insertStaff(newStaff.toEntity())
        return newStaff
    }

    suspend fun updateStaff(id: String, s: StaffMember): StaffMember {
        val updated = api.updateStaff(id, s)
        db.staffDao().updateStaff(updated.toEntity())
        return updated
    }

    suspend fun deleteStaff(id: String) {
        api.deleteStaff(id)
        db.staffDao().getStaffById(id)?.let { db.staffDao().deleteStaff(it) }
    }

    suspend fun getOwnerServices(): List<Service> {
        refreshServices()
        return emptyList()
    }

    suspend fun addService(s: Service): Service {
        val newService = api.addService(s).toDomain()
        db.serviceDao().insertService(newService.toEntity())
        return newService
    }

    suspend fun updateService(id: String, s: Service): Service {
        val updated = api.updateService(id, s).toDomain()
        db.serviceDao().updateService(updated.toEntity())
        return updated
    }

    suspend fun deleteService(id: String) {
        api.deleteService(id)
        db.serviceDao().getServiceById(id)?.let { db.serviceDao().deleteService(it) }
    }

    suspend fun getOwnerBookings(): List<Booking> {
        refreshBookings()
        return emptyList()
    }

    suspend fun getStaffRequests(): List<StaffRequest> {
        refreshStaffRequests()
        return emptyList()
    }

    suspend fun getDailyRevenue(): List<RevenuePoint> {
        refreshRevenue()
        return emptyList()
    }

    suspend fun getMonthlyRevenue(): List<RevenuePoint> {
        refreshRevenue()
        return emptyList()
    }

    suspend fun getTopServices(): List<TopService> {
        refreshRevenue()
        return emptyList()
    }

    suspend fun updateStaffRequestStatus(id: String, status: String): StaffRequest {
        val updated = api.updateStaffRequestStatus(id, mapOf("status" to status))
        // Re-sync after update
        refreshStaffRequests()
        return updated
    }

    suspend fun getAvailableStaff(date: String): List<StaffAvailabilityResponse> {
        return api.getAvailableStaff(date)
    }

    suspend fun createPayment(bookingId: Long, gateway: String): PaymentResponseDto {
        return api.createPayment(PaymentCreateRequest(bookingId, gateway))
    }

    suspend fun getPaymentStatus(bookingId: Long): PaymentTransactionDto {
        return api.getPaymentStatus(bookingId)
    }

    suspend fun uploadImage(filePart: MultipartBody.Part, type: String): String? {
        return try {
            val response = api.uploadImage(filePart, type)
            response["url"]
        } catch (e: Exception) {
            null
        }
    }

    fun getTimeSlots(staffId: String, date: String, staffList: List<StaffAvailabilityResponse>): List<String> {
        return if (staffId == "any" || staffId.isEmpty()) {
            // Lấy hợp (Union) của tất cả slots từ tất cả nhân viên rảnh
            staffList.flatMap { it.availableSlots }.distinct().sorted()
        } else {
            // Lấy slots của nhân viên cụ thể
            staffList.find { it.staffId.toString() == staffId }?.availableSlots ?: emptyList()
        }
    }

    fun getBookedSlots(date: String) = emptyList<String>() // Sẽ xử lý bằng cách ẩn các slot không có trong availableSlots
}
