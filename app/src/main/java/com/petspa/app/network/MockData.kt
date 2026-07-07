package com.petspa.app.network

import com.petspa.app.model.*
import com.petspa.app.network.dto.request.BookingRequest
import com.petspa.app.network.dto.request.PaymentCreateRequest
import com.petspa.app.network.dto.request.PaymentRequest
import com.petspa.app.network.dto.request.UpdateUserRequest
import com.petspa.app.network.dto.response.*
import kotlinx.coroutines.delay

object MockData {
    val initialUser = User("u1", "Nguyễn Văn Khách", "customer@petspa.com", "0912345678", role = UserRole.CUSTOMER)
    val technician = TechnicianProfile("st1", "Trần Minh Tuấn", "tuan.tm@petspa.com", "0922334455", "Chuyên gia cắt tỉa", "👨‍🔬", listOf("Cắt tỉa", "Tạo kiểu"), "Nam", "1995-05-15", "Hà Nội", "2023-01-10")
    val ownerUser = User("o1", "Lê Văn Chủ", "owner@petspa.com", "0988776655", role = UserRole.OWNER)

    val demoAccounts = mapOf(
        "customer@petspa.com" to Triple("123456", UserRole.CUSTOMER, "u1"),
        "tuan.tm@petspa.com" to Triple("123456", UserRole.STAFF, "st1"),
        "owner@petspa.com" to Triple("123456", UserRole.OWNER, "o1")
    )

    val services = listOf(
        Service("s1", "Tắm cơ bản", "Tắm", 150000, 45, "🚿", "Dịch vụ tắm sạch và sấy khô cho thú cưng"),
        Service("s2", "Cắt tỉa lông", "Cắt tỉa lông", 300000, 90, "✂️", "Cắt tỉa lông theo yêu cầu và vệ sinh tai móng"),
        Service("s3", "Spa cao cấp", "Spa cao cấp", 500000, 120, "🛁", "Gói chăm sóc toàn diện: tắm, cắt tỉa, spa đá nóng"),
        Service("s4", "Chăm sóc da", "Chăm sóc da", 250000, 60, "🧴", "Trị liệu cho da nhạy cảm và dưỡng lông")
    )

    val staffMembers = listOf(
        StaffMember("st1", "Trần Minh Tuấn", specialty = "Cắt tỉa & Tạo kiểu", emoji = "👨‍🔬"),
        StaffMember("st2", "Lê Thu Hương", specialty = "Spa & Chăm sóc da", emoji = "👩‍⚕️"),
        StaffMember("st3", "Phạm Quốc Bảo", specialty = "Tắm & Vệ sinh", emoji = "👨‍🍳")
    )

    val initialPets = mutableListOf(
        Pet("p1", "Lu Lu", "Chó", "Poodle", "Đực", 2, 4.5, "Không", "Đã tiêm phòng đầy đủ", "Thân thiện", "🐩"),
        Pet("p2", "Mimi", "Mèo", "Anh lông ngắn", "Cái", 1, 3.2, "Dị ứng hải sản", "Hay bị viêm tai", "Hơi nhát", "🐈")
    )

    val customerBookings = mutableListOf<Booking>()
    val customerNotifications = listOf(
        NotificationItem("n1", "BOOKING_CONFIRMED", "Đã xác nhận lịch hẹn", "Lịch hẹn cắt tỉa lông cho Lu Lu lúc 10:00 ngày 2027-08-15 đã được xác nhận.", "10 phút trước", true),
        NotificationItem("n2", "REMINDER", "Nhắc nhở lịch hẹn", "Bạn có lịch hẹn tắm cho Mimi vào 15:00 chiều nay.", "2 giờ trước", false)
    )

    var notifSettings = NotifSettings()

    val shifts = listOf(
        Shift("sh1", "2027-08-15", "Sáng", "08:00", "12:00", "approved", "st1"),
        Shift("sh2", "2027-08-15", "Chiều", "13:00", "17:00", "approved", "st1")
    )

    val ownerCustomers = mutableListOf(
        Customer("c1", "Nguyễn Văn Khách", "customer@petspa.com", "0912345678", totalBookings = 5, totalSpent = 1200000),
        Customer("c2", "Trần Thị Bé", "be.tt@gmail.com", "0988001122", totalBookings = 2, totalSpent = 450000)
    )

