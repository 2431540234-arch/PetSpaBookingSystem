package com.petspa.app.network

import com.petspa.app.model.*
import kotlinx.coroutines.delay

// Dữ liệu mock từ Figma exports
object MockData {

    val demoAccounts = mapOf(
        "vanvan@gmail.com" to Triple("Password1", UserRole.CUSTOMER, "u1"),
        "tuan.tran@petspa.com" to Triple("password123", UserRole.STAFF, "TECH001"),
        "admin@petspa.com" to Triple("admin123", UserRole.OWNER, "OWNER001")
    )

    val initialUser = User(
        id = "u1", name = "Nguyễn Văn An", email = "vanvan@gmail.com",
        phone = "0912345678", gender = "Nam", dob = "1995-03-20",
        address = "123 Nguyễn Huệ, Q.1, TP.HCM", role = UserRole.CUSTOMER
    )

    var initialPets = mutableListOf(
        Pet("p1", "Lucky", "Chó", "Poodle", "Đực", 3, 4.5, "Không có", "Đã tiêm phòng đầy đủ", "Thích được vuốt ve", "🐩"),
        Pet("p2", "Mimi", "Mèo", "Ba Tư", "Cái", 2, 3.2, "Dị ứng hải sản", "Sức khỏe tốt", "Nhút nhát với người lạ", "🐱")
    )

    val services = listOf(
        Service("s1", "Tắm Cơ Bản", "Tắm", 150000, 45, "🛁", "Tắm sạch bằng sữa tắm chuyên dụng, sấy khô và chải lông."),
        Service("s2", "Tắm Cao Cấp", "Tắm", 250000, 60, "✨", "Tắm với sản phẩm cao cấp, massage nhẹ."),
        Service("s3", "Cắt Tỉa Lông", "Cắt tỉa lông", 200000, 60, "✂️", "Cắt và tỉa lông theo yêu cầu."),
        Service("s4", "Cắt Lông Full Style", "Cắt tỉa lông", 350000, 90, "💈", "Cắt lông toàn thân theo kiểu yêu thích."),
        Service("s5", "Chăm Sóc Da", "Chăm sóc da", 180000, 45, "💆", "Điều trị da khô, ngứa."),
        Service("s6", "Trị Liệu Da Chuyên Sâu", "Chăm sóc da", 300000, 75, "🌿", "Điều trị viêm da, dị ứng."),
        Service("s7", "Spa Thư Giãn", "Spa cao cấp", 400000, 90, "🌸", "Gói spa toàn diện."),
        Service("s8", "Royal Spa", "Spa cao cấp", 650000, 120, "👑", "Trải nghiệm spa đẳng cấp nhất.")
    )

    val staffMembers = listOf(
        StaffMember("st1", "Trần Minh Tuấn", specialty = "Cắt tỉa & Tạo kiểu", emoji = "👨‍🔬"),
        StaffMember("st2", "Lê Thu Hương", specialty = "Spa & Chăm sóc da", emoji = "👩‍⚕️"),
        StaffMember("st3", "Phạm Quốc Bảo", specialty = "Tắm & Vệ sinh", emoji = "👨‍🍳")
    )

    var customerBookings = mutableListOf(
        Booking("B001", "p1", "Lucky", "s3", "Cắt Tỉa Lông", "st1", "Trần Minh Tuấn", date = "2027-08-15", time = "10:00", notes = "Cắt kiểu Poodle truyền thống", status = "confirmed", paymentStatus = "partially-paid", totalAmount = 200000, paidAmount = 100000, paymentMethod = "MoMo", transactionId = "MM20270815001", createdAt = "2027-08-10"),
        Booking("B002", "p2", "Mimi", "s5", "Chăm Sóc Da", "st2", "Lê Thu Hương", date = "2027-08-20", time = "14:30", notes = "Da hơi khô", status = "pending", paymentStatus = "unpaid", totalAmount = 180000, createdAt = "2027-08-12"),
        Booking("B003", "p1", "Lucky", "s7", "Spa Thư Giãn", "st2", "Lê Thu Hương", date = "2027-07-20", time = "09:00", status = "completed", paymentStatus = "fully-paid", totalAmount = 400000, paidAmount = 400000, paymentMethod = "VNPay", createdAt = "2027-07-15"),
        Booking("B004", "p2", "Mimi", "s1", "Tắm Cơ Bản", "st3", "Phạm Quốc Bảo", date = "2027-07-05", time = "11:00", status = "cancelled", paymentStatus = "unpaid", totalAmount = 150000, createdAt = "2027-07-01")
    )