    val ownerStaff = mutableListOf(
        StaffMember("st1", "Trần Minh Tuấn", "tuan.tm@petspa.com", "0922334455", "Cắt tỉa & Tạo kiểu", "Staff", completedBookings = 124, customers = 85),
        StaffMember("st2", "Lê Thu Hương", "huong.lt@petspa.com", "0933445566", "Spa & Chăm sóc da", "Staff", completedBookings = 98, customers = 72)
    )

    val ownerServices = mutableListOf(
        Service("s1", "Tắm cơ bản", "Tắm", 150000, 45, "🚿"),
        Service("s2", "Cắt tỉa lông", "Cắt tỉa lông", 300000, 90, "✂️"),
        Service("s3", "Spa cao cấp", "Spa cao cấp", 500000, 120, "🛁")
    )

    val ownerBookings = listOf<Booking>()
    var staffRequests = listOf<StaffRequest>()

    val revenueByDay = listOf(
        RevenuePoint("01/08", 1200000, 5),
        RevenuePoint("02/08", 850000, 3),
        RevenuePoint("03/08", 2100000, 8)
    )
    val revenueByMonth = listOf(
        RevenuePoint("Jan", 45000000, 120),
        RevenuePoint("Feb", 52000000, 145)
    )
    val topServices = listOf(
        TopService("Cắt tỉa lông", 45, 13500000),
        TopService("Tắm cơ bản", 38, 5700000)
    )
}

// Mock implementation Retrofit
class MockApiService : ApiService {
    override suspend fun login(request: LoginRequest): AuthResponse {
        delay(800)
        val account = MockData.demoAccounts[request.email.trim().lowercase()]
            ?: throw IllegalArgumentException("Email hoặc mật khẩu không đúng")
        if (account.first != request.password) throw IllegalArgumentException("Email hoặc mật khẩu không đúng")
        val user = when (account.second) {
            UserRole.CUSTOMER -> MockData.initialUser
            UserRole.STAFF -> MockData.technician.let { User(it.id, it.name, it.email, it.phone, role = UserRole.STAFF) }
            UserRole.OWNER -> MockData.ownerUser
        }
        return AuthResponse("token_${account.third}_${System.currentTimeMillis()}", user.copy(role = account.second))
    }

    override suspend fun getPets(): List<PetResponse> { delay(400); return emptyList() }

    override suspend fun addPet(pet: Pet): PetResponse {
        delay(500)
        return PetResponse(1L, pet.name, pet.species, pet.breed, customerId = 1L)
    }

    override suspend fun updatePet(id: String, pet: Pet): PetResponse {
        delay(500)
        return PetResponse(id.toLongOrNull() ?: 1L, pet.name, pet.species, pet.breed, customerId = 1L)
    }

    override suspend fun deletePet(id: String) {
        delay(500)
    }

    override suspend fun getServices(): List<ServiceResponse> { delay(400); return emptyList() }
    override suspend fun getCustomerBookings(): List<BookingResponse> { delay(400); return emptyList() }

    override suspend fun createBooking(booking: BookingRequest): BookingResponse {
        delay(1000)
        return BookingResponse(1L, booking.petId, booking.serviceId, booking.staffId, 1L)
    }

    override suspend fun createPayment(request: PaymentCreateRequest): PaymentResponseDto {
        delay(1000)
        return PaymentResponseDto("https://momo.vn/pending", request.bookingId.toString(), "Success")
    }

    override suspend fun getPaymentStatus(bookingId: Long): PaymentTransactionDto {
        delay(800)
        return PaymentTransactionDto(
            id = 1L,
            bookingId = bookingId,
            paymentGateway = "MOMO",
            requestId = "req_123",
            amount = java.math.BigDecimal.valueOf(500000),
            paymentStatus = "SUCCESS"
        )
    }

    override suspend fun uploadImage(file: okhttp3.MultipartBody.Part, type: String): Map<String, String> {
        delay(1500)
        return mapOf("url" to "https://res.cloudinary.com/demo/image/upload/sample.jpg")
    }

    override suspend fun cancelBooking(id: String) {
        delay(500)
    }

    override suspend fun rescheduleBooking(id: String, data: Map<String, String>): BookingResponse {
        delay(800)
        return BookingResponse(id.toLongOrNull() ?: 1L, 1L, 1L, 1L, 1L)
    }