    val customerNotifications = listOf(
        NotificationItem("n1", "booking", "🎉 Đặt lịch thành công", "Lịch hẹn B001 của Lucky đã được xác nhận.", "2 giờ trước", false, "B001"),
        NotificationItem("n2", "payment", "💳 Thanh toán thành công", "Đã thanh toán đặt cọc 100.000đ cho B001.", "2 giờ trước", false, "B001"),
        NotificationItem("n3", "service", "✅ Dịch vụ hoàn thành", "Lucky đã hoàn thành Spa Thư Giãn.", "1 ngày trước", true, "B003"),
        NotificationItem("n4", "booking", "📅 Nhắc lịch hẹn", "Lịch hẹn B001 còn 5 ngày nữa.", "3 ngày trước", true, "B001"),
        NotificationItem("n5", "booking", "❌ Lịch hẹn bị hủy", "Lịch hẹn B004 của Mimi đã bị hủy.", "5 ngày trước", true, "B004")
    )

    var notifSettings = NotifSettings()

    val timeSlots = listOf("08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00")
    val bookedSlots = mapOf("2027-08-15" to listOf("08:00","09:00","13:00"), "2027-08-20" to listOf("10:00","14:00","15:30"))

    val technician = TechnicianProfile("TECH001", "Trần Minh Tuấn", "tuan.tran@petspa.com", "0901234567", "Senior Pet Groomer", "👨‍⚕️", listOf("Grooming","Bathing","Pet Care"), "Nam", "1995-05-15", "123 Nguyễn Văn Linh, Q.7, TP.HCM", "2020-01-10")

    val staffBookings = listOf(
        Booking("BK001", "PET001", "Lucky", serviceName = "Tắm + Cắt tỉa lông", customerId = "CUS001", customerName = "Nguyễn Văn An", customerPhone = "0912345678", date = "2027-08-15", time = "10:00", status = "pending", serviceDuration = 90, customerRequests = "Cắt lông ngắn, phong cách Teddy Bear"),
        Booking("BK002", "PET002", "Mimi", serviceName = "Tắm + Chải lông", customerId = "CUS002", customerName = "Lê Thị Bình", customerPhone = "0923456789", date = "2027-08-15", time = "14:00", status = "pending", serviceDuration = 60, customerRequests = "Chải lông kỹ"),
        Booking("BK003", "PET003", "Max", serviceName = "Tắm + Massage + Cắt tỉa lông", customerId = "CUS003", customerName = "Phạm Văn Cường", customerPhone = "0934567890", date = "2027-08-16", time = "09:00", status = "pending", serviceDuration = 120, customerRequests = "Massage nhẹ nhàng")
    )

    val shifts = listOf(
        Shift("SHIFT001", "2027-08-15", "morning", "08:00", "12:00", "approved", "TECH001"),
        Shift("SHIFT002", "2027-08-15", "afternoon", "13:00", "17:00", "approved", "TECH001"),
        Shift("SHIFT003", "2027-08-16", "morning", "08:00", "12:00", "approved", "TECH001")
    )

    val staffNotifications = listOf(
        NotificationItem("NOT001", "booking-new", "Booking mới được giao", "Lucky - Poodle vào 10:00 ngày 15/08/2027", "2027-08-14T15:30:00", false, "BK001"),
        NotificationItem("NOT002", "shift-approved", "Ca làm được duyệt", "Ca sáng ngày 15/08/2027 đã được phê duyệt", "2027-08-14T10:00:00", false, "SHIFT001"),
        NotificationItem("NOT003", "booking-new", "Booking mới được giao", "Mimi - Persian vào 14:00 ngày 15/08/2027", "2027-08-14T09:00:00", true, "BK002")
    )

    var ownerCustomers = mutableListOf(
        Customer("KH001", "Nguyễn Văn An", "an.nguyen@email.com", "0901234567", "active", "2024-01-15", "male", "1990-05-20", "123 Lê Lợi, Q1, TP.HCM", 12, 10, 2, 4500000),
        Customer("KH002", "Trần Thị Bích", "bich.tran@email.com", "0912345678", "active", "2024-02-10", "female", "1995-08-15", "456 Nguyễn Huệ, Q1, TP.HCM", 8, 7, 1, 3200000),
        Customer("KH003", "Lê Minh Dũng", "dung.le@email.com", "0923456789", "locked", "2024-01-20", "male", "1988-12-03", "789 Trần Hưng Đạo, Q5, TP.HCM", 3, 2, 1, 900000),
        Customer("KH004", "Phạm Thu Hương", "huong.pham@email.com", "0934567890", "active", "2024-03-05", "female", "1992-03-28", "321 Điện Biên Phủ, Bình Thạnh", 15, 14, 1, 7800000),
        Customer("KH005", "Hoàng Quốc Tuấn", "tuan.hoang@email.com", "0945678901", "active", "2024-02-28", "male", "1985-07-10", "654 CMT8, Q10", 6, 5, 1, 2100000),
        Customer("KH006", "Vũ Thị Lan", "lan.vu@email.com", "0956789012", "active", "2024-03-15", "female", "1997-11-22", "987 Lý Thường Kiệt, Q11", 4, 3, 1, 1600000)
    )

    var ownerStaff = mutableListOf(
        StaffMember("NV001", "Trần Minh Tuấn", "tuan.tran@petspa.com", "0901111111", role = "Groomer", status = "active", completedBookings = 145, customers = 98, createdAt = "2023-06-01"),
        StaffMember("NV002", "Nguyễn Thị Mai", "mai.nguyen@petspa.com", "0902222222", role = "Groomer", status = "active", completedBookings = 132, customers = 87, createdAt = "2023-07-15"),
        StaffMember("NV003", "Lê Văn Hùng", "hung.le@petspa.com", "0903333333", role = "Vet Assistant", status = "active", completedBookings = 89, customers = 65, createdAt = "2023-08-01"),
        StaffMember("NV004", "Phạm Thị Linh", "linh.pham@petspa.com", "0904444444", role = "Receptionist", status = "locked", completedBookings = 45, customers = 40, createdAt = "2023-09-10"),
        StaffMember("NV005", "Hoàng Văn Nam", "nam.hoang@petspa.com", "0905555555", role = "Groomer", status = "active", completedBookings = 67, customers = 52, createdAt = "2024-01-05")
    )

    var ownerServices = mutableListOf(
        Service("DV001", "Tắm & Sấy", price = 150000, duration = 60, description = "Tắm sạch với sữa tắm chuyên dụng", status = "active"),
        Service("DV002", "Cắt tỉa lông", price = 200000, duration = 90, description = "Cắt tỉa lông chuyên nghiệp", status = "active"),
        Service("DV003", "Tắm + Cắt tỉa lông", price = 320000, duration = 120, description = "Combo ưu đãi", status = "active"),
        Service("DV004", "Spa thư giãn", price = 450000, duration = 150, description = "Massage và chăm sóc cao cấp", status = "active"),
        Service("DV005", "Kiểm tra sức khỏe", price = 250000, duration = 60, description = "Kiểm tra tổng quát", status = "active"),
        Service("DV006", "Vệ sinh tai & răng", price = 120000, duration = 45, description = "Vệ sinh chuyên nghiệp", status = "inactive")
    )

    val ownerBookings = listOf(
        Booking("BK001", "TC001", "Lucky", "DV003", "Tắm + Cắt tỉa lông", "NV001", "Trần Minh Tuấn", "KH001", "Nguyễn Văn An", date = "2025-01-15", time = "09:00", status = "completed", paymentStatus = "fully_paid", totalAmount = 320000, paymentMethod = "MoMo"),
        Booking("BK002", "TC003", "Mimi", "DV001", "Tắm & Sấy", "NV002", "Nguyễn Thị Mai", "KH002", "Trần Thị Bích", date = "2025-01-16", time = "10:30", status = "confirmed", paymentStatus = "unpaid", totalAmount = 150000, paymentMethod = "Cash", notes = "Mèo nhạy cảm"),
        Booking("BK003", "TC004", "Rex", "DV004", "Spa thư giãn", "NV001", "Trần Minh Tuấn", "KH004", "Phạm Thu Hương", date = "2025-01-16", time = "14:00", status = "in_progress", paymentStatus = "partially_paid", totalAmount = 450000, paymentMethod = "VNPay"),
        Booking("BK004", "TC005", "Coco", "DV002", "Cắt tỉa lông", "NV005", "Hoàng Văn Nam", "KH005", "Hoàng Quốc Tuấn", date = "2025-01-17", time = "08:30", status = "pending", paymentStatus = "unpaid", totalAmount = 200000),
        Booking("BK005", "TC006", "Tom", "DV005", "Kiểm tra sức khỏe", "NV003", "Lê Văn Hùng", "KH006", "Vũ Thị Lan", date = "2025-01-17", time = "11:00", status = "pending", paymentStatus = "unpaid", totalAmount = 250000),
        Booking("BK006", "TC002", "Bông", "DV001", "Tắm & Sấy", "NV002", "Nguyễn Thị Mai", "KH001", "Nguyễn Văn An", date = "2025-01-14", time = "15:00", status = "cancelled", paymentStatus = "fully_paid", totalAmount = 150000, paymentMethod = "MoMo", notes = "Khách hủy do bận")
    )