    override suspend fun updatePayment(id: String, payment: PaymentRequest): BookingResponse {
        delay(500)
        return BookingResponse(
            id = id.toLongOrNull() ?: 1L,
            petId = 1L,
            serviceId = 1L,
            staffId = 1L,
            customerId = 1L,
            paymentStatus = payment.paymentStatus,
            paidAmount = payment.paidAmount ?: java.math.BigDecimal.ZERO,
            transactionId = payment.transactionId ?: ""
        )
    }

    override suspend fun getCustomerNotifications(): List<NotificationResponse> { delay(300); return emptyList() }
    override suspend fun getNotifSettings(): NotifSettings { delay(200); return MockData.notifSettings }
    override suspend fun updateNotifSettings(settings: NotifSettings): NotifSettings { delay(300); MockData.notifSettings = settings; return settings }
    override suspend fun getCustomerUser(): User { delay(200); return MockData.initialUser }
    override suspend fun updateFcmToken(request: Map<String, String>) { delay(100) }
    override suspend fun updateCustomerUser(request: UpdateUserRequest): User {
        delay(300)
        // Partial update - chỉ update field khác null
        val currentUser = MockData.initialUser
        return currentUser.copy(
            name = request.name ?: currentUser.name,
            phone = request.phone ?: currentUser.phone,
            gender = request.gender ?: currentUser.gender,
            dob = request.dob ?: currentUser.dob,
            address = request.address ?: currentUser.address
        )
    }
    override suspend fun getStaffBookings(): List<BookingResponse> { delay(400); return emptyList() }
    override suspend fun getShifts(): List<Shift> { delay(300); return MockData.shifts }
    override suspend fun getStaffNotifications(): List<NotificationResponse> { delay(300); return emptyList() }
    override suspend fun getTechnicianProfile(): TechnicianProfile { delay(300); return MockData.technician }

    override suspend fun updateTechnicianProfile(profile: TechnicianProfile): TechnicianProfile {
        delay(500)
        return profile
    }

    override suspend fun getCustomers(): List<Customer> { delay(400); return MockData.ownerCustomers.toList() }
    override suspend fun addCustomer(customer: Customer): Customer {
        delay(500)
        return customer
    }
    override suspend fun updateCustomer(id: String, customer: Customer): Customer {
        delay(500)
        return customer
    }
    override suspend fun deleteCustomer(id: String) {
        delay(500)
    }

    override suspend fun getStaffList(): List<StaffMember> { delay(400); return MockData.ownerStaff.toList() }

    override suspend fun getAvailableStaff(date: String): List<StaffAvailabilityResponse> {
        delay(500)
        return emptyList()
    }

    override suspend fun addStaff(staff: StaffMember): StaffMember {
        delay(500)
        return staff
    }
    override suspend fun updateStaff(id: String, staff: StaffMember): StaffMember {
        delay(500)
        return staff
    }
    override suspend fun deleteStaff(id: String) {
        delay(500)
    }

    override suspend fun getOwnerServices(): List<ServiceResponse> { delay(400); return emptyList() }
    override suspend fun addService(service: Service): ServiceResponse {
        delay(500)
        return ServiceResponse(1L, service.name, price = java.math.BigDecimal.valueOf(service.price), duration = service.duration)
    }
    override suspend fun updateService(id: String, service: Service): ServiceResponse {
        delay(500)
        return ServiceResponse(id.toLongOrNull() ?: 1L, service.name, price = java.math.BigDecimal.valueOf(service.price), duration = service.duration)
    }
    override suspend fun deleteService(id: String) {
        delay(500)
    }

    override suspend fun getOwnerBookings(): List<BookingResponse> { delay(400); return emptyList() }
    override suspend fun getStaffRequests(): List<StaffRequest> { delay(300); return MockData.staffRequests }
    override suspend fun getDailyRevenue(): List<RevenuePoint> { delay(300); return MockData.revenueByDay }
    override suspend fun getMonthlyRevenue(): List<RevenuePoint> { delay(300); return MockData.revenueByMonth }
    override suspend fun getTopServices(): List<TopService> { delay(300); return MockData.topServices }

    override suspend fun updateStaffRequestStatus(id: String, status: Map<String, String>): StaffRequest {
        delay(300)
        return StaffRequest("", "", "", "", "", "")
    }
}