    var staffRequests = listOf(
        StaffRequest("YC001", "NV001", "Trần Minh Tuấn", "register_shift", "2025-01-20", "Muốn đăng ký ca sáng thứ Hai", "pending", "2025-01-15"),
        StaffRequest("YC002", "NV002", "Nguyễn Thị Mai", "day_off", "2025-01-22", "Có việc gia đình", "approved", "2025-01-14"),
        StaffRequest("YC003", "NV005", "Hoàng Văn Nam", "busy", "2025-01-18", "Bận đi khám bác sĩ", "rejected", "2025-01-13"),
        StaffRequest("YC004", "NV003", "Lê Văn Hùng", "day_off", "2025-01-25", "Nghỉ phép năm", "pending", "2025-01-16")
    )

    val revenueByDay = listOf(
        RevenuePoint("10/01", 2800000, 8), RevenuePoint("11/01", 3200000, 10), RevenuePoint("12/01", 2100000, 6),
        RevenuePoint("13/01", 4500000, 14), RevenuePoint("14/01", 3800000, 12), RevenuePoint("15/01", 5200000, 16),
        RevenuePoint("16/01", 4100000, 13)
    )

    val revenueByMonth = listOf(
        RevenuePoint("T7/24", 28500000, 89), RevenuePoint("T8/24", 31200000, 97), RevenuePoint("T9/24", 29800000, 93),
        RevenuePoint("T10/24", 35600000, 112), RevenuePoint("T11/24", 38900000, 122), RevenuePoint("T12/24", 42100000, 132),
        RevenuePoint("T1/25", 35500000, 111)
    )

    val topServices = listOf(
        TopService("Tắm + Cắt tỉa lông", 145, 46400000),
        TopService("Tắm & Sấy", 132, 19800000),
        TopService("Cắt tỉa lông", 98, 19600000),
        TopService("Spa thư giãn", 67, 30150000),
        TopService("Kiểm tra sức khỏe", 45, 11250000)
    )

    val ownerUser = User("OWNER001", "Admin Pet Spa", "admin@petspa.com", "0900000000", role = UserRole.OWNER)
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

    override suspend fun getPets(): List<Pet> { delay(400); return MockData.initialPets.toList() }

    override suspend fun addPet(pet: Pet): Pet {
        delay(500)
        val newPet = pet.copy(id = "p${System.currentTimeMillis()}")
        MockData.initialPets.add(newPet)
        return newPet
    }

    override suspend fun updatePet(id: String, pet: Pet): Pet {
        delay(500)
        val idx = MockData.initialPets.indexOfFirst { it.id == id }
        if (idx != -1) {
            MockData.initialPets[idx] = pet
            return pet
        }
        throw Exception("Pet not found")
    }

    override suspend fun deletePet(id: String) {
        delay(500)
        MockData.initialPets.removeAll { it.id == id }
    }

    override suspend fun getServices(): List<Service> { delay(400); return MockData.services }
    override suspend fun getCustomerBookings(): List<Booking> { delay(400); return MockData.customerBookings.toList() }

    override suspend fun createBooking(booking: BookingDraft): Booking {
        delay(1000)
        val service = MockData.services.find { it.id == booking.serviceId }
        val pet = MockData.initialPets.find { it.id == booking.petId }
        val staff = MockData.staffMembers.find { it.id == booking.staffId }
        val newBooking = Booking(
            id = "B${System.currentTimeMillis() % 10000}",
            petId = booking.petId,
            petName = pet?.name ?: "Unknown",
            serviceId = booking.serviceId,
            serviceName = service?.name ?: "Unknown",
            staffId = booking.staffId,
            staffName = staff?.name ?: "Bất kỳ",
            date = booking.date,
            time = booking.time,
            notes = booking.notes,
            status = "pending",
            paymentStatus = "unpaid",
            totalAmount = service?.price ?: 0,
            createdAt = "2024-03-20"
        )
        MockData.customerBookings.add(0, newBooking)
        return newBooking
    }

    override suspend fun cancelBooking(id: String): Booking {
        delay(500)
        val idx = MockData.customerBookings.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = MockData.customerBookings[idx].copy(status = "cancelled")
            MockData.customerBookings[idx] = updated
            return updated
        }
        throw Exception("Booking not found")
    }

    override suspend fun rescheduleBooking(id: String, data: Map<String, String>): Booking {
        delay(800)
        val idx = MockData.customerBookings.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = MockData.customerBookings[idx].copy(
                date = data["date"] ?: MockData.customerBookings[idx].date,
                time = data["time"] ?: MockData.customerBookings[idx].time,
                status = "pending" // Reset to pending for re-confirmation
            )
            MockData.customerBookings[idx] = updated
            return updated
        }
        throw Exception("Booking not found")
    }

    override suspend fun getCustomerNotifications(): List<NotificationItem> { delay(300); return MockData.customerNotifications }
    override suspend fun getNotifSettings(): NotifSettings { delay(200); return MockData.notifSettings }
    override suspend fun updateNotifSettings(settings: NotifSettings): NotifSettings { delay(300); MockData.notifSettings = settings; return settings }
    override suspend fun getCustomerUser(): User { delay(200); return MockData.initialUser }
    override suspend fun getStaffBookings(): List<Booking> { delay(400); return MockData.staffBookings }
    override suspend fun getShifts(): List<Shift> { delay(300); return MockData.shifts }
    override suspend fun getStaffNotifications(): List<NotificationItem> { delay(300); return MockData.staffNotifications }
    override suspend fun getTechnicianProfile(): TechnicianProfile { delay(300); return MockData.technician }

    override suspend fun getCustomers(): List<Customer> { delay(400); return MockData.ownerCustomers.toList() }
    override suspend fun addCustomer(customer: Customer): Customer {
        delay(500)
        val newC = customer.copy(id = "KH${System.currentTimeMillis()}")
        MockData.ownerCustomers.add(newC)
        return newC
    }
    override suspend fun updateCustomer(id: String, customer: Customer): Customer {
        delay(500)
        val idx = MockData.ownerCustomers.indexOfFirst { it.id == id }
        if (idx != -1) { MockData.ownerCustomers[idx] = customer; return customer }
        throw Exception("Customer not found")
    }
    override suspend fun deleteCustomer(id: String) {
        delay(500)
        MockData.ownerCustomers.removeAll { it.id == id }
    }

    override suspend fun getStaffList(): List<StaffMember> { delay(400); return MockData.ownerStaff.toList() }
    override suspend fun addStaff(staff: StaffMember): StaffMember {
        delay(500)
        val newS = staff.copy(id = "NV${System.currentTimeMillis()}")
        MockData.ownerStaff.add(newS)
        return newS
    }
    override suspend fun updateStaff(id: String, staff: StaffMember): StaffMember {
        delay(500)
        val idx = MockData.ownerStaff.indexOfFirst { it.id == id }
        if (idx != -1) { MockData.ownerStaff[idx] = staff; return staff }
        throw Exception("Staff not found")
    }
    override suspend fun deleteStaff(id: String) {
        delay(500)
        MockData.ownerStaff.removeAll { it.id == id }
    }

    override suspend fun getOwnerServices(): List<Service> { delay(400); return MockData.ownerServices.toList() }
    override suspend fun addService(service: Service): Service {
        delay(500)
        val newS = service.copy(id = "DV${System.currentTimeMillis()}")
        MockData.ownerServices.add(newS)
        return newS
    }
    override suspend fun updateService(id: String, service: Service): Service {
        delay(500)
        val idx = MockData.ownerServices.indexOfFirst { it.id == id }
        if (idx != -1) { MockData.ownerServices[idx] = service; return service }
        throw Exception("Service not found")
    }
    override suspend fun deleteService(id: String) {
        delay(500)
        MockData.ownerServices.removeAll { it.id == id }
    }

    override suspend fun getOwnerBookings(): List<Booking> { delay(400); return MockData.ownerBookings }
    override suspend fun getStaffRequests(): List<StaffRequest> { delay(300); return MockData.staffRequests }
    override suspend fun getDailyRevenue(): List<RevenuePoint> { delay(300); return MockData.revenueByDay }
    override suspend fun getMonthlyRevenue(): List<RevenuePoint> { delay(300); return MockData.revenueByMonth }
    override suspend fun getTopServices(): List<TopService> { delay(300); return MockData.topServices }

    override suspend fun updateStaffRequestStatus(id: String, status: Map<String, String>): StaffRequest {
        delay(300)
        val s = status["status"] ?: "pending"
        val idx = MockData.staffRequests.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = MockData.staffRequests[idx].copy(status = s)
            val newList = MockData.staffRequests.toMutableList()
            newList[idx] = updated
            MockData.staffRequests = newList
            return updated
        }
        throw Exception("Request not found")
    }
}
